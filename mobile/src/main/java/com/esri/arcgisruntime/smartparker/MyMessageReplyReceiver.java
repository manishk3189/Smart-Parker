package com.esri.arcgisruntime.smartparker;

/**
 * Created by mani8177 on 7/28/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

public class MyMessageReplyReceiver extends BroadcastReceiver {
    public MyMessageReplyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        int conversationId = intent.getIntExtra("conversation_id",-1);

        Log.d("MyMessageReplyReceiver", "conversation");
        getMessageText(intent);

    }

    /**
     * Get the message text from the intent.
     * Note that you should call
     * RemoteInput.getResultsFromIntent() to process
     * the RemoteInput.
     */
    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput =
                RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            CharSequence reply = remoteInput.getCharSequence("voice_reply_key");
            Log.d("MyMsgReplyReceiver",reply + "");

            return remoteInput.getCharSequence("voice_reply_key");
        }
        return null;
    }
}