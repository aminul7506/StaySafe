package com.example.sojal.staysafe;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReportVerificationActivity extends AppCompatActivity {
    EditText name,gender,mobile,age,title,date,time,location,description;
    Button verified,delete,fraud,crimeArea;
    private Firebase mDatabase1;
    private Firebase mDatabase2;
    String imei,post_title,post_location;
    Firebase taskRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_report_verification);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Report Details");
        name = (EditText)findViewById(R.id.name);
        gender = (EditText)findViewById(R.id.gender);
        mobile = (EditText)findViewById(R.id.mobile);
        age = (EditText)findViewById(R.id.age);
        title = (EditText)findViewById(R.id.title);
        date = (EditText)findViewById(R.id.date);
        time = (EditText)findViewById(R.id.time);
        location = (EditText)findViewById(R.id.location);
        description = (EditText)findViewById(R.id.description);
        verified = (Button)findViewById(R.id.verified);
        delete = (Button)findViewById(R.id.delete);
        fraud = (Button)findViewById(R.id.fraud);
        crimeArea = (Button) findViewById(R.id.crimeArea);
        Firebase.setAndroidContext(this);
        mDatabase2 = new Firebase(FirebaseUrl.signInURL);
        mDatabase1 = new Firebase(FirebaseUrl.reportedUrl);
        imei = View_holder2.reportedImei;
        post_title = View_holder2.post_title;
        post_location = View_holder2.post_location;
        setDetailsOfUser();
        setDetailsOfReport();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecord();
            }
        });

        fraud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fraudRecord();
            }
        });

        verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyRecord();
            }
        });

        crimeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crimeArea();
            }
        });
    }

    private void setDetailsOfUser()
    {
        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if(key.endsWith(imei)){
                        SignInClass signInClass = postSnapshot.getValue(SignInClass.class);
                        name.setText("Name : " + signInClass.getFirstName() + " " + signInClass.getSurName());
                        gender.setText("Gender : " + signInClass.getGender());
                        mobile.setText("Mobile : " + signInClass.getPhone());
                        age.setText("Age : " + getAge(signInClass.getDateOfBirth()));
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void setDetailsOfReport()
    {
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if(key.endsWith(imei)){
                        CrimeClass crimeClass= postSnapshot.getValue(CrimeClass.class);
                        if(post_title.equals(crimeClass.getTitle()) && post_location.equals(crimeClass.getLocation())) {
                            title.setText("Title : " + crimeClass.getTitle());
                            description.setText("Problem Description :\n" + crimeClass.getPost_description());
                            date.setText("Date : " + crimeClass.getDate());
                            time.setText("Time : " + crimeClass.getTime());
                            location.setText("Location : " + crimeClass.getLocation());
                            break;
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

    private void deleteRecord()
    {
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (final DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if(key.endsWith(imei)){
                        CrimeClass crimeClass= postSnapshot.getValue(CrimeClass.class);
                        if(post_title.equals(crimeClass.getTitle()) && post_location.equals(crimeClass.getLocation())) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ReportVerificationActivity.this);
                            builder1.setMessage("Are you sure you want to delete this report ?");
                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            postSnapshot.getRef().removeValue();
                                            Toast.makeText(ReportVerificationActivity.this, "Report has been deleted!",
                                                    Toast.LENGTH_LONG).show();
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
                            break;
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

    private void verifyRecord()
    {
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if(key.endsWith(imei)){
                        CrimeClass crimeClass= postSnapshot.getValue(CrimeClass.class);
                        if(post_title.equals(crimeClass.getTitle()) && post_location.equals(crimeClass.getLocation())) {
                            taskRef = mDatabase1.child(key);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ReportVerificationActivity.this);
                            builder1.setMessage("Are you sure you want to mark this report as verified ?");
                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Firebase status1 = taskRef.child("status");
                                            status1.setValue("Verified");
                                            Toast.makeText(ReportVerificationActivity.this, "Report verified!",
                                                    Toast.LENGTH_LONG).show();
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
                            break;
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

    private void fraudRecord()
    {
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if(key.endsWith(imei)){
                        CrimeClass crimeClass= postSnapshot.getValue(CrimeClass.class);
                        if(post_title.equals(crimeClass.getTitle()) && post_location.equals(crimeClass.getLocation())) {
                            taskRef = mDatabase1.child(key);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ReportVerificationActivity.this);
                            builder1.setMessage("Are you sure you want to mark this report as fraud ?");
                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Firebase status1 = taskRef.child("status");
                                            status1.setValue("Fraud");
                                            Toast.makeText(ReportVerificationActivity.this, "Report marked as fraud!",
                                                    Toast.LENGTH_LONG).show();
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
                            break;
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


    private void crimeArea()
    {
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if(key.endsWith(imei)){
                        CrimeClass crimeClass= postSnapshot.getValue(CrimeClass.class);
                        if(post_title.equals(crimeClass.getTitle()) && post_location.equals(crimeClass.getLocation())) {
                            taskRef = mDatabase1.child(key);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ReportVerificationActivity.this);
                            builder1.setMessage("Are you sure you want to mark this area as a crime area ?");
                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Firebase status1 = taskRef.child("status");
                                            status1.setValue("CrimeArea");
                                            Toast.makeText(ReportVerificationActivity.this, "This area is marked as crime area!",
                                                    Toast.LENGTH_LONG).show();
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
                            break;
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

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(2000);
        getWindow().setEnterTransition(fade);
        Slide slide = new Slide();
        fade.setDuration(2000);
        getWindow().setReturnTransition(slide);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back) {
            getWindow().setSharedElementExitTransition(new Explode());
            Intent intent = new Intent(ReportVerificationActivity.this, ReportedActivityforPolice.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(ReportVerificationActivity.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());

    }

    private int getAge(String yourDate)
    {
        Calendar currentDate = Calendar.getInstance();

        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");

        Date birthdate = null;
        try {
            birthdate = myFormat.parse(yourDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long time= currentDate.getTime().getTime() / 1000 - birthdate.getTime() / 1000;
        int years = Math.round(time) / 31536000;
        return years;
    }
}
