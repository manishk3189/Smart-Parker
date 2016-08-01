package com.esri.arcgisruntime.smartparker;

/**
 * Created by mani8177 on 7/28/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyMessageHeardReceiver extends BroadcastReceiver {
    public MyMessageHeardReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        int conversationId = intent.getIntExtra("conversation_id",0);

        Log.d("MyMessageHeardReceiver", "conversation");

    }
}