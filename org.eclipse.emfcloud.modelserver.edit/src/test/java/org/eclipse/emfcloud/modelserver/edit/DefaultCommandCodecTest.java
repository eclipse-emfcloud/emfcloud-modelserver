/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.edit;

import static org.eclipse.emfcloud.modelserver.edit.tests.util.EMFMatchers.commandEqualTo;
import static org.eclipse.emf.common.notify.Notification.NO_INDEX;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.emfjson.jackson.resource.JsonResourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.eclipse.emfcloud.modelserver.coffee.model.coffee.CoffeePackage;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.command.CommandKind;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.emfcloud.modelserver.common.codecs.EMFJsonConverter;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.edit.tests.util.EMFMatchers;

/**
 * Test cases for the {@link DefaultCommandCodec} class.
 */
@RunWith(Parameterized.class)
public class DefaultCommandCodecTest {

   private static final String N_A = "n/a";
   private static final String ATTRIBUTE = "attribute";
   private static final String REFERENCE = "reference";
   private static final String REFERENCE_MANY = "reference (many)";
   private static final String REFERENCE_BY_INDEX = "reference (by index)";

   private static ResourceSet resourceSet;
   private static EditingDomain domain;
   private static EPackage ePackage;
   private static CCommand commandFixture;

   private final Command editCommand;
   private final CCommand commandModel;

   public DefaultCommandCodecTest(final CommandKind type, final String featureKind, final Command editCommand,
      final CCommand commandModel) {
      super();

      this.editCommand = editCommand;
      this.commandModel = commandModel;
   }

   @Test
   public void encode() throws EncodingException {
      CCommand encoded = new DefaultCommandCodec().encode(editCommand);
      assertThat(encoded, EMFMatchers.eEqualTo(commandModel));
   }

   @Test
   public void decode() throws DecodingException {
      Command decoded = new DefaultCommandCodec().decode(domain, commandModel);
      assertThat(decoded, commandEqualTo(editCommand));
   }

   //
   // Test framework
   //

   @Parameters(name = "{0} {1}")
   public static Iterable<Object[]> parameters() {
      initializeResourceSet();

      return Arrays.asList(new Object[][] { //
         new Object[] { CommandKind.SET, ATTRIBUTE, createAttributeSetCommand(), createAttributeSetModel() }, //
         new Object[] { CommandKind.SET, REFERENCE, createReferenceSetCommand(), createReferenceSetModel() }, //
         new Object[] { CommandKind.ADD, ATTRIBUTE, createAttributeAddCommand(), createAttributeAddModel() }, //
         new Object[] { CommandKind.ADD, REFERENCE, createReferenceAddCommand(), createReferenceAddModel() }, //
         new Object[] { CommandKind.ADD, REFERENCE_MANY, createReferenceAddMultipleCommand(),
            createReferenceAddMultipleModel() }, //
         new Object[] { CommandKind.REMOVE, ATTRIBUTE, createAttributeRemoveCommand(),
            createAttributeRemoveModel() }, //
         new Object[] { CommandKind.REMOVE, REFERENCE, createReferenceRemoveCommand(),
            createReferenceRemoveModel() }, //
         new Object[] { CommandKind.REMOVE, REFERENCE_MANY, createReferenceRemoveMultipleCommand(),
            createReferenceRemoveMultipleModel() }, //
         new Object[] { CommandKind.REMOVE, REFERENCE_BY_INDEX, createReferenceRemoveByIndexCommand(),
            createReferenceRemoveByIndexModel() }, //
         new Object[] { CommandKind.COMPOUND, N_A, createCompoundCommand(), createCompoundModel() }, //
      });
   }

