/********************************************************************************
 * Copyright (c) 2020-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.example.client;

import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2.FORMAT_JSON;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2.FORMAT_JSON_V2;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2.FORMAT_XMI;
import static org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV2.PATHS_URI_FRAGMENTS;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emfcloud.modelserver.client.Model;
import org.eclipse.emfcloud.modelserver.client.ModelServerClient;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.client.SubscriptionListener;
import org.eclipse.emfcloud.modelserver.client.SubscriptionOptions;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.common.APIVersion;
import org.eclipse.emfcloud.modelserver.edit.command.SetCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.util.CommandUtil;
import org.eclipse.emfcloud.modelserver.example.CoffeePackageConfiguration;
import org.eclipse.emfcloud.modelserver.example.UpdateTaskNameCommandContribution;
import org.eclipse.emfcloud.modelserver.example.util.PrintUtil;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

@SuppressWarnings("all")
public final class ExampleModelServerClient {

   private static final String CMD_QUIT = "quit";
   private static final String CMD_HELP = "help";
   private static final String CMD_PING = "ping";
   private static final String CMD_REDO = "redo";
   private static final String CMD_UNDO = "undo";
   private static final String CMD_RENAME_WORKFLOW = "rename-workflow";
   private static final String CMD_UPDATE_TASKS = "update-tasks";
   private static final String CMD_GET = "get";
   private static final String CMD_GET_ALL = "getAll";
   private static final String CMD_UNSUBSCRIBE = "unsubscribe";
   private static final String CMD_SUBSCRIBE = "subscribe";
   private static final String CMD_ECORE = "ecore";

   private static final String COFFEE_ECORE = "Coffee.ecore";
   private static final String SUPER_BREWER_3000_COFFEE = "SuperBrewer3000.coffee";
   private static final String SUPER_BREWER_3000_JSON = "SuperBrewer3000.json";

   private static final String ARG_AS = "as";
   private static final String ARG_EOBJECT = "eobject";

   private static final String ARG_WITH = "with";
   private static final String ARG_URIS = "uris";

   private static final APIVersion API_VERSION = APIVersion.API_V2;

   private ExampleModelServerClient() {}

   public static void main(final String[] args) {

      try (
         ModelServerClient client = new ModelServerClient("http://localhost:8081/api/v2/",
            new CoffeePackageConfiguration());
         Scanner userInput = new Scanner(System.in)) {
         System.out.println("Simple Model Server Client Interface");
         System.out.println("====================================");
         printHelp();

         String input = "";
         while (!input.contentEquals(CMD_QUIT)) {
            System.out.print("\n> ");
            input = userInput.nextLine();
            if (input.contentEquals(CMD_QUIT)) {
               System.out.println("< Goodbye!");
               break;
            }
            if (input.isEmpty()) {
               continue;
            }
            try {
               String[] commandAndArgs = input.split(" ");
               String command = commandAndArgs[0];
               if (command.contentEquals("1")) {
                  handleSubscribe(client, new String[] { CMD_SUBSCRIBE, SUPER_BREWER_3000_COFFEE, FORMAT_JSON_V2 });
               } else if (command.contentEquals("2")) {
                  handleSubscribe(client, new String[] { CMD_SUBSCRIBE, SUPER_BREWER_3000_JSON, FORMAT_JSON_V2 });
               } else if (command.contentEquals("3")) {
                  handleSubscribe(client, new String[] { CMD_SUBSCRIBE, COFFEE_ECORE, FORMAT_XMI });
               } else if (command.contentEquals("4")) {
                  handleUnsubscribe(client, new String[] { CMD_UNSUBSCRIBE, SUPER_BREWER_3000_COFFEE });
               } else if (command.contentEquals("5")) {
                  handleUnsubscribe(client, new String[] { CMD_UNSUBSCRIBE, SUPER_BREWER_3000_JSON });
               } else if (command.contentEquals("6")) {
                  handleUnsubscribe(client, new String[] { CMD_UNSUBSCRIBE, COFFEE_ECORE });
               } else if (command.contentEquals("7")) {
                  handleRenameWorkflow(client, new String[] { CMD_RENAME_WORKFLOW, "Simple Renamed Workflow" });
               } else if (command.contentEquals(CMD_SUBSCRIBE)) {
                  handleSubscribe(client, commandAndArgs);
               } else if (command.contentEquals(CMD_UNSUBSCRIBE)) {
                  handleUnsubscribe(client, commandAndArgs);
               } else if (command.contentEquals(CMD_GET)) {
                  handleGet(client, commandAndArgs);
               } else if (command.contentEquals(CMD_GET_ALL)) {
                  handleGetAll(client, commandAndArgs);
               } else if (command.contentEquals(CMD_UPDATE_TASKS)) {
                  handleUpdateTasks(client, commandAndArgs);
               } else if (command.contentEquals(CMD_UNDO)) {
                  handleUndo(client, commandAndArgs);
               } else if (command.contentEquals(CMD_REDO)) {
                  handleRedo(client, commandAndArgs);
               } else if (command.contentEquals(CMD_PING)) {
                  handlePing(client);
               } else if (command.contentEquals(CMD_ECORE)) {
                  handleRegisterEcore(commandAndArgs);
               } else if (command.contentEquals(CMD_HELP)) {
                  printHelp();
               } else {
                  System.out.println("> Unknown command '" + command + "'.");
               }
               // sleep for any additional client output
               Thread.sleep(500);
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      } catch (MalformedURLException exception) {
         exception.printStackTrace();
      }

   }

   private static void handleRegisterEcore(final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {

      Future<EPackage> registeredPackage = CompletableFuture.supplyAsync(() -> {
         EPackage result = null;
         ResourceSet rset = new ResourceSetImpl();
         EcorePackage.eINSTANCE.getEAnnotation();
         rset.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

         File file = new File(commandAndArgs[1]);
         if (!file.exists()) {
            System.err.println("< No such Ecore model: " + file);
         } else {
            try {
               Resource res = rset.getResource(URI.createFileURI(file.getAbsolutePath()), true);
               if (res.getContents().isEmpty() || !(res.getContents().get(0) instanceof EPackage)) {
                  System.err.println("< No EPackage found in model: " + file);
               } else {
                  result = (EPackage) res.getContents().get(0);
                  if (!EPackage.Registry.INSTANCE.containsKey(result.getNsURI())) {
                     EPackage.Registry.INSTANCE.put(result.getNsURI(), result);
                  } else {
                     result = EPackage.Registry.INSTANCE.getEPackage(result.getNsURI());
                  }
               }
            } catch (WrappedException e) {
               System.err.printf("< Failed to load Ecore model \"%s\": %s%n", file, e.getMessage());
            }
         }

         return result;

      });

      EPackage ePackage = registeredPackage.get();
      if (ePackage != null) {
         System.out.printf("< %s%n", ePackage.getNsURI());
      } // Otherwise we already printed an error
   }

   private static void handleRedo(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {
      Response<String> response = client.redo(commandAndArgs[1]).join();
      System.out.println("< " + toString(response));
   }

   private static void handleUndo(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {
      Response<String> response = client.undo(commandAndArgs[1]).join();
      System.out.println("< " + toString(response));
   }

   private static void handleRenameWorkflow(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {
      CCommand command = SetCommandContribution.clientCommand(
         CommandUtil.createProxy(CoffeePackage.Literals.WORKFLOW, "SuperBrewer3000.coffee#//@workflows.0"),
         EcorePackage.Literals.ENAMED_ELEMENT__NAME,
         commandAndArgs[1]);
      Response<String> response = client.edit(SUPER_BREWER_3000_COFFEE, command, FORMAT_JSON_V2).join();
      System.out.println("< " + toString(response));
   }

   private static void handleUpdateTasks(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {
      CCommand command = UpdateTaskNameCommandContribution.clientCommand(commandAndArgs[1]);
      Response<String> response = client.edit(SUPER_BREWER_3000_JSON, command, FORMAT_JSON_V2).join();
      System.out.println("< " + toString(response));
   }

   private static void handleGet(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException, IOException {
      if (isAsEObject(commandAndArgs)) {
         String format = APIVersion.API_V2.range().includes(API_VERSION) ? FORMAT_JSON_V2 : FORMAT_JSON;
         Response<EObject> response = client.get(commandAndArgs[1], format).join();
         System.out.println("< " + commandAndArgs[1]);
         System.out.println(PrintUtil.toPrettyString(response.body()));
      } else {
         Response<String> response = client.get(commandAndArgs[1]).join();
         System.out.println("< " + commandAndArgs[1]);
         System.out.println(Json.parse(response.body()).toPrettyString());
      }
   }

   private static void handleGetAll(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String format = commandAndArgs.length > 1 ? commandAndArgs[1] : null;
      if (format == null) {
         // use getAll without parameters and expect String values (json)
         Response<List<Model<String>>> response = client.getAll().join();
         System.out.println(response.body().size() + " models");
         for (Model<String> model : response.body()) {
            System.out.println("- " + model.getModelUri() + ": " + model.getContent());
         }
      } else {
         // use getAll with format (json or xmi) and expect EObject
         Response<List<Model<EObject>>> response = client.getAll(format).join();
         System.out.println(response.body().size() + " models");
         for (Model<EObject> model : response.body()) {
            System.out.println("- " + model.getModelUri() + ": " + model.getContent().eClass());
         }
      }
   }

   private static void handleSubscribe(final ModelServerClient client, final String[] command)
      throws InterruptedException {

      String modelUri = command.length > 1 ? command[1] : "";
      String format = command.length > 2 ? command[2] : FORMAT_JSON_V2;

      SubscriptionListener listener = format.contentEquals(FORMAT_XMI)
         ? new ExampleXMISubscriptionListener(modelUri)
         : isAsEObject(command)
            ? new ExampleEObjectSubscriptionListener(modelUri, API_VERSION)
            : new ExampleJsonStringSubscriptionListener(modelUri);

      SubscriptionOptions.Builder options = SubscriptionOptions.builder()
         .withFormat(format);
      if (isWithURIs(command)) {
         options = options.withPathScheme(PATHS_URI_FRAGMENTS);
      }

      client.subscribe(modelUri, listener, options.build());
      System.out.println("< OK");
   }

   private static boolean isAsEObject(final String[] command) {
      int last = command.length - 1;

      for (int i = 0; i < last; i++) {
         if (ARG_AS.equalsIgnoreCase(command[i]) && ARG_EOBJECT.equalsIgnoreCase(command[i + 1])) {
            return true;
         }
      }
      return false;
   }

   private static boolean isWithURIs(final String[] command) {
      int last = command.length - 1;

      for (int i = 0; i < last; i++) {
         if (ARG_WITH.equalsIgnoreCase(command[i]) && ARG_URIS.equalsIgnoreCase(command[i + 1])) {
            return true;
         }
      }
      return false;
   }

   private static void handleUnsubscribe(final ModelServerClient client, final String[] command)
      throws InterruptedException {
      String modelUri = command.length >= 1 ? command[1] : "";
      client.unsubscribe(modelUri);
      System.out.println("< OK");
   }

   private static void handlePing(final ModelServerClient client)
      throws InterruptedException {
      client.ping().thenAccept(response -> System.out.println("< " + response.body()));
   }

   private static void printHelp() {
      System.out.println("Supported commands:");
      System.out.println("- " + CMD_SUBSCRIBE + " <modelUri> <format> [as eobject] [with uris]");
      System.out.println("- " + CMD_UNSUBSCRIBE + " <modelUri>");
      System.out.println("- " + CMD_GET + " <modelUri> [as eobject]");
      System.out.println("- " + CMD_GET_ALL + " <format>");
      System.out.println("- " + CMD_UPDATE_TASKS + " <name> // adapts all task names in SuperBrewer3000.json (custom)");
      System.out.println("- " + CMD_UNDO + " <modelUri>");
      System.out.println("- " + CMD_REDO + " <modelUri>");
      System.out.println("- " + CMD_ECORE + " <file> // load and register an Ecore model used on the server");
      System.out.println("- " + CMD_PING);
      System.out.println("- " + CMD_HELP);
      System.out.println("- " + CMD_QUIT);
      System.out.println();
      System.out.println("Supported shortcuts:");
      System.out.println("- 1: Subscribe to SuperBrewer3000.coffee in JSON");
      System.out.println("- 2: Subscribe to SuperBrewer3000.json in JSON");
      System.out.println("- 3: Subscribe to Coffee.ecore in XMI");
      System.out.println("- 4: Unsubscribe from SuperBrewer3000.coffee");
      System.out.println("- 5: Unsubscribe from SuperBrewer3000.json");
      System.out.println("- 6: Unsubscribe from Coffee.ecore");
      System.out.println("- 7: Rename first workflow to 'Simple Renamed Workflow' in SuperBrewer3000.coffee");
   }

   private static String toString(final Response<?> response) {
      return response.getStatusCode() + ": " + String.valueOf(response.body()) + " " + response.getMessage();
   }

}
