/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.example.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emfcloud.modelserver.client.ModelServerClient;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.client.SubscriptionListener;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.edit.command.SetCommandContribution;
import org.eclipse.emfcloud.modelserver.edit.util.CommandUtil;
import org.eclipse.emfcloud.modelserver.example.CoffeePackageConfiguration;
import org.eclipse.emfcloud.modelserver.example.UpdateTaskNameCommandContribution;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;

@SuppressWarnings("all")
public final class ExampleModelServerClient {

   private static final String CMD_QUIT = "quit";
   private static final String CMD_HELP = "help";
   private static final String CMD_REDO = "redo";
   private static final String CMD_UNDO = "undo";
   private static final String CMD_RENAME_WORKFLOW = "rename-workflow";
   private static final String CMD_UPDATE_TASKS = "update-tasks";
   private static final String CMD_GET = "get";
   private static final String CMD_UNSUBSCRIBE = "unsubscribe";
   private static final String CMD_SUBSCRIBE = "subscribe";

   private static final String COFFEE_ECORE = "Coffee.ecore";
   private static final String SUPER_BREWER_3000_COFFEE = "SuperBrewer3000.coffee";
   private static final String SUPER_BREWER_3000_JSON = "SuperBrewer3000.json";

   private ExampleModelServerClient() {}

   public static void main(final String[] args) {
      try (
         ModelServerClient client = new ModelServerClient("http://localhost:8081/api/v1/",
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
                  handleSubscribe(client, new String[] { CMD_SUBSCRIBE, SUPER_BREWER_3000_COFFEE, "json" });
               } else if (command.contentEquals("2")) {
                  handleSubscribe(client, new String[] { CMD_SUBSCRIBE, SUPER_BREWER_3000_JSON, "json" });
               } else if (command.contentEquals("3")) {
                  handleSubscribe(client, new String[] { CMD_SUBSCRIBE, COFFEE_ECORE, "xmi" });
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
               } else if (command.contentEquals(CMD_UPDATE_TASKS)) {
                  handleUpdateTasks(client, commandAndArgs);
               } else if (command.contentEquals(CMD_UNDO)) {
                  handleUndo(client, commandAndArgs);
               } else if (command.contentEquals(CMD_REDO)) {
                  handleRedo(client, commandAndArgs);
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

   private static void handleRedo(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {
      Response<Boolean> response = client.redo(commandAndArgs[1]).join();
      System.out.println("< " + toString(response));
   }

   private static void handleUndo(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {
      Response<Boolean> response = client.undo(commandAndArgs[1]).join();
      System.out.println("< " + toString(response));
   }

   private static void handleRenameWorkflow(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {
      CCommand command = SetCommandContribution.clientCommand(
         CommandUtil.createProxy(CoffeePackage.Literals.WORKFLOW, "SuperBrewer3000.coffee#//@workflows.0"),
         EcorePackage.Literals.ENAMED_ELEMENT__NAME,
         commandAndArgs[1]);
      Response<Boolean> response = client.edit(SUPER_BREWER_3000_COFFEE, command, null).join();
      System.out.println("< " + toString(response));
   }

   private static void handleUpdateTasks(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException {
      CCommand command = UpdateTaskNameCommandContribution.clientCommand(commandAndArgs[1]);
      Response<Boolean> response = client.edit(SUPER_BREWER_3000_JSON, command, null).join();
      System.out.println("< " + toString(response));
   }

   private static void handleGet(final ModelServerClient client, final String[] commandAndArgs)
      throws InterruptedException, ExecutionException, TimeoutException, IOException {
      Response<String> response = client.get(commandAndArgs[1]).join();
      System.out.println("< " + commandAndArgs[1]);
      System.out.println(Json.parse(response.body()).toPrettyString());
   }

   private static void handleSubscribe(final ModelServerClient client, final String[] command)
      throws InterruptedException {
      String modelUri = command.length > 1 ? command[1] : "";
      String format = command.length > 2 ? command[2] : "json";
      SubscriptionListener listener = format.contentEquals("json")
         ? new ExampleJsonStringSubscriptionListener(modelUri)
         : new ExampleXMISubscriptionListener(modelUri);
      client.subscribe(modelUri, listener, format);
      System.out.println("< OK");
   }

   private static void handleUnsubscribe(final ModelServerClient client, final String[] command)
      throws InterruptedException {
      String modelUri = command.length >= 1 ? command[1] : "";
      client.unsubscribe(modelUri);
      System.out.println("< OK");
   }

   private static void printHelp() {
      System.out.println("Supported commands:");
      System.out.println("- " + CMD_SUBSCRIBE + " <modelUri> <format>");
      System.out.println("- " + CMD_UNSUBSCRIBE + " <modelUri>");
      System.out.println("- " + CMD_GET + " <modelUri>");
      System.out.println("- " + CMD_UPDATE_TASKS + " <name> // adapts all task names in SuperBrewer3000.json (custom)");
      System.out.println("- " + CMD_UNDO + " <modelUri>");
      System.out.println("- " + CMD_REDO + " <modelUri>");
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

   private static String toString(final Response<Boolean> response) {
      return response.getStatusCode() + ": " + response.body() + " " + response.getMessage();
   }

}
