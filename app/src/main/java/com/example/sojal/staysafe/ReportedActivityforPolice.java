package com.example.sojal.staysafe;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportedActivityforPolice extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Firebase mDatabase;
    private FirebaseAuth.AuthStateListener mListener;
    public static List<DataforCardview2> data = new ArrayList<>();
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_reported_activityfor_police);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Reported Crimes");
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = new Firebase(FirebaseUrl.reportedUrl);
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    email = firebaseAuth.getCurrentUser().getEmail();
                    if(email == null)email = MainActivity.userNameForPolice;
                }
               else email = "nothing";
            }
        };
        if (MainActivity.loggedIn.equals("Log In")) {
            //Toast.makeText(MapsActivity.this, "You have to log in first to get nearby friends.",
            //      Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ReportedActivityforPolice.this);
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
        else try {
            setReports();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setReports() throws IOException {
            data.clear();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        //Getting the data from snapshot
                        CrimeClass cc = postSnapshot.getValue(CrimeClass.class);
                        String police_station = cc.getPolice_station();
                        police_station = police_station.toLowerCase();
                        try {
                            if(police_station.contains(getPoliceStation())){
                                DataforCardview2 dataforCardview = new DataforCardview2();
                                dataforCardview.setProblem_name(cc.getTitle());
                                String date = cc.getCurrent_time();
                                String[] parts = date.split(" ");
                                String t = "Date: " + parts[0] + "\nTime: " + time12(parts[1]);
                                dataforCardview.setTime(t);
                                dataforCardview.setLocation(cc.getLocation());
                                dataforCardview.setStatus(cc.getStatus());
                                dataforCardview.setImei(cc.getImei());
                                data.add(dataforCardview);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view1);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    Recycler_View_Adapter2 adapter = new Recycler_View_Adapter2(data, getApplication());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ReportedActivityforPolice.this));
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
    }

    private String getPoliceStation() throws IOException {
        if(email == null)email = MainActivity.userNameForPolice;
        if(email.equals("nothing"))email = MainActivity.userNameForPolice;
        String email1 = email.substring(0,email.length() - 12);
        Geocoder geocoder = new Geocoder(ReportedActivityforPolice.this, Locale.getDefault());
        String knownName = "Not Available!";
        List<Address> addresses;
        addresses = geocoder.getFromLocationName(email1, 1);
        knownName = addresses.get(0).getFeatureName().toLowerCase();
//        Log.d("police : " ,addresses.get(0).getAddressLine(0).toLowerCase());
        return knownName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String time12(String _24HourTime)
    {
        String t = "";
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
            t = _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back) {
            getWindow().setSharedElementExitTransition(new Explode());
            Intent intent = new Intent(ReportedActivityforPolice.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        else if(id == R.id.refresh){
            try {
                setReports();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
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
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mListener);
    }

    @Override
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(ReportedActivityforPolice.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());

    }
}
