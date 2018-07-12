package com.example.ayesha.fyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ayesha on 3/30/2018.
 */

public class UpdateProcedure extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static EditText procedureName;
    private static EditText procedureLevel;
    private static Button saveChanges_btn;

    private ProgressDialog mProgressDialog;

    private static final String TAG = "UpdateProcedureActivity";

    private static String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_procedure);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Init();

        Intent i = getIntent();
        id = i.getStringExtra("ID");


        mProgressDialog = new ProgressDialog(UpdateProcedure.this);

        SaveChanges();
    }


    boolean only_Numbers(String name) {
        int count = 0;
        boolean only = false;

        for(int i=0; i < name.length(); i++)
        {
            if(isNumber(name.charAt(i)))
                count++;
        }

        if(count == name.length())
            only = true;

        return  only;
    }

    boolean validName(String name) { //Special Characters other than numbers and letters
        boolean valid = true;

        for(int i=0; i < name.length(); i++)
        {
            if(!isNumber(name.charAt(i)) && !isLetter(name.charAt(i) )) {

                if(name.charAt(i) != ' ') {
                    valid = false;
                    return valid;
                }
            }
        }
        return  valid;
    }
    boolean isLetter(char c) {
        boolean bool = false;

        if((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
            bool = true;
        return  bool;
    }
    boolean isNumber(char c) {
        boolean bool = false;
        if(c >= '0' && c <= '9')
            bool = true;
        return  bool;
    }

    private boolean validate() {
        boolean valid = true;

        String _name = procedureName.getText().toString();
        int _level = Integer.valueOf(procedureLevel.getText().toString());


        if (_name.isEmpty() || _name.length() < 3 ||only_Numbers(_name)) {
            procedureName.setError("Enter valid Procedure Name");
            valid = false;
        } else {
            procedureName.setError(null);
        }

        if (procedureLevel.getText().toString().isEmpty()  || _level > 10 ||_level < 0) {
            procedureLevel.setError("Please Enter a valid level(>0)");
            valid = false;
        } else {
            procedureLevel.setError(null);
        }


        return valid;
    }

    public void SaveChanges() {

        saveChanges_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validate()) {

                            mProgressDialog.setMessage("Updating..");
                            mProgressDialog.show();

                            new CheckNetworkConnection(UpdateProcedure.this, new CheckNetworkConnection.OnConnectionCallback() {

                                @Override
                                public void onConnectionSuccess() {

                                    Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
                                    String n = procedureName.getText().toString();
                                    int l = Integer.valueOf(procedureLevel.getText().toString());
                                    Procedure procedure = new Procedure(id,n,l);
                                    RootURL.child("Procedures").child(id).setValue(procedure);

                                    mProgressDialog.dismiss();

                                    Toast.makeText(UpdateProcedure.this, "Changes saved", Toast.LENGTH_SHORT).show();
                                    finish();

                                    Intent viewProcedures = new Intent(UpdateProcedure.this, ViewProcedures.class);
                                    startActivity(viewProcedures);

                                }

                                @Override
                                public void onConnectionFail(String msg) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.warning_offline, Toast.LENGTH_SHORT).show();
                                }
                            }).execute();

                            //mProgressDialog.dismiss();

                            return;
                        }
                        else{
                            mProgressDialog.dismiss();
                            Toast.makeText(UpdateProcedure.this, "Could not save changes", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );

    }


    private void Init() {
        procedureName = (EditText) findViewById(R.id.proc_name);
        procedureLevel = (EditText) findViewById(R.id.proc_level);
        saveChanges_btn = (Button) findViewById(R.id.update_procedure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trainer_profile_menu, menu);
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

                Intent i = new Intent(UpdateProcedure.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent viewProcedures = new Intent(UpdateProcedure.this, ViewProcedures.class);
        startActivity(viewProcedures);
    }
}

