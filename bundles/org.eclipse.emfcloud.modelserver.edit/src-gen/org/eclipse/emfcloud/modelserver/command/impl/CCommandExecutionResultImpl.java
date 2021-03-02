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
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.emfcloud.modelserver.command.CCommandPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Execution Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getType <em>Type</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getSource <em>Source</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getAffectedObjects <em>Affected
 * Objects</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getChangeDescription <em>Change
 * Description</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getDetails
 * <em>Details</em>}</li>
 * </ul>
 *
 * @generated
 */
@SuppressWarnings("all")
public class CCommandExecutionResultImpl extends MinimalEObjectImpl.Container implements CCommandExecutionResult {
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
    * The cached value of the '{@link #getSource() <em>Source</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getSource()
    * @generated
    * @ordered
    */
   protected CCommand source;

   /**
    * The cached value of the '{@link #getAffectedObjects() <em>Affected Objects</em>}' reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getAffectedObjects()
    * @generated
    * @ordered
    */
   protected EList<EObject> affectedObjects;

   /**
    * The cached value of the '{@link #getChangeDescription() <em>Change Description</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getChangeDescription()
    * @generated
    * @ordered
    */
   protected EObject changeDescription;

   /**
    * The cached value of the '{@link #getDetails() <em>Details</em>}' map.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getDetails()
    * @generated
    * @ordered
    */
   protected EMap<String, String> details;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   protected CCommandExecutionResultImpl() {
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
      return CCommandPackage.Literals.COMMAND_EXECUTION_RESULT;
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
         eNotify(new ENotificationImpl(this, Notification.SET, CCommandPackage.COMMAND_EXECUTION_RESULT__TYPE, oldType,
            type));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public CCommand getSource() { return source; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public NotificationChain basicSetSource(final CCommand newSource, NotificationChain msgs) {
      CCommand oldSource = source;
      source = newSource;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE, oldSource, newSource);
         if (msgs == null) {
            msgs = notification;
         } else {
            msgs.add(notification);
         }
      }
      return msgs;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setSource(final CCommand newSource) {
      if (newSource != source) {
         NotificationChain msgs = null;
         if (source != null) {
            msgs = ((InternalEObject) source).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE, null, msgs);
         }
         if (newSource != null) {
            msgs = ((InternalEObject) newSource).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE, null, msgs);
         }
         msgs = basicSetSource(newSource, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE,
            newSource, newSource));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EList<EObject> getAffectedObjects() {
      if (affectedObjects == null) {
         affectedObjects = new EObjectResolvingEList<>(EObject.class, this,
            CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS);
      }
      return affectedObjects;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EObject getChangeDescription() { return changeDescription; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public NotificationChain basicSetChangeDescription(final EObject newChangeDescription, NotificationChain msgs) {
      EObject oldChangeDescription = changeDescription;
      changeDescription = newChangeDescription;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION, oldChangeDescription, newChangeDescription);
         if (msgs == null) {
            msgs = notification;
         } else {
            msgs.add(notification);
         }
      }
      return msgs;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setChangeDescription(final EObject newChangeDescription) {
      if (newChangeDescription != changeDescription) {
         NotificationChain msgs = null;
         if (changeDescription != null) {
            msgs = ((InternalEObject) changeDescription).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION, null, msgs);
         }
         if (newChangeDescription != null) {
            msgs = ((InternalEObject) newChangeDescription).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION, null, msgs);
         }
         msgs = basicSetChangeDescription(newChangeDescription, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION, newChangeDescription, newChangeDescription));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public EMap<String, String> getDetails() {
      if (details == null) {
         details = new EcoreEMap<>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY,
            EStringToStringMapEntryImpl.class, this, CCommandPackage.COMMAND_EXECUTION_RESULT__DETAILS);
      }
      return details;
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            return basicSetSource(null, msgs);
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION:
            return basicSetChangeDescription(null, msgs);
         case CCommandPackage.COMMAND_EXECUTION_RESULT__DETAILS:
            return ((InternalEList<?>) getDetails()).basicRemove(otherEnd, msgs);
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__TYPE:
            return getType();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            return getSource();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS:
            return getAffectedObjects();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION:
            return getChangeDescription();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__DETAILS:
            if (coreType) {
               return getDetails();
            } else {
               return getDetails().map();
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__TYPE:
            setType((String) newValue);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            setSource((CCommand) newValue);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS:
            getAffectedObjects().clear();
            getAffectedObjects().addAll((Collection<? extends EObject>) newValue);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION:
            setChangeDescription((EObject) newValue);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__DETAILS:
            ((EStructuralFeature.Setting) getDetails()).set(newValue);
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__TYPE:
            setType(TYPE_EDEFAULT);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            setSource((CCommand) null);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS:
            getAffectedObjects().clear();
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION:
            setChangeDescription((EObject) null);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__DETAILS:
            getDetails().clear();
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__TYPE:
            return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            return source != null;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS:
            return affectedObjects != null && !affectedObjects.isEmpty();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGE_DESCRIPTION:
            return changeDescription != null;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__DETAILS:
            return details != null && !details.isEmpty();
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
      result.append(')');
      return result.toString();
   }

} // CCommandExecutionResultImpl
