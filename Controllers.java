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
 * TEAM 6: Field Mapper + Red Zone
 * MATCHES UI: btnStart, btnStop, tglRedZone, txtStatus, txtAlert, mapCoverage
 */
public class Controllers extends EObjectImpl implements IControllers {

    private static final FCALLogs LOG = FCALLogs.getInstance();
    private static final int MAX_POINTS = 5000;

    private static final double[][] RED_ZONE = {
        {51.1234, 9.5678}, {51.1240, 9.5685}, {51.1237, 9.5690},
        {51.1230, 9.5683}, {51.1234, 9.5678}
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
                log("No implement connected");
                return;
            }
            implement = getImplements().get(0);
            log("INIT: Implement ready");
            updateStatus("Ready");
        } catch (Exception e) {
            log("INIT FAILED: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @Override
    public synchronized void run() {
        if (implement == null || !recording) return;

        try {
            double lat = implement.getPosition().getLatitude();
            double lng = implement.getPosition().getLongitude();

            if (lat == 0 || lng == 0) return;

            boolean inRedZone = isInRedZone(lat, lng);

            if (inRedZone && !lastInRedZone) {
                updateAlert("RED ZONE! PAUSED");
                log("ENTERED RED ZONE");
            } else if (!inRedZone && lastInRedZone) {
                updateAlert("");
                log("EXITED RED ZONE");
            }

            lastInRedZone = inRedZone;

            if (!inRedZone || !redZoneProtection) {
                if (points.size() < MAX_POINTS) {
                    points.add(new double[]{lat, lng});
                    updateMap();
                } else {
                    stopRecording();
                }
            }

            updateStatus("Recording: " + points.size() + " pts");

        } catch (Exception e) {
            log("RUN ERROR: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private boolean isInRedZone(double lat, double lng) {
        int n = RED_ZONE.length;
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double yi = RED_ZONE[i][0], xi = RED_ZONE[i][1];
            double yj = RED_ZONE[j][0], xj = RED_ZONE[j][1];
            if ((yi > lat) != (yj > lat) && (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
        }
        return inside;
    }

    private void updateMap() {
        StringBuilder sb = new StringBuilder("[");
        for (double[] p : points) {
            sb.append(String.format("[[%f,%f],1],", p[0], p[1]));
        }
        if (sb.length() > 1) sb.setLength(sb.length() - 1);
        sb.append("]");
        log("MAP_UPDATE: " + sb.toString().substring(0, Math.min(100, sb.length())) + "...");
        // In real UI: UserControls.getInstance().setOutputWidget("mapCoverage", sb.toString());
    }

    private void updateStatus(String msg) {
        log("STATUS: " + msg);
        // In real UI: UserControls.getInstance().setOutputWidget("txtStatus", msg);
    }

    private void updateAlert(String msg) {
        log("ALERT: " + msg);
        // In real UI: UserControls.getInstance().setOutputWidget("txtAlert", msg);
    }

    private void log(String msg) {
        LOG.info("team6-fieldmapper: " + msg);
        System.out.println("team6-fieldmapper: " + msg);
    }

    // === BUTTONS ===
    public void onBtnStart() {
        recording = true;
        points.clear();
        updateMap();
        updateStatus("Recording...");
        log("START PRESSED");
    }

    public void onBtnStop() {
        recording = false;
        updateStatus("Stopped: " + points.size() + " pts");
        log("STOP PRESSED - Final count: " + points.size());
    }

    public void onTglRedZone(boolean enabled) {
        redZoneProtection = enabled;
        log("RED ZONE PROTECTION: " + (enabled ? "ON" : "OFF"));
    }

    // === GENERATED CODE ===
    protected EList<IImplement> implements_;

    protected Controllers() { super(); }

    @Override
    protected EClass eStaticClass() { return MainPackage.Literals.CONTROLLERS; }

    public List<IImplement> getImplements() {
        if (implements_ == null) implements_ = new BasicInternalEList<>(IImplement.class);
        return implements_;
    }

    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        if (featureID == MainPackage.CONTROLLERS__IMPLEMENTS)
            return ((InternalEList<?>) getImplements()).basicRemove(otherEnd, msgs);
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        if (featureID == MainPackage.CONTROLLERS__IMPLEMENTS) return getImplements();
        return super.eGet(featureID, resolve, coreType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        if (featureID == MainPackage.CONTROLLERS__IMPLEMENTS) {
            getImplements().clear();
            getImplements().addAll((Collection<? extends IImplement>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    @Override
    public void eUnset(int featureID) {
        if (featureID == MainPackage.CONTROLLERS__IMPLEMENTS) getImplements().clear();
        super.eUnset(featureID);
    }

    @Override
    public boolean eIsSet(int featureID) {
        return featureID == MainPackage.CONTROLLERS__IMPLEMENTS && implements_ != null && !implements_.isEmpty()
            || super.eIsSet(featureID);
    }

    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
        if (baseClass == IApplicationInputData.class && derivedFeatureID == MainPackage.CONTROLLERS__IMPLEMENTS)
            return MainPackage.APPLICATION_INPUT_DATA__IMPLEMENTS;
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
        if (baseClass == IApplicationInputData.class && baseFeatureID == MainPackage.APPLICATION_INPUT_DATA__IMPLEMENTS)
            return MainPackage.CONTROLLERS__IMPLEMENTS;
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }
}