package com.esri.arcgisruntime.smartparker;

import android.app.Service;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class CarConnectService extends Service {
    public static boolean isCarConnected = false;
    private static final String TAG = "CarConnectService";
    private boolean flag = true;
    public CarConnectService() {
    }



    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

       /* for (int i = 0; i < 3; i++)
        {*/

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(flag) {

                        /*long endTime = System.currentTimeMillis() + 10*1000;
                        while (System.currentTimeMillis() < endTime) {
                            synchronized (this) {
                                try {
                                    wait(endTime - System.currentTimeMillis());
                                } catch (Exception e) {
                                }
                            }
                        }*/
                        /*try {
                            synchronized (this) {
                                wait(2000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(Context.UI_MODE_SERVICE);
                        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR) {
                            flag = false;
                            Log.d(TAG, "before-"+ System.currentTimeMillis());
                            Log.d(TAG,"Launching navigation");
                            launchMaps("google.navigation:q=840+E+Citrus+Ave,+Redlands,+CA+92374");

                            /*Uri uri = Uri.parse("google.navigation:q=1400+Barton+Rd,+Redlands,+CA+92373");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setPackage("com.google.android.apps.maps");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);*/
                            //mapIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            //startActivity(mapIntent);
                            //Log.d(TAG,"Navigation launched");
                            /*Intent messagingService = new Intent(getApplicationContext(),MyMessagingService.class);
                            startService(messagingService);*/
                            //Log.d(TAG,"Message Service started");
                            try {
                                synchronized (this) {
                                    wait(30000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "after"+ System.currentTimeMillis());
                            launchMaps2("google.navigation:q=1400+Barton+Rd,+Redlands,+CA+92373");
                            /*Log.d(TAG,"Launching 2nd navigation");
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=840+E+Citrus+Ave,+Redlands,+CA+92374");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(mapIntent);*/
                            // 840 E Citrus Ave, Redlands, CA 92374



                        } else {
                            //Log.d(TAG, "Running on a non-Car mode");

                        }
                        /*Intent i = new Intent(getApplicationContext(), CarReceiver.class);
                        startService(i);*/
                    }
                }
            });

            t.start();
        //}
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        onCreate();
        Log.i(TAG, "Service onDestroy");
    }

    public void launchMaps2(final String uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://www.google.com/maps?saddr=Esri&daddr=Los+Angeles+to:San+Bernardino";
                Uri gmmIntentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mapIntent);
            }
        }).start();
    }


    public void launchMaps(final String uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri gmmIntentUri = Uri.parse(uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mapIntent);
            }
        }).start();
    }
}
