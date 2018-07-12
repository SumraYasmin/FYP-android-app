package com.example.ayesha.fyp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Ayesha on 3/27/2018.
 */

public class PreviewTrainee extends AppCompatActivity {

    Firebase RootURL;
    private ListView LearningListView, PracticeListView;

    private String Name;
    private ArrayList<String> Learninglist = new ArrayList<String>();
    private ArrayList<PracticedTask> PracticeList = new ArrayList<PracticedTask>();   // ye practice task ki list hogi

    //in dono ko , 2 listviews ko aik he activity main show krwana hy dono k adapters aur compnent layouts hain
    private ListviewAdapter LearninglistAdapter = null;
    private PracticeTaskListAdapter PracticelistAdapter = null;
    private TextView add_Score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_trainee);

        //fetch it from cloud later on
        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("UserID");

        RootURL = new Firebase(FirebaseConstants.RootURL);

        LearningListView = (ListView) findViewById(R.id.Learning_list);
        ListUtils.setDynamicHeight(LearningListView);
        PracticeListView = (ListView) findViewById(R.id.Practice_list);
        ListUtils.setDynamicHeight(PracticeListView);

        getPracticeRecords(id);
        getLearningRecords(id);

        add_Score = (TextView) findViewById(R.id.add_score);
        add_Score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("UserID", id);

                Intent i = new Intent(PreviewTrainee.this,AddScore.class);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        });


    }

    private void getLearningRecords(final String id) {

        RootURL.child("Trainees").child(id).child("learntTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String s = ds.getValue(String.class);
                    //Toast.makeText(getApplicationContext(), "Adding user", Toast.LENGTH_SHORT).show();
                    Learninglist.add(s);
                }
                LearninglistAdapter = new ListviewAdapter(PreviewTrainee.this, R.layout.list_content, Learninglist,id);
                LearningListView.setAdapter(LearninglistAdapter);
                LearninglistAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Previously was tring to read full array
        /*
        final Firebase RootURL = new Firebase(FirebaseConstants.RootURL);
        //Firebase node = RootURL.child("Users").child(user.getUid()).child("username");
        Firebase node = RootURL.child("Trainees").child(id).child("LearntTasksList");
        node.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                ArrayList<String>  arr = (ArrayList<String>)dataSnapshot.getValue();

                if(arr != null) {
                    LearningListView = (ListView) findViewById(R.id.Learning_list);
                    LearninglistAdapter = new ListviewAdapter(PreviewTrainee.this, R.layout.list_content, arr);
                    LearningListView.setAdapter(LearninglistAdapter);

                }
               else {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
*/
    }

    private void getPracticeRecords(String id) {

        RootURL.child("Trainees").child(id).child("practiceTasksList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PracticedTask practicedTask = ds.getValue(PracticedTask.class);
                    //Toast.makeText(getApplicationContext(), "Adding user", Toast.LENGTH_SHORT).show();
                    PracticeList.add(new PracticedTask(practicedTask.getProcedureName(), practicedTask.getScore()));
                }

                PracticelistAdapter = new PracticeTaskListAdapter(PreviewTrainee.this, R.layout.two_row_content, PracticeList);
                PracticeListView.setAdapter(PracticelistAdapter);
                PracticelistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
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

                Intent i = new Intent(PreviewTrainee.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //LearninglistAdapter.notifyDataSetChanged();
        //PracticelistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(PreviewTrainee.this, ViewTrainees.class);
        startActivity(i);
    }
}
