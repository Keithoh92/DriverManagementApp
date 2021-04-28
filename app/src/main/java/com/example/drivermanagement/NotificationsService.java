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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.drivermanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*

    ALL NOTIFICATIONS PASS THROUGH THIS SERVICE
        - LOCATION REQUESTS FROM THE MANAGER
        - LOCATION UPDATE RECEIVED FROM DRIVER
        - MESSAGE NOTIFICATIONS

 */
public class NotificationsService extends IntentService {

    private static final String CHANNEL_ID_LOCATION_REQUESTS = "5643";
    private static final String CHANNEL_ID_LOCATION_RECEIVED = "7845";
    private static final String CHANNEL_ID_MESSAGE = "9173";
    String username;

    NotificationChannel channel, channel2;
    NotificationManager notificationManager;

    private FirebaseAuth fAuth;
    private DatabaseReference usersRef;


    String ManagersID, DriversID, messengersID, messageReceived, messageFromUsername;

    public NotificationsService() {super("NotificationsService");}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //SET UP NOTIFICATION CHANNEL
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        usersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");


        //LOCATION REQUEST RECEIVED FROM MANAGER - NOTIFY DRIVER
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
        //LOCATION UPDATE RECEIVED FROM USER - NOTIFY MANAGER
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

        /////Received message notification
        if(intent.getExtras().containsKey("messageReceivedFrom")) {
            messengersID = intent.getStringExtra("messageReceivedFrom");
            messageReceived = intent.getStringExtra("message");
            messageFromUsername = intent.getStringExtra("fromUsername");
            Log.d("NotificationsService", "Received messangers ID from Chat Activity on received message: " + messengersID);
            Log.d("NotificationsService", "Received message from Chat Activity on received message: " + messageReceived);


            usersRef.child(messengersID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    username = snapshot.child("username").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            CharSequence name2 = getString(R.string.channel_name2);
            channel2 = new NotificationChannel(CHANNEL_ID_MESSAGE, name2, importance);
            try{
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                channel2.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel2);



                Intent notificationIntent = new Intent(this, ChatActivity.class);
                notificationIntent.putExtra("MessengersID", messengersID);
                notificationIntent.putExtra("MessengersUsername", username);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_MESSAGE)
                        .setSmallIcon(R.drawable.mess_icon2)
                        .setContentTitle("DriverX")
                        .setContentText("Received message from "+username+": \n"+messageReceived)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
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
