package com.example.ayesha.fyp;

/**
 * Created by Ayesha on 12/2/2017.
 */
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
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static EditText name;
    private static EditText uname;
    private static EditText email;
    private static Button saveChanges_btn ;

    private ProgressDialog mProgressDialog;

    private static final String TAG = "EditProfileActivity";

    private String id, nname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Init();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String _name = user.getDisplayName();

            name.setText(_name);
            email.setText(user.getEmail());

            Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
            Firebase node = RootURL.child("Trainees").child(user.getUid()).child("username");
            final String[] s = new String[1];
            node.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.getValue(String.class);
                    //s[0] = username;
                    uname.setText(username);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }

            });

            nname = _name;
            id = user.getUid();
        }

        mProgressDialog = new ProgressDialog(EditProfile.this);

        SaveChanges();
    }


    private boolean validate() {
        boolean valid = true;

        String _name = name.getText().toString();
        String username = uname.getText().toString();
        String _email = email.getText().toString();

        //iska both name space ka check reh gya hai
        if (_name.isEmpty() || _name.length() < 3 || BothNamesCheck(_name) == false || atleastOneDigit(_name) == true) {
            //Name.setError("Enter both First Name and Last Name and each name must be at least 3 characters (no digits)");
            name.setError("Enter both First and Last Name!");
            valid = false;
        } else {
            name.setError(null);
        }

        if (username.isEmpty() || username.length() < 5) {
            uname.setError("At least 5 characters");
            valid = false;
        } else {
            uname.setError(null);
        }


        if (_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
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

                            new CheckNetworkConnection(EditProfile.this, new CheckNetworkConnection.OnConnectionCallback() {

                                @Override
                                public void onConnectionSuccess()  {


                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name.getText().toString())
                                            .build();
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User Name updated.");
                                                    }
                                                }
                                            });
                                    user.updateEmail(email.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User email address updated.");
                                                    }
                                                }
                                            });

                                    Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
                                    RootURL.child("Trainees").child(user.getUid()).child("name").setValue(name.getText().toString());
                                    //RootURL.child("Trainees").child(user.getUid()).child("email").setValue(email.getText().toString());
                                    RootURL.child("Trainees").child(user.getUid()).child("username").setValue(uname.getText().toString());



                                    Toast.makeText(EditProfile.this, "Changes saved", Toast.LENGTH_SHORT).show();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("UserID", id);
                                    bundle.putString("Name", nname);
                                    Intent SeeProfile = new Intent(EditProfile.this, Profile.class);
                                    SeeProfile.putExtras(bundle);

                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    mProgressDialog.dismiss();
                                    startActivity(SeeProfile);
                                    finish();
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
                            Toast.makeText(EditProfile.this, "Could not save changes", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );

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

    private boolean BothNamesCheck(String _name) {
        boolean valid = false;
        int i=0;
        for(i=0;i<_name.length(); i++ )
        {
            if(_name.charAt(i) == ' ')
            {
                if(i>2) {
                    //first name is a valid name (length >3)
                    valid = true;
                }
            }
        }
        //at least length of first name and last name 4 plus 1 space
        // also 0 sy i start toh after loop i will be >=9
        if(i<7) //valid if upr wali cnditions  plus i>=7
            valid = false;
        return valid;
    }

    private void Init() {
        name = (EditText) findViewById(R.id.name);
        uname = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        saveChanges_btn = (Button) findViewById(R.id.save);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.btn_change_password:{

                Intent i = new Intent(EditProfile.this, ChangePassword.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
            case R.id.btn_logout:{
                FirebaseAuth.getInstance().signOut();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }

                Intent i = new Intent(EditProfile.this, MainActivity.class);
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

        Bundle bundle = new Bundle();
        bundle.putString("UserID", id);
        bundle.putString("Name", nname);

        Intent SeeProfile = new Intent(EditProfile.this, Profile.class);
        SeeProfile.putExtras(bundle);
        startActivity(SeeProfile);
    }
}
