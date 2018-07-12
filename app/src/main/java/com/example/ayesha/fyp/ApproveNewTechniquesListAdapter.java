package com.example.ayesha.fyp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.ayesha.fyp.ApproveNewTechniques.verifyStoragePermissions;

/**
 * Created by Ayesha on 5/1/2018.
 */

public class ApproveNewTechniquesListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Procedure> approved_procedures_list;
    private ArrayList<Procedure> procedures_list;
    private ProgressDialog mProgressDialog;
    Firebase RootURL;
    public static String PACKAGE_NAME;

    public ApproveNewTechniquesListAdapter(Context context, int layout, ArrayList<Procedure> approved_procedures_list, ArrayList<Procedure> procedures_list) {
        this.context = context;
        this.layout = layout;
        this.approved_procedures_list = approved_procedures_list;
        this.procedures_list = procedures_list;
        mProgressDialog = new ProgressDialog(this.context);
        PACKAGE_NAME = this.context.getPackageName();
    }


    @Override
    public int getCount() {
        return approved_procedures_list.size();
    }

    @Override
    public Object getItem(int position) {
        return approved_procedures_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView procName;
        TextView score;
        ImageView options;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ApproveNewTechniquesListAdapter.ViewHolder holder = new ApproveNewTechniquesListAdapter.ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.procName = (TextView) row.findViewById(R.id.procName);
            holder.score = (TextView) row.findViewById(R.id.second);
            holder.options = (ImageView) row.findViewById(R.id.menu_options);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.getEmail().equals("interactive.first.aid.training@gmail.com")) {
                    LinearLayout linearLayout = (LinearLayout) row.findViewById(R.id.layout_options);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
            row.setTag(holder);
        } else {
            holder = (ApproveNewTechniquesListAdapter.ViewHolder) row.getTag();
        }

        Procedure procedure = approved_procedures_list.get(position);

        holder.procName.setText(procedure.getName());
        holder.score.setText("Level: " + String.valueOf(procedure.getLevel()));


        if (procedure.getName().equals("Drowning")) {
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.drowning);
        } else if (procedure.getName().equals("Hyperventilation")) {
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.hyperventilation);
        } else if (procedure.getName().equals("How to put on a dressing")) {
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.dressing);
        } else if (procedure.getName().equals("How to bandage a Hand")) {
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.bandage);
        } else {
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.defolt);
        }

        RootURL = new Firebase(FirebaseConstants.RootURL);

        final int pos = position;
        final ApproveNewTechniquesListAdapter.ViewHolder holder1 = holder;
        row.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(context, holder1.options);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.menu_context_approve_techniques, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.view:
                                        verifyStoragePermissions((Activity) context);
                                        newTechnique newTechniquee = new newTechnique();
                                        newTechniquee.setContext(context);
                                        newTechniquee.execute();

                                        item.setEnabled(true);
                                        break;
                                    case R.id.approve:
                                        //Approve krne par vo procedures list main add hojayn gy aur push notification trainee ko chali jayeg
                                        //remove from approve procedures
                                        Procedure procedure = approved_procedures_list.get(pos);
                                        RootURL.child("ProceduresToBeApproved").child(approved_procedures_list.get(pos).getId()).removeValue();
                                        approved_procedures_list.remove(pos);
                                        //add to procedures
                                        procedures_list.add(procedure);
                                        RootURL.child("Procedures").removeValue();
                                        Firebase Node = RootURL.child("Procedures");
                                        for (int i = 0; i < procedures_list.size(); i++)
                                            Node.child(procedures_list.get(i).getId()).setValue(procedures_list.get(i));
                                        // RootURL.child("Procedures").setValue(procedures_list);

                                        //set sab Trainees ki approved ki value

                                        //call dialog box here
                                        // Use the Builder class for convenient dialog construction
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("An Update is Available!");
                                        builder.setMessage("Do you want to download and install the updated version of the app?")
                                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // FIRE ZE MISSILES!
                                                        UpdateAPK updateAPK = new UpdateAPK();
                                                        updateAPK.setContext(context);
                                                        updateAPK.execute();
                                                    }
                                                })
                                                .setNegativeButton("Remind me Later", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // User cancelled the dialog
                                                        RootURL.child("Approved").setValue("Yes");
                                                        dialog.dismiss();

                                                        //refresh this activity
                                                        Intent rrefresh = new Intent(context, ApproveNewTechniques.class);
                                                        context.startActivity(rrefresh);
                                                        ((Activity) context).finish();

                                                        //NotificationUtils notificationUtils = new NotificationUtils();
                                                        //notificationUtils.displayNotification(context,PACKAGE_NAME);
                                                    }
                                                });

                                        builder.setCancelable(false);
                                        builder.show();

                                        item.setEnabled(true);
                                        break;

                                    case R.id.disapprove:

                                        RootURL.child("ProceduresToBeApproved").child(approved_procedures_list.get(pos).getId()).removeValue();
                                        approved_procedures_list.remove(pos);

                                        Intent refresh = new Intent(context, ApproveNewTechniques.class);
                                        context.startActivity(refresh);
                                        ((Activity)context).finish();

                                        //also delete the downloaded file if it exists
                                        item.setEnabled(true);
                                        break;
                                    default:
                                        break;

                                }
                                return true;
                            }

                        });

                        popup.show();
                    }
                }
        );
        return row;

    }

    public static class DownloadDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("An Update is Available!");
            builder.setMessage("Do you want to download and install the updated version of the app?")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    })
                    .setNegativeButton("Remind me Later", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            builder.show();
            return builder.create();
        }
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

    class newTechnique extends AsyncTask<Void, Void, String> {
        private Context context;

        public void setContext(Context contextf) {
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

