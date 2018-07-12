package com.example.ayesha.fyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ayesha on 12/2/2017.
 */

public class ChangePassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static EditText _password;
    private static EditText _confirm_password;
    private static EditText new_password;
    private static Button update_btn ;
    private static TextView msg;
    private final String[] original_pwd = new String[1];

    private ProgressDialog mProgressDialog;

    private static final String TAG = "ChangePasswordActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Init();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
        Firebase node = RootURL.child("Trainees").child(user.getUid()).child("password");

        node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                original_pwd[0] = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        UpdatePassword();

        mProgressDialog = new ProgressDialog(ChangePassword.this);
    }


    public void UpdatePassword() {

        update_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (validate()) {

                            mProgressDialog.setMessage("Updating..");
                            mProgressDialog.show();

                            new CheckNetworkConnection(ChangePassword.this, new CheckNetworkConnection.OnConnectionCallback() {

                                @Override
                                public void onConnectionSuccess() {

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.updatePassword(new_password.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User password updated.");
                                                    }
                                                }
                                            });
                                    //Change in database
                                    Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
                                    RootURL.child("Trainees").child(user.getUid()).child("password").setValue(new_password.getText().toString());
                                    mProgressDialog.dismiss();
                                    Toast.makeText(ChangePassword.this, "Password updated!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onConnectionFail(String msg) {

                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.warning_offline, Toast.LENGTH_SHORT).show();
                                }
                            }).execute();




                            return;
                        }
                        else{
                            msg.setText("Incorrect Password!");
                            Toast.makeText(ChangePassword.this, "Password could not be updated", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                }

        );

    }

    private boolean validate() {
        boolean valid = true;


        String pwd = _password.getText().toString();
        String new_pwd = new_password.getText().toString();
        String confirm_pwd = _confirm_password.getText().toString();


        if (!(pwd.equals(original_pwd[0]))) {
            _password.setText("");
            _password.setError("Please enter your password!");
            valid = false;
        } else {
            _password.setError(null);
        }

        if (new_pwd.isEmpty() || new_pwd.length() < 6 || atleastOneCapitalLetter(new_pwd) == false || atleastOneDigit(new_pwd) == false) {
            new_password.setError("At least 6 charcters, one capital letter and one digit");
            valid = false;
        } else {
            new_password.setError(null);
        }

        if (new_pwd.equals(pwd)) {
            new_password.setError("Enter a new password");
            valid = false;
        } else {
            new_password.setError(null);
        }

        if (confirm_pwd.isEmpty() || confirm_pwd.length() < 4 || confirm_pwd.length() > 10 || !(confirm_pwd.equals(new_pwd))) {
            _confirm_password.setError("Password Do not match");
            valid = false;
        } else {
            _confirm_password.setError(null);
        }


        return valid;
    }

    private boolean atleastOneDigit(String temp) {
        boolean found=false;
        for(int i=0;i<temp.length(); i++ )
        {
            if(temp.charAt(i) >= '0' && temp.charAt(i) <='9' )
            {
                found = true;
                break;
            }
        }
        return  found;
    }

    private  boolean atleastOneCapitalLetter(String temp){

        boolean found=false;
        for(int i=0;i<temp.length(); i++ )
        {
            if(temp.charAt(i) >= 'A' && temp.charAt(i) <='Z' )
            {
                found = true;
                break;
            }
        }
        return  found;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chg_pwd_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.btn_logout:{
                FirebaseAuth.getInstance().signOut();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }

                Intent i = new Intent(ChangePassword.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Init() {
        _password = (EditText) findViewById(R.id.password);
        new_password = (EditText) findViewById(R.id.new_password);
        _confirm_password = (EditText) findViewById(R.id.confirm_password);
        update_btn = (Button) findViewById(R.id.update_password);
        msg = (TextView) findViewById(R.id.Message);
    }
}


