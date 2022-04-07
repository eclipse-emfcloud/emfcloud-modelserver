/********************************************************************************
 * Copyright (c) 2022 STMicroelectronics and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.modelserver.client;

import static org.eclipse.emfcloud.modelserver.common.APIVersion.API_V2;
import static org.eclipse.emfcloud.modelserver.tests.util.EMFMatchers.eEqualTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emfcloud.modelserver.common.codecs.Codec;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponseType;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodecV2;
import org.eclipse.emfcloud.modelserver.jsonpatch.Add;
import org.eclipse.emfcloud.modelserver.jsonpatch.JsonPatch;
import org.eclipse.emfcloud.modelserver.jsonpatch.ObjectValue;
import org.eclipse.emfcloud.modelserver.jsonschema.Json;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Suppliers;

@RunWith(MockitoJUnitRunner.class)
public class EObjectSubscriptionListenerTest {

   private Codec codec;

   private EObjectSubscriptionListener fixture;

   private EPackage model;

   public EObjectSubscriptionListenerTest() {
      super();
   }

   @Test
   public void onFullUpdate() {
      fixture.onNotification(fullUpdate());

      ArgumentCaptor<EObject> modelCaptor = ArgumentCaptor.forClass(EObject.class);
      verify(fixture).onFullUpdate(modelCaptor.capture());

      EObject reconstitutedModel = modelCaptor.getValue();
      assertThat(reconstitutedModel, eEqualTo(model));
   }

   @Test
   public void onIncrementalUpdate() {
      fixture.onNotification(incrementalUpdate());

      ArgumentCaptor<JsonPatch> patchCaptor = ArgumentCaptor.forClass(JsonPatch.class);
      verify(fixture).onIncrementalUpdate(patchCaptor.capture());

      JsonPatch patch = patchCaptor.getValue();
      assertThat(patch.getPatch().size(), is(1));
      assertThat(patch.getPatch().get(0), instanceOf(Add.class));

      Add addOp = (Add) patch.getPatch().get(0);
      assertThat(addOp.getPath(), is("/eSubpackages/-"));
      assertThat(addOp.getValue(), instanceOf(ObjectValue.class));
      ObjectValue added = (ObjectValue) addOp.getValue();
      assertThat(added.getValue(),
         eEqualTo(model.getESubpackages().get(0), EcorePackage.Literals.EPACKAGE__ESUPER_PACKAGE));
   }

   //
   // Test framework
   //

   @Before
   public void createFixture() {
      codec = new JsonCodecV2();
      EClass rootClass = EcorePackage.Literals.EPACKAGE;
      EObjectSubscriptionListener listener = new EObjectSubscriptionListener(codec);
      listener.setAPIVersion(API_V2);
      listener.setModelTypeSupplier(Suppliers.ofInstance(rootClass));
      fixture = spy(listener);
   }

   @Before
   public void createTestModel() {
      EPackage fakeEcorePackage = EcoreFactory.eINSTANCE.createEPackage();
      fakeEcorePackage.setName("ecore");
      fakeEcorePackage.setNsURI("http://www.eclipse.org/emfcloud/test/Ecore");
      List.of("EClass", "EStructuralFeature", "EDataType", "EPackage").forEach(name -> {
         EClass eClass = EcoreFactory.eINSTANCE.createEClass();
         eClass.setName(name);
         fakeEcorePackage.getEClassifiers().add(eClass);
      });

      EPackage nestedPackage = EcoreFactory.eINSTANCE.createEPackage();
      nestedPackage.setName("nested");
      nestedPackage.setNsURI("http://www.eclipse.org/emfcloud/test/Ecore/nested");
      List.of("Nested1", "Nested2").forEach(name -> {
         EClass eClass = EcoreFactory.eINSTANCE.createEClass();
         eClass.setName(name);
         nestedPackage.getEClassifiers().add(eClass);
      });

      fakeEcorePackage.getESubpackages().add(nestedPackage);

      new ResourceImpl(URI.createURI(fakeEcorePackage.getNsURI())).getContents().add(fakeEcorePackage);
      this.model = fakeEcorePackage;
   }

   ModelServerNotification fullUpdate() {
      try {
         return new ModelServerNotification(JsonResponseType.FULLUPDATE, Optional.of(codec.encode(model).toString()));
      } catch (EncodingException e) {
         e.printStackTrace();
         fail("Encoding test object failed.");
         return null; // Unreachable
      }
   }

   ModelServerNotification incrementalUpdate() {
      try {
         JsonNode modelJSON = codec.encode(model);
         JsonNode nestedPackage = modelJSON.get("eSubpackages").get(0);

         // Create an 'add' op for a nested package because nested packages do not serialize the EClass type
         // on account of the package's actual EClass being the same as the containment reference type
         JsonNode addOp = Json.object(Map.of(
            "op", Json.text("add"),
            "path", Json.text("/eSubpackages/-"),
            "value", nestedPackage));
         JsonNode patch = Json.array(addOp);

         return new ModelServerNotification(JsonResponseType.INCREMENTALUPDATE, Optional.of(patch.toString()));
      } catch (EncodingException e) {
         e.printStackTrace();
         fail("Encoding test object failed.");
         return null; // Unreachable
      }
   }

}
