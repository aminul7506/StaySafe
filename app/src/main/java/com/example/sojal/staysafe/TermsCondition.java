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
import android.widget.TextView;

public class TermsCondition extends AppCompatActivity {
    TextView textView;
    String st;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_terms_condition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupWindowAnimations();
        setTitle("Terms & Conditions");
        setString();
        textView = (TextView)findViewById(R.id.textView);
        textView.setText(st);
    }

    private void setString(){
        st = "As a condition of use you promise not to do anything that is unlawful or any other purpose not reasonably " +
                "intended" +
                " to harm others.\n\n" +
                "You promise not to report a fake crime or a fake call to police station.\n\n" +
                "You confirm that all the information  given to us in creating account is true.\n\n" +
                "You give permission of getting your IMEI number of your phone for the security of your phone.\n\n" +
                "Your reported crimes can be visible to other users if verified by police without mentioning your information.\n\n" +
                "If any violation of rules is found against you , the authority can take proper steps to you.\n\n" +
                "We confirm you that your personal security won't be affected from your given personal information.\n\n" +
                "Thank you.";
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
            Intent intent = new Intent(TermsCondition.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());

        }
        return super.onOptionsItemSelected(item);
    }

}
