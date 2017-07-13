package com.example.sojal.staysafe;

import android.*;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.Address;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ConnectWithNearbyFriend extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private Firebase mDatabase;
    private int checker = 0;
    private int min_time = 2880;
    private int min_Distance_in_meter = 2000;
    ContactList contactList;
    public static List<DataforCardview> data = new ArrayList<>();
    private ArrayList<PhoneLocation> phoneNo;
    double mLatitude,mLongitude;
    public static String myPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_connect_with_nearby_friend);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Connect With Friends");
        contactList = new ContactList();
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = new Firebase(FirebaseUrl.signInURL);
        phoneNo = new ArrayList<>();
        // getContactList();
        //  getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getNumber(this.getContentResolver());
       // List<DataforCardview> data = fill_with_data();
        if (MainActivity.loggedIn.equals("Log In")) {
            //Toast.makeText(MapsActivity.this, "You have to log in first to get nearby friends.",
            //      Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ConnectWithNearbyFriend.this);
            builder1.setMessage("You have to log in first to connect with nearby friends.");
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
            try {
                setFriends();
            } catch (IOException e) {
                e.printStackTrace();
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
            Intent intent = new Intent(ConnectWithNearbyFriend.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        else if(id == R.id.refresh){
            try {
                setFriends();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(ConnectWithNearbyFriend.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());

    }

    private void setFriends() throws IOException {
        data.clear();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        final String provider;
        Location location;
        provider = locationManager.getBestProvider(criteria, true);
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(provider);
            Geocoder geocoder = new Geocoder(ConnectWithNearbyFriend.this, Locale.getDefault());
            if (location != null) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                List<Address> addresses;
                String city = "Not Available!", state = "Not Available!", zip = "Not Available!", country = "Not available!",
                        knownName = "Not Available!", address = "Not Available!";
                addresses = geocoder.getFromLocation(mLatitude,mLongitude, 1);
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
                if (knownName == null) knownName = "Not Available!";
                if (address == null) address = "Not Available!";
                myPosition = "I am currently " + address + "," + knownName + "," + city + " here ";
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
                                    }
                                }
                            }

                            int siz = contactList.getContactLists().size();
                            int siz1 = phoneNo.size();
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
                                        if (time_diff <= min_time) {
                                            String t = minuteToTime(time_diff);
                                            String dis = meterToKm(phoneNo.get(j).getDistance());
                                            boolean index = isAlreadyAvailble(data,contactList.getContactLists().get(i).contactName);
                                            if(index == false) {
                                                Geocoder geocoder = new Geocoder(ConnectWithNearbyFriend.this, Locale.getDefault());
                                                String city = "Not Available!", state = "Not Available!", zip = "Not Available!", country = "Not available!",
                                                        knownName = "Not Available!", address = "Not Available!";
                                                List<Address> addresses;
                                                try {
                                                    addresses = geocoder.getFromLocation(phoneNo.get(j).getLatitude(), phoneNo.get(j).getLongitude(), 1);
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
                                                    String location = "Location : " + address + "," + knownName + "," + city;
                                                    DataforCardview dataforCardview = new DataforCardview();
                                                    dataforCardview.setName(contactList.getContactLists().get(i).contactName);
                                                    dataforCardview.setLocation(location);
                                                    String dis1 = meterToKm(phoneNo.get(j).getDistance());
                                                    dataforCardview.setDistance("Distance : " + dis1);
                                                    dataforCardview.setTime("Time : " + t);
                                                    dataforCardview.setPhone(phoneNo.get(j).getPhoneNo());
                                                    data.add(dataforCardview);
                                                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                                    Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getApplication());
                                                    recyclerView.setAdapter(adapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(ConnectWithNearbyFriend.this));
                                                    break;
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
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
            else Toast.makeText(ConnectWithNearbyFriend.this, "GPS is not available! Please ensure GPS and Internet connection.",Toast.LENGTH_LONG).show();
        }
        else ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
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

    private boolean isAlreadyAvailble(List<DataforCardview> list,String name)
    {
        int siz = list.size();
        for(int i = 0; i < siz; i++){
            if(list.get(i).getName().equals(name))return true;
        }
        return false;
    }

    private  long getTimeDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public  String minuteToTime(long minute) {
        long hour = minute/60;
        long min = minute%60;
        if(hour >= 2 && min > 1)return hour + " hours " + min + " minutes ago here";
        else if(hour == 1 && min >= 2) return hour + " hour " + min + " minutes ago here";
        else if(hour == 1 && min == 0) return hour + " hour ago here";
        else if(hour >= 2 && min == 0) return hour + " hours ago here";
        else if(hour == 0 && min == 0) return "Currently here";
        else if(hour == 0 && min == 1) return min + " minute ago here";
        return min + " minutes ago here";
    }

    public  String meterToKm(double distance) {
        double km = distance/1000;
        double m = distance%1000;
        if(km >= 2 && km > 1)return (int)km + " kilometers " + (int)m + " meters away from you";
        else if(km == 1 && m >= 2) return (int)km + " kilometer " + (int)m + " meters away from you";
        else if(km == 1 && m == 0) return (int)km + " kilometer away from you";
        else if(km >= 2 && m == 0) return (int)km + " kilometers away from you";
        else if(km == 0 && m == 0) return "Currently here";
        else if(km == 0 && m == 1) return (int)km + " meters away from you";
        return (int)m + " meters away from you";
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

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                       // mMap.setMyLocationEnabled(true);
                    }
                }
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

}
