/**
Copyright (c) Robert Bosch GmbH. All rights reserved.
*/
package com.bosch.nevonex.main.impl;

import com.bosch.fsp.logger.FCALLogs;

import com.bosch.nevonex.implement.IImplement;

import com.bosch.nevonex.main.IApplicationInputData;
import com.bosch.nevonex.main.IControllers;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * An implementation of the model object '<em><b>Controllers</b></em>'.
 
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.bosch.nevonex.main.impl.Controllers#getImplements <em>Implements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Controllers extends EObjectImpl implements IControllers {
	/**
	 * The cached value of the '{@link #getImplements() <em>Implements</em>}' containment reference list.
	 * @see #getImplements()
	 * @generated
	 * @ordered
	 */
	protected EList<IImplement> implements_;

	/**
	 * @generated
	 */
	protected Controllers() {
		super();
	}

	@Override
	public synchronized void run() {
		try {
			im
			//UserControls.getInstance().markPosition(48.81923026884091,9.144052121074528);

			//List<double[]>  drawFence = new ArrayList<double[]>();
			//double[] point= {48.81923026884091,9.138219403610915};
			//drawFence.add(point);
			//UserControls.getInstance().drawFence(drawFence);

			//List<double[]>  inputrate = new ArrayList<double[]>();
			//double[] inpoint= {48.80521281465467,9.138734055151817,200.23};
			//inputrate.add(inpoint);
			//UserControls.getInstance().inputRate(inputrate);

			//charts
			//float applicationrate =100.0f;
			//float[] applicationratevalues = {applicationrate};
			//UserControls.getInstance().setmyCustomRate(applicationratevalues);
			// Cloud.getInstance().uploadData("data", 1);
		} catch (Exception e) {
			FCALLogs.getInstance().log.error(ExceptionUtils.getRootCauseMessage(e));
		}
	}

	/**
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MainPackage.Literals.CONTROLLERS;
	}

	/**
	 * @generated
	 */
	public List<IImplement> getImplements() {
		if (implements_ == null) {
			implements_ = new BasicInternalEList<IImplement>(IImplement.class);
		}
		return implements_;
	}

	/**
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case MainPackage.CONTROLLERS__IMPLEMENTS:
			return ((InternalEList<?>) getImplements()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case MainPackage.CONTROLLERS__IMPLEMENTS:
			return getImplements();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case MainPackage.CONTROLLERS__IMPLEMENTS:
			getImplements().clear();
			getImplements().addAll((Collection<? extends IImplement>) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case MainPackage.CONTROLLERS__IMPLEMENTS:
			getImplements().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case MainPackage.CONTROLLERS__IMPLEMENTS:
			return implements_ != null && !implements_.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == IApplicationInputData.class) {
			switch (derivedFeatureID) {
			case MainPackage.CONTROLLERS__IMPLEMENTS:
				return MainPackage.APPLICATION_INPUT_DATA__IMPLEMENTS;
			default:
				return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == IApplicationInputData.class) {
			switch (baseFeatureID) {
			case MainPackage.APPLICATION_INPUT_DATA__IMPLEMENTS:
				return MainPackage.CONTROLLERS__IMPLEMENTS;
			default:
				return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}
} //Controllers
