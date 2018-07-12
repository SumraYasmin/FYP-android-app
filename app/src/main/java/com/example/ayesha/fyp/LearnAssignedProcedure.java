package com.example.ayesha.fyp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//import com.MyCompany.CPR.UnityPlayerActivity;

/**
 * Created by Ayesha on 12/3/2017.
 */

public class LearnAssignedProcedure  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_assigned_procedure);
        Button unityButton = (Button) findViewById(R.id.button1);

        unityButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //startActivity(new Intent(LearnAssignedProcedure.this,UnityPlayerActivity.class));
            }
        });


    }
}

