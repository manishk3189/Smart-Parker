package com.esri.arcgisruntime.smartparker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

public class CarReceiver extends BroadcastReceiver {
    private static final String TAG = "CarReceiver";


    public CarReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String status = intent.getStringExtra("media_connection_status");
        boolean isConnectedToCar = "media_connected".equals(status);
        Log.d(TAG,isConnectedToCar+"");
        if(isConnectedToCar) {
            Log.d(TAG,"launching navigator");
            Uri gmmIntentUri = Uri.parse("google.navigation:q=341+N+Grove+St,Redlands+California");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //mapIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(mapIntent);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
