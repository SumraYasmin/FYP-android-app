package com.example.ayesha.fyp;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * Created by Ayesha on 3/27/2018.
 */

public class ViewProcedures extends AppCompatActivity {

    private ListView ProceduresListview;
    private ArrayList<Procedure> procedures_list = new ArrayList<Procedure>();

    private  ProcedureListViewAdapter listviewAdapter;
    Firebase RootURL;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_procedures);

        RootURL = new Firebase(FirebaseConstants.RootURL);

        ProceduresListview = (ListView) findViewById(R.id.List_Procedures);
        getProceduresList(); //from cloud Database

        registerForContextMenu(ProceduresListview);
    }

    private  void getProceduresList()
    {
        RootURL.child("Procedures").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Procedure procedure = ds.getValue(Procedure.class);
                    //Toast.makeText(getApplicationContext(), "Adding user", Toast.LENGTH_SHORT).show();
                    procedures_list.add(new Procedure(procedure.getId(),procedure.getName(), procedure.getLevel()));
                }
                listviewAdapter = new ProcedureListViewAdapter(ViewProcedures.this, R.layout.two_row_content, procedures_list);
                ProceduresListview.setAdapter(listviewAdapter);
                listviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()== R.id.List_Procedures) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                //long selectid = menuinfo.id; //_id from database in this case
                //int selectpos = menuinfo.position; //position in the adapter

                Procedure procedure = procedures_list.get(info.position);
                Intent updateProcedure = new Intent(ViewProcedures.this, UpdateProcedure.class);
                updateProcedure.putExtra("ID", procedure.getId());
                startActivity(updateProcedure);
                //getProceduresList();
                //listviewAdapter..notifyDataSetChanged();
                finish();
                item.setEnabled(true);
                break;

            case R.id.view:
                //procedures_list.remove(info.position);
                //RootURL.child("Procedures").child(procedures_list.get(info.position).getId()).removeValue();
                //listviewAdapter.notifyDataSetChanged();
                Procedure proc = procedures_list.get(info.position);
                if(proc.getName().equals("Shock Treatment")) {
                    startActivity(new Intent(getBaseContext().getPackageManager().getLaunchIntentForPackage("com.Ayesha1.ShockTreatment")));
                }
                else if(proc.getName().equals("Cardio Pulmonary Resuscitation")) {
                    startActivity(new Intent(getBaseContext().getPackageManager().getLaunchIntentForPackage("com.Ayesha2.CPR")));
                }
                else if(proc.getName().equals("Heimlich Maneuver")) {
                    startActivity(new Intent(getBaseContext().getPackageManager().getLaunchIntentForPackage("com.Ayesha3.Choking")));
                }
                else if(proc.getName().equals("Recovery Position")) {
                    startActivity(new Intent(getBaseContext().getPackageManager().getLaunchIntentForPackage("com.Ayesha4.RecoveryPosition")));
                }
                else
                    startActivity(new Intent(getBaseContext().getPackageManager().getLaunchIntentForPackage("com.Ayesha2.CPR")));
                /*if(!isActivityRunning(getBaseContext().getPackageManager().getLaunchIntentForPackage("com.Server.LM").getClass()))
                {
                    Intent i = new Intent(getBaseContext(),ViewProcedures.class);
                    startActivity(i);
                }*/
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

                Intent i = new Intent(ViewProcedures.this, MainActivity.class);
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
        //listviewAdapter.notifyDataSetChanged();
    }

    //I could also make this a static method of any class nad access it via class name
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
}



