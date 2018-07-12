package com.example.ayesha.fyp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class TrainerProfile extends AppCompatActivity {

    private static TextView TextView_ViewFirstAidTechniques;
    private static TextView TextView_ViewTrainees;
    private static TextView TextView_ApproveNewTechniques;

    private static String s;

    private static final String TAG = "TrainerProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_action_menu);


        Init();

        ViewFirstAidTechniques();
        ViewTrainees();
        ApproveNewTechniques();
    }

    public void ViewFirstAidTechniques(){
        TextView_ViewFirstAidTechniques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TrainerProfile.this,ViewProcedures.class));
            }
        });
    }

    public void ViewTrainees(){
        TextView_ViewTrainees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TrainerProfile.this,ViewTrainees.class));
            }
        });
    }

    public void ApproveNewTechniques(){
        TextView_ApproveNewTechniques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(TrainerProfile.this,ApproveNewTechniques.class));
            }
        });
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

                Intent i = new Intent(TrainerProfile.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url

            if (user.getEmail() != null) {
                String email = user.getEmail();

                if (email.equals("")) {
                    //trainer profile nahi toh trainee profile
                }
            }
        }
        */
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    private void Init() {
        TextView_ViewFirstAidTechniques = (TextView) findViewById(R.id.view_first_aid_techniques);
        TextView_ViewTrainees = (TextView) findViewById(R.id.view_trainees);
        TextView_ApproveNewTechniques = (TextView) findViewById(R.id.approve_new_techniques);

    }



}
