package com.example.sojal.staysafe;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Notifications extends AppCompatActivity {
    public static List<DataforCardView3> data = new ArrayList<>();
    public  static SQLiteDatabase db;
    public static Context context;
    public  static RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_notifications);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Notifications");
        context = Notifications.this;
        data.clear();
        db = openOrCreateDatabase("notificationDB",Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications1DB(title VARCHAR,date1" +
                " VARCHAR, body VARCHAR);");
        setList();
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



    private void setList()
    {
        Cursor c = db.rawQuery("SELECT * FROM notifications1DB", null);
        try {
            c.moveToFirst();
            do {
                DataforCardView3 dataforCardView3 = new DataforCardView3();
                String s1 = c.getString(0);
                String s2 = c.getString(1);
                String s3 = c.getString(2);
              //  System.out.println(s2);
                String[] parts = s2.split(" ");
                String date = "Date: " + parts[0];
                String time = "Time: " + time12(parts[1]);
                dataforCardView3.setTime(time);
                dataforCardView3.setDate(date);
                dataforCardView3.setBody(s3);
                dataforCardView3.setTitle(s1);
                data.add(dataforCardView3);
            } while (c.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        List<DataforCardView3> data1 = new ArrayList<>();
        int size = data.size();
        for(int i = size - 1; i >= 0; i--){
            data1.add(data.get(i));
        }
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view3);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Recycler_View_Adapter3 adapter = new Recycler_View_Adapter3(data1, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notifications.this));

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
            Intent intent = new Intent(Notifications.this, MainActivity.class);
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

    @Override
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(Notifications.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());

    }

}
