package com.example.sojal.staysafe;

import android.*;
import android.Manifest;
import java.util.*;
import java.text.*;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import android.view.Window;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.util.Base64.CRLF;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    private int mapReadyFlag = 0;
    protected GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    ContactList contactList;
    double mLatitude,mLongitude;
    private FirebaseAuth mAuth;
    private Firebase mDatabase;
    private int checker = 0;
    private int min_time = 2880;
    private int min_Distance_in_meter = 2000;
    ArrayList<PhoneLocation> phoneNo;
    Marker marker;
    List<Marker> markerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        setContentView(R.layout.activity_maps);
        setupWindowAnimations();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setTitle("Nearby Friends");
        //toolbar.getContentInsetLeft();
        //((AppCompatActivity) MapsActivity.this()).getSupportActionBar().setTitle("Nearby Friends");
        contactList = new ContactList();
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = new Firebase(FirebaseUrl.signInURL);
        phoneNo = new ArrayList<>();
        // getContactList();
        //  getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getNumber(this.getContentResolver());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerList = new ArrayList<Marker>();
        if (MainActivity.loggedIn.equals("Log In")) {
            //Toast.makeText(MapsActivity.this, "You have to log in first to get nearby friends.",
            //      Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MapsActivity.this);
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
           // if(mMap != null)onMapChanged();
            setFriends();
        }



    }

    private void setFriends(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!MainActivity.loggedIn.equals("Log In")) {
                    phoneNo.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (mLatitude != 0 && mLongitude != 0) {
                            SignInClass person = postSnapshot.getValue(SignInClass.class);
                            double latitude = person.getLatitude();
                            double longitude = person.getLongitude();
                            double distance = distFrom(mLatitude, mLongitude, latitude, longitude);
                            if (distance <= min_Distance_in_meter) {
                                PhoneLocation phoneLocation = new PhoneLocation();
                                phoneLocation.setLatitude(latitude);
                                phoneLocation.setLongitude(longitude);
                                if (person.getPhone().startsWith("+"))
                                    person.setPhone(person.getPhone().substring(3));
                                phoneLocation.setPhoneNo(person.getPhone());
                                phoneLocation.setLastTime(person.getCurrentTime());
                                phoneLocation.setDistance(distance);
                                phoneNo.add(phoneLocation);
                                //int siz = contactList.getContactLists().size();
                         /*   for(int k = 0; k < siz; k++){
                                if(contactList.getContactLists().get(k).getContactNumber() == person.getPhone()){
                                    LatLng latLng = new LatLng(latitude,longitude);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                    mMap.addMarker(new MarkerOptions().position(latLng).title(contactList.getContactLists().get(k).getContactNumber()));
                                }
                            }*/
                                //LatLng latLng = new LatLng(latitude,longitude);
                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                //mMap.addMarker(new MarkerOptions().position(latLng).title("Friends here"));
                            }
                        }
                    }

                    int siz = contactList.getContactLists().size();
                    int siz1 = phoneNo.size();
                    mMap.clear();  // wait wait .. here you are not allowed to remain happy .
                    LatLng latLng = new LatLng(mLatitude,mLongitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!")).showInfoWindow();
                    latLng = new LatLng(mLatitude, mLongitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!")).showInfoWindow();
                    //Log.d("Server Size : " + siz1,"  contactlistsize : " + siz + "\n");
                    for (int i = 0; i < siz; i++) {
                        for (int j = 0; j < siz1; j++) {
                            //Log.d("contact :" + contactList.getContactLists().get(i).getContactNumber()," server " +
                            //phoneNo.get(j).getPhoneNo() + "\n");
                            String contactNumber = contactList.getContactLists().get(i).getContactNumber();
                            if (contactNumber.startsWith("+"))
                                contactNumber = contactNumber.substring(3);
                            if (contactNumber.equals(phoneNo.get(j).getPhoneNo())) {
                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                Date date1 = new Date();
                                dateFormat.format(date1);
                                String dateString2 = phoneNo.get(j).getLastTime();
                                Date date2 = new Date();
                                try {
                                    date2 = dateFormat.parse(dateString2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long time_diff = getTimeDiff(date2, date1, TimeUnit.MINUTES);
                                if (time_diff < 0) time_diff = -time_diff;
                                Log.d("contact :" + contactNumber, " server " +
                                        phoneNo.get(j).getPhoneNo() + "Time diff : " + time_diff + "\n");
                                if (time_diff <= min_time) {
                                    String t = minuteToTime(time_diff);
                                    String dis = meterToKm(phoneNo.get(j).getDistance());
                                    latLng = new LatLng(phoneNo.get(j).getLatitude(), phoneNo.get(j).getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                    mMap.addMarker(new MarkerOptions().position(latLng).title(contactList.getContactLists().get(i).contactName +
                                            " here").snippet(t));
                                    break;
                                }
                            }
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

    private double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist;
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


    private  long getTimeDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public  String minuteToTime(long minute) {
        long hour = minute/60;
        long min = minute%60;
        if(hour >= 2 && min > 1)return hour + " hours " + min + " minutes ago";
        else if(hour == 1 && min >= 2) return hour + " hour " + min + " minutes ago";
        else if(hour == 1 && min == 0) return hour + " hour ago";
        else if(hour >= 2 && min == 0) return hour + " hours ago";
        else if(hour == 0 && min == 0) return "Currently here";
        else if(hour == 0 && min == 1) return min + " minute ago";
        return min + " minutes ago";
    }

    public  String meterToKm(double distance) {
        double km = distance/1000;
        double m = distance%1000;
        if(km >= 2 && km > 1)return (int)km + " kilometers " + (int)m + " meters away";
        else if(km == 1 && m >= 2) return (int)km + " kilometer " + (int)m + " meters away";
        else if(km == 1 && m == 0) return (int)km + " kilometer away";
        else if(km >= 2 && m == 0) return (int)km + " kilometers away";
        else if(km == 0 && m == 0) return "Currently here";
        else if(km == 0 && m == 1) return (int)km + " meters away";
        return (int)m + " meters away";
    }

    public void getNumber(ContentResolver cr)
    {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        // use the cursor to access the contacts
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            // get display name
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            // get phone number
            Contact contact = new Contact();
            contact.setContactName(name);
            contact.setContactNumber(phoneNumber);
            contactList.addToContactList(contact);
        }

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
        else if(checker == 1)setFriends();
        LatLng latLng = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!")).showInfoWindow();
        if(checker == 0)checker = 1;
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
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap1 = googleMap;
        // Add a marker in Sydney and move the camera
        //int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        //if (permissionCheck == PackageManager.PERMISSION_GRANTED) mMap.setMyLocationEnabled(true);
   //     LatLng sydney = new LatLng(-34, 151);
     //   mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

         //   mMap1.setMyLocationEnabled(true);
        }
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

    private void onMapChanged()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            //   mMap1.setMyLocationEnabled(true);
        }
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
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        else if(id == R.id.refresh)setFriends();
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
}
