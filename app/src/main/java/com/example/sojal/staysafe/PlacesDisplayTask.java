package com.example.sojal.staysafe;

/**
 * Created by Sojal on 19-May-17.
 */


import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.example.sojal.staysafe.ReportedActivtyFromClient.context;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.date2;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.latitude;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.location2;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.longitude;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.police_station;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.post_description2;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.status;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.time2;
import static com.example.sojal.staysafe.ReportedActivtyFromClient.title2;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    private FirebaseAuth mAuth;
    private Firebase mDatabase;
    private ProgressDialog mProg;
    private double mLatitude,mLongitude;
    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        googleMap.clear();
        Firebase.setAndroidContext(context.getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = new Firebase(FirebaseUrl.reportedUrl);
        mProg = new ProgressDialog(context.getApplicationContext());
        double dist = 100000;
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = list.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);

            if(placeName.contains("Police Station") || placeName.contains("Police station") ||
                    placeName.contains("police station") || placeName.contains("thana") ||
                    placeName.contains("Thana")){
                    double distance =  distFrom(lat,lng,ReportedActivtyFromClient.latitude,ReportedActivtyFromClient.longitude);
                    if(distance < dist){
                        dist = distance;
                        police_station = placeName;
                        googleMap.addMarker(markerOptions);
                        mLatitude = lat;
                        mLongitude = lng;
                    }

             //   Toast.makeText(context.getApplicationContext(),placeName,Toast.LENGTH_LONG).show();
                //return ;
            }
        }

        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(context);

        try {
            addressList = geocoder.getFromLocationName(location2, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList.size() > 0) {
            // police_station = "";
            Address address = addressList.get(0);
            CrimeClass cc = new CrimeClass();
            cc.setTime(time2);
            cc.setDate(date2);
            cc.setLatitude(latitude);
            cc.setLongitude(longitude);
            cc.setLocation(address.getFeatureName() + "," + address.getLocality());
            cc.setPost_description(post_description2);
            cc.setTitle(title2);
            cc.setPolice_station(police_station);
            cc.setPolice_station_latitude(mLatitude);
            cc.setPolice_station_longitude(mLongitude);
            cc.setCurrent_time();
            cc.setStatus(status);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String imei = tm.getDeviceId();
            cc.setImei(imei);
            String userId = mDatabase.push().getKey() + imei;
            mDatabase.child(userId).setValue(cc);
        //Log.d("mo ",police_station);
      //  mProg.setMessage("Posting Report .... ");
       // mProg.show();
            int i = 0, j = 0;
            while (i > 1000000) {
                if (j % 5 == 0) i++;
            }

        //mProg.dismiss();
            Toast.makeText(context, "Report has been posted successfully.",
                        Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
            //startActivity(intent,
              //    ActivityOptions.makeSceneTransitionAnimation(context).toBundle());

        }

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
        if(dist < 0) dist = - dist;
        return dist;
    }
}