package com.example.sojal.staysafe;

import android.*;
import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ConnectWithNearbyThana extends AppCompatActivity implements LocationListener,OnMapReadyCallback {

    GoogleMap mMap;
    NetworkInfo[] info;
   // private static final String GOOGLE_API_KEY = "AIzaSyAZ20VbUM0NrMl_qrDj44Qv6PukmNS8wsk";
    private static final  String GOOGLE_API_KEY = "AIzaSyAZ20VbUM0NrMl_qrDj44Qv6PukmNS8wsk";
    private int PROXIMITY_RADIUS = 5000;
    int ind = 0,ind1 = 0;
    public static double mLatitude = 0,mLongitude = 0;
    private int checker = 0;
    int map_ready = 0;
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_connect_with_nearby_thana);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Police Station");
        context = getApplicationContext();
        setSupportActionBar(toolbar);
        setupWindowAnimations();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        if (MainActivity.loggedIn.equals("Log In")) {
            //Toast.makeText(MapsActivity.this, "You have to log in first to get nearby friends.",
            //      Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ConnectWithNearbyThana.this);
            builder1.setMessage("You have to log in first to get nearby friends.");
            builder1.setNeutralButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        else {
           // onMapReady(mMap);
            showNearbyThana();
        }

    }

    private void showNearbyThana()
    {
        ind = 0;
        ind1 = 0;
        ConnectivityManager check1 = (ConnectivityManager)
                ConnectWithNearbyThana.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = check1.getAllNetworkInfo();
        for (int i = 0; i < info.length; i++) {
            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                ind = 1;
            }
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))ind1 = 1;

        if(ind == 0){
            Toast.makeText(ConnectWithNearbyThana.this,"Please enable internet connection.",Toast.LENGTH_LONG).show();
        }
        if(ind1 == 0){
            Toast.makeText(ConnectWithNearbyThana.this, "Please enable gps connection in your devide", Toast.LENGTH_LONG).show();
        }
        if(ind == 1 && ind1 == 1){
            if(mLatitude != 0 && mLongitude != 0)
            {
                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlacesUrl.append("location=" + mLatitude + "," + mLongitude);
                googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                googlePlacesUrl.append("&types=" + "police");
                googlePlacesUrl.append("&sensor=true");
                googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

                GooglePlacesReadTask1 googlePlacesReadTask = new GooglePlacesReadTask1();
                Object[] toPass = new Object[2];
                toPass[0] = mMap;
                toPass[1] = googlePlacesUrl.toString();
             //   googlePlacesReadTask.execute(toPass);
                googlePlacesReadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,toPass);
            }

            else{
                Toast.makeText(ConnectWithNearbyThana.this, "Location has noy set yet.Please try to refresh.",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mLatitude = latitude;
        mLongitude = longitude;
        if(checker == 0)mMap.clear();  // wait wait .. here you are not allowed to remain happy .
        else if(checker == 1)showNearbyThana();
        LatLng latLng = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!")).showInfoWindow();
        if(checker == 0)checker = 1;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) mMap.setMyLocationEnabled(true);
        else ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
        map_ready = 1;
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
        }
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(2000);
        getWindow().setEnterTransition(fade);
        Slide slide = new Slide();
        fade.setDuration(2000);
        getWindow().setReturnTransition(slide);
    }
    @Override
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(ConnectWithNearbyThana.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());

    }

    private void giveCall()
    {
        if(mLatitude != 0 && mLongitude != 0 && PlacesDisplayTask1.police_station != null){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ConnectWithNearbyThana.this);
            builder1.setMessage("Are you sure you want to call in " + PlacesDisplayTask1.police_station + " ?");
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + PlacesDisplayTask1.phone_no));
                            int permissionCheck = ContextCompat.checkSelfPermission(ConnectWithNearbyThana.this, android.Manifest.permission.CALL_PHONE);
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                ConnectWithNearbyThana.this.startActivity(callIntent);
                            }
                        }
                    });
            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
        else{
            Toast.makeText(ConnectWithNearbyThana.this, "Location has noy set yet.Please try to refresh.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void giveMessage()
    {
        if(mLatitude != 0 && mLongitude != 0 && PlacesDisplayTask1.police_station != null) {
            Geocoder geocoder = new Geocoder(ConnectWithNearbyThana.this, Locale.getDefault());
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
                String msg = "Hello Police officer of " + PlacesDisplayTask1.police_station +
                        ",I am currently " + location + " here . I am in danger now. Please try to help me.";
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + PlacesDisplayTask1.phone_no));
                sendIntent.putExtra("sms_body", msg);
                ConnectWithNearbyThana.this.startActivity(sendIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            else {
            Toast.makeText(ConnectWithNearbyThana.this, "Location has noy set yet.Please try to refresh.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thana, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.call){
            giveCall();
        }
        else if(id == R.id.message){
            giveMessage();
        }
        else if (id == R.id.back) {
            getWindow().setSharedElementExitTransition(new Explode());
            Intent intent = new Intent(ConnectWithNearbyThana.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        else if(id == R.id.refresh)showNearbyThana();
        return super.onOptionsItemSelected(item);
    }

}
