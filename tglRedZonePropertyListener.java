package com.bosch.nevonex.main.impl;
public class tglRedZonePropertyListener {
    public void propertyChanged(boolean value) { ((Controllers) getFeature()).onTglRedZone(value); }
}