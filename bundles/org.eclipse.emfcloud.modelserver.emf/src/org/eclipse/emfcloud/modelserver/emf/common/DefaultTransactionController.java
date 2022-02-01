/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.emfcloud.modelserver.emf.common;

import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.conflict;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.decodingError;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.encodingError;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.error;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.modelNotFound;
import static org.eclipse.emfcloud.modelserver.emf.common.util.ContextResponse.success;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchException;
import org.eclipse.emfcloud.modelserver.common.patch.JsonPatchTestException;
import org.eclipse.emfcloud.modelserver.common.patch.PatchCommand;
import org.eclipse.emfcloud.modelserver.edit.util.CommandUtil;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.util.ContextRequest;
import org.eclipse.emfcloud.modelserver.emf.common.util.Message;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.patch.PatchCommandHandler;
import org.eclipse.emfcloud.modelserver.emf.util.JsonPatchHelper;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Suppliers;
import com.google.inject.Inject;

import io.javalin.http.Context;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;

public class DefaultTransactionController implements TransactionController {
   protected static Logger LOG = Logger.getLogger(DefaultTransactionController.class.getSimpleName());

   private static final Supplier<String> TRANSACTION_ID_FACTORY = () -> UUID.randomUUID().toString();

   protected final ModelRepository modelRepository;
   protected final SessionController sessionController;
   protected final ServerConfiguration serverConfiguration;
   protected final ModelResourceManager modelResourceManager;
   protected final ObjectMapper objectMapper;
   protected final CodecsManager codecs;
   protected final PatchCommandHandler.Registry patchCommandHandlerRegistry;
   protected final JsonPatchHelper jsonPatchHelper;

   /** Map of URL path ID portion (e.g., last segment of "/api/v2/transaction/<transaction-id>") to transaction. */
   private final Map<String, TransactionContext> transactions = new LinkedHashMap<>();

   @Inject
   public DefaultTransactionController(final ModelRepository modelRepository, final SessionController sessionController,
      final ServerConfiguration serverConfiguration, final ModelResourceManager modelResourceManager,
      final CodecsManager codecs, final ObjectMapper objectMapper,
      final PatchCommandHandler.Registry patchCommandHandlerRegistry) {

      this.modelRepository = modelRepository;
      this.sessionController = sessionController;
      this.serverConfiguration = serverConfiguration;
      this.modelResourceManager = modelResourceManager;
      this.codecs = codecs;
      this.objectMapper = objectMapper;
      this.patchCommandHandlerRegistry = patchCommandHandlerRegistry;
      this.jsonPatchHelper = new JsonPatchHelper(modelResourceManager, serverConfiguration);
   }

   @Override
   public void create(final Context ctx, final String modeluri) {
      Optional<String> clientID = ContextRequest.readData(ctx);
      if (clientID.isEmpty()) {
         return;
      }
      if (this.transactions.containsKey(modeluri)) {
         conflict(ctx, "A transaction is already open on the model.");
         return;
      }

      TransactionContext transaction = new TransactionContext(ctx, modeluri, clientID.get());
      String transactionURI = ctx.url() + "/" + transaction.getID();
      JsonNode uriResult = Json.object(Map.of("uri", Json.text(transactionURI)));

      transactions.put(transaction.getID(), transaction);
      success(ctx, uriResult);
   }

   @Override
   @SuppressWarnings("checkstyle:IllegalCatch")
   public void onOpen(final WsConnectContext ctx) {
      getTransaction(ctx).ifPresentOrElse(tc -> {
         try {
            tc.open();
            success(ctx);
         } catch (Exception e) {
            transactionError(ctx, tc, "Failed to open transaction '%s'.", e);
         }
      }, noSuchTransaction(ctx));
   }

