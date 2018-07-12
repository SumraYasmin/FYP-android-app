package com.example.ayesha.fyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

/**
 * Created by Ayesha on 10/30/2017.
 */

public class Register extends AppCompatActivity {

    Firebase RootURL;

    private static EditText name;
    private static EditText uname;
    private static EditText email;
    private static EditText pass;
    private static EditText cpass;
    private static Button signup_btn;
    private static TextView Login_link;
    private static TextView msg;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mProgressDialog;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RootURL = new Firebase(FirebaseConstants.RootURL);

        Init();

        signup_btn.setEnabled(true);
        LoginLink();
        SignupButton();


        mProgressDialog = new ProgressDialog(Register.this);

        mAuth = FirebaseAuth.getInstance();

        /*

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //toastMessage("Successfully signed in with: " + user.getEmail());


                    //closeOptionsMenu();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name.getText().toString())
                            .setPhotoUri(Uri.parse("android.resource://com.example.tasirana.project/drawable/dp"))
                            .build();

                    user.updateProfile(profileUpdates);
                    //user.updateEmail(email.getText().toString());

                    mProgressDialog.dismiss();

                    //firebaseAuth.getInstance().signOut();


                    Toast.makeText(Register.this, R.string.feedback_signup, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Register.this, MainActivity.class);
                    i.putExtra("Name",name.getText().toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(i);

                    //Toast.makeText(Register.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    //Intent SeeProfile = new Intent(Register.this, Profile.class);
                    //startActivity(SeeProfile);
                    //finish();



                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //toastMessage("Successfully signed out.");
                }
                // ...
            }
        }; */

    }

    public void SignupButton() {
        signup_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signup();
                    }
                }

        );
    }

    public void LoginLink() {

        Login_link.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Register.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(i);
                    }
                }

        );
    }

    private void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        } else {
            onSignupSuccess();
        }
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        signup_btn.setEnabled(true);
    }

    private void onSignupSuccess() {

        new CheckNetworkConnection(this, new CheckNetworkConnection.OnConnectionCallback() {

            @Override
            public void onConnectionSuccess() {
                //Toast.makeText(getApplicationContext(), "onSuccess()", Toast.LENGTH_SHORT).show();
                signup_btn.setEnabled(false);
                setResult(RESULT_OK, null);


                mProgressDialog.setMessage("Creating Account...");
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText()
                        .toString()).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {
                                mProgressDialog.dismiss();

                                if (!user.getEmail().equalsIgnoreCase("interactive.first.aid.training@gmail.com")) {
                                    //UserClass tempUser = new UserClass(uname.getText().toString(), name.getText().toString(),pass.getText().toString(),email.getText().toString());
                                    Firebase Node = RootURL.child("Trainees");  // node created
                                    //Node.push().setValue(tempUser); // unique id

                                    //final int[] childrenCount = {0};
                                    //Node.addValueEventListener(new ValueEventListener() {
                                    //  @Override
                                    //public void onDataChange(DataSnapshot dataSnapshot) {
                                    //  childrenCount[0] = (int)dataSnapshot.getChildrenCount();
                                    //}
                                    //@Override
                                    //public void onCancelled(FirebaseError firebaseError) {}
                                    //});

                                    //int count = childrenCount[0] + 1 ;
                                    String userId = user.getUid();
                                    //Assign First Task
                                    ArrayList<AssignedTask> arr = new ArrayList<AssignedTask>();
                                    AssignedTask assignedTask = new AssignedTask("New Task!", "Learn: Shock Treatment");
                                    arr.add(assignedTask);
                                    //dummy
                                    //arr.add(new AssignedTask( "Assigned Again!","Learn: Shock Treatment"));
                                    //arr.add(new AssignedTask( "New Task!","Learn: Recovery Position"));

                                    //real thing
                                    ArrayList<String> arr2 = new ArrayList<String>();
                                    ArrayList<PracticedTask> arr3 = new ArrayList<PracticedTask>();
                                    ArrayList<RecentTask> arr4 = new ArrayList<RecentTask>();

                                    //add dummy learnt and practiced records
                                    //ArrayList<String> arr2 = new ArrayList<String>();
                                    //arr2.add("Learnt: Shock Treatment");
                                    //arr2.add("Learnt: Cardio Pulmonary Resuscitation");arr2.add("Learnt: Cardio Pulmonary Resuscitation");arr2.add("Learnt: Cardio Pulmonary Resuscitation");arr2.add("Learnt: Cardio Pulmonary Resuscitation");

                                    //Node.child(userId).child("LearntTasksList").setValue(arr2);

                                    //ArrayList<PracticedTask> arr3 = new ArrayList<PracticedTask>();
                                    //arr3.add(new PracticedTask("Shock Treatment", "8"));
                                    //arr3.add(new PracticedTask("Shock Treatment", "8"));
                                    //arr3.add(new PracticedTask("Shock Treatment", "8"));
                                    //arr3.add(new PracticedTask("Shock Treatment", "8"));
                                    //arr3.add(new PracticedTask("Cardio Pulmonary Resuscitation", "9"));
                                    //Node.child(userId).child("PracticedTasksList").setValue(arr3);
                                    //dummy thingys ends here

                                    Trainee tempUser = new Trainee(userId, uname.getText().toString(), name.getText().toString(), pass.getText().toString(), email.getText().toString(), arr, arr2, arr3, arr4);

                                    Node.child(userId).setValue(tempUser); //Assign the task to the user

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name.getText().toString())
                                            .setPhotoUri(Uri.parse("android.resource://com.example.ayesha.fyp/drawable/dp"))
                                            .build();

                                    user.updateProfile(profileUpdates);

                                    //Assign first Task

                                    //Node.child(userId).child("AssignedTasksList").setValue(arr);

                                    Toast.makeText(Register.this, R.string.feedback_signup, Toast.LENGTH_SHORT).show();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("UserID", userId);
                                    bundle.putString("Name", name.getText().toString());

                                    Intent i = new Intent(Register.this, Profile.class);
                                    i.putExtras(bundle);
                                    startActivity(i);
                                    finish();

                                } else {
                                    clear();
                                    msg.setText(R.string.trainer_email_error);
                                    Toast.makeText(Register.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    signup_btn.setEnabled(true);
                                }
                            } else {
                                Toast.makeText(Register.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }

                        } else {
                            mProgressDialog.dismiss();
                            signup_btn.setEnabled(true);
                            clear();
                            msg.setText("Authentication Failed!");

                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(TAG, "onComplete: malformed_email");
                                // TODO: Take your action
                                msg.setText("This email does not exists!");
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Log.d(TAG, "onComplete: exist_email");
                                msg.setText("An account with this email already exists!!");
                                // TODO: Take your action
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                                msg.setText("Authentication Failed!");
                            }
                        }
                    }
                });

            }

            @Override
            public void onConnectionFail(String msg) {
                Toast.makeText(getApplicationContext(), R.string.warning_offline, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

    private void clear() {
        name.setText("");
        uname.setText("");
        email.setText("");
        pass.setText("");
        cpass.setText("");
    }

    private boolean atleastOneCapitalLetter(String temp) {

        boolean found = false;
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) >= 'A' && temp.charAt(i) <= 'Z') {
                found = true;
                break;
            }
        }
        return found;
    }

    private boolean atleastOneDigit(String temp) {
        boolean found = false;
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) >= '0' && temp.charAt(i) <= '9') {
                found = true;
                break;
            }
        }
        return found;
    }

    private boolean BothNamesCheck(String _name) {
        boolean valid = false;
        int i = 0;
        for (i = 0; i < _name.length(); i++) {
            if (_name.charAt(i) == ' ') {
                if (i > 2) {
                    //first name is a valid name (length >3)
                    valid = true;
                }
            }
        }
        //at least length of first name and last name 4 plus 1 space
        // also 0 sy i start toh after loop i will be >=9
        if (i < 7) //valid if upr wali cnditions  plus i>=7
            valid = false;
        return valid;
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean validate() {
        boolean valid = true;

        String _name = name.getText().toString();
        String username = uname.getText().toString();
        String _email = email.getText().toString();
        String _password = pass.getText().toString();
        String reEnterPassword = cpass.getText().toString();

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

        if (_password.isEmpty() || _password.length() < 6 || atleastOneCapitalLetter(_password) == false || atleastOneDigit(_password) == false) {
            pass.setError("At least 6 charcters, one capital letter and one digit");
            valid = false;
        } else {
            pass.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(_password))) {
            cpass.setError("Password Do not match");
            valid = false;
        } else {
            cpass.setError(null);
        }

        return valid;
    }

    private void Init() {
        name = (EditText) findViewById(R.id.name);
        uname = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        cpass = (EditText) findViewById(R.id.confirmpass);
        signup_btn = (Button) findViewById(R.id.register);
        Login_link = (TextView) findViewById(R.id.login);
        msg = (TextView) findViewById(R.id.Message);
    }

    @Override
    public void onStart() {
        super.onStart();
        // mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //if (mAuthListener != null) {
        //   mAuth.removeAuthStateListener(mAuthListener);
        //}
    }

    @Override
    public void onResume() {
        super.onResume();
        //mAuth.addAuthStateListener(mAuthListener);
    }

}
