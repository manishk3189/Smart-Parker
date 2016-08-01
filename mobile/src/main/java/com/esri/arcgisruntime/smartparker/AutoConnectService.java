package com.esri.arcgisruntime.smartparker;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AutoConnectService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.google.arcgisruntime.smartparker.action.FOO";
    private static final String ACTION_BAZ = "com.google.arcgisruntime.smartparker.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.google.arcgisruntime.smartparker.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.google.arcgisruntime.smartparker.extra.PARAM2";
    private static final String TAG = "AutoConnectService";
    private static boolean isConnectedToCar = false;

    public AutoConnectService() {
        super("AutoConnectService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AutoConnectService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AutoConnectService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

/*            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }*/
            if(isCarUiMode(getApplicationContext())) {
                Intent i = new Intent(this, CarReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this.getApplicationContext(), 234324243, i, 0);
                startService(i);
            }

        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public boolean isCarUiMode(final Context c) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UiModeManager uiModeManager = (UiModeManager) c.getSystemService(Context.UI_MODE_SERVICE);
                if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR) {
                    isConnectedToCar = true;
                    Log.d(TAG, "Running in Car mode");


                } else {
                    Log.d(TAG, "Running on a non-Car mode");

                }
                Intent i = new Intent(c,CarReceiver.class);
                startService(i);
            }
        });

        t.start();
        return isConnectedToCar;

    }

}
