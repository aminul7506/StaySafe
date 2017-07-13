package com.example.sojal.staysafe;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class ReportDetailsForUser extends AppCompatActivity {
    EditText title,date,time,location,description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        setContentView(R.layout.activity_report_details_for_user);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Crime Details");
        title = (EditText)findViewById(R.id.title);
        date = (EditText)findViewById(R.id.date);
        time = (EditText)findViewById(R.id.time);
        location = (EditText)findViewById(R.id.location);
        description = (EditText)findViewById(R.id.description);
        title.setText("Title : " + CrimeAreaForUser.markerClasses.get(CrimeAreaForUser.markerPosition).getTitle());
        time.setText("Time : " + CrimeAreaForUser.markerClasses.get(CrimeAreaForUser.markerPosition).getTime());
        location.setText("Location : " + CrimeAreaForUser.markerClasses.get(CrimeAreaForUser.markerPosition).getLocation());
        description.setText("problem Description : " + CrimeAreaForUser.markerClasses.get(CrimeAreaForUser.markerPosition).getDescription());
        date.setText("Date : " + CrimeAreaForUser.markerClasses.get(CrimeAreaForUser.markerPosition).getDate());
    }

    @Override
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(ReportDetailsForUser.this, CrimeAreaForUser.class);
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
            Intent intent = new Intent(ReportDetailsForUser.this, CrimeAreaForUser.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
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
