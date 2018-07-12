package com.example.ayesha.fyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Firebase RootURL;
    // UI references
    private static EditText email;
    private static EditText pass;
    private static Button mEmailSignInButton;
    private static TextView sign;
    private static TextView msg;

    final ArrayList<Trainee> users = new ArrayList<Trainee>();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mProgressDialog;
    private static String Name = null, userName = null;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RootURL = new Firebase(FirebaseConstants.RootURL);

        mAuth = FirebaseAuth.getInstance();
        StartFirebase();
        Init();
        mEmailSignInButton.setEnabled(true);
        LoginButton();
        SignupLink();

        mProgressDialog = new ProgressDialog(MainActivity.this);
    }


    private void StartFirebase() {

        /*
        //Add Trainer in the database
        mAuth.createUserWithEmailAndPassword("interactive.first.aid.training@gmail.com", "Interactive1122"
                .toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user != null) {
                            //UserClass tempUser = new UserClass(uname.getText().toString(), name.getText().toString(),pass.getText().toString(),email.getText().toString());
                            Firebase userNode = RootURL.child("Trainer");  // node created
                            //userNode.push().setValue(tempUser); // unique id

                            String userId = user.getUid();
                            Trainer tempUser = new Trainer(userId, "Interactive1122", "interactive.first.aid.training@gmail.com");
                            userNode.setValue(tempUser);

                            Toast.makeText(MainActivity.this, R.string.feedback_signup, Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(MainActivity.this, TrainerProfile.class);
                            finish();
                            startActivity(i);

                    } else {
                        Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });*/

        //Procedures
        /*Firebase Node = RootURL.child("Procedures");
        String Id = Node.push().getKey();
        Procedure proc1 = new Procedure(Id,"Shock Treatment", 1);
        Node.child(Id).setValue(proc1);
        Id = Node.push().getKey();
        Procedure proc2 = new Procedure(Id,"Cardio Pulmonary Resuscitation", 2);
        Node.child(Id).setValue(proc2);
        Id = Node.push().getKey();
        Procedure proc3 = new Procedure(Id,"Heimlich Maneuver", 3);
        Node.child(Id).setValue(proc3);
        Id = Node.push().getKey();
        Procedure proc4 = new Procedure(Id,"Recovery Position", 4);
        Node.child(Id).setValue(proc4);*/


        //Approve Procedures
        /*Firebase Node = RootURL.child("ProceduresToBeApproved");
        String Id = Node.push().getKey();
        Procedure proc1 = new Procedure(Id,"Drowning", 5);
        Node.child(Id).setValue(proc1);
        Id = Node.push().getKey();
        Procedure proc2 = new Procedure(Id,"Hyperventilation", 6);
        Node.child(Id).setValue(proc2);
        Id = Node.push().getKey();
        Procedure proc3 = new Procedure(Id,"How to put on a dressing", 7);
        Node.child(Id).setValue(proc3);
        Id = Node.push().getKey();
        Procedure proc4 = new Procedure(Id,"How to bandage a Hand", 8);
        Node.child(Id).setValue(proc4);*/
        //RootURL.child("Approved").setValue("No");

        RootURL.child("Trainees").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Trainee u = ds.getValue(Trainee.class);
                    users.add(new Trainee(u.getId(),u.getUsername(), u.getName(), u.getPassword(), u.getEmail(),u.getAssignedTasksList(),u.getLearntTasksList(),u.getPracticeTasksList(), u.getRecentTasksList()));
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void SignupLink() {
        sign.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, Register.class);
                        finish();
                        startActivity(i);
                    }
                }

        );
    }

    public void LoginButton() {
        mEmailSignInButton.setOnClickListener
                (new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                         msg.setText("");
                         login();
                     }
                 }
                );
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        } else {
            onLoginSuccess();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mEmailSignInButton.setEnabled(true);

        mProgressDialog.dismiss();
        msg.setText("Login failed!");
    }

    public void onLoginSuccess() {
        //Check Internet connection here
        new CheckNetworkConnection(this, new CheckNetworkConnection.OnConnectionCallback() {

            @Override
            public void onConnectionSuccess() {

                mEmailSignInButton.setEnabled(false);
                email.setError(null);
                pass.setError(null);

                final boolean[] flag = {false};
                //Toast.makeText(getApplicationContext(), "11111", Toast.LENGTH_SHORT).show();

                mProgressDialog.setMessage("Signing In..");
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);

                //if(checkAccountEmailExistInFirebase(email.getText().toString())) {
                mAuth.signInWithEmailAndPassword(email.getText().toString(),
                        pass.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {
                                mProgressDialog.dismiss();

                                if (!user.getEmail().equalsIgnoreCase("interactive.first.aid.training@gmail.com")) {

                                    for (int i = 0; i < users.size(); i++) {
                                        // Its Available...
                                        if (users.get(i).email.equals(email.getText().toString()) && users.get(i).password.equals(pass.getText().toString())) {
                                            flag[0] = true;
                                            //Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                                            userName = users.get(i).username;
                                            Name = users.get(i).name;
                                            if (Name != null) {
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(Name)
                                                        .build();

                                                user.updateProfile(profileUpdates);
                                            }


                                            Bundle bundle = new Bundle();
                                            bundle.putString("UserID", users.get(i).id);
                                            bundle.putString("Name", Name);

                                            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                            Intent SeeProfile = new Intent(MainActivity.this, Profile.class);
                                            //SeeProfile.putExtra("uname", userName);
                                            SeeProfile.putExtras(bundle);
                                            startActivity(SeeProfile);
                                            MainActivity.this.finish();
                                        }
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                    Intent SeeProfile = new Intent(MainActivity.this, TrainerProfile.class);
                                    startActivity(SeeProfile);
                                    finish();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                msg.setText("Authentication Failed!");
                            }
                        } else {
                            mProgressDialog.dismiss();
                            msg.setText("Username or password is Incorrect!");
                            mEmailSignInButton.setEnabled(true);
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

    private boolean checkAccountEmailExistInFirebase(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final boolean[] b = new boolean[1];
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                b[0] = !task.getResult().getProviders().isEmpty();
            }
        });
        return b[0];
    }

    public boolean validate() {
        boolean valid = true;

        String _email = email.getText().toString();
        String _password = pass.getText().toString();

        if (_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            email.setError("Enter a valid email address");
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

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);


        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getEmail().equals("interactive.first.aid.training@gmail.com")) {
                Intent SeeProfile = new Intent(MainActivity.this, TrainerProfile.class);
                startActivity(SeeProfile);
                finish();
            } else {
                Intent SeeProfile = new Intent(MainActivity.this, Profile.class);
                startActivity(SeeProfile);
                finish();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //if (mAuthListener != null) {
        //mAuth.removeAuthStateListener(mAuthListener);
        //}

       // FirebaseAuth.getInstance().signOut();

    }

    @Override
    public void onResume() {
        super.onResume();
        //mAuth.addAuthStateListener(mAuthListener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getEmail().equals("interactive.first.aid.training@gmail.com")) {
                Intent SeeProfile = new Intent(MainActivity.this, TrainerProfile.class);
                startActivity(SeeProfile);
                finish();
            } else {
                Intent SeeProfile = new Intent(MainActivity.this, Profile.class);
                startActivity(SeeProfile);
                finish();
            }
        }
    }

    private void Init() {

        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.login);
        sign = (TextView) findViewById(R.id.signup);
        msg = (TextView) findViewById(R.id.Message);
    }

    //Helping Functions

    public boolean atleastOneCapitalLetter(String temp) {

        boolean found = false;
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) >= 'A' && temp.charAt(i) <= 'Z') {
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean atleastOneDigit(String temp) {
        boolean found = false;
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) >= '0' && temp.charAt(i) <= '9') {
                found = true;
                break;
            }
        }
        return found;
    }

    //add a toast to show when successfully signed in

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
