package com.example.sojal.staysafe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

import java.util.Collections;
import java.util.List;

/**
 * Created by Sojal on 04-Jun-17.
 */

public class Recycler_View_Adapter2 extends RecyclerView.Adapter<View_holder2> {

    List<DataforCardview2> list = Collections.emptyList();
    Context context;
    private int lastPosition = -1;
    private final static int FADE_DURATION = 1000;

    public Recycler_View_Adapter2(List<DataforCardview2> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_holder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview2, parent, false);
        View_holder2 holder = new View_holder2(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_holder2 holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.problem_name.setText(list.get(position).getProblem_name());
        holder.time.setText(list.get(position).getTime());
        holder.status.setText(list.get(position).getStatus());
        holder.location.setText(list.get(position).getLocation());
        setAnimation(holder.itemView, position);
        //animate(holder);
        setScaleAnimation(holder.itemView);

    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, DataforCardview2 data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(DataforCardview2 data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
