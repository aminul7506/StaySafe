package com.example.sojal.staysafe;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class CrimeReporting extends AppCompatActivity {
    GraphView graph;
    private Firebase mDatabase;
    String [] type = {"acid throwing","blackmail","bomb threat","death threat","domestic violence","fraud","killing",
            "obscene phone call","reckless burning","robbery","theft","threat","others"};
    ArrayList<Integer> typeArray = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_crime_reporting);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Crime Statistics");
        int len = type.length;
        for(int i = 0; i < len; i++){
            typeArray.add(0);
        }
        mDatabase = new Firebase(FirebaseUrl.reportedUrl);
        graph = (GraphView) findViewById(R.id.graph);

        if (MainActivity.loggedIn.equals("Log In")) {
            //Toast.makeText(MapsActivity.this, "You have to log in first to get nearby friends.",
            //      Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(CrimeReporting.this);
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

    private void getStatistics()
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
                        String title = crimeClass.getTitle();
                        if (title.equals("acid throwing")) typeArray.add(0, typeArray.get(0) + 1);
                        else if (title.equals("others"))
                            typeArray.add(12, typeArray.get(12) + 1);
                        else if (title.equals("blackmail")) typeArray.add(1, typeArray.get(1) + 1);
                        else if (title.equals("bomb threat"))
                            typeArray.add(2, typeArray.get(2) + 1);
                        else if (title.equals("death threat"))
                            typeArray.add(3, typeArray.get(3) + 1);
                        else if (title.equals("domestic violence"))
                            typeArray.add(4, typeArray.get(4) + 1);
                        else if (title.equals("fraud")) typeArray.add(5, typeArray.get(5) + 1);
                        else if (title.equals("killing")) typeArray.add(6, typeArray.get(6) + 1);
                        else if (title.equals("obscene phone call"))
                            typeArray.add(7, typeArray.get(7) + 1);
                        else if (title.equals("reckless burning"))
                            typeArray.add(8, typeArray.get(8) + 1);
                        else if (title.equals("robbery")) typeArray.add(9, typeArray.get(9) + 1);
                        else if (title.equals("theft")) typeArray.add(10, typeArray.get(10) + 1);
                        else if (title.equals("others")) typeArray.add(11, typeArray.get(11) + 1);
                    }
                    //else if(title.equals("acid others"))typeArray.add(13,typeArray.get(13) + 1);
                }
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 0),
                        new DataPoint(1, typeArray.get(0)),
                        new DataPoint(2, typeArray.get(1)),
                        new DataPoint(3, typeArray.get(2)),
                        new DataPoint(4, typeArray.get(3)),
                        new DataPoint(5, typeArray.get(4)),
                        new DataPoint(6, typeArray.get(5)),
                        new DataPoint(7, typeArray.get(6)),
                        new DataPoint(8, typeArray.get(7)),
                        new DataPoint(9, typeArray.get(8)),
                        new DataPoint(10, typeArray.get(9)),
                        new DataPoint(11, typeArray.get(10)),
                        new DataPoint(12, typeArray.get(11)),
                        new DataPoint(13, typeArray.get(12))
                      //  new DataPoint(14, typeArray.get(13))
                });



                //graph.getViewport().setYAxisBoundsManual(true);
                //graph.getViewport().setMinY(0);
                //graph.getViewport().setMaxY(150);

                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(16);

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
        Intent intent = new Intent(CrimeReporting.this, MainActivity.class);
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
            Intent intent = new Intent(CrimeReporting.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        return super.onOptionsItemSelected(item);
    }

}
