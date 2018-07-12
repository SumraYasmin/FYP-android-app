package com.example.ayesha.fyp;

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

/**
 * Created by Ayesha on 12/28/2017.
 */

public class ViewPracticeHistory extends AppCompatActivity {
    Firebase RootURL;
    private ListView list;
    private PracticeTaskListAdapter practicelistViewAdapter = null;
    private ArrayList<PracticedTask> practicedTasksList = new ArrayList<PracticedTask>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_practice_history);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("UserID");

        RootURL = new Firebase(FirebaseConstants.RootURL);

        list = (ListView) findViewById(R.id.ListPracticeHistory);
        getPracticedTasksLists(id);
        registerForContextMenu(list);
    }


    private void getPracticedTasksLists(String id) {

        RootURL.child("Trainees").child(id).child("practiceTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PracticedTask practicedTask = ds.getValue(PracticedTask.class);
                    practicedTasksList.add(new PracticedTask(practicedTask.getProcedureName(),practicedTask.getScore()));
                }
                practicelistViewAdapter = new PracticeTaskListAdapter(ViewPracticeHistory.this, R.layout.two_row_content, practicedTasksList);
                list.setAdapter(practicelistViewAdapter);
                practicelistViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()== R.id.ListPracticeHistory) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_context_practice_history, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.practice:
                startActivity(new Intent(getBaseContext().getPackageManager().getLaunchIntentForPackage("com.Server.LM")));
                item.setEnabled(true);
                break;
            default:
                break;

        }
        return true;
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

                Intent i = new Intent(ViewPracticeHistory.this, ChangePassword.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                //closeOptionsMenu();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }

                Intent ii = new Intent(ViewPracticeHistory.this, MainActivity.class);
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
        //practicelistViewAdapter.notifyDataSetChanged();
    }
}

