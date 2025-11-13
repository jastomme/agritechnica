package com.bosch.nevonex.main.impl;

public class btnStartPropertyListener {
    public Object getFeature() {
        return null; // Will be set by framework
    }

    public void propertyChanged() {
        Controllers controller = (Controllers) getFeature();
        if (controller != null) {
            controller.onBtnStart();
        }
    }
}