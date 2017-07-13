package com.example.sojal.staysafe;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService implements LocationListener {

    private FirebaseAuth mAuth;
    private Firebase mDatabase;
    private Firebase mDatabase1;
    private GoogleMap mMap;
    Firebase taskRef;
    String key,imei;
    double mLatitude,mLongitude;
    SQLiteDatabase db;
    public MyIntentService() {
        super("MyIntentService");
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = new Firebase(FirebaseUrl.signInURL);
        mDatabase1 = new Firebase(FirebaseUrl.reportedUrl);
        key = MainActivity.pref.getString("keyValue","notSignedIn");
        taskRef = mDatabase.child(key);
        imei = MainActivity.imei;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            if (!MainActivity.loggedIn.equals("Log In")) {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider;
                Location location;
                provider = locationManager.getBestProvider(criteria, true);
                ContextCompat.checkSelfPermission(MyIntentService.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
                location = locationManager.getLastKnownLocation(provider);
                if (!key.equals("notSignedIn") && !MainActivity.loggedIn.equals("Log In") && location != null) {
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                    SignInClass person = new SignInClass();
                    person.setCurrentTime();
                    Firebase cTime = taskRef.child("currentTime");
                    cTime.setValue(person.getCurrentTime());
                    person.setLatitude(mLatitude);
                    person.setLongitude(mLongitude);
                    Firebase cLatitude = taskRef.child("latitude");
                    cLatitude.setValue(person.getLatitude());
                    Firebase cLongitude = taskRef.child("longitude");
                    cLongitude.setValue(person.getLongitude());
                }
                SystemClock.sleep(120000);
                if (location != null) {
                    List<Address> addressList = null;
                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {
                        addressList = geocoder.getFromLocation(mLatitude, mLongitude, 1);
                        if (addressList.size() > 0) {
                            // police_station = "";
                            Address address = addressList.get(0);
                            final String loc = address.getFeatureName() + "," + address.getLocality();
                            final String loc2 = loc;
                            final String loc1 = loc.toLowerCase();
                            final double lat = address.getLatitude();
                            final double log = address.getLongitude();
                            mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                       // String key = postSnapshot.getKey();
                                        CrimeClass crimeClass = postSnapshot.getValue(CrimeClass.class);
                                        double diffLat = lat - crimeClass.getLatitude();
                                        if(diffLat < 0) diffLat = -diffLat;
                                        double diffLog = log - crimeClass.getLongitude();
                                        if(diffLog < 0) diffLog = -diffLog;
                                        final String message = "You are now in " + loc2  + " which is marked as a crime area. It will be" +
                                                " better if you avoid this place as soon as possible. If you feel danger please" +
                                                " contact in " + crimeClass.getPolice_station() + " from this app. Thank you.";
                                        if ((crimeClass.getLocation().toLowerCase().equals(loc1)
                                                || (diffLat <= 0.2 && diffLog <= 0.2))
                                                && crimeClass.getStatus().equals("CrimeArea")) {
                                            Context ctx = getApplicationContext();
                                           // SystemClock.sleep(30000);
                                            Intent intent = new Intent(ctx, Notifications.class);
                                            PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
                                            Notification noti = new Notification.Builder(ctx)
                                                    .setContentTitle("Stay Safe : Alert Message")
                                                    .setContentText(message).setSmallIcon(R.mipmap.staysafe)
                                                    .setContentIntent(pIntent)
                                                    .build();
                                            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                                            // hide the notification after its selected
                                            noti.defaults |= Notification.DEFAULT_SOUND;
                                            noti.flags |= Notification.FLAG_AUTO_CANCEL;
                                            notificationManager.notify(0, noti);
                                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                            Date date = new Date();
                                            String currentTime = dateFormat.format(date).toString();
                                            db = openOrCreateDatabase("notificationDB",Context.MODE_PRIVATE, null);
                                            db.execSQL("CREATE TABLE IF NOT EXISTS notifications1DB(title VARCHAR,date1" +
                                                    " VARCHAR, body VARCHAR);");
                                            db.execSQL("INSERT INTO notifications1DB VALUES('" + "Alert Message : Crime Area" + "','" +
                                                    currentTime + "','" + message + "');");
                                            //SystemClock.sleep(120000);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    System.out.println("The read failed: " + firebaseError.getMessage());
                                }
                            });
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        //double latitude = location.getLatitude();
        //double longitude = location.getLongitude();
        //mLatitude = latitude;
        //mLongitude = longitude;
        //mMap.clear();
        //LatLng latLng = new LatLng(latitude,longitude);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
       // mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!"));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


}
