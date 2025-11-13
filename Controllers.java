/**
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.nevonex.main.impl;

import com.bosch.fsp.logger.FCALLogs;
import com.bosch.nevonex.api.*;
import com.bosch.nevonex.cloud.Cloud;
import com.bosch.nevonex.cloud.CloudPriority;
import com.bosch.nevonex.implement.IImplement;
import com.bosch.nevonex.implement.IImplementControl;
import com.bosch.nevonex.implement.ImplementState;
import com.bosch.nevonex.main.IApplicationInputData;
import com.bosch.nevonex.main.IControllers;
import com.bosch.nevonex.usercontrols.UserControls;
import com.bosch.nevonex.usercontrols.UserControlsException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TEAM 6: Field Mapper + Red Zone Auto-Shutoff + Cloud Integration
 */
public class Controllers extends EObjectImpl implements IControllers {

    private static final FCALLogs LOG = FCALLogs.getInstance();
    private static final int MAX_POINTS = 10000;
    private static final Gson GSON = new Gson();

    // Dynamic Red Zone (updated from cloud)
    private double[][] RED_ZONE = null;

    // Runtime state
    private final List<LatLng> points = new ArrayList<>();
    private boolean recording = false;
    private boolean redZoneProtection = true;
    private boolean lastInRedZone = false;
    private IImplement implement;
    private IImplementControl control;

