/**
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.nevonex.main.impl;

import com.bosch.fsp.logger.FCALLogs;
import com.bosch.nevonex.implement.IImplement;
import com.bosch.nevonex.main.IApplicationInputData;
import com.bosch.nevonex.main.IControllers;
import com.bosch.nevonex.usercontrols.UserControls;
import com.bosch.nevonex.usercontrols.UserControlsException;

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
 * FULLY COMPATIBLE WITH YOUR SKELETON
 */
public class Controllers extends EObjectImpl implements IControllers {

    private static final FCALLogs LOG = FCALLogs.getInstance();
    private static final int MAX_POINTS = 5000;

    // Hardcoded red zone polygon (lat, lng)
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
                UserControls.getInstance().setOutputWidget("txtStatus", "No Machine");
                return;
            }

            implement = getImplements().get(0);

            UserControls.getInstance().setOutputWidget("txtTeam", "TEAM 6");
            UserControls.getInstance().setOutputWidget("txtStatus", "Ready");
            UserControls.getInstance().setOutputWidget("tglRedZone", true);
            UserControls.getInstance().setOutputWidget("txtAlert", "");

            LOG.info("team6-fieldmapper: INIT SUCCESS");
        } catch (Exception e) {
            LOG.error("team6-fieldmapper: Init failed: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @Override
    public synchronized void run() {
        if (implement == null || !recording) return;

        try {
            // === GET GPS FROM IMPLEMENT ===
            double lat = implement.getPosition().getLatitude();
            double lng = implement.getPosition().getLongitude();

            if (lat == 0 || lng == 0) {
                LOG.warn("team6-fieldmapper: Invalid GPS");
                return;
            }

            boolean inRedZone = isInRedZone(lat, lng);

            // === TOGGLE FROM UI ===
            try {
                redZoneProtection = UserControls.getInstance().getInputWidget("tglRedZone", Boolean.class);
            } catch (UserControlsException ignored) {}

            // === RED ZONE ALERT ===
            if (inRedZone && !lastInRedZone) {
                UserControls.getInstance().setOutputWidget("txtAlert", "RED ZONE! NO RECORDING");
                LOG.warn("team6-fieldmapper: ENTERED RED ZONE");
            } else if (!inRedZone && lastInRedZone) {
                UserControls.getInstance().setOutputWidget("txtAlert", "");
                LOG.info("team6-fieldmapper: EXITED RED ZONE");
            }

            lastInRedZone = inRedZone;

            // === RECORD IF SAFE ===
            if (!inRedZone || !redZoneProtection) {
                if (points.size() < MAX_POINTS) {
                    points.add(new double[]{lat, lng});
                    updateHeatMap();
                } else {
                    LOG.warn("team6-fieldmapper: Max points reached");
                    stopRecording();
                }
            }

            updateStatus();

        } catch (Exception e) {
            LOG.error("team6-fieldmapper: Run error: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    // === POINT-IN-POLYGON ALGORITHM ===
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

    // === UPDATE HEAT MAP ===
    private void updateHeatMap() {
        if (points.isEmpty()) return;
        StringBuilder geo = new StringBuilder("[");
        for (double[] p : points) {
            geo.append(String.format("[[%f,%f],1],", p[0], p[1]));
        }
        geo.setLength(geo.length() - 1);
        geo.append("]");
        try {
            UserControls.getInstance().setOutputWidget("mapCoverage", geo.toString());
        } catch (UserControlsException e) {
            LOG.error("Map update failed: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    // === UPDATE STATUS TEXT ===
    private void updateStatus() {
        String status = recording ? "Recording… " + points.size() + " pts" : "Ready";
        try {
            UserControls.getInstance().setOutputWidget("txtStatus", status);
        } catch (UserControlsException ignored) {}
    }

    // === BUTTON HANDLERS ===
    public void onBtnStart() {
        recording = true;
        points.clear();
        updateHeatMap();
        updateStatus();
        LOG.info("team6-fieldmapper: START RECORDING");
    }

    public void onBtnStop() {
        stopRecording();
    }

    private void stopRecording() {
        recording = false;
        try {
            UserControls.getInstance().setOutputWidget("txtStatus", "Stopped: " + points.size() + " pts");
            LOG.info("team6-fieldmapper: STOPPED - " + points.size() + " points recorded");
        } catch (UserControlsException ignored) {}
    }

    public void onTglRedZone(boolean enabled) {
        redZoneProtection = enabled;
        LOG.info("team6-fieldmapper: Red Zone Protection " + (enabled ? "ON" : "OFF"));
    }

    // === GENERATED CODE BELOW (FROM YOUR SKELETON) ===
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