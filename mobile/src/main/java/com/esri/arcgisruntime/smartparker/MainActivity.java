package com.esri.arcgisruntime.smartparker;

import android.app.PendingIntent;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.datasource.arcgis.ArcGISFeature;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AutoParking";
    private static final int NOTIFICATION_ID = 1;
    private MapView mMapView;
    private ArcGISMap mMap;
    private Portal mPortal;
    private PortalItem mPortalItem;
    private android.graphics.Point mClickPoint;
    private ArcGISFeature mSelectedArcGISFeature;
    private int requestCode = 2;
    String[] reqPermissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission
            .ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private LocationDisplay mLocationDisplay;
    FloatingActionButton mLocationFAB;
    private SearchView mSearchView;
    FeatureLayer mFeatureLayer;
    private int reqTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkifConnectedtoAuto();
        // inflate MapView from layout
        mMapView = (MapView) findViewById(R.id.mapView);
        // create a map with the BasemapType topographic
        final ArcGISMapImageLayer mapImageLayer = new ArcGISMapImageLayer(getResources().getString(R.string.world_street));
        // create an empty map instance
        // set tiled layer as basemap
        Basemap basemap = new Basemap(mapImageLayer);
        mMap = new ArcGISMap(getResources().getString(R.string.world_street));

        mMap.setInitialViewpoint(new Viewpoint(new Point(-118.2446708, 34.0224386, SpatialReferences.getWgs84()), 5e4));
        //initializeSensorFeatureLayer();
        initializeSensorFeatureLayer();
        mMapView.setMap(mMap);

        mLocationFAB = (FloatingActionButton) findViewById(R.id.locationFab);
        // get the MapView's LocationDisplay
        mLocationDisplay = mMapView.getLocationDisplay();

        // Listen to changes in the status of the location data source.
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {

                // If LocationDisplay started OK, then continue.
                if (dataSourceStatusChangedEvent.isStarted())
                    return;

                // No error is reported, then continue.
                if (dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError() == null)
                    return;

                // If an error is found, handle the failure to start.
                // Check permissions to see if failure may be due to lack of permissions.
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
                } else {
                    // Report other unknown failure types to the user - for example, location services may not
                    // be enabled on the device.
                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                            .getSource().getLocationDataSource().getError().getMessage());
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    buildAlertMessageNoGps();

                }
            }
        });

        mLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mLocationDisplay.isStarted()) {
                    mLocationDisplay.startAsync();
                }
                //sendNotification("Hey");



                if(mLocationDisplay.isStarted()) {
                    Log.d(TAG,"showLocation");
                    Point currentLocation = mLocationDisplay.getMapLocation();
                    Viewpoint vp = new Viewpoint(currentLocation, 5e3);
                    mMapView.setViewpointWithDurationAsync(vp, 6);
                }
            }
        });
    }

    public void checkifConnectedtoAuto() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(Context.UI_MODE_SERVICE);
                if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR) {
                    Log.d(TAG, "Running in Car mode");
                    // 840 E Citrus Ave, Redlands, CA 92374
                    //launchMaps("google.navigation:q=840+E+Citrus+Ave,+Redlands,+CA+92374");
                    /*try {
                        wait(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    sendNotification("Do you want parking assistance?");
                    //1400 Barton Rd, Redlands, CA 92373
                    Log.d(TAG,"second request");
                    //launchMaps2("google.navigation:q=1400+Barton+Rd,+Redlands,+CA+92373");

                    sendNotification("Do you want parking assistance?");



                } else {
                    Log.d(TAG, "Running on a non-Car mode");

                }
            }
        });

        t.start();
    }

    public void launchMaps(final String uri) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri gmmIntentUri = Uri.parse(uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mapIntent);
            }
        });
        t.start();
    }
    public void launchMaps2(final String uri) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri gmmIntentUri = Uri.parse(uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mapIntent);
            }
        });
    }

    private void initializeSensorFeatureLayer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final ServiceFeatureTable mSensorFeatureTable = new ServiceFeatureTable("http://maps.lacity.org/lahub/rest/services/LADOT/MapServer/16");
                mFeatureLayer = new FeatureLayer(mSensorFeatureTable);
                mSensorFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);
                mSensorFeatureTable.loadAsync();
                mFeatureLayer.loadAsync();
                mFeatureLayer.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        if(mFeatureLayer.getLoadStatus() == LoadStatus.LOADED) {
                            Log.d(TAG,"service ft loaded");
                            mMapView.getMap().getOperationalLayers().add(mFeatureLayer);
                        }
                    }
                });

                mSensorFeatureTable.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        if(mSensorFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                            Log.d(TAG,"service table loaded");
                        }
                    }
                });
            }
        });
        t.start();


    }

    @Override
    protected void onPause(){
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Location permission was granted. This would have been triggered in response to failing to start the
            // LocationDisplay, so try starting this again.
            mLocationDisplay.startAsync();
        } else {
            // If permission was denied, show toast to inform user what was chosen. If LocationDisplay is started again,
            // request permission UX will be shown again, option should be shown to allow never showing the UX again.
            // Alternative would be to disable functionality so request is not shown again.
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMessage("Please enable your GPS before proceeding")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void sendNotification(final String message) {
                Log.d(TAG,"sendNotification");
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                // END_INCLUDE(build_action)

                int thisConversationID = 42;
                Intent msgHeardIntent = new Intent()
                        .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                        .setAction("com.example.android.basicnotifications.MY_ACTION_MESSAGE_HEARD")
                        .putExtra("conversation_id",thisConversationID);

                PendingIntent msgHeardPendingIntent =
                        PendingIntent.getBroadcast(getApplicationContext(),
                                thisConversationID,
                                msgHeardIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                Intent msgReplyIntent = new Intent()
                        .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                        .setAction("com.myapp.messagingservice.MY_ACTION_MESSAGE_REPLY")
                        .putExtra("conversation_id", thisConversationID);

                PendingIntent msgReplyPendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(),
                        thisConversationID,
                        msgReplyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                // Build a RemoteInput for receiving voice input in a Car Notification
                RemoteInput remoteInput = new RemoteInput.Builder("voice_reply_key")
                        .setLabel("Prompt Text")
                        .build();

                // Create an unread conversation object to organize a group of messages
// from a particular sender.
                NotificationCompat.CarExtender.UnreadConversation.Builder unreadConvBuilder =
                        new NotificationCompat.CarExtender.UnreadConversation.Builder("Smart Parker")
                                .setReadPendingIntent(msgHeardPendingIntent)
                                .setReplyAction(msgReplyPendingIntent, remoteInput);


                unreadConvBuilder.addMessage(message).setLatestTimestamp(System.currentTimeMillis());

// Create an unread conversation object to organize a group of messages
// from a particular sender.
        /*NotificationCompat.CarExtender.UnreadConversation.Builder unreadConvBuilder =
                new NotificationCompat.CarExtender.UnreadConversation.Builder(conversationName)
                        .setReadPendingIntent(msgHeardPendingIntent)
                        .setReplyAction(msgReplyPendingIntent, remoteInput);*/
                // BEGIN_INCLUDE (build_notification)
                /**
                 * Use NotificationCompat.Builder to set up our notification.
                 */
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.extend(new NotificationCompat.CarExtender()
                        .setUnreadConversation(unreadConvBuilder.build()));

                /** Set the icon that will appear in the notification bar. This icon also appears
                 * in the lower right hand corner of the notification itself.
                 *
                 * Important note: although you can use any drawable as the small icon, Android
                 * design guidelines state that the icon should be simple and monochrome. Full-color
                 * bitmaps or busy images don't render well on smaller screens and can end up
                 * confusing the user.
                 */
                builder.setSmallIcon(android.R.drawable.stat_notify_more);

                // Set the intent that will fire when the user taps the notification.
                builder.setContentIntent(pendingIntent);

                // Set the notification to auto-cancel. This means that the notification will disappear
                // after the user taps it, rather than remaining until it's explicitly dismissed.
                builder.setAutoCancel(true);

                /**
                 *Build the notification's appearance.
                 * Set the large icon, which appears on the left of the notification. In this
                 * sample we'll set the large icon to be the same as our app icon. The app icon is a
                 * reasonable default if you don't have anything more compelling to use as an icon.
                 */
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.stat_notify_chat));

                /**
                 * Set the text of the notification. This sample sets the three most commononly used
                 * text areas:
                 * 1. The content title, which appears in large type at the top of the notification
                 * 2. The content text, which appears in smaller text below the title
                 * 3. The subtext, which appears under the text on newer devices. Devices running
                 *    versions of Android prior to 4.2 will ignore this field, so don't use it for
                 *    anything vital!
                 */
                builder.setContentTitle("Smart Parker");
                builder.setContentText("Do you want parking assisatance?");
                builder.setSubText("Tap to view documentation about notifications.");

                // END_INCLUDE (build_notification)

                // BEGIN_INCLUDE(send_notification)
                /**
                 * Send the notification. This will immediately display the notification icon in the
                 * notification bar.
                 */
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                // END_INCLUDE(send_notification)

    }
}
