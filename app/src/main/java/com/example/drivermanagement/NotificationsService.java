package com.example.drivermanagement;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.drivermanagement.R;

public class NotificationsService extends IntentService {

    private static final String CHANNEL_ID_LOCATION_REQUESTS = "5643";
    private static final String CHANNEL_ID_LOCATION_RECEIVED = "7845";
    private static final String CHANNEL_ID_MESSAGE = "9173";

    NotificationChannel channel;
    NotificationManager notificationManager;

    String ManagersID, DriversID;

    public NotificationsService() {super("NotificationsService");}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;


        if(intent.getExtras().containsKey("ManagerID")) {
            ManagersID = intent.getStringExtra("ManagerID");
            Log.d("NotificationsService", "Received ManagerID from Profile Activity on received location Request: " + ManagersID);
            channel = new NotificationChannel(CHANNEL_ID_LOCATION_REQUESTS, name, importance);

            try{
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);



                Intent notificationIntent = new Intent(this, ProfileActivity.class);
                notificationIntent.putExtra("ManagersID", ManagersID);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_LOCATION_REQUESTS)
                        .setSmallIcon(R.drawable.location_request)
                        .setContentTitle("DriverX")
                        .setContentText("Management sent you a location update request")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(0, builder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }



        }
        if(intent.getExtras().containsKey("DriversID")) {
            DriversID = intent.getStringExtra("DriversID");
            Log.d("NotificationsService", "Received DriversID from Profile Activity on received Location Update: " + DriversID);
            channel = new NotificationChannel(CHANNEL_ID_LOCATION_RECEIVED, name, importance);
            try{
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);



                Intent notificationIntent = new Intent(this, ProfileActivity.class);
                notificationIntent.putExtra("DriversID1", DriversID);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_LOCATION_RECEIVED)
                        .setSmallIcon(R.drawable.location_request)
                        .setContentTitle("DriverX")
                        .setContentText("Received location update from driver")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(0, builder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
