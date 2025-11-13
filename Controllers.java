/**
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.nevonex.main.impl;

import com.bosch.fsp.logger.FCALLogs;
import com.bosch.nevonex.implement.IImplement;
import com.bosch.nevonex.main.IApplicationInputData;
import com.bosch.nevonex.main.IControllers;

import java.util.ArrayList;
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
 * TEAM 6: Field Mapper (NO GPS — MOCK MODE)
 * 100% COMPATIBLE WITH YOUR SKELETON
 */
public class Controllers extends EObjectImpl implements IControllers {

    private static final FCALLogs LOG = FCALLogs.getInstance();
    private static final int MAX_POINTS = 1000;

    private final List<double[]> points = new ArrayList<>();
    private boolean recording = false;
    private boolean redZoneProtection = true;
    private boolean lastInRedZone = false;

    // MOCK GPS — simulate movement
    private double mockLat = 51.1234;
    private double mockLng = 9.5678;
    private int step = 0;

    @Override
    public void init() {
        try {
            if (getImplements().isEmpty()) {
                LOG.error("team6-fieldmapper: No implement");
                return;
            }
            LOG.info("team6-fieldmapper: INIT OK (MOCK GPS MODE)");
        } catch (Exception e) {
            LOG.error("Init failed: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @Override
    public synchronized void run() {
        if (!recording) return;

        try {
            // === MOCK GPS ===
            step++;
            mockLat += 0.0001 * (step % 20 < 10 ? 1 : -1);
            mockLng += 0.00005;
            if (mockLng > 9.5690) mockLng = 9.5678;

            double lat = mockLat;
            double lng = mockLng;

            boolean inRedZone = isInRedZone(lat, lng);

            if (inRedZone && !lastInRedZone) {
                LOG.warn("MOCK: ENTERED RED ZONE");
            } else if (!inRedZone && lastInRedZone) {
                LOG.info("MOCK: EXITED RED ZONE");
            }

            lastInRedZone = inRedZone;

            if (!inRedZone || !redZoneProtection) {
                if (points.size() < MAX_POINTS) {
                    points.add(new double[]{lat, lng});
                    if (points.size() % 20 == 0) {
                        LOG.info("MOCK: Recorded " + points.size() + " points");
                    }
                } else {
                    stopRecording();
                }
            }

        } catch (Exception e) {
            LOG.error("Run error: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private boolean isInRedZone(double lat, double lng) {
        double[][] zone = {
            {51.1234, 9.5678}, {51.1240, 9.5685}, {51.1237, 9.5690},
            {51.1230, 9.5683}, {51.1234, 9.5678}
        };
        int n = zone.length;
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double yi = zone[i][0], xi = zone[i][1];
            double yj = zone[j][0], xj = zone[j][1];
            if ((yi > lat) != (yj > lat) && (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
        }
        return inside;
    }

    public void onBtnStart() {
        recording = true;
        points.clear();
        step = 0;
        mockLat = 51.1234;
        mockLng = 9.5678;
        LOG.info("MOCK: START RECORDING");
    }

    public void onBtnStop() {
        stopRecording();
    }

    private void stopRecording() {
        recording = false;
        LOG.info("MOCK: STOPPED - " + points.size() + " points");
    }

    public void onTglRedZone(boolean enabled) {
        redZoneProtection = enabled;
        LOG.info("MOCK: Red Zone Protection " + (enabled ? "ON" : "OFF"));
    }

    // === GENERATED CODE (EXACT FROM SKELETON) ===
    protected EList<IImplement> implements_;

    protected Controllers() { super(); }

    @Override
    protected EClass eStaticClass() { return MainPackage.Literals.CONTROLLERS; }

    public List<IImplement> getImplements() {
        if (implements_ == null) {
            implements_ = new BasicInternalEList<IImplement>(IImplement.class);
        }
        return implements_;
    }

    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case MainPackage.CONTROLLERS__IMPLEMENTS:
                return ((InternalEList<?>) getImplements()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case MainPackage.CONTROLLERS__IMPLEMENTS:
                return getImplements();
        }
        return super.eGet(featureID, resolve, coreType);
    }

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

    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case MainPackage.CONTROLLERS__IMPLEMENTS:
                getImplements().clear();
                return;
        }
        super.eUnset(featureID);
    }

    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case MainPackage.CONTROLLERS__IMPLEMENTS:
                return implements_ != null && !implements_.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
        if (baseClass == IApplicationInputData.class) {
            switch (derivedFeatureID) {
                case MainPackage.CONTROLLERS__IMPLEMENTS:
                    return MainPackage.APPLICATION_INPUT_DATA__IMPLEMENTS;
                default: return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
        if (baseClass == IApplicationInputData.class) {
            switch (baseFeatureID) {
                case MainPackage.APPLICATION_INPUT_DATA__IMPLEMENTS:
                    return MainPackage.CONTROLLERS__IMPLEMENTS;
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }
}