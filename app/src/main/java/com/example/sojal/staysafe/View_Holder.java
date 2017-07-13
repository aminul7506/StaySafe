package com.example.sojal.staysafe;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;


/**
 * Created by Sojal on 30-Apr-17.
 */

public class View_Holder extends RecyclerView.ViewHolder {

    CardView cv;
    TextView name,location,time,distance;
    Button message,phone,emergency;

    View_Holder(final View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        name = (TextView) itemView.findViewById(R.id.name);
        location = (TextView) itemView.findViewById(R.id.location);
        time = (TextView)itemView.findViewById(R.id.time);
        distance = (TextView) itemView.findViewById(R.id.distance) ;
        message = (Button) itemView.findViewById(R.id.message);
        phone = (Button) itemView.findViewById(R.id.phone);
        emergency = (Button) itemView.findViewById(R.id.emergency);
        phone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + ConnectWithNearbyFriend.data.get(position).getPhone()));
                int permissionCheck = ContextCompat.checkSelfPermission(itemView.getContext(), android.Manifest.permission.CALL_PHONE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    itemView.getContext().startActivity(callIntent);
                }
            }
        });
        message.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                int permissionCheck = ContextCompat.checkSelfPermission(itemView.getContext(), Manifest.permission.SEND_SMS);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED && !ConnectWithNearbyFriend.myPosition.equals(null)) {
                    String sms = "Hello " + ConnectWithNearbyFriend.data.get(position).getName() + ",\n"
                            + ConnectWithNearbyFriend.myPosition + "\n " + ConnectWithNearbyFriend.data.get(position).getDistance()
                            + ".";
                  //  SmsManager smsManager = SmsManager.getDefault();
                    //smsManager.sendTextMessage(ConnectWithNearbyFriend.data.get(position).getPhone(), null,sms, null, null);
                    //Toast.makeText(itemView.getContext().getApplicationContext(), "SMS Sent Successfully.",
                      //      Toast.LENGTH_LONG).show();
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:" + ConnectWithNearbyFriend.data.get(position).getPhone()));
                    sendIntent.putExtra("sms_body", sms);
                    itemView.getContext().startActivity(sendIntent);
                }
                else {
               //     Toast.makeText(itemView.getContext().getApplicationContext(),
                 //           "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        emergency.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                int permissionCheck = ContextCompat.checkSelfPermission(itemView.getContext(), Manifest.permission.SEND_SMS);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED && !ConnectWithNearbyFriend.myPosition.equals(null)) {
                    String sms = "Hello " + ConnectWithNearbyFriend.data.get(position).getName() + ",\n"
                            + ConnectWithNearbyFriend.myPosition + "\n " + ConnectWithNearbyFriend.data.get(position).getDistance()
                            + "." + "I'm in danger now. Please try to help me.";
                    //  SmsManager smsManager = SmsManager.getDefault();
                    //smsManager.sendTextMessage(ConnectWithNearbyFriend.data.get(position).getPhone(), null,sms, null, null);
                    //Toast.makeText(itemView.getContext().getApplicationContext(), "SMS Sent Successfully.",
                    //      Toast.LENGTH_LONG).show();
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:" + ConnectWithNearbyFriend.data.get(position).getPhone()));
                    sendIntent.putExtra("sms_body", sms);
                    itemView.getContext().startActivity(sendIntent);
                }
                else {
                    //     Toast.makeText(itemView.getContext().getApplicationContext(),
                    //           "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}