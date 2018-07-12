package com.example.ayesha.fyp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

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

public class ViewTrainees extends AppCompatActivity {

    Firebase RootURL;
    private SearchView searchView;

    private ListView list;
    private TraineeListViewAdapter listViewAdapter;
    List<Trainee> my_list = new ArrayList<Trainee>();

    private static final String TAG = "ViewTraineesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trainees);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_action_menu);

        RootURL = new Firebase(FirebaseConstants.RootURL);

        list = (ListView) findViewById(R.id.list);
        getTraineesList();

    }

    private void SearchTheList() {
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //setAdapters()
                //listViewAdapter.getFilter().filter(newText);
                if (newText != null && !newText.isEmpty()) {
                    List<Trainee> IstFound = new ArrayList<Trainee>();
                    for (Trainee trainee : my_list) {
                        if (trainee.getName().contains(newText))
                            IstFound.add(trainee);
                    }
                    listViewAdapter = new TraineeListViewAdapter(ViewTrainees.this, R.layout.component_list, IstFound);
                    list.setAdapter(listViewAdapter);
                } else {
                    listViewAdapter = new TraineeListViewAdapter(ViewTrainees.this, R.layout.component_list, my_list);
                    list.setAdapter(listViewAdapter);
                }
                return true;
            }

        });

    }


    private void getTraineesList() {

        RootURL.child("Trainees").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Trainee u = ds.getValue(Trainee.class);
                    //Toast.makeText(getApplicationContext(), "Adding user", Toast.LENGTH_SHORT).show();
                    my_list.add(new Trainee(u.getId(),u.getUsername(), u.getName(), u.getPassword(), u.getEmail(),u.getAssignedTasksList(),u.getLearntTasksList(),u.getPracticeTasksList(),u.getRecentTasksList()));
                }
                listViewAdapter = new TraineeListViewAdapter(ViewTrainees.this, R.layout.component_list, my_list);
                list.setAdapter(listViewAdapter);
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_trainees, menu);


       /* if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }*/

        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search..");
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchTheList();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.btn_logout:{
                FirebaseAuth.getInstance().signOut();
                //closeOptionsMenu();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }

                Intent i = new Intent(ViewTrainees.this, MainActivity.class);
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
