package com.alberto.medaap2.utilities;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

public class ScannerConfig {

    private Activity activity;

    public ScannerConfig(Activity activity) {
        this.activity = activity;
    }

    public void escanear() {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setCameraId(0);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

}


