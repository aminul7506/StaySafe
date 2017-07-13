package com.example.sojal.staysafe;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class CrimeReportingHour extends AppCompatActivity {
    GraphView graph;
    private Firebase mDatabase;
    private int hourOfaDay = 24;
    ArrayList<Integer> hourArray = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_crime_reporting_hour);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Crime Statistics");
        mDatabase = new Firebase(FirebaseUrl.reportedUrl);
        graph = (GraphView) findViewById(R.id.graph);
        for(int i = 0; i < hourOfaDay; i++){
            hourArray.add(0);
        }
        if (MainActivity.loggedIn.equals("Log In")) {
            //Toast.makeText(MapsActivity.this, "You have to log in first to get nearby friends.",
            //      Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(CrimeReportingHour.this);
            builder1.setMessage("You have to log in first to show statistics.");
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
        else getStatistics();
    }

    private void  getStatistics()
    {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    String key = postSnapshot.getKey();
                    CrimeClass crimeClass = postSnapshot.getValue(CrimeClass.class);
                    String status = crimeClass.getStatus();
                    if(status.equals("Verified") | status.equals("CrimeArea")) {
                        String time = crimeClass.getTime();
                        String hour = time.substring(0,2);
                        String format = time.substring(6,7);
                        Integer t = Integer.parseInt(hour);
                        if(format.equals("p")) t += 12;
                        if(t == 1) hourArray.add(0,hourArray.get(0) + 1);
                        else if(t == 2) hourArray.add(1,hourArray.get(1) + 1);
                        else if(t == 3) hourArray.add(2,hourArray.get(2) + 1);
                        else if(t == 4) hourArray.add(3,hourArray.get(3) + 1);
                        else if(t == 5) hourArray.add(4,hourArray.get(4) + 1);
                        else if(t == 6) hourArray.add(5,hourArray.get(5) + 1);
                        else if(t == 7) hourArray.add(6,hourArray.get(6) + 1);
                        else if(t == 8) hourArray.add(7,hourArray.get(7) + 1);
                        else if(t == 9) hourArray.add(8,hourArray.get(8) + 1);
                        else if(t == 10) hourArray.add(9,hourArray.get(9) + 1);
                        else if(t == 11) hourArray.add(10,hourArray.get(10) + 1);
                        else if(t == 12) hourArray.add(11,hourArray.get(11) + 1);
                        else if(t == 13) hourArray.add(12,hourArray.get(12) + 1);
                        else if(t == 14) hourArray.add(13,hourArray.get(13) + 1);
                        else if(t == 15) hourArray.add(14,hourArray.get(14) + 1);
                        else if(t == 16) hourArray.add(15,hourArray.get(15) + 1);
                        else if(t == 17) hourArray.add(16,hourArray.get(16) + 1);
                        else if(t == 18) hourArray.add(17,hourArray.get(17) + 1);
                        else if(t == 19) hourArray.add(18,hourArray.get(18) + 1);
                        else if(t == 20) hourArray.add(19,hourArray.get(19) + 1);
                        else if(t == 21) hourArray.add(20,hourArray.get(20) + 1);
                        else if(t == 22) hourArray.add(21,hourArray.get(21) + 1);
                        else if(t == 23) hourArray.add(22,hourArray.get(22) + 1);
                        else if(t == 24) hourArray.add(23,hourArray.get(23) + 1);
                    }
                    //else if(title.equals("acid others"))typeArray.add(13,typeArray.get(13) + 1);
                }
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 0),
                        new DataPoint(1, hourArray.get(0)),
                        new DataPoint(2, hourArray.get(1)),
                        new DataPoint(3, hourArray.get(2)),
                        new DataPoint(4, hourArray.get(3)),
                        new DataPoint(5, hourArray.get(4)),
                        new DataPoint(6, hourArray.get(5)),
                        new DataPoint(7, hourArray.get(6)),
                        new DataPoint(8, hourArray.get(7)),
                        new DataPoint(9, hourArray.get(8)),
                        new DataPoint(10, hourArray.get(9)),
                        new DataPoint(11, hourArray.get(10)),
                        new DataPoint(12, hourArray.get(11)),
                        new DataPoint(13, hourArray.get(12)),
                        new DataPoint(14, hourArray.get(13)),
                        new DataPoint(15, hourArray.get(14)),
                        new DataPoint(16, hourArray.get(15)),
                        new DataPoint(17, hourArray.get(16)),
                        new DataPoint(18, hourArray.get(17)),
                        new DataPoint(19, hourArray.get(18)),
                        new DataPoint(20, hourArray.get(19)),
                        new DataPoint(21, hourArray.get(20)),
                        new DataPoint(22, hourArray.get(21)),
                        new DataPoint(23, hourArray.get(22)),
                        new DataPoint(24, hourArray.get(23))

                });



                //graph.getViewport().setYAxisBoundsManual(true);
                //graph.getViewport().setMinY(0);
                //graph.getViewport().setMaxY(150);

                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(25);

                // graph.getViewport().setScrollable(true); // enables horizontal scrolling
                //graph.getViewport().setScrollableY(true); // enables vertical scrolling
                //graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
                //graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
                //StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                // staticLabelsFormatter.setHorizontalLabels(new String[] {"acid throwing","blackmail","bomb threat","death threat",
                //       "domestic violence","fraud","killing",
                //     "obscene phone call","reckless burning","robbery","theft","threat","others"});
                //staticLabelsFormatter.setHorizontalLabels(new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13"
                //,"14","15"});
                //graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                graph.addSeries(series);
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
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(CrimeReportingHour.this, MainActivity.class);
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
            Intent intent = new Intent(CrimeReportingHour.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        return super.onOptionsItemSelected(item);
    }


}