   private static void initializeResourceSet() {
      // Registry the packages we need
      CoffeePackage.eINSTANCE.eClass();
      CCommandPackage.eINSTANCE.eClass();

      domain = new AdapterFactoryEditingDomain(new EcoreAdapterFactory(), new BasicCommandStack());
      resourceSet = domain.getResourceSet();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("json",
         new JsonResourceFactory(EMFJsonConverter.setupDefaultMapper()));
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
         new EcoreResourceFactoryImpl());
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
      Resource resource = resourceSet.getResource(URI.createFileURI("src/test/resources/Coffee.ecore"), true);
      ePackage = (EPackage) resource.getContents().get(0);
      resource = resourceSet.getResource(URI.createFileURI("src/test/resources/Command.xmi"), true);
      commandFixture = (CCommand) resource.getContents().get(0);
   }

   static Command createAttributeSetCommand() {
      return SetCommand.create(domain, ePackage, EcorePackage.Literals.ENAMED_ELEMENT__NAME, "Foo");
   }

   static CCommand createAttributeSetModel() {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.SET);
      result.setOwner(ePackage);
      result.setFeature("name");
      result.getDataValues().add("Foo");
      result.getIndices().add(NO_INDEX);
      return result;
   }

   static Command createReferenceSetCommand() {
      EClass newClass = EcoreFactory.eINSTANCE.createEClass();
      newClass.setName("Foo");

      return SetCommand.create(domain, ePackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS, newClass, 1);
   }

   static CCommand createReferenceSetModel() {
      EClass newClass = EcoreFactory.eINSTANCE.createEClass();
      newClass.setName("Foo");

      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.SET);
      result.setOwner(ePackage);
      result.setFeature("eClassifiers");
      result.getObjectValues().add(newClass);
      result.getObjectsToAdd().add(newClass);
      result.getIndices().add(1);
      return result;
   }

   static Command createAttributeAddCommand() {
      return AddCommand.create(domain, commandFixture, CCommandPackage.Literals.COMMAND__DATA_VALUES, "Foo", 0);
   }

   static CCommand createAttributeAddModel() {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.ADD);
      result.setOwner(commandFixture);
      result.setFeature("dataValues");
      result.getDataValues().add("Foo");
      result.getIndices().add(0);
      return result;
   }

   static Command createReferenceAddCommand() {
      EClass newClass = EcoreFactory.eINSTANCE.createEClass();
      newClass.setName("Foo");

      return AddCommand.create(domain, ePackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS, newClass, 2);
   }

   static CCommand createReferenceAddModel() {
      EClass newClass = EcoreFactory.eINSTANCE.createEClass();
      newClass.setName("Foo");

      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.ADD);
      result.setOwner(ePackage);
      result.setFeature("eClassifiers");
      result.getObjectValues().add(newClass);
      result.getObjectsToAdd().add(newClass);
      result.getIndices().add(2);
      return result;
   }

   static Command createReferenceAddMultipleCommand() {
      EClass foo = EcoreFactory.eINSTANCE.createEClass();
      foo.setName("Foo");
      EDataType bar = EcoreFactory.eINSTANCE.createEDataType();
      bar.setName("Bar");

      return AddCommand.create(domain, ePackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
         Arrays.asList(foo, bar), 2);
   }

   static CCommand createReferenceAddMultipleModel() {
      EClass foo = EcoreFactory.eINSTANCE.createEClass();
      foo.setName("Foo");
      EDataType bar = EcoreFactory.eINSTANCE.createEDataType();
      bar.setName("Bar");

      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.ADD);
      result.setOwner(ePackage);
      result.setFeature("eClassifiers");
      result.getObjectValues().addAll(Arrays.asList(foo, bar));
      result.getObjectsToAdd().addAll(Arrays.asList(foo, bar));
      result.getIndices().add(2);
      return result;
   }

   static Command createAttributeRemoveCommand() {
      return RemoveCommand.create(domain, commandFixture, CCommandPackage.Literals.COMMAND__DATA_VALUES, "Foo");
   }

   static CCommand createAttributeRemoveModel() {
      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.REMOVE);
      result.setOwner(commandFixture);
      result.setFeature("dataValues");
      result.getDataValues().add("Foo");
      return result;
   }

   static Command createReferenceRemoveCommand() {
      EClassifier removeMe = EcorePackage.Literals.ESTRING;

      return RemoveCommand.create(domain, ePackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS, removeMe);
   }

   static CCommand createReferenceRemoveModel() {
      EClassifier removeMe = EcorePackage.Literals.ESTRING;

      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.REMOVE);
      result.setOwner(ePackage);
      result.setFeature("eClassifiers");
      result.getObjectValues().add(removeMe);
      return result;
   }

   static Command createReferenceRemoveMultipleCommand() {
      EClassifier remove1 = EcorePackage.Literals.EFACTORY;
      EClassifier remove2 = EcorePackage.Literals.ESTRING;

      return RemoveCommand.create(domain, ePackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
         Arrays.asList(remove1, remove2));
   }

   static CCommand createReferenceRemoveMultipleModel() {
      EClassifier remove1 = EcorePackage.Literals.EFACTORY;
      EClassifier remove2 = EcorePackage.Literals.ESTRING;

      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.REMOVE);
      result.setOwner(ePackage);
      result.setFeature("eClassifiers");
      result.getObjectValues().addAll(Arrays.asList(remove1, remove2));
      return result;
   }

   static Command createReferenceRemoveByIndexCommand() {
      EClassifier remove1 = EcorePackage.Literals.EFACTORY;
      EClassifier remove2 = EcorePackage.Literals.ESTRING;

      return RemoveCommand.create(domain, ePackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
         remove1.getClassifierID(), remove2.getClassifierID());
   }

   static CCommand createReferenceRemoveByIndexModel() {
      EClassifier remove1 = EcorePackage.Literals.EFACTORY;
      EClassifier remove2 = EcorePackage.Literals.ESTRING;

      CCommand result = CCommandFactory.eINSTANCE.createCommand();
      result.setType(CommandKind.REMOVE);
      result.setOwner(ePackage);
      result.setFeature("eClassifiers");
      result.getIndices().add(remove1.getClassifierID());
      result.getIndices().add(remove2.getClassifierID());
      return result;
   }

   static Command createCompoundCommand() {
      return createAttributeSetCommand().chain(createReferenceAddCommand());
   }

   static CCommand createCompoundModel() {
      CCompoundCommand result = CCommandFactory.eINSTANCE.createCompoundCommand();
      result.setType(CommandKind.COMPOUND);
      result.getCommands().add(createAttributeSetModel());
      result.getCommands().add(createReferenceAddModel());
      return result;
   }

}
