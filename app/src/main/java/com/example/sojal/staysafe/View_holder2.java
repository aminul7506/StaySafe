package com.example.sojal.staysafe;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Sojal on 03-Jun-17.
 */

public class View_holder2 extends RecyclerView.ViewHolder {
    CardView cv;
    public static String reportedImei,post_title,post_location;
    TextView problem_name,time,status,location;
    View_holder2(final View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView2);
        problem_name = (TextView) itemView.findViewById(R.id.problem_name);
        status = (TextView) itemView.findViewById(R.id.status);
        time = (TextView) itemView.findViewById(R.id.time);
        location = (TextView)itemView.findViewById(R.id.locationName);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int position = getAdapterPosition();
                reportedImei = ReportedActivityforPolice.data.get(position).getImei();
                post_title = ReportedActivityforPolice.data.get(position).getProblem_name();
                post_location = ReportedActivityforPolice.data.get(position).getLocation();
                Intent intent = new Intent(itemView.getContext(),ReportVerificationActivity.class);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
