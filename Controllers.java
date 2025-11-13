package com.bosch.nevonex.main.impl;

import com.bosch.nevonex.main.IControllers;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.List;
import java.util.ArrayList;

/**
 * TEAM 6: Minimal working mapper (no SDK extras)
 */
public class Controllers extends EObjectImpl implements IControllers {

    private boolean recording = false;
    private final List<double[]> points = new ArrayList<>();
    private static final int MAX_POINTS = 1000;

    @Override
    public void init() {
        // UI: Show "Ready"
        try {
            // UserControls not available → skip
        } catch (Exception ignored) {}
    }

    @Override
    public synchronized void run() {
        if (!recording) return;

        // Simulate GPS (no Position class)
        double lat = 51.1234 + (Math.random() * 0.001);
        double lng = 9.5678 + (Math.random() * 0.001);

        // Simulate red zone
        boolean inRedZone = Math.abs(lat - 51.1235) < 0.0002 && Math.abs(lng - 9.5680) < 0.0002;

        if (!inRedZone && points.size() < MAX_POINTS) {
            points.add(new double[]{lat, lng});
        }

        // Update map (no UserControls → skip)
        // updateHeatMap();
    }

    // === BUTTONS (called by listeners) ===
    public void onBtnStart() {
        recording = true;
        points.clear();
    }

    public void onBtnStop() {
        recording = false;
        // No cloud → just log size
        System.out.println("Recorded " + points.size() + " points");
    }

    public void onTglRedZone(boolean enabled) {
        // Red zone toggle
    }

    // === GENERATED CODE BELOW (DO NOT EDIT) ===
    protected EList<com.bosch.nevonex.implement.IImplement> implements_;

    protected Controllers() { super(); }

    @Override
    protected EClass eStaticClass() {
        return com.bosch.nevonex.main.MainPackage.Literals.CONTROLLERS;
    }

    public List<com.bosch.nevonex.implement.IImplement> getImplements() {
        if (implements_ == null) {
            implements_ = new BasicInternalEList<>(com.bosch.nevonex.implement.IImplement.class);
        }
        return implements_;
    }

    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        if (featureID == com.bosch.nevonex.main.MainPackage.CONTROLLERS__IMPLEMENTS) {
            return ((org.eclipse.emf.ecore.util.InternalEList<?>) getImplements()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        if (featureID == com.bosch.nevonex.main.MainPackage.CONTROLLERS__IMPLEMENTS) {
            return getImplements();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        if (featureID == com.bosch.nevonex.main.MainPackage.CONTROLLERS__IMPLEMENTS) {
            getImplements().clear();
            getImplements().addAll((java.util.Collection<? extends com.bosch.nevonex.implement.IImplement>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    @Override
    public void eUnset(int featureID) {
        if (featureID == com.bosch.nevonex.main.MainPackage.CONTROLLERS__IMPLEMENTS) {
            getImplements().clear();
        }
        super.eUnset(featureID);
    }

    @Override
    public boolean eIsSet(int featureID) {
        if (featureID == com.bosch.nevonex.main.MainPackage.CONTROLLERS__IMPLEMENTS) {
            return implements_ != null && !implements_.isEmpty();
        }
        return super.eIsSet(featureID);
    }
}