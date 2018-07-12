package com.example.ayesha.fyp;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.firebase.client.Firebase;

import java.io.File;
import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

/**
 * Created by Ayesha on 5/1/2018.
 */

public class NotificationUtils {
    public static final int NOTIFICATION_ID = 1;

    public static final String ACTION_1 = "action_1";

    private static Context context;
    private static String packageName;
    static NotificationCompat.Builder notificationBuilder;
    static NotificationManagerCompat notificationManager;
    private static ProgressDialog mProgressDialog;
    //static Firebase RootURL;


    public static void displayNotification(Context _context, String _packageName) {

        packageName = _packageName;
        context = _context;
        mProgressDialog = new ProgressDialog(context);
        //RootURL = new Firebase(FirebaseConstants.RootURL);

        Intent action1Intent = new Intent(_context, NotificationActionService.class)
                .setAction(ACTION_1);

        PendingIntent action1PendingIntent = PendingIntent.getService(_context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);

        //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //setContentText("Click to download a new First Aid Technique for training.")
        notificationBuilder =
                new NotificationCompat.Builder(_context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("An Update is Available!")

                        .setSound(soundUri) //This sets the sound to play
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setProgress(15,0, false)
                        .addAction(new NotificationCompat.Action(R.mipmap.ic_launcher,
                                "Update", action1PendingIntent));

        notificationManager = NotificationManagerCompat.from(_context);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            //DebugUtils.Log("Received notification action: " + action);
            if (ACTION_1.equals(action)) {
                // TODO: handle action 1.
                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);

                NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);

                UpdateAPK updateAPK = new UpdateAPK();
                updateAPK.setContext(context);
                updateAPK.execute();

            }
        }

        class UpdateAPK extends AsyncTask<Void, Void, String> {
            private Context _context;
            private NotificationCompat.Builder Builder;
            public void setContext(Context contextf) {

                _context = contextf;

                Builder =
                        new NotificationCompat.Builder(_context)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("An Update is Available!")
                                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                .setProgress(15,0, false);
            }

            @Override
            protected void onPostExecute(String result) {
                //mProgressDialog.dismiss();

                Builder.setContentText("Download Complete");
                Builder.setProgress(0,0,false);
                notificationManager.notify(NOTIFICATION_ID, Builder.build());

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
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //mProgressDialog.setMessage("Downloading updated apk file.\nPlease wait..");
                //mProgressDialog.show();
                //mProgressDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                int incr;
                // Do the "lengthy" operation 20 times
                for (incr = 0; incr <= 15; ++incr) {
                    // Sets the progress indicator to a max value, the
                    // current completion percentage, and "determinate"
                    // state
                    Builder.setProgress(15, incr, false);
                    // Displays the progress bar for the first time.
                    notificationManager.notify(NOTIFICATION_ID, Builder.build());
                    // Sleeps the thread, simulating an operation
                    // that takes time
                    try {
                        // Sleep for 5 seconds
                        Thread.sleep(2*1000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "sleep failure");
                    }
                }
                return null;
            }
        }
    }
}