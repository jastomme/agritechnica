package com.bosch.nevonex.main.impl;

public class btnStopPropertyListener {
    public void propertyChanged() {
        ((Controllers) getFeature()).onBtnStop();
    }
}