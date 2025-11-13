package com.bosch.nevonex.main.impl;

public class tglRedZonePropertyListener {
    public Object getFeature() {
        return null; // Will be set by framework
    }

    public void propertyChanged(boolean value) {
        Controllers controller = (Controllers) getFeature();
        if (controller != null) {
            controller.onTglRedZone(value);
        }
    }
}