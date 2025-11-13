package com.bosch.nevonex.main.impl;

public class btnStartPropertyListener {
    public void propertyChanged() {
        ((Controllers) getFeature()).onBtnStart();
    }
}