package com.example.ayesha.fyp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ayesha on 4/30/2018.
 */

public class ApproveNewTechniques extends AppCompatActivity {

    private ListView ProceduresListview;
    private ArrayList<Procedure> approved_procedures_list = new ArrayList<Procedure>();
    private ArrayList<Procedure> procedures_list = new ArrayList<Procedure>();

    private  ApproveNewTechniquesListAdapter listviewAdapter;
    Firebase RootURL;
    private ProgressDialog mProgressDialog;

    public static String PACKAGE_NAME;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_new_techniques);

        RootURL = new Firebase(FirebaseConstants.RootURL);

        CheckNotifications();
        getProceduresList();

        ProceduresListview = (ListView) findViewById(R.id.List_Procedures);
        getApprovedProceduresList(); //from cloud Database
        registerForContextMenu(ProceduresListview);

        mProgressDialog = new ProgressDialog(ApproveNewTechniques.this);
        PACKAGE_NAME = getApplicationContext().getPackageName();

    }

    public void  CheckNotifications(){
        RootURL.child("Approved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value.equals("Yes"))
                {
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.displayNotification(ApproveNewTechniques.this,PACKAGE_NAME);
                }

                //Make Approved No here
                //RootURL.child("Approved").setValue("No");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private  void getApprovedProceduresList()
    {
        RootURL.child("ProceduresToBeApproved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " children");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Procedure procedure = ds.getValue(Procedure.class);
                    //Toast.makeText(getApplicationContext(), "Adding user", Toast.LENGTH_SHORT).show();
                    approved_procedures_list.add(new Procedure(procedure.getId(),procedure.getName(), procedure.getLevel()));
                }
                listviewAdapter = new ApproveNewTechniquesListAdapter(ApproveNewTechniques.this, R.layout.two_row_content, approved_procedures_list,procedures_list);
                ProceduresListview.setAdapter(listviewAdapter);
                listviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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
            inflater.inflate(R.menu.menu_context_approve_techniques, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.view:
                verifyStoragePermissions(ApproveNewTechniques.this);
                //Toast.makeText(ApproveNewTechniques.this, "New Technique Download in progress..", Toast.LENGTH_SHORT).show();

                //DownloadTechnique downloadTechnique = new DownloadTechnique();
                //downloadTechnique.setContext(getApplicationContext());
                //downloadTechnique.execute("https://firebasestorage.googleapis.com/v0/b/final-year-project-105d0.appspot.com/o/NewTechniques%2FCPR.apk?alt=media&token=bcab4832-5e71-47bd-b4a1-517d08639794");
                newTechnique newTechniquee = new newTechnique();
                newTechniquee.setContext(ApproveNewTechniques.this);
                newTechniquee.execute();

                item.setEnabled(true);
                break;
            case R.id.approve:
                //Approve krne par vo procedures list main add hojayn gy aur push notification trainee ko chali jayeg
                //remove from approve procedures
                Procedure procedure = approved_procedures_list.get(info.position);
                RootURL.child("ProceduresToBeApproved").child(approved_procedures_list.get(info.position).getId()).removeValue();
                approved_procedures_list.remove(info.position);
                //add to procedures
                procedures_list.add(procedure);
                RootURL.child("Procedures").removeValue();
                Firebase Node = RootURL.child("Procedures");
                 for(int i=0;i<procedures_list.size(); i++)
                     Node.child(procedures_list.get(i).getId()).setValue(procedures_list.get(i));
               // RootURL.child("Procedures").setValue(procedures_list);

                //send notification to users
                UpdateAPK updateAPK = new UpdateAPK();
                updateAPK.setContext(ApproveNewTechniques.this);
                updateAPK.execute();

                item.setEnabled(true);
                break;

            case R.id.disapprove:

                RootURL.child("ProceduresToBeApproved").child(approved_procedures_list.get(info.position).getId()).removeValue();
                approved_procedures_list.remove(info.position);

                Intent refresh = new Intent(this, ApproveNewTechniques.class);
                startActivity(refresh);
                this.finish();

                //also delete the downloaded file if it exists
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

                Intent i = new Intent(ApproveNewTechniques.this, MainActivity.class);
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


    public class DownloadTechnique extends AsyncTask<String,Void,Void> {
        private Context context;
        public void setContext(Context contextf){
            context = contextf;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                //Environment.getExternalCacheDir().getAbsolutePath()(Environment.DIRECTORY_DOWNLOADS)
                String PATH ;
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                file.mkdirs();
                File outputFile = new File(file, "/newtechnique.apk");
                if(outputFile.exists()){ //already exists
                    outputFile.delete();
                }

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();
                byte[] buffer = new byte[1024*1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();

                 //open the application
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setDataAndType(Uri.fromFile(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))+"/newtechnique.apk")), "application/vnd.android.package-archive");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                Uri apkURI = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))));
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e("DownloadTechnique", "Download Technique error" + e.getMessage());
            }
            return null;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
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

    public class newTechnique extends AsyncTask<Void,Void,String> {
        private Context context;
        public void setContext(Context contextf){
            context = contextf;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Ayesha2.CPR")));
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Downloading Tutorial animation.\nPlease wait..");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class UpdateAPK extends AsyncTask<Void, Void, String> {
        private Context context;

        public void setContext(Context contextf) {
            context = contextf;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();

            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //Uri.fromFile(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + "/app-debug.apk"))
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            Uri apkURI = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext()
                            .getPackageName() + ".provider", new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + "/app-debug.apk"));
            intent1.setDataAndType(Uri.fromFile(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + "/app-debug.apk")), "application/vnd.android.package-archive");
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent1);

            //Intent refresh = new Intent(context.getApplicationContext(), ApproveNewTechniques.class);
            //context.getApplicationContext().startActivity(refresh);
            //((Activity)context).finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Downloading updated apk file.\nPlease wait..");
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}




