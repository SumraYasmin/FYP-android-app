package com.example.ayesha.fyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Ayesha on 12/7/2017.
 */

public class ViewAssignedTasks extends AppCompatActivity {

    Firebase RootURL;
    private ListView list;
    private AssignedTaskAdapter listViewAdapter;
    ArrayList<AssignedTask> my_list = new ArrayList<AssignedTask>();
    ArrayList<String> learntTasksList = new ArrayList<String>();
    ArrayList<RecentTask> recentTaskslist = new ArrayList<RecentTask>();
    private ListView recentListview;
    private RecentTaskAdapter recentTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assigned_tasks);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("UserID");

        RootURL = new Firebase(FirebaseConstants.RootURL);

        getLearntTasksList(id);
        list = (ListView) findViewById(R.id.ListAssignedTasks);
        PreviewTrainee.ListUtils.setDynamicHeight(list);

        recentListview = (ListView) findViewById(R.id.ListRecentTasks);
        PreviewTrainee.ListUtils.setDynamicHeight(recentListview);

        getRecentTasksList(id);
        getAssignedTasksList(id);
    }

    private void getAssignedTasksList(final String id) {

        RootURL.child("Trainees").child(id).child("assignedTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    AssignedTask assignedTask = ds.getValue(AssignedTask.class);
                    my_list.add(new AssignedTask(assignedTask.getStatus(),assignedTask.getStatement()));
                }
                listViewAdapter = new AssignedTaskAdapter(ViewAssignedTasks.this, R.layout.two_row_content, my_list, learntTasksList,recentTaskslist,id);
                list.setAdapter(listViewAdapter);
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getRecentTasksList(final String id) {

        RootURL.child("Trainees").child(id).child("recentTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    RecentTask recentTask = ds.getValue(RecentTask.class);
                    recentTaskslist.add(new RecentTask(recentTask.getStatement(),recentTask.getClick()));
                }
                recentTaskAdapter = new RecentTaskAdapter(ViewAssignedTasks.this, R.layout.two_row_content, recentTaskslist, learntTasksList,id);
                recentListview.setAdapter(recentTaskAdapter);
                recentTaskAdapter.notifyDataSetChanged();

                if (recentTaskslist.size()>=1) {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.Recent_Layout);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getLearntTasksList(String id) {

        RootURL.child("Trainees").child(id).child("learntTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String learntTask = ds.getValue(String.class);
                    learntTasksList.add(learntTask);
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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

                Intent i = new Intent(ViewAssignedTasks.this, ChangePassword.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                //closeOptionsMenu();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }

                Intent ii = new Intent(ViewAssignedTasks.this, MainActivity.class);
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

