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
import org.eclipse.emfcloud.modelserver.command.ExecutionContext;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Execution Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getSource <em>Source</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getAffectedObjects <em>Affected
 * Objects</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getContext
 * <em>Context</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getProperties
 * <em>Properties</em>}</li>
 * <li>{@link org.eclipse.emfcloud.modelserver.command.impl.CCommandExecutionResultImpl#getChanges
 * <em>Changes</em>}</li>
 * </ul>
 *
 * @generated
 */
@SuppressWarnings("all")
public class CCommandExecutionResultImpl extends MinimalEObjectImpl.Container implements CCommandExecutionResult {
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
    * The default value of the '{@link #getContext() <em>Context</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getContext()
    * @generated
    * @ordered
    */
   protected static final ExecutionContext CONTEXT_EDEFAULT = ExecutionContext.EXECUTE;

   /**
    * The cached value of the '{@link #getContext() <em>Context</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getContext()
    * @generated
    * @ordered
    */
   protected ExecutionContext context = CONTEXT_EDEFAULT;

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
    * The cached value of the '{@link #getChanges() <em>Changes</em>}' containment reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @see #getChanges()
    * @generated
    * @ordered
    */
   protected EObject changes;

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
   public ExecutionContext getContext() { return context; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   @Override
   public void setContext(final ExecutionContext newContext) {
      ExecutionContext oldContext = context;
      context = newContext == null ? CONTEXT_EDEFAULT : newContext;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CCommandPackage.COMMAND_EXECUTION_RESULT__CONTEXT,
            oldContext, context));
      }
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
            EStringToStringMapEntryImpl.class, this, CCommandPackage.COMMAND_EXECUTION_RESULT__PROPERTIES);
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
   public EObject getChanges() { return changes; }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    *
    * @generated
    */
   public NotificationChain basicSetChanges(final EObject newChanges, NotificationChain msgs) {
      EObject oldChanges = changes;
      changes = newChanges;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES, oldChanges, newChanges);
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
   public void setChanges(final EObject newChanges) {
      if (newChanges != changes) {
         NotificationChain msgs = null;
         if (changes != null) {
            msgs = ((InternalEObject) changes).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES, null, msgs);
         }
         if (newChanges != null) {
            msgs = ((InternalEObject) newChanges).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES, null, msgs);
         }
         msgs = basicSetChanges(newChanges, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES,
            newChanges, newChanges));
      }
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__PROPERTIES:
            return ((InternalEList<?>) getProperties()).basicRemove(otherEnd, msgs);
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES:
            return basicSetChanges(null, msgs);
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            return getSource();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS:
            return getAffectedObjects();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CONTEXT:
            return getContext();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__PROPERTIES:
            if (coreType) {
               return getProperties();
            } else {
               return getProperties().map();
            }
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES:
            return getChanges();
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            setSource((CCommand) newValue);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS:
            getAffectedObjects().clear();
            getAffectedObjects().addAll((Collection<? extends EObject>) newValue);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CONTEXT:
            setContext((ExecutionContext) newValue);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__PROPERTIES:
            ((EStructuralFeature.Setting) getProperties()).set(newValue);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES:
            setChanges((EObject) newValue);
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            setSource((CCommand) null);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS:
            getAffectedObjects().clear();
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CONTEXT:
            setContext(CONTEXT_EDEFAULT);
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__PROPERTIES:
            getProperties().clear();
            return;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES:
            setChanges((EObject) null);
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
         case CCommandPackage.COMMAND_EXECUTION_RESULT__SOURCE:
            return source != null;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__AFFECTED_OBJECTS:
            return affectedObjects != null && !affectedObjects.isEmpty();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CONTEXT:
            return context != CONTEXT_EDEFAULT;
         case CCommandPackage.COMMAND_EXECUTION_RESULT__PROPERTIES:
            return properties != null && !properties.isEmpty();
         case CCommandPackage.COMMAND_EXECUTION_RESULT__CHANGES:
            return changes != null;
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
      result.append(" (context: ");
      result.append(context);
      result.append(')');
      return result.toString();
   }

} // CCommandExecutionResultImpl
