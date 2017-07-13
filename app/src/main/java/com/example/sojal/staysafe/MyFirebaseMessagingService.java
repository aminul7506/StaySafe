package com.example.sojal.staysafe;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sojal on 15-Jun-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    SQLiteDatabase db;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
       /* db = openOrCreateDatabase("notDB",Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications(body VARCHAR);");
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            db.execSQL("INSERT INTO notifications VALUES('" + remoteMessage.getData() + "');");
        }
        if (remoteMessage.getNotification() != null) {
            db.execSQL("INSERT INTO notifications VALUES('" + remoteMessage.getNotification().getBody() + "');");
        }
*/
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        String currentTime = dateFormat.format(date).toString();
        db = openOrCreateDatabase("notificationDB",Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications1DB(title VARCHAR,date1" +
                " VARCHAR, body VARCHAR);");
        db.execSQL("INSERT INTO notifications1DB VALUES('" + "Emergency Message" + "','" +
                currentTime + "','" + remoteMessage.getNotification().getBody() + "');");



  //      sendNotification(remoteMessage.getNotification().getBody());

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
        Intent i = new Intent(getApplicationContext(), Notifications.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {500,500,500,500,500};

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.BLUE,1,1)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    
}