   @Override
   @SuppressWarnings("checkstyle:IllegalCatch")
   public void onClose(final WsCloseContext ctx) {
      getTransaction(ctx).ifPresent(tc -> {
         try {
            tc.rollback();
         } catch (Exception e) {
            transactionError(ctx, tc, "Failed to roll back transaction '%s'.", e);
         }
      });
   }

   @Override
   public void onError(final WsErrorContext ctx) {
      getTransaction(ctx).ifPresentOrElse(tc -> {
         LOG.error("Error in transaction protocol.", ctx.error());
      }, noSuchTransaction(ctx));
   }

   @Override
   public void onMessage(final WsMessageContext ctx) {
      getTransaction(ctx).ifPresentOrElse(tc -> {
         readMessage(ctx).ifPresent(message -> {
            switch (message.getType()) {
               case JsonRequestType.EXECUTE:
                  message.as(JsonNode.class)
                     .flatMap(node -> patchCommandHandlerRegistry.getPatchCommand(ctx, node.getData()))
                     .ifPresentOrElse(patchCommand -> tc.execute(ctx, patchCommand),
                        () -> error(ctx, "Missing command or patch to execute."));
                  break;
               case JsonRequestType.CLOSE:
                  try {
                     tc.close();
                  } finally {
                     ctx.session.close();
                  }
                  break;
               case JsonRequestType.ROLL_BACK:
                  try {
                     tc.rollback(); // TODO: Diagnostic
                  } finally {
                     ctx.session.close();
                  }
                  break;
               default:
                  break;
            }
         });
      }, noSuchTransaction(ctx));
   }

   protected Optional<TransactionContext> getTransaction(final WsContext wsCtx) {
      return Optional.ofNullable(transactions.get(wsCtx.pathParam("id")));
   }

   protected Runnable noSuchTransaction(final WsContext wsCtx) {
      return () -> {
         error(wsCtx, "No such transaction '%s'.", getTransactionID(wsCtx.pathParam("id")));
         wsCtx.session.close();
      };
   }

   protected final String getTransactionID(final String url) {
      String[] segs = url.split("/");
      return segs[segs.length - 1];
   }

   protected void transactionError(final WsContext wsCtx, final TransactionContext transaction,
      final String messagePattern, final Exception e) {
      String message = String.format(messagePattern, transaction.getID());
      error(wsCtx, message, e);
      wsCtx.session.close();
   }

   protected Optional<Message<JsonNode>> readMessage(final WsMessageContext ctx) {
      return ContextRequest.readMessage(ctx)
         .flatMap(msg -> {
            switch (msg.getType()) {
               case JsonRequestType.EXECUTE:
                  return msg.as(json -> {
                     try {
                        return objectMapper.readTree(json);
                     } catch (JsonProcessingException e) {
                        // TODO: Return an error node?
                        return null;
                     }
                  });
               default:
                  return msg.asNull();
            }
         });
   }

   //
   // Nested types
   //

   /**
    * Enumeration of the possible states of a {@link TransactionContext}.
    */
   enum State {
      NONE,
      OPEN,
      CLOSED,
      ROLLED_BACK;

      public boolean isOpen() { return this == OPEN; }

      public boolean isClosed() { return this == CLOSED || this == ROLLED_BACK; }
   }

   /**
    * A context tracking the state of a transaction currently open on some model URI.
    * This effectively is the resource created by the {@code POST} request to the
    * {@code transaction} endpoint, having a {@code /transaction/<id>} URI.
    */
   public final class TransactionContext {

      private final String id;
      private final String modelURI;
      private final String clientID;

      private State state = State.NONE;

      private List<CCommandExecutionResult> executions;

      TransactionContext(final Context ctx, final String modelURI, final String clientID) {
         super();

         this.id = TRANSACTION_ID_FACTORY.get();
         this.modelURI = modelURI;
         this.clientID = clientID;
      }

      public String getID() { return id; }

      public String getModelURI() { return modelURI; }

      public String getClientID() { return clientID; }

      public boolean isOpen() { return state.isOpen(); }

