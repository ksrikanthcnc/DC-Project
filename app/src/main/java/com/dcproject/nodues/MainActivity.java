package com.dcproject.nodues;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.dcproject.nodues.MainActivity.TAG;

public class MainActivity extends Activity {

    FirebaseAuth firebaseAuth;
    ProgressBar loadstatus;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        loadstatus=(ProgressBar)findViewById(R.id.loading);
        loadstatus.setVisibility(View.VISIBLE);


        //if(ConnectivityReceiver.isConnected()) {
            if (firebaseAuth.getCurrentUser() != null) {
                Log.d(TAG, "Login Exist loging in");
                Loginexist();
            } else {
                Log.d(TAG, "Not loged in going to LoginActivity");
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        //}
        /*else{
            //showSnack();
        }
        */

    }

    public void Loginexist() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query DeptRef = db.child("Departments").orderByChild("email").equalTo(user.getEmail());
        Query StuRef = db.child("Students").orderByChild("email").equalTo(user.getEmail());


        StuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.d(TAG, "Student exist");
                    StuExist();
                } else {
                    Log.d(TAG, "Student does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "In onCancelled Student");
            }
        });
        DeptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.d(TAG, "Dept exist");
                    DeptExist();
                } else {
                    Log.d(TAG, "Faculty does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "In onCancelled Faculty");
            }
        });
    }


    public void StuExist() {
        Log.d(TAG, "In Student Exists");
        Log.d(TAG, "Start Student Intent");
        finish();
        Intent intent = new Intent(MainActivity.this, StudentActivity.class);
        startActivity(intent);

    }

    public void DeptExist() {
        Log.d(TAG, "Start Dept Exists");
        Log.d(TAG, "Start Dept Intent");
        finish();
        Intent intent = new Intent(MainActivity.this, DepartmentActivity.class);
        startActivity(intent);


    }

    /*private void showSnack() {
        String message;
        int color;
        message = "Not connected to internet";
        color = Color.RED;

        Snackbar snackbar = Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }*/
}