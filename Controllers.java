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
 * TEAM 6: Field Mapper + Red Zone Protection
 * 100% COMPATIBLE WITH YOUR SKELETON
 */
public class Controllers extends EObjectImpl implements IControllers {

    private static final FCALLogs LOG = FCALLogs.getInstance();
    private static final int MAX_POINTS = 5000;

    // Hardcoded red zone (lat, lng)
    private static final double[][] RED_ZONE = {
        {51.1234, 9.5678},
        {51.1240, 9.5685},
        {51.1237, 9.5690},
        {51.1230, 9.5683},
        {51.1234, 9.5678}
    };

    private final List<double[]> points = new ArrayList<>();
    private boolean recording = false;
    private boolean redZoneProtection = true;
    private boolean lastInRedZone = false;
    private IImplement implement;

    @Override
    public void init() {
        try {
            if (getImplements().isEmpty()) {
                LOG.error("team6-fieldmapper: No implement connected");
                return;
            }

            implement = getImplements().get(0);
            LOG.info("team6-fieldmapper: INIT SUCCESS - Implement connected");

        } catch (Exception e) {
            LOG.error("team6-fieldmapper: Init failed: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @Override
    public synchronized void run() {
        if (implement == null || !recording) return;

        try {
            double lat = implement.getPosition().getLatitude();
            double lng = implement.getPosition().getLongitude();

            if (lat == 0 || lng == 0) {
                LOG.warn("team6-fieldmapper: Invalid GPS");
                return;
            }

            boolean inRedZone = isInRedZone(lat, lng);

            // === RED ZONE LOGIC ===
            if (inRedZone && !lastInRedZone) {
                LOG.warn("team6-fieldmapper: ENTERED RED ZONE - PAUSING RECORD");
            } else if (!inRedZone && lastInRedZone) {
                LOG.info("team6-fieldmapper: EXITED RED ZONE - RESUMED");
            }

            lastInRedZone = inRedZone;

            if (!inRedZone || !redZoneProtection) {
                if (points.size() < MAX_POINTS) {
                    points.add(new double[]{lat, lng});
                    LOG.info("team6-fieldmapper: Point recorded: " + lat + ", " + lng);
                } else {
                    LOG.warn("team6-fieldmapper: Max points reached");
                    stopRecording();
                }
            }

            if (points.size() % 100 == 0) {
                LOG.info("team6-fieldmapper: Recorded " + points.size() + " points");
            }

        } catch (Exception e) {
            LOG.error("team6-fieldmapper: Run error: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private boolean isInRedZone(double lat, double lng) {
        int n = RED_ZONE.length;
        boolean inside = false;
        double x = lng, y = lat;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = RED_ZONE[i][1], yi = RED_ZONE[i][0];
            double xj = RED_ZONE[j][1], yj = RED_ZONE[j][0];
            boolean intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }

    // === BUTTONS ===
    public void onBtnStart() {
        recording = true;
        points.clear();
        LOG.info("team6-fieldmapper: START RECORDING");
    }

    public void onBtnStop() {
        stopRecording();
    }

    private void stopRecording() {
        recording = false;
        LOG.info("team6-fieldmapper: STOPPED - " + points.size() + " points recorded");
    }

    public void onTglRedZone(boolean enabled) {
        redZoneProtection = enabled;
        LOG.info("team6-fieldmapper: Red Zone Protection " + (enabled ? "ON" : "OFF"));
    }

    // === GENERATED CODE BELOW (DO NOT EDIT) ===
    protected EList<IImplement> implements_;

    protected Controllers() {
        super();
    }

    @Override
    protected EClass eStaticClass() {
        return MainPackage.Literals.CONTROLLERS;
    }

    public List<IImplement> getImplements() {
        if (implements_ == null) {
            implements_ = new BasicInternalEList<>(IImplement.class);
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
                default:
                    return -1;
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
                default:
                    return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }
}