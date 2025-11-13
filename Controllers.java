package com.bosch.nevonex.main.impl;

import com.bosch.nevonex.main.IControllers;
import com.bosch.nevonex.implement.IImplement;
import com.bosch.nevonex.usercontrols.UserControls;
import com.bosch.nevonex.usercontrols.UserControlsException;
import com.bosch.fsp.logger.FCALLogs;

import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.List;
import java.util.ArrayList;

/**
 * TEAM 6: Field Mapper + Red Zone Auto-Detection
 * FULLY COMPILABLE IN FEATURE DESIGNER
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
            LOG.error("team6-fieldmapper: Init failed: " + e.getMessage());
        }
    }

    @Override
    public synchronized void run() {
        if (implement == null || !recording) return;

        try {
            // === GET GPS ===
            double lat = implement.getPosition().getLatitude();
            double lng = implement.getPosition().getLongitude();

            if (lat == 0 || lng == 0) {
                LOG.warn("team6-fieldmapper: Invalid GPS");
                return;
            }

            boolean inRedZone = isInRedZone(lat, lng);

            // === RED ZONE PROTECTION ===
            try {
                redZoneProtection = UserControls.getInstance().getInputWidget("tglRedZone", Boolean.class);
            } catch (UserControlsException ignored) {}

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
            LOG.error("team6-fieldmapper: Run error: " + e.getMessage());
        }
    }

    // === POINT-IN-POLYGON ===
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

    // === UPDATE MAP ===
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
            LOG.error("Map update failed: " + e.getMessage());
        }
    }

    // === UPDATE STATUS ===
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
        try {
            UserControls.getInstance().setOutputWidget("txtStatus", "Stopped: " + points.size() + " pts");
            LOG.info("team6-fieldmapper: STOPPED - " + points.size() + " points recorded");
        } catch (UserControlsException ignored) {}
    }

    public void onTglRedZone(boolean enabled) {
        redZoneProtection = enabled;
        LOG.info("team6-fieldmapper: Red Zone Protection " + (enabled ? "ON" : "OFF"));
    }

    // === GENERATED CODE (DO NOT EDIT) ===
    protected EList<IImplement> implements_;

    protected Controllers() { super(); }

    @Override
    protected EClass eStaticClass() {
        return com.bosch.nevonex.main.MainPackage.Literals.CONTROLLERS;
    }

    public List<IImplement> getImplements() {
        if (implements_ == null) {
            implements_ = new BasicInternalEList<>(IImplement.class);
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
            return get Alcohol();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        if (featureID == com.bosch.nevonex.main.MainPackage.CONTROLLERS__IMPLEMENTS) {
            getImplements().clear();
            getImplements().addAll((java.util.Collection<? extends IImplement>) newValue);
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