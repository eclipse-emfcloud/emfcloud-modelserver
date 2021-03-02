/**
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package org.eclipse.emfcloud.modelserver.command.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Command</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl#getType <em>Type</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl#getOwner <em>Owner</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl#getFeature <em>Feature</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl#getIndices <em>Indices</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl#getDataValues <em>Data Values</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl#getObjectValues <em>Object Values</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl#getObjectsToAdd <em>Objects To Add</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandImpl#getProperties <em>Properties</em>}</li>
 * </ul>
 *
 * @generated
 */
@SuppressWarnings("all")
public class CCommandImpl extends MinimalEObjectImpl.Container implements CCommand {
   /**
    * The default value of the '{@link #getType() <em>Type</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getType()
    * @generated
    * @ordered
    */
   protected static final String TYPE_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getType()
    * @generated
    * @ordered
    */
   protected String type = TYPE_EDEFAULT;

   /**
    * The cached value of the '{@link #getOwner() <em>Owner</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getOwner()
    * @generated
    * @ordered
    */
   protected EObject owner;

   /**
    * The default value of the '{@link #getFeature() <em>Feature</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getFeature()
    * @generated
    * @ordered
    */
   protected static final String FEATURE_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getFeature() <em>Feature</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getFeature()
    * @generated
    * @ordered
    */
   protected String feature = FEATURE_EDEFAULT;

   /**
    * The cached value of the '{@link #getIndices() <em>Indices</em>}' attribute list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getIndices()
    * @generated
    * @ordered
    */
   protected EList<Integer> indices;

   /**
    * The cached value of the '{@link #getDataValues() <em>Data Values</em>}' attribute list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getDataValues()
    * @generated
    * @ordered
    */
   protected EList<String> dataValues;

   /**
    * The cached value of the '{@link #getObjectValues() <em>Object Values</em>}' reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getObjectValues()
    * @generated
    * @ordered
    */
   protected EList<EObject> objectValues;

   /**
    * The cached value of the '{@link #getObjectsToAdd() <em>Objects To Add</em>}' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getObjectsToAdd()
    * @generated
    * @ordered
    */
   protected EList<EObject> objectsToAdd;