    // === INIT ===
    @Override
    public void init() {
        try {
            if (getImplements().isEmpty()) {
                LOG.error("team6-fieldmapper: No implement found");
                UserControls.getInstance().setOutputWidget("txtStatus", "No Machine");
                return;
            }

            implement = getImplements().get(0);
            control = (implement instanceof IImplementControl) ? (IImplementControl) implement : null;

            // UI Init
            UserControls.getInstance().setOutputWidget("txtTeam", "TEAM 6");
            UserControls.getInstance().setOutputWidget("txtStatus", "Ready");
            UserControls.getInstance().setOutputWidget("tglRedZone", true);

            LOG.info("team6-fieldmapper: INIT SUCCESS | Control: " + (control != null));
        } catch (Exception e) {
            LOG.error("team6-fieldmapper: Init failed - " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    // === MAIN LOOP ===
    @Override
    public synchronized void run() {
        if (implement == null || !recording) return;

        try {
            Position pos = implement.getPosition();
            if (pos == null || pos.getLatitude() == 0 || pos.getLongitude() == 0) {
                LOG.warn("team6-fieldmapper: Invalid GPS");
                return;
            }

            double lat = pos.getLatitude();
            double lng = pos.getLongitude();
            boolean inRedZone = RED_ZONE != null && isInRedZone(lat, lng);

            // Toggle from UI
            try {
                redZoneProtection = UserControls.getInstance().getInputWidget("tglRedZone", Boolean.class);
            } catch (UserControlsException ignored) {}

            // === AUTO SHUTOFF ===
            if (control != null && redZoneProtection && inRedZone && !lastInRedZone) {
                control.setImplementState(ImplementState.OFF);
                UserControls.getInstance().setOutputWidget("txtAlert", "RED ZONE! SPRAYER OFF");
                LOG.warn("team6-fieldmapper: ENTERED RED ZONE - SPRAYER OFF");
            } else if (control != null && redZoneProtection && !inRedZone && lastInRedZone) {
                control.setImplementState(ImplementState.ON);
                UserControls.getInstance().setOutputWidget("txtAlert", "Safe Zone - SPRAYER ON");
                LOG.info("team6-fieldmapper: EXITED RED ZONE - SPRAYER ON");
            }

            lastInRedZone = inRedZone;

            // === RECORD ONLY IF SAFE OR PROTECTION OFF ===
            if (!inRedZone || !redZoneProtection) {
                if (points.size() < MAX_POINTS) {
                    points.add(new LatLng(lat, lng));
                    updateHeatMap();
                } else if (points.size() == MAX_POINTS) {
                    LOG.warn("team6-fieldmapper: Max points reached");
                    stopRecording();
                }
            }

            updateStatus();
            if (!inRedZone) UserControls.getInstance().setOutputWidget("txtAlert", "");

        } catch (Exception e) {
            LOG.error("team6-fieldmapper: Run error - " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    // === POINT-IN-POLYGON ===
    private boolean isInRedZone(double lat, double lng) {
        if (RED_ZONE == null || RED_ZONE.length < 3) return false;
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

    // === UI: HEAT MAP ===
    private void updateHeatMap() {
        if (points.isEmpty()) return;
        StringBuilder geo = new StringBuilder("[");
        for (LatLng p : points) {
            geo.append(String.format("[[%f,%f],1],", p.lat, p.lng));
        }
        geo.setLength(geo.length() - 1);
        geo.append("]");
        try {
            UserControls.getInstance().setOutputWidget("mapCoverage", geo.toString());
        } catch (UserControlsException e) {
            LOG.error("Map update failed: " + e.getMessage());
        }
    }

    // === UI: STATUS ===
    private void updateStatus() {
        String status = recording ? "Recording… " + points.size() + " pts" : "Ready";
        try {
            UserControls.getInstance().setOutputWidget("txtStatus", status);
        } catch (UserControlsException ignored) {}
    }

    // === BUTTONS ===
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
        if (points.isEmpty()) {
            try { UserControls.getInstance().setOutputWidget("txtStatus", "No data"); } catch (Exception ignored) {}
            LOG.warn("team6-fieldmapper: No data to upload");
            return;
        }
        uploadToCloud();
    }

    // === CLOUD UPLOAD ===
    private void uploadToCloud() {
        try {
            String json = GSON.toJson(points);
            Cloud.getInstance().uploadData(json, CloudPriority.HIGH);
            UserControls.getInstance().setOutputWidget("txtStatus", "Uploaded! " + points.size() + " pts");
            LOG.info("team6-fieldmapper: Cloud upload SUCCESS");
        } catch (Exception e) {
            LOG.error("Upload failed: " + e.getMessage());
            try { UserControls.getInstance().setOutputWidget("txtStatus", "Upload FAILED"); } catch (Exception ignored) {}
            // Retry
            try {
                Thread.sleep(1000);
                Cloud.getInstance().uploadData(GSON.toJson(points), CloudPriority.HIGH);
                UserControls.getInstance().setOutputWidget("txtStatus", "Retry SUCCESS!");
            } catch (Exception ignored) {}
        }
    }

    // === TOGGLE ===
    public void onTglRedZone(boolean enabled) {
        redZoneProtection = enabled;
        LOG.info("team6-fieldmapper: Red Zone Protection " + (enabled ? "ON" : "OFF"));
        if (!enabled && lastInRedZone && control != null) {
            control.setImplementState(ImplementState.ON);
            try { UserControls.getInstance().setOutputWidget("txtAlert", "Protection OFF - SPRAYER ON"); } catch (Exception ignored) {}
        }
    }

    // === CLOUD → DEVICE: MESSAGE ===
    @Override
    public void onCloudMessageReceived(String message) {
        LOG.info("team6-fieldmapper: Cloud message: " + message);
        if ("START_RECORDING".equalsIgnoreCase(message)) onBtnStart();
        else if ("STOP_RECORDING".equalsIgnoreCase(message)) onBtnStop();
    }

    // === CLOUD → DEVICE: FILE (Dynamic Red Zone) ===
    @Override
    public void onCloudFileDownloaded(String fileId, byte[] data) {
        try {
            String json = new String(data);
            JsonArray zones = GSON.fromJson(json, JsonArray.class);
            if (zones.size() > 0) {
                JsonArray poly = zones.get(0).getAsJsonArray();
                RED_ZONE = new double[poly.size()][2];
                for (int i = 0; i < poly.size(); i++) {
                    JsonArray pt = poly.get(i).getAsJsonArray();
                    RED_ZONE[i][0] = pt.get(1).getAsDouble(); // lat
                    RED_ZONE[i][1] = pt.get(0).getAsDouble(); // lng
                }
                LOG.info("team6-fieldmapper: Red zone updated from cloud");
            }
        } catch (Exception e) {
            LOG.error("Failed to parse red zone file: " + e.getMessage());
        }
    }

    // === HELPER ===
    private static class LatLng {
        double lat, lng;
        LatLng(double lat, double lng) { this.lat = lat; this.lng = lng; }
    }

    // === GENERATED CODE BELOW (DO NOT EDIT) ===
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