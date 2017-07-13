package com.example.sojal.staysafe;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sojal on 19-Jun-17.
 */

public class View_Holder3 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView time,date,body,title;
        SQLiteDatabase db;
    Context ctx;
        View_Holder3(final View itemView) {
        super(itemView);
            ctx = Notifications.context;
        cv = (CardView) itemView.findViewById(R.id.cardView3);
        time = (TextView) itemView.findViewById(R.id.time);
        body = (TextView) itemView.findViewById(R.id.body);
        date = (TextView) itemView.findViewById(R.id.date);
        title = (TextView)itemView.findViewById(R.id.title);


        itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                final int position = getAdapterPosition();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                builder1.setMessage("Are you sure you want to delete this message?");
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        removeItem(position);
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
                }
            });
        }

    private void removeItem(int position)
    {
        int position1 = Notifications.data.size() - position - 1;
        String time = convertTo24Hour(Notifications.data.get(position1).getTime().substring(6));
        String date = Notifications.data.get(position1).getDate().substring(6) + " " + time ;
       // System.out.println(date);
        Notifications.db.execSQL("DELETE FROM notifications1DB WHERE date1 LIKE '" + date + "%'");
        Notifications.data.remove(position1);
        List<DataforCardView3> data1 = new ArrayList<>();
        int size = Notifications.data.size();
        for(int i = size - 1; i >= 0; i--){
            data1.add(Notifications.data.get(i));
        }
       // Notifications notifications = new Notifications();
        //RecyclerView recyclerView = Notifications.recyclerView;
        //recyclerView.setAdapter(null);
        //recyclerView.clearAnimation();
       // recyclerView.removeAllViews();
        //recyclerView.remove
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        Recycler_View_Adapter3 adapter = new Recycler_View_Adapter3(data1,ctx);
       // adapter.remove(data1.get(position1));
        Notifications.recyclerView.setAdapter(adapter);
        Notifications.recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
    }

    public static String convertTo24Hour(String Time) {
        DateFormat f1 = new SimpleDateFormat("hh:mm a"); //11:00 pm
        Date d = null;
        try {
            d = f1.parse(Time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DateFormat f2 = new SimpleDateFormat("HH:mm");
        String x = f2.format(d); // "23:00"

        return x;
    }

}
