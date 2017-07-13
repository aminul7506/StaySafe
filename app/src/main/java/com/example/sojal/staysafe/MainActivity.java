package com.example.sojal.staysafe;

import android.*;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.location.LocationRequest;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;

import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,LocationListener {
    GoogleMap mMap;
    private ProgressDialog mProg;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mListener;
    public static double mLatitude,mLongitude;
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    public static String loggedIn = "Log In",userNameForPolice;
    String name = null,number = null;
    final int PICK_CONTACT = 9;
    public static String imei;
    TextView textView;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        hideItemForUser();
        textView = (TextView)hView.findViewById(R.id.username);
        MainActivity.pref = getSharedPreferences("key", MODE_PRIVATE);
        mProg = new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stay Safe");
        toolbar.getContentInsetLeft();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
       // onMapReady(mMap);
       // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        final MenuItem nav_login = menu.findItem(R.id.nav_login);
        navigationView.setNavigationItemSelectedListener(this);
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    nav_login.setTitle(firebaseAuth.getCurrentUser().getEmail() + "(Log Out)");
                    loggedIn = nav_login.getTitle().toString();
                    textView.setText(firebaseAuth.getCurrentUser().getEmail());
                    userNameForPolice = firebaseAuth.getCurrentUser().getEmail();
                    if(!loggedIn.contains("ps")){
                        hideItemForUser();
                    }
                    else hideItemForPolice();
                }
                else{
                    loggedIn = "Log In";
                    textView.setText("You are currently not logged in");
                }
            }
        };
        Intent intent = new Intent(this, MyIntentService.class);
        startService(intent);

       // Intent intent2 = new Intent(this,FirebaseIdService.class);
        //startActivity(intent2);

        //Intent intent1 = new Intent(this,MyFirebaseMessagingService.class);
        //startActivity(intent1);
        try {
            String extra = getIntent().getExtras().toString();
            if (getIntent().getExtras() != null) {
                // Call your NotificationActivity here..
                //  Intent intent1 = new Intent(MainActivity.this, MyFirebaseMessagingService.class);
                //startActivity(intent);
                SQLiteDatabase db;
                db = openOrCreateDatabase("notDB",Context.MODE_PRIVATE, null);
                db.execSQL("CREATE TABLE IF NOT EXISTS notifications(body VARCHAR);");
                //Log.d("msg", "Notification Message Body: " + getIntent().getExtras().getString());
                db.execSQL("INSERT INTO notifications VALUES('" + getIntent().getExtras() + "');");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            getWindow().setExitTransition(new Explode());
            Intent endActivity = new Intent(Intent.ACTION_MAIN);
            endActivity.addCategory(Intent.CATEGORY_HOME);
            endActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(endActivity,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
            finish();

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mLatitude = latitude;
        mLongitude = longitude;
        mMap.clear();
        LatLng latLng = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here")).showInfoWindow();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
                break;
            }

            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        //
                    }
                }
                break;
            }

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionCheck1 == PackageManager.PERMISSION_GRANTED) ;
        else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},2);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck2 == PackageManager.PERMISSION_GRANTED) ;
        else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},3);
        mMap = googleMap;
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) mMap.setMyLocationEnabled(true);
        else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
    }


    private void connectWithContactList()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cur = managedQuery(contactData, null, null, null, null);
                    ContentResolver contect_resolver = getContentResolver();

                    if (cur.moveToFirst()) {
                        String id = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        Cursor phoneCur = contect_resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                        if (phoneCur.moveToFirst()) {
                            name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            number = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                    }
                }
                break;
        }

        if(mLatitude != 0 && mLongitude != 0 && name != null) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Are you sure you want to give emergency message to " + name + " ?");
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            giveCall(name,number);
                           // Log.d("no : ", name);
                        }
                    });
            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();


        }

    }

    private void giveCall(String name1,String number1)
    {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        String city = "Not Available!", state = "Not Available!", zip = "Not Available!", country = "Not available!",
                knownName = "Not Available!", address = "Not Available!";
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            knownName = addresses.get(0).getFeatureName();
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            zip = addresses.get(0).getPostalCode();
            country = addresses.get(0).getCountryName();
            if (city == null) city = "Not Available!";
            if (state == null) state = "Not Available!";
            if (zip == null) zip = "Not Available!";
            if (country == null) country = "Not Available!";
            if (knownName == null)
                knownName = "Not Available!";
            if (address == null) address = "Not Available!";
            String location = address + "," + knownName + "," + city;
            String msg = "Hello " + name1 +
                    ",I am currently " + location + " here . I am in danger now. Please try to help me.";
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + number1));
            sendIntent.putExtra("sms_body", msg);
            MainActivity.this.startActivity(sendIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nearbyFriends) {
            //Intent intent = new Intent(MainActivity.this, SignIn.class);
            //startActivity(intent);
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        else if (id == R.id.connectWithNearbyFriends){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, ConnectWithNearbyFriend.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        else if (id == R.id.nearbyThana){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, ConnectWithNearbyThana.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        else if (id == R.id.notifications){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, Notifications.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
           /* SQLiteDatabase db;
            db = openOrCreateDatabase("notDB", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS notifications(body VARCHAR);");
            Cursor c = db.rawQuery("SELECT * FROM notifications", null);
           /* if (c.moveToFirst()) {
                String s =  c.getString(0);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
            }

            c.moveToFirst();
            do {
                String s =  c.getString(0);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());*/
        }

        else if(id == R.id.report_a_Crime){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, ReportedActivtyFromClient.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        else if(id == R.id.contactList){
            connectWithContactList();
        }

        else if(id == R.id.crime_in_your_area){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, CrimeAreaForUser.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        else if(id == R.id.nearbyHospital){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, ConnectWithNearbyHospital.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        else if(id == R.id.reported_Crimes){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, ReportedActivityforPolice.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        else if(id == R.id.crime_Statistics){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, CrimeReporting.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        else if(id == R.id.crime_Statistics_time){
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, CrimeReportingHour.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }

        if (id == R.id.terms_conditions) {
            getWindow().setSharedElementExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this,TermsCondition.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());

      } else if (id == R.id.nav_signIn) {
        getWindow().setExitTransition(new Explode());
        Intent intent = new Intent(MainActivity.this, SignIn.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());
        }
        else if (id == R.id.nav_login) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        final MenuItem nav_login = menu.findItem(R.id.nav_login);
        navigationView.setNavigationItemSelectedListener(this);
        if(nav_login.getTitle().equals("Log In")) {
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        }
        else{
            FirebaseAuth.getInstance().signOut();
            mProg.setMessage("Logging Out .... ");
            mProg.show();
            int i = 0, j = 0;
            while (i > 1000000) {
                if (j % 5 == 0) i++;
            }
            nav_login.setTitle("Log In");
            //mProg.dismiss();
            Toast.makeText(MainActivity.this, "Log Out Successful!",
                    Toast.LENGTH_LONG).show();
            getWindow().setExitTransition(new Explode());
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        }
        };
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mListener);
    }
    private void hideItemForPolice()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nearbyFriends).setVisible(false);
        nav_Menu.findItem(R.id.report_a_Crime).setVisible(false);
        nav_Menu.findItem(R.id.connectWithNearbyFriends).setVisible(false);
        nav_Menu.findItem(R.id.nearbyThana).setVisible(false);
        nav_Menu.findItem(R.id.contactList).setVisible(false);
        nav_Menu.findItem(R.id.nav_signIn).setVisible(false);
        nav_Menu.findItem(R.id.crime_in_your_area).setVisible(false);
        nav_Menu.findItem(R.id.nearbyHospital).setVisible(false);
        nav_Menu.findItem(R.id.reported_Crimes).setVisible(true);
        nav_Menu.findItem(R.id.notifications).setVisible(false);
        nav_Menu.findItem(R.id.terms_conditions).setVisible(false);
    }

    private void hideItemForUser()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.reported_Crimes).setVisible(false);
    }
    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(2000);
        getWindow().setExitTransition(slide);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back) {
            System.exit(0);

        }
        else if(id == R.id.refresh)onMapReady(mMap);
        return super.onOptionsItemSelected(item);
    }
}
