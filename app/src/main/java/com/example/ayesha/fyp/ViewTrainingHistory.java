package com.example.ayesha.fyp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ayesha on 12/7/2017.
 */

public class ViewTrainingHistory extends AppCompatActivity {

    Firebase RootURL;
    private ListView list;
    private ListviewAdapter listViewAdapter = null;
    ArrayList<String> learntTasksList = new ArrayList<String>();
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_history);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("UserID");

        RootURL = new Firebase(FirebaseConstants.RootURL);


        list = (ListView) findViewById(R.id.ListTrainingHistory);
        getLearntTasksLists(id);
        registerForContextMenu(list);
    }


    private void getLearntTasksLists(final String id) {

        RootURL.child("Trainees").child(id).child("learntTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String task = ds.getValue(String.class);
                    learntTasksList.add(task);
                }
                listViewAdapter = new ListviewAdapter(ViewTrainingHistory.this, R.layout.list_content, learntTasksList, id);
                list.setAdapter(listViewAdapter);
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.ListTrainingHistory) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_context_training_history, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.learn:
                String learntTask = learntTasksList.get(info.position);
                if (learntTask.contains("Shock Treatment")) {
                    AddtoTrainingHistory(learntTask, "com.Ayesha1.ShockTreatment");
                } else if (learntTask.contains("Cardio Pulmonary Resuscitation")) {
                    AddtoTrainingHistory(learntTask, "com.Ayesha2.CPR");
                } else if (learntTask.contains("Heimlich Maneuver")) {
                    AddtoTrainingHistory(learntTask, "com.Ayesha3.Choking");
                } else if (learntTask.contains("Recovery Position")) {
                    AddtoTrainingHistory(learntTask, "com.Ayesha4.RecoveryPosition");
                }
                else
                    AddtoTrainingHistory(learntTask, "com.Ayesha2.CPR");

                item.setEnabled(true);
                break;
            default:
                break;

        }
        return true;
    }

    private void AddtoTrainingHistory(String learntTask,String PackageName) {
        startActivity(new Intent(getBaseContext().getPackageManager().getLaunchIntentForPackage(PackageName)));
        ViewTrainingHistory.this.finish();

        learntTasksList.add(learntTask);
        Firebase node = RootURL.child("Trainees").child(id).child("learntTasksList");
        node.removeValue();
        node.setValue(learntTasksList);

        if(!isActivityRunning(getBaseContext().getPackageManager().getLaunchIntentForPackage(PackageName).getClass()))
        {
            Bundle bundle = new Bundle();
            bundle.putString("UserID", id);

            Intent i = new Intent(getBaseContext(),ViewTrainingHistory.class);
            i.putExtras(bundle);
            startActivity(i);
        }
    }

    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);


        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.btn_change_password:

                Intent i = new Intent(ViewTrainingHistory.this, ChangePassword.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                //closeOptionsMenu();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }

                Intent ii = new Intent(ViewTrainingHistory.this, MainActivity.class);
                ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(ii);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
        //listViewAdapter.notifyDataSetChanged();
    }
}

