package com.example.sojal.staysafe;

import android.*;
import android.Manifest;
import android.app.ActivityOptions;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Geocoder;
import android.location.Location;
import android.location.Address;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportedActivtyFromClient extends AppCompatActivity implements LocationListener,OnMapReadyCallback  {
    private FirebaseAuth mAuth;
    private Firebase mDatabase;
    EditText time,date,problem_description,location;
    AutoCompleteTextView title;
    Button postReport;
    GoogleMap mMap;
    public static Context context;
    private ProgressDialog mProg;
    NetworkInfo[] info;
    public static String police_station = "Not Available";
    public static double latitude = -1,longitude = -1;
    private static final String GOOGLE_API_KEY = "AIzaSyAZ20VbUM0NrMl_qrDj44Qv6PukmNS8wsk";
    private int PROXIMITY_RADIUS = 5000;

    public static String time2,title2,date2,post_description2,location2,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        MainActivity.pref = getSharedPreferences("key", MODE_PRIVATE);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_reported_activty_from_client);
        setupWindowAnimations();
        String [] type = {"acid throwing","blackmail","bomb threat","death threat","domestic violence","fraud","killing",
                "obscene phone call","reckless burning","robbery","theft","threat","others"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, type);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        mProg = new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        time = (EditText)findViewById(R.id.time);
        date = (EditText)findViewById(R.id.date);
        problem_description = (EditText)findViewById(R.id.problem_description);
        location = (EditText)findViewById(R.id.location);
        title = (AutoCompleteTextView) findViewById(R.id.title);
        postReport = (Button) findViewById(R.id.postReport);
        //Set the adapter
        title.setAdapter(adapter);
        title.setThreshold(0);
        setTitle("Report a Crime");
        if (MainActivity.loggedIn.equals("Log In")) {
            //Toast.makeText(MapsActivity.this, "You have to log in first to get nearby friends.",
            //      Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ReportedActivtyFromClient.this);
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
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = new Firebase(FirebaseUrl.reportedUrl);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence constraint = title.getText();
                adapter.getFilter().filter(constraint);
                title.showDropDown();
            }
        });
        postReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createReport();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void createReport() throws InterruptedException, ExecutionException {
        if (!MainActivity.loggedIn.equals("Log In")) {
            police_station = "Not Available";
            latitude = -1;
            longitude = -1;
            int ind = 0,ind1 = 0;
            int check = 0;
            String title1 = title.getText().toString();
            String time1 = time.getText().toString();
            String date1 = date.getText().toString();
            String problem_description1 = problem_description.getText().toString();
            String location1 = location.getText().toString();
            ConnectivityManager check1 = (ConnectivityManager)
                    ReportedActivtyFromClient.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = check1.getAllNetworkInfo();
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    ind = 1;
                }
            }
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))ind1 = 1;

            if (TextUtils.isEmpty(title1)) {
                title.setError("Title cannot be empty.");
                check = 1;
            }
            if (TextUtils.isEmpty(time1)) {
                time.setError("Time cannot be empty.");
                check = 1;
            }
            if (TextUtils.isEmpty(date1)) {
                date.setError("Date cannot be empty.");
                check = 1;
            }
            if (TextUtils.isEmpty(problem_description1)) {
                problem_description.setError("Problem Description cannot be empty.");
                check = 1;
            }
            if (TextUtils.isEmpty(location1)) {
                location.setError("Location cannot be empty.");
                check = 1;
            }
            if (!isValidDate(date1) && !TextUtils.isEmpty(date1)) {
                date.setError("Enter a valid date.");
                check = 1;
            }
            if (!isValidTime(time1) && !TextUtils.isEmpty(time1)) {
                time.setError("Enter a valid time.");
                check = 1;
            }
            if(ind == 0){
                Toast.makeText(ReportedActivtyFromClient.this,"Please enable internet connection.",Toast.LENGTH_LONG).show();
                check = 1;
            }
            if(ind1 == 0){
                Toast.makeText(ReportedActivtyFromClient.this, "Please enable gps connection in your devide", Toast.LENGTH_LONG).show();
                check = 1;
            }
            if (check == 0) {
                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(ReportedActivtyFromClient.this);
                try {
                    addressList = geocoder.getFromLocationName(location1, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList.size() > 0) {
                    police_station = "Not Available";
                    latitude = -1;
                    longitude = -1;
                    Address address = addressList.get(0);
                    if (address.getCountryName().equals("Bangladesh")) {


                        title2 = title.getText().toString();
                        time2 = time.getText().toString();
                        date2 = date.getText().toString();
                        post_description2 = problem_description.getText().toString();
                        location2 = location.getText().toString();
                        status = "Not verified";

                        mMap.clear();
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Here")).showInfoWindow();


                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                        googlePlacesUrl.append("location=" + address.getLatitude() + "," + address.getLongitude());
                        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                        googlePlacesUrl.append("&types=" + "police");
                        googlePlacesUrl.append("&sensor=true");
                        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

                        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                        Object[] toPass = new Object[2];
                        toPass[0] = mMap;
                        toPass[1] = googlePlacesUrl.toString();
                       // googlePlacesReadTask.execute(toPass);
                        googlePlacesReadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,toPass);
                      //  while (googlePlacesReadTask.getStatus() != AsyncTask.Status.FINISHED);
                  /*      CrimeClass cc = new CrimeClass();
                        cc.setTime(time1);
                        cc.setDate(date1);
                        cc.setLatitude(address.getLatitude());
                        cc.setLongitude(address.getLongitude());
                        cc.setLocation(address.getFeatureName() + "," + address.getLocality());
                        cc.setPost_description(problem_description1);
                        cc.setTitle(title1);
                        cc.setPolice_station(police_station);
                        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        final String imei = tm.getDeviceId();
                        String userId = mDatabase.push().getKey() + imei;
                        mDatabase.child(userId).setValue(cc);
                        Log.d("mo ",police_station);
                        mProg.setMessage("Posting Report .... ");
                        mProg.show();
                        Log.d("mo ",police_station);
                        int i = 0, j = 0;
                        while (i > 1000000) {
                            if (j % 5 == 0) i++;
                        }


                        mProg.dismiss();
                        Toast.makeText(ReportedActivtyFromClient.this, "Report has been posted successfully.",
                               Toast.LENGTH_LONG).show();*/
                       // while(PlacesDisplayTask.end != 1){Log.d("dhuru ", String.valueOf(PlacesDisplayTask.end));}
                     //   while(true)Log.d("ho",police_station);
                        //Intent intent = new Intent(ReportedActivtyFromClient.this, MainActivity.class);
                        //startActivity(intent,
                          //      ActivityOptions.makeSceneTransitionAnimation(ReportedActivtyFromClient.this).toBundle());
                    } else {
                        location.setError("Please enter a valid location from bangladesh.");
                    }
                } else location.setError("Please provide a valid location");
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
        Intent intent = new Intent(ReportedActivtyFromClient.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back) {
            getWindow().setSharedElementExitTransition(new Explode());
            Intent intent = new Intent(ReportedActivtyFromClient.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        return super.onOptionsItemSelected(item);
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

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        String year = inDate.substring(inDate.length()-4);
        int yr = Integer.parseInt(year);
        if(yr >= 2018) return false;
        return true;
    }

    boolean isValidTime(String time)
    {
         Pattern pattern;
         Matcher matcher;
         String TIME12HOURS_PATTERN = "(1[012]|[0]*[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)";
         pattern = Pattern.compile(TIME12HOURS_PATTERN);
         matcher = pattern.matcher(time);
         return matcher.matches();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
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
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
      //  mLatitude = latitude;
        //mLongitude = longitude;
        //mMap.clear();
       // LatLng latLng = new LatLng(latitude,longitude);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //mMap.addMarker(new MarkerOptions().position(latLng).title("You are here")).showInfoWindow();
    }



}