   /**
    * The cached value of the '{@link #getProperties() <em>Properties</em>}' map.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getProperties()
    * @generated
    * @ordered
    */
   protected EMap<String, String> properties;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected CCommandImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return CCommandPackage.Literals.COMMAND;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String getType() { return type; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setType(final String newType) {
      String oldType = type;
      type = newType;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CCommandPackage.COMMAND__TYPE, oldType, type));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EObject getOwner() {
      if (owner != null && owner.eIsProxy()) {
         InternalEObject oldOwner = (InternalEObject) owner;
         owner = eResolveProxy(oldOwner);
         if (owner != oldOwner) {
            if (eNotificationRequired()) {
               eNotify(
                  new ENotificationImpl(this, Notification.RESOLVE, CCommandPackage.COMMAND__OWNER, oldOwner, owner));
            }
         }
      }
      return owner;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public EObject basicGetOwner() {
      return owner;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setOwner(final EObject newOwner) {
      EObject oldOwner = owner;
      owner = newOwner;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CCommandPackage.COMMAND__OWNER, oldOwner, owner));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String getFeature() { return feature; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setFeature(final String newFeature) {
      String oldFeature = feature;
      feature = newFeature;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CCommandPackage.COMMAND__FEATURE, oldFeature, feature));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EList<Integer> getIndices() {
      if (indices == null) {
         indices = new EDataTypeUniqueEList<>(Integer.class, this, CCommandPackage.COMMAND__INDICES);
      }
      return indices;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EList<String> getDataValues() {
      if (dataValues == null) {
         dataValues = new EDataTypeUniqueEList<>(String.class, this, CCommandPackage.COMMAND__DATA_VALUES);
      }
      return dataValues;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EList<EObject> getObjectValues() {
      if (objectValues == null) {
         objectValues = new EObjectResolvingEList<>(EObject.class, this, CCommandPackage.COMMAND__OBJECT_VALUES);
      }
      return objectValues;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EList<EObject> getObjectsToAdd() {
      if (objectsToAdd == null) {
         objectsToAdd = new EObjectContainmentEList<>(EObject.class, this,
            CCommandPackage.COMMAND__OBJECTS_TO_ADD);
      }
      return objectsToAdd;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EMap<String, String> getProperties() {
      if (properties == null) {
         properties = new EcoreEMap<>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY,
            EStringToStringMapEntryImpl.class, this, CCommandPackage.COMMAND__PROPERTIES);
      }
      return properties;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID,
      final NotificationChain msgs) {
      switch (featureID) {
         case CCommandPackage.COMMAND__OBJECTS_TO_ADD:
            return ((InternalEList<?>) getObjectsToAdd()).basicRemove(otherEnd, msgs);
         case CCommandPackage.COMMAND__PROPERTIES:
            return ((InternalEList<?>) getProperties()).basicRemove(otherEnd, msgs);
      }
      return super.eInverseRemove(otherEnd, featureID, msgs);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
      switch (featureID) {
         case CCommandPackage.COMMAND__TYPE:
            return getType();
         case CCommandPackage.COMMAND__OWNER:
            if (resolve) {
               return getOwner();
            }
            return basicGetOwner();
         case CCommandPackage.COMMAND__FEATURE:
            return getFeature();
         case CCommandPackage.COMMAND__INDICES:
            return getIndices();
         case CCommandPackage.COMMAND__DATA_VALUES:
            return getDataValues();
         case CCommandPackage.COMMAND__OBJECT_VALUES:
            return getObjectValues();
         case CCommandPackage.COMMAND__OBJECTS_TO_ADD:
            return getObjectsToAdd();
         case CCommandPackage.COMMAND__PROPERTIES:
            if (coreType) {
               return getProperties();
            } else {
               return getProperties().map();
            }
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @SuppressWarnings("unchecked")
   @Override
   public void eSet(final int featureID, final Object newValue) {
      switch (featureID) {
         case CCommandPackage.COMMAND__TYPE:
            setType((String) newValue);
            return;
         case CCommandPackage.COMMAND__OWNER:
            setOwner((EObject) newValue);
            return;
         case CCommandPackage.COMMAND__FEATURE:
            setFeature((String) newValue);
            return;
         case CCommandPackage.COMMAND__INDICES:
            getIndices().clear();
            getIndices().addAll((Collection<? extends Integer>) newValue);
            return;
         case CCommandPackage.COMMAND__DATA_VALUES:
            getDataValues().clear();
            getDataValues().addAll((Collection<? extends String>) newValue);
            return;
         case CCommandPackage.COMMAND__OBJECT_VALUES:
            getObjectValues().clear();
            getObjectValues().addAll((Collection<? extends EObject>) newValue);
            return;
         case CCommandPackage.COMMAND__OBJECTS_TO_ADD:
            getObjectsToAdd().clear();
            getObjectsToAdd().addAll((Collection<? extends EObject>) newValue);
            return;
         case CCommandPackage.COMMAND__PROPERTIES:
            ((EStructuralFeature.Setting) getProperties()).set(newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void eUnset(final int featureID) {
      switch (featureID) {
         case CCommandPackage.COMMAND__TYPE:
            setType(TYPE_EDEFAULT);
            return;
         case CCommandPackage.COMMAND__OWNER:
            setOwner((EObject) null);
            return;
         case CCommandPackage.COMMAND__FEATURE:
            setFeature(FEATURE_EDEFAULT);
            return;
         case CCommandPackage.COMMAND__INDICES:
            getIndices().clear();
            return;
         case CCommandPackage.COMMAND__DATA_VALUES:
            getDataValues().clear();
            return;
         case CCommandPackage.COMMAND__OBJECT_VALUES:
            getObjectValues().clear();
            return;
         case CCommandPackage.COMMAND__OBJECTS_TO_ADD:
            getObjectsToAdd().clear();
            return;
         case CCommandPackage.COMMAND__PROPERTIES:
            getProperties().clear();
            return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public boolean eIsSet(final int featureID) {
      switch (featureID) {
         case CCommandPackage.COMMAND__TYPE:
            return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
         case CCommandPackage.COMMAND__OWNER:
            return owner != null;
         case CCommandPackage.COMMAND__FEATURE:
            return FEATURE_EDEFAULT == null ? feature != null : !FEATURE_EDEFAULT.equals(feature);
         case CCommandPackage.COMMAND__INDICES:
            return indices != null && !indices.isEmpty();
         case CCommandPackage.COMMAND__DATA_VALUES:
            return dataValues != null && !dataValues.isEmpty();
         case CCommandPackage.COMMAND__OBJECT_VALUES:
            return objectValues != null && !objectValues.isEmpty();
         case CCommandPackage.COMMAND__OBJECTS_TO_ADD:
            return objectsToAdd != null && !objectsToAdd.isEmpty();
         case CCommandPackage.COMMAND__PROPERTIES:
            return properties != null && !properties.isEmpty();
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public String toString() {
      if (eIsProxy()) {
         return super.toString();
      }

      StringBuilder result = new StringBuilder(super.toString());
      result.append(" (type: ");
      result.append(type);
      result.append(", feature: ");
      result.append(feature);
      result.append(", indices: ");
      result.append(indices);
      result.append(", dataValues: ");
      result.append(dataValues);
      result.append(')');
      return result.toString();
   }

} // CCommandImpl
