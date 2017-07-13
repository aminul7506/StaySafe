package com.example.sojal.staysafe;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sojal on 14-Jun-17.
 */

public class PlacesDisplayTask2 extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    private double latitude,longitude;
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
        if (googleMap != null) {
            googleMap.clear();
            LatLng latLng = new LatLng(ConnectWithNearbyHospital.mLatitude, ConnectWithNearbyHospital.mLongitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here")).showInfoWindow();
            MarkerOptions markerOptions = new MarkerOptions();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    // googleMap.addMarker(new MarkerOptions().position(latLng).title("Yhere")).showInfoWindow();
                    HashMap<String, String> googlePlace = list.get(i);
                    double lat = Double.parseDouble(googlePlace.get("lat"));
                    double lng = Double.parseDouble(googlePlace.get("lng"));
                    String placeName = googlePlace.get("place_name");
             //       if (placeName.contains("hospital") || placeName.contains("Hospital") ||
               //             placeName.contains("Hospitals") || placeName.contains("Hospitals")) {
                      //  double distance = distFrom(lat, lng, ConnectWithNearbyHospital.mLatitude, ConnectWithNearbyHospital.mLongitude);
                        latitude = lat;
                        longitude = lng;
                        String vicinity = googlePlace.get("vicinity");
                        LatLng latLng1 = new LatLng(latitude, longitude);
                        markerOptions.position(latLng1);
                        markerOptions.title(placeName + " : " + vicinity);
                        googleMap.addMarker(markerOptions).showInfoWindow();
                        //googleMap.addMarker(markerOptions).showInfoWindow();
                        //  Toast.makeText(ConnectWithNearbyThana.context, "Report has been posted successfully.",
                        //        Toast.LENGTH_LONG).show();
                 //   }
                }
            }
        }
    }
}
