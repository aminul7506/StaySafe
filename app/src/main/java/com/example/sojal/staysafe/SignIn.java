package com.example.sojal.staysafe;

import android.app.ActionBar;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.RunLoop;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    EditText firstName,surName,dateOfBirth,email,phone;
    EditText password,confirmPassword;
    RadioGroup sex;
    RadioButton female,male;
    Button createAccount;
    private FirebaseAuth mAuth;
    private Firebase mDatabase;
    private ProgressDialog mProg;
    private int verified = -1,verified2 = 0;
    private int check = 0,onceLoaded = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_sign_in);
        setupWindowAnimations();
        setTitle("Sign In");
        mProg = new ProgressDialog(this);
        MainActivity.pref = getSharedPreferences("key", MODE_PRIVATE);
        firstName = (EditText) findViewById(R.id.firstName);
        surName = (EditText)findViewById(R.id.surName);
        dateOfBirth = (EditText)findViewById(R.id.dateOfBirth);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        female = (RadioButton) findViewById(R.id.female);
        male = (RadioButton)findViewById(R.id.male);
        createAccount = (Button)findViewById(R.id.createAccount);
        sex = (RadioGroup)findViewById(R.id.sex);
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = new Firebase(FirebaseUrl.signInURL);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.p1 || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });
        confirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.p2 || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        String year = inDate.substring(inDate.length()-4);
        int yr = Integer.parseInt(year);
        if(yr >= 2017) return false;
        return true;
    }
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(2000);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        fade.setDuration(2000);
        getWindow().setReturnTransition(slide);
    }

    public void startSignIn() {
        verified = -1;
        verified2 = 0;
        check = 0;
        final String firstName1 = firstName.getText().toString();
        final String surName1 = surName.getText().toString();
        final String email1 = email.getText().toString();
        final String phone1 = phone.getText().toString();
        final String dateOfBirth1 = dateOfBirth.getText().toString();
        final String password1 = password.getText().toString();
        final String confirmPassword1 = confirmPassword.getText().toString();
        int selectedId = sex.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String gender = "";
        if (selectedId != -1) {
            gender = radioButton.getText().toString();
        }
        final String gender1 = gender;
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String imei = tm.getDeviceId();
        if (TextUtils.isEmpty(firstName1)) {
            firstName.setError("First name cannot be empty!");
            check = 1;
        }
        if (TextUtils.isEmpty(surName1)) {
            surName.setError("Surname cannot be empty!");
            check = 1;
        }
        if (TextUtils.isEmpty(email1)) {
            email.setError("Email cannot be empty!");
            check = 1;
        }
        if (TextUtils.isEmpty(phone1)) {
            phone.setError("Phone number cannot be empty!");
            check = 1;
        }
        if (TextUtils.isEmpty(dateOfBirth1)) {
            dateOfBirth.setError("Date of birth cannot be empty!");
            check = 1;
        }
        if (TextUtils.isEmpty(password1)) {
            password.setError("Password cannot be empty!");
            check = 1;
        }
        if (!TextUtils.isEmpty(password1) && password1.length() < 8) {
            password.setError("Password must be at least 8 characters long.");
            check = 1;
        }
        if (TextUtils.isEmpty(confirmPassword1) && !TextUtils.isEmpty(password1)) {
            confirmPassword.setError("Confirm your password!");
            check = 1;
        }
        if (!TextUtils.isEmpty(password1) && !TextUtils.isEmpty(confirmPassword1) && !password1.equals(confirmPassword1)) {
            confirmPassword.setError("Password does not match!");
            check = 1;
        }
        if (gender1 == "") {
            male.setError("Select your gender.");
            check = 1;
        }
        if (!TextUtils.isEmpty(dateOfBirth1) && !isValidDate(dateOfBirth1)) {
            dateOfBirth.setError("Enter a valid date.");
            check = 1;
        }
        if (!TextUtils.isEmpty(email1) && !isValidEmailAddress(email1)) {
            email.setError("Enter a valid email.");
            check = 1;
        }
       /* if (!TextUtils.isEmpty(firstName1) && !TextUtils.isEmpty(surName1) && !TextUtils.isEmpty(phone1)
                && !TextUtils.isEmpty(email1) && !TextUtils.isEmpty(dateOfBirth1) && !TextUtils.isEmpty(gender1) &&
                !TextUtils.isEmpty(imei) && !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(confirmPassword1) &&
                isValidDate(dateOfBirth1) && isValidEmailAddress(email1) && password1.length() >= 8
                && password1.equals(confirmPassword1)) {*/
        if (check == 0) {
            //lock.lock();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    verified = 1;
                    verified2 = 1;
                    if(onceLoaded == 0) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //Getting the data from snapshot
                            String key = postSnapshot.getKey();
                            if (key.endsWith(imei)) {
                                verified = 0;
                                verified2 = 1;
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(SignIn.this);
                                builder1.setMessage("You have created an account from this device before!");
                                builder1.setNeutralButton(
                                        "Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                                break;
                            } else {
                                SignInClass person = postSnapshot.getValue(SignInClass.class);
                                String emailt = person.getEmail();
                                String phont = person.getPhone();
                                if (emailt.equals(email1)) {
                                    verified = 0;
                                    verified2 = 1;
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SignIn.this);
                                    builder1.setMessage("There is already an account using the email id you provided!");
                                    builder1.setNeutralButton(
                                            "Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                    verified = 0;
                                    break;
                                } else if (phont.equals(phone1)) {
                                    verified = 0;
                                    verified2 = 1;
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SignIn.this);
                                    builder1.setMessage("There is already an account using the phone number you provided!");
                                    builder1.setNeutralButton(
                                            "Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                    verified = 0;
                                    break;
                                }
                            }
                        }
                    }
                    if(onceLoaded == 0)onceLoaded = 1;
                    if (verified == 1 && check == 0) {
         /*   if (!TextUtils.isEmpty(firstName1) && !TextUtils.isEmpty(surName1) && !TextUtils.isEmpty(phone1)
                    && !TextUtils.isEmpty(email1) && !TextUtils.isEmpty(dateOfBirth1) && !TextUtils.isEmpty(gender1) &&
                    !TextUtils.isEmpty(imei) && !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(confirmPassword1) &&
                    isValidDate(dateOfBirth1) && isValidEmailAddress(email1) && password1.length() >= 8
                    && password1.equals(confirmPassword1)) {*/
                        mAuth.createUserWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    verified2 = 1;
                                    FirebaseUser user = task.getResult().getUser();
                                    String uid = user.getUid();
                                    mProg.setMessage("Signing In .... ");
                                    mProg.show();
                                    int i = 0, j = 0;
                                    while (i > 1000000) {
                                        if (j % 5 == 0) i++;
                                    }
                                    String userId = mDatabase.push().getKey() + imei;
                                    SignInClass person = new SignInClass();
                                    person.setFirstName(firstName1);
                                    person.setSurName(surName1);
                                    person.setPhone(phone1);
                                    person.setEmail(email1);
                                    person.setPassword(password1);
                                    person.setImei(imei);
                                    person.setDateOfBirth(dateOfBirth1);
                                    person.setGender(gender1);
                                    person.setUid(uid);
                                    person.setCurrentTime();
                                    person.setLatitude(MainActivity.mLatitude);
                                    person.setLongitude(MainActivity.mLongitude);
                                    mDatabase.child(userId).setValue(person);
                                    MainActivity.editor = MainActivity.pref.edit();
                                    MainActivity.editor.putString("keyValue",userId);
                                    MainActivity.editor.commit();
                                    MainActivity.editor = MainActivity.pref.edit();
                                    MainActivity.editor.putString("email",email1);
                                    MainActivity.editor.commit();
                                    Toast.makeText(SignIn.this, "Sign In Successful!",
                                            Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                                    startActivity(intent,
                                            ActivityOptions.makeSceneTransitionAnimation(SignIn.this).toBundle());

                                }
                            }
                        });
                    }
                    //lock.unlock();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }
    //}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(SignIn.this, MainActivity.class);
        startActivity(intent,
                ActivityOptions
                        .makeSceneTransitionAnimation(this).toBundle());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back) {
            getWindow().setSharedElementExitTransition(new Explode());
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent,
                    ActivityOptions
                            .makeSceneTransitionAnimation(this).toBundle());
        }
        return super.onOptionsItemSelected(item);
    }
}
