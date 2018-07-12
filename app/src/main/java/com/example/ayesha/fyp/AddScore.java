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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ayesha on 3/27/2018.
 */

public class AddScore extends AppCompatActivity {
    private static EditText ProcedureName;
    private static EditText Add_Score;
    private static Button save_btn;
    private static Button cancel_btn;
    private static TextView msg;

    private String ID;
    private ProgressDialog mProgressDialog;

    List<AssignedTask> assignedTasksList = new ArrayList<AssignedTask>();
    List<PracticedTask> practicedTasksList = new ArrayList<PracticedTask>();
    List<Procedure> proceduresList = new ArrayList<Procedure>();
    List<RecentTask>  recentTasksList = new ArrayList<RecentTask>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);

        Init();

        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("UserID");

        ID = id;
        mProgressDialog = new ProgressDialog(AddScore.this);

        getPracticedTasksList(id);
        getAssignedTasksList(id);

        getProcedures();
        getRecentTasksList(id);

        Save(id);
        Cancel(id);
    }

    private void SaveData(final String id) {

        final String procedureName = ProcedureName.getText().toString();
        final String score = Add_Score.getText().toString();

        if (Validate(procedureName, score))
        {
            new CheckNetworkConnection(this, new CheckNetworkConnection.OnConnectionCallback() {
                @Override
                public void onConnectionSuccess() {
                    setResult(RESULT_OK, null);

                    mProgressDialog.setMessage("Adding Score...");
                    mProgressDialog.show();

                    int index = getIndex(procedureName);
                    if (index > -1) { //aesa bhi toh ho skta hai k task assign nahi hui aur vo usy practice kr lai
                        PracticedTask practicedTask = new PracticedTask(procedureName, score);//Practice task object

                        Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
                        Firebase Node = RootURL.child("Trainees").child(id).child("practiceTasksList");
                        practicedTasksList.add(practicedTask);
                        Node.removeValue(); //add this to practice history
                        Node.setValue(practicedTasksList);

                        //Add to recently done tasks
                        RecentTask recentTask = new RecentTask("Practiced: "+procedureName, "Click to Practice Again!");
                        if(recentTasksList.size() >= 3) //we'll maintain its size to be always <= 3
                            recentTasksList.remove(recentTasksList.size()-1); //remove from end
                        recentTasksList.add(0,recentTask); //add always at start so that recent ones are at top
                        Firebase node11 = RootURL.child("Trainees").child(id).child("recentTasksList");
                        node11.removeValue();
                        node11.setValue(recentTasksList);

                        //if the score <8
                        //Assign this task again to the user else
                        //remove it from Asssigned tasks and assign new task on the basis of level of Intensity of fat
                        if (Integer.valueOf(score) >= 7) {
                            RootURL.child("Trainees").child(id).child("assignedTasksList").child(String.valueOf(index)).removeValue();

                            //Assign new Task on the basis of Level of intensity of techniques
                            Firebase node = RootURL.child("Trainees").child(id).child("assignedTasksList");
                            String nextProcedure = getNextTaskProcedureName(procedureName);
                            if(!nextProcedure.equals("")) {
                                AssignedTask assignedTask = new AssignedTask("New Task!", "Learn: " + nextProcedure);
                                assignedTasksList.remove(index); //remove previous task
                                assignedTasksList.add(assignedTask); //Add new task
                                node.removeValue(); //remove from db
                                node.setValue(assignedTasksList); //add to db

                                mProgressDialog.dismiss();
                                Toast.makeText(AddScore.this, "Score Added!", Toast.LENGTH_SHORT).show();

                                Bundle bundle = new Bundle();
                                bundle.putString("UserID", id);
                                Intent i = new Intent(AddScore.this, PreviewTrainee.class);
                                i.putExtras(bundle);
                                startActivity(i);
                                AddScore.this.finish();
                            }
                            else
                            {
                                mProgressDialog.dismiss();
                                msg.setText("Score added!\nNo New techniques to be assigned!\nThis trainee has learnt and practiced all the First Aid Techniques!");
                            }

                        } else {
                            //remove kr k
                            RootURL.child("Trainees").child(id).child("assignedTasksList").child(String.valueOf(index)).removeValue();
                            //assign again this task the same name with different status (to learn and practice again)

                            Firebase node = RootURL.child("Trainees").child(id).child("assignedTasksList");

                            assignedTasksList.remove(index);
                            AssignedTask learnAgain = new AssignedTask("Assigned Again!", "Learn: " + procedureName);
                            AssignedTask praciceAgain = new AssignedTask("Assigned Again!", "Practice: " + procedureName);
                            assignedTasksList.add(learnAgain);
                            assignedTasksList.add(praciceAgain);
                            node.removeValue();
                            node.setValue(assignedTasksList);

                            mProgressDialog.dismiss();
                            Toast.makeText(AddScore.this, "Score Added!", Toast.LENGTH_SHORT).show();

                            Bundle bundle = new Bundle();
                            bundle.putString("UserID", id);
                            Intent i = new Intent(AddScore.this, PreviewTrainee.class);
                            i.putExtras(bundle);
                            startActivity(i);
                            AddScore.this.finish();
                        }
                    } else { //aesa bhi toh ho skta hai k task assign nahi hui aur vo usy practice kr lai jese already practice ko practice
                        //is case main bss us k records main dobara aa jayega

                        //check valid procedure Name then add to its records else shw error
                        if(procedureName.equals("Shock Treatment") || procedureName.equals("Cardio Pulmonary Resuscitation") || procedureName.equals("Heimlich Maneuver") || procedureName.equals("Recovery Position"))
                        {
                            if(alreadyPracticed(procedureName)) {
                                PracticedTask practicedTask = new PracticedTask(procedureName, score);//Practice task object

                                Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
                                Firebase Node = RootURL.child("Trainees").child(id).child("practiceTasksList");
                                practicedTasksList.add(practicedTask);
                                Node.removeValue(); //add this to practice history
                                Node.setValue(practicedTasksList);
                                mProgressDialog.dismiss();

                                Toast.makeText(AddScore.this, "Score Added!", Toast.LENGTH_SHORT).show();

                                Bundle bundle = new Bundle();
                                bundle.putString("UserID", id);
                                Intent i = new Intent(AddScore.this, PreviewTrainee.class);
                                i.putExtras(bundle);
                                startActivity(i);
                                AddScore.this.finish();
                            }
                            else{
                                mProgressDialog.dismiss();
                                msg.setText("This Technique is neither assigned nor previously being practiced by the Trainee!");
                            }
                        }
                        else {
                            mProgressDialog.dismiss();
                            //msg.setText("This Techniques is not being assigned to Trainee!");
                            msg.setText("Please enter a valid First Aid Technique Name!");
                        }
                    }

                }

                @Override
                public void onConnectionFail(String msg) {
                    Toast.makeText(getApplicationContext(), R.string.warning_offline, Toast.LENGTH_SHORT).show();
                }
            }).execute();

        }
    }

    private String getNextTaskProcedureName(String procedureName)
    {
        int level = -1;
        //find level of current procedure
        for(int i=0;i<proceduresList.size();i++)
        {
            if(proceduresList.get(i).getName().equals(procedureName)){
                level = proceduresList.get(i).getLevel();
                break;
            }
        }
        if(level == 4)
            return "";
        //level == -1 nahi hoga kyunk already check kr liya tha upr k ye procedure assigned hy trainee ko
        ++level; //return next level procedure Name
        for(int i=0;i<proceduresList.size();i++)
        {
            if(proceduresList.get(i).getLevel() == level){
                return proceduresList.get(i).getName();
            }
        }

        return "";
    }


    private void getAssignedTasksList(String id) {

        Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
        RootURL.child("Trainees").child(id).child("assignedTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    AssignedTask assignedTask = ds.getValue(AssignedTask.class);
                    //Toast.makeText(getApplicationContext(), "Adding user", Toast.LENGTH_SHORT).show();
                    assignedTasksList.add(new AssignedTask(assignedTask.getStatus(),assignedTask.getStatement()));
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void getRecentTasksList(String id) {

        Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
        RootURL.child("Trainees").child(id).child("recentTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    RecentTask recentTask = ds.getValue(RecentTask.class);
                    recentTasksList.add(new RecentTask(recentTask.getStatement(),recentTask.getClick()));
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void getProcedures() {

        Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
        RootURL.child("Procedures").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Procedure procedure = ds.getValue(Procedure.class);
                    proceduresList.add(new Procedure(procedure.getId(), procedure.getName(),procedure.getLevel()));
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getPracticedTasksList(String id) {

        Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
        RootURL.child("Trainees").child(id).child("practiceTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PracticedTask practicedTask = ds.getValue(PracticedTask.class);
                    //Toast.makeText(getApplicationContext(), "Adding user", Toast.LENGTH_SHORT).show();
                    practicedTasksList.add(new PracticedTask(practicedTask.getProcedureName(), practicedTask.getScore()));
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void Cancel(final String id) {
        cancel_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString("UserID", id);

                        Intent i = new Intent(AddScore.this, PreviewTrainee.class);
                        i.putExtras(bundle);
                        startActivity(i);
                        AddScore.this.finish();
                    }
                }
        );
    }

    public void Save(final String id) {
        save_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SaveData(id);
                    }
                }
        );
    }

    private boolean Validate(String procName, String score) {
        boolean valid = true;

        if (procName.isEmpty()) {
            ProcedureName.setError("Please Enter a technique Name!");
            valid = false;
        } else if (score.isEmpty()) {
            Add_Score.setError("Please Enter a technique Name!");
            valid = false;
        }

        return valid;
    }

    private int getIndex(String procedureName)
    {
        int index = -1;
        for (int i = 0; i < assignedTasksList.size(); i++) {
            String statement = "Practice: " + procedureName;
            if (assignedTasksList.get(i).getStatement().equals(statement)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private boolean alreadyPracticed(String procedureName)
    {
        for (int i = 0; i < practicedTasksList.size(); i++) {
            if (practicedTasksList.get(i).getProcedureName().equals(procedureName)) {
                return true;
            }
        }
        return false;
    }

    private void Init() {

        ProcedureName = (EditText) findViewById(R.id.procedure);
        Add_Score = (EditText) findViewById(R.id.score);

        save_btn = (Button) findViewById(R.id.IDSaveButton);
        cancel_btn = (Button) findViewById(R.id.IDCancelButton);

        msg = (TextView) findViewById(R.id.Message);
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

                Intent i = new Intent(AddScore.this, MainActivity.class);
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
        bundle.putString("UserID", ID);

        Intent i = new Intent(AddScore.this, PreviewTrainee.class);
        i.putExtras(bundle);
        startActivity(i);
        //finish();
    }
}


