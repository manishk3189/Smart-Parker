package com.esri.arcgisruntime.smartparker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CarConnectReceiver extends BroadcastReceiver {
    public CarConnectReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d  (  TAG, "onReceive()..." + intent.getAction()) ;
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static String TAG = "CarConnectReceiver";
}
