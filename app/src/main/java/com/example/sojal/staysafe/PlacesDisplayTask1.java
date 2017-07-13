package com.example.sojal.staysafe;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * Created by Sojal on 20-May-17.
 */

public class PlacesDisplayTask1 extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    private double latitude,longitude;
    public static String police_station,phone_no;
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
        if(googleMap != null) {
            googleMap.clear();
            LatLng latLng = new LatLng(ConnectWithNearbyThana.mLatitude, ConnectWithNearbyThana.mLongitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here")).showInfoWindow();
            double dist = 100000;
            MarkerOptions markerOptions = new MarkerOptions();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                   // googleMap.addMarker(new MarkerOptions().position(latLng).title("Yhere")).showInfoWindow();
                    HashMap<String, String> googlePlace = list.get(i);
                    double lat = Double.parseDouble(googlePlace.get("lat"));
                    double lng = Double.parseDouble(googlePlace.get("lng"));
                    String placeName = googlePlace.get("place_name");
                    if (placeName.contains("Police Station") || placeName.contains("Police station") ||
                            placeName.contains("police station") || placeName.contains("thana") ||
                            placeName.contains("Thana")) {
                        double distance = distFrom(lat, lng, ConnectWithNearbyThana.mLatitude, ConnectWithNearbyThana.mLongitude);
                        Log.d("thana", placeName + " " + distance);
                        if (distance < dist) {
                            dist = distance;
                            latitude = lat;
                            longitude = lng;
                            police_station = placeName;
                            String vicinity = googlePlace.get("vicinity");
                            LatLng latLng1 = new LatLng(latitude, longitude);
                            markerOptions.position(latLng1);
                            markerOptions.title(placeName + " : " + vicinity);
                            //googleMap.addMarker(markerOptions).showInfoWindow();
                            //  Toast.makeText(ConnectWithNearbyThana.context, "Report has been posted successfully.",
                            //        Toast.LENGTH_LONG).show();
                        }
                    }
                }

                googleMap.addMarker(markerOptions).showInfoWindow();

                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(ConnectWithNearbyThana.context, Locale.getDefault());
                try {
                    addressList = geocoder.getFromLocation(latitude,longitude,1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addressList.size() > 0) {
                    // police_station = "";
                    for(int i = 0; i < addressList.size();i++){
                        String phone;
                        phone = addressList.get(i).getPhone();
                        if(phone != null){
                            phone_no = phone;
                            break;
                        }
                    }
                    if(phone_no == null){
                        getPhoneNoofPoliceSTation(police_station);
                    }
                }
            } else {
                Toast.makeText(ConnectWithNearbyThana.context, "Location has noy set yet.Please try to refresh.",
                        Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(ConnectWithNearbyThana.context, "Location has noy set yet.Please try to refresh.",
                    Toast.LENGTH_LONG).show();
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

    void  getPhoneNoofPoliceSTation(String name) {
            if(name.contains("Chawk"))phone_no = "027313966";
            else if(name.contains("Ramna")) phone_no = "01713373125";
            else if(name.contains("Shahbagh")) phone_no = "01713373127";
            else if(name.contains("Dhanmondi")) phone_no = "01713373126";
            else if(name.contains("New Market")) phone_no = "01713373128";
            else if(name.contains("Lalbagh")) phone_no = "01713373134";
            else if(name.contains("Kotwali")) phone_no = "01713373135";
            else if(name.contains("Hajaribagh")) phone_no = "01713373136";
            else phone_no = "01682916111";
    }
}