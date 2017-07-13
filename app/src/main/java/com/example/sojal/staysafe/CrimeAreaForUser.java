package com.example.sojal.staysafe;

import android.*;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class CrimeAreaForUser extends AppCompatActivity implements LocationListener,OnMapReadyCallback {
    GoogleMap mMap;
    double mLatitude = 0,mLongitude = 0;
    private int checker = 0;
    int map_ready = 0;
    private Firebase mDatabase;
    public static int markerPosition = -1;
    public static List<MarkerClass> markerClasses = new ArrayList<MarkerClass>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        setContentView(R.layout.activity_crime_area_for_user);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Crimes Report");
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase = new Firebase(FirebaseUrl.reportedUrl);
        if (MainActivity.loggedIn.equals("Log In")) {
            //Toast.makeText(MapsActivity.this, "You have to log in first to get nearby friends.",
            //      Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(CrimeAreaForUser.this);
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
            setCrimeArea();
        }
    }




    private void setCrimeArea()
    {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(mLatitude != 0 && mLongitude != 0) {
                    mMap.clear();
                    markerClasses.clear();
                    LatLng latLng1 = new LatLng(mLatitude, mLongitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.addMarker(new MarkerOptions().position(latLng1).title("You are here!")).showInfoWindow();
                    int id = 0;
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        CrimeClass crimeClass = postSnapshot.getValue(CrimeClass.class);
                        if (crimeClass.getStatus().equals("Verified") || crimeClass.getStatus().equals("CrimeArea")) {
                            double latitude = crimeClass.getLatitude();
                            double longitude = crimeClass.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(crimeClass.getTitle()));
                            marker.showInfoWindow();
                            marker.setTag(marker.getPosition());
                            MarkerClass markerClass = new MarkerClass();
                            markerClass.setTime(crimeClass.getTime());
                            markerClass.setDate(crimeClass.getDate());
                            markerClass.setDescription(crimeClass.getPost_description());
                            markerClass.setMarker(marker);
                            markerClass.setMarkerId(id);
                            markerClass.setTitle(crimeClass.getTitle());
                            markerClass.setLocation(crimeClass.getLocation());
                            markerClasses.add(markerClass);
                            id++;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mLatitude = latitude;
        mLongitude = longitude;
        if(checker == 0)mMap.clear();  // wait wait .. here you are not allowed to remain happy .
        else if(checker == 1)setCrimeArea();
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
       // mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
        map_ready = 1;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int size = markerClasses.size();
                for(int i = 0; i < size; i++){
                    if(marker.equals(markerClasses.get(i).getMarker())){
                        markerPosition = i;
                        getWindow().setSharedElementExitTransition(new Explode());
                        Intent intent = new Intent(CrimeAreaForUser.this, ReportDetailsForUser.class);
                        startActivity(intent,
                                ActivityOptions
                                        .makeSceneTransitionAnimation(CrimeAreaForUser.this).toBundle());
                        break;
                    }
                }
                return false;
            }
        });
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

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back) {
            getWindow().setSharedElementExitTransition(new Explode());
            Intent intent = new Intent(CrimeAreaForUser.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        else if(id == R.id.refresh)setCrimeArea();
        return super.onOptionsItemSelected(item);
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
        Intent intent = new Intent(CrimeAreaForUser.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());
    }

}