      /**
       * Execute the given {@code command}. Its {@link CCommandExecutionResult result} or
       * JSON Patch is sent to the client in a success reply message and is queued for all
       * other clients to receive upon close of the transaction.
       *
       * @param ctx            the websocket message context
       * @param patchOrCommand the command or JSON Patch from the websocket message
       */
      public void execute(final WsMessageContext ctx, final PatchCommand<?> patchOrCommand) {
         Optional<EObject> root = modelRepository.getModel(modelURI);
         if (root.isEmpty()) {
            modelNotFound(ctx, modelURI);
            return;
         }

         switch (patchOrCommand.getType()) {
            case ModelServerPathParametersV2.EMF_COMMAND:
               execute(ctx, (CCommand) patchOrCommand.getData());
               break;
            case ModelServerPathParametersV2.JSON_PATCH:
               apply(ctx, (ArrayNode) patchOrCommand.getData());
               break;
            default:
               error(ctx, "Unrecognized patch or command type: " + patchOrCommand.getType());
               return;
         }
      }

      private void execute(final WsMessageContext ctx, final CCommand command) {
         try {
            CCommandExecutionResult execution = modelRepository.executeCommand(modelURI, command);
            JsonNode response = codecs.encode(ctx, execution);
            executions.add(execution);
            success(ctx, response);
         } catch (EncodingException exception) {
            encodingError(ctx, exception);
         } catch (DecodingException exception) {
            decodingError(ctx, exception);
         }
      }

      public void apply(final WsMessageContext ctx, final ArrayNode patch) {
         try {
            CCommandExecutionResult execution = modelRepository.executeCommand(modelURI, patch);

            JsonNode response = getJSONPatchUpdate(execution);
            if (response == null) {
               // For now, let the original patch stand in for its own result
               response = patch;
            }

            executions.add(execution);
            success(ctx, response);
         } catch (JsonPatchException | JsonPatchTestException exception) {
            error(ctx, "Inapplicable JSON patch", exception);
         }
      }

      /**
       * Open the transaction. At this point a compound is opened in the editing domain to collect
       * the commands executed during the transaction.
       */
      void open() {
         if (state == State.NONE) {
            executions = new ArrayList<>();
            getEditingDomain().openCompoundCommand();
            state = State.OPEN;
         }
      }

      /**
       * Close the transaction. At this point the commands executed and collected during the transaction
       * are put onto the stack in a compound for undo/redo and the execution results are broadcast to
       * all subscribers.
       */
      void close() {
         if (isOpen()) {
            try {
               getEditingDomain().closeCompoundCommand();

               executions.stream().reduce(CommandUtil::compose)
                  .ifPresent(aggregate -> {
                     sessionController.commandExecuted(modelURI, Suppliers.ofInstance(aggregate),
                        Suppliers.memoize(() -> getJSONPatchUpdate(aggregate)));
                  });

               executions = null;
               transactions.remove(getID());
            } finally {
               state = State.CLOSED;
            }
         }
      }

      private JsonNode getJSONPatchUpdate(final CCommandExecutionResult executionResult) {
         Optional<EObject> root = modelRepository.getModel(modelURI);
         if (root.isEmpty()) {
            return null;
         }

         try {
            return jsonPatchHelper.getJsonPatch(root.get(), executionResult);
         } catch (EncodingException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
         }
      }

      /**
       * Cancel and close the transaction. At this point the commands executed and collected during the transaction
       * are undone and discarded. No execution results are broadcast to any subscribers.
       */
      void rollback() {
         if (isOpen()) {
            try {
               getEditingDomain().rollbackCompoundCommand();

               executions = null;
               transactions.remove(getID());
            } finally {
               state = State.ROLLED_BACK;
            }
         }
      }

      ModelServerEditingDomain getEditingDomain() {
         return modelResourceManager.getEditingDomain(modelResourceManager.getResourceSet(modelURI));
      }

   }

}
