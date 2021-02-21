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

import java.net.MalformedURLException;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emfcloud.modelserver.client.ModelServerClient;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeeFactory;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.coffee.model.coffee.Workflow;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.edit.EMFCommandCodec;

@SuppressWarnings("uncommentedmain")
public final class ExampleModelServerSubscriptionClient {

   private ExampleModelServerSubscriptionClient() {}

   public static void main(final String[] args) {
      try {
         ModelServerClient client = new ModelServerClient("http://localhost:8081/api/v1/");

         registerPackages();

         client.subscribe("SuperBrewer3000.coffee",
            new ExampleJsonStringSubscriptionListener("SuperBrewer3000.coffee"));
         client.subscribe("SuperBrewer3000.json", new ExampleJsonStringSubscriptionListener("SuperBrewer3000.json"));
         client.subscribe("Coffee.ecore", new ExampleXMISubscriptionListener("Coffee.ecore"), "xmi");

         client.edit("SuperBrewer3000.coffee", getEditWorkflowNameCommand(), null).thenAccept(response -> {
            if (response.body()) {
               client.undo("SuperBrewer3000.coffee");
            }
         });
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }
   }

   private static void registerPackages() {
      EcorePackage.eINSTANCE.eClass();
      CCommandPackage.eINSTANCE.eClass();
      CoffeePackage.eINSTANCE.eClass();
   }

   private static CCommand getEditWorkflowNameCommand() {
      AdapterFactoryEditingDomain domain = new AdapterFactoryEditingDomain(new ComposedAdapterFactory(),
         new BasicCommandStack());
      Workflow workflowEClass = CoffeeFactory.eINSTANCE.createWorkflow();
      ((InternalEObject) workflowEClass).eSetProxyURI(URI.createURI("SuperBrewer3000.coffee#//@workflows.0"));
      Command setCommand = SetCommand.create(domain, workflowEClass, EcorePackage.Literals.ENAMED_ELEMENT__NAME,
         "Simple Renamed Workflow");
      return EMFCommandCodec.clientCommand((SetCommand) setCommand);
   }

}
