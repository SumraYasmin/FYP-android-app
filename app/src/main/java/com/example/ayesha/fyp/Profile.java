package com.example.ayesha.fyp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Profile extends AppCompatActivity {

    private static TextView Text_Name;
    private static ImageView Profile_Picture;
    private static TextView TextView_EditProfile;
    private static TextView TextView_UploadPicture;
    private static TextView TextView_ViewAssignedTasks;
    private static TextView TextView_LearnAssignedProcedures;
    private static TextView TextView_PracticeAsssignedProcedure;
    private static TextView TextView_ViewTrainingHistory;
    private static TextView TextView_VewPracticeHistory;

    Firebase RootURL;
    private ProgressDialog mProgressDialog;
    private static String s = "";

    private static final String TAG = "ProfileActivity";
    private static final int  REQUEST_CODE_GALLERY = 999;
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_action_menu);


        Init();
        RootURL = new Firebase(FirebaseConstants.RootURL);
        mProgressDialog = new ProgressDialog(Profile.this);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        //Intent i = getIntent();
        //s = i.getStringExtra("Name");
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        s = bundle.getString("Name");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url

            if(user.getDisplayName() != null) {
                String name = user.getDisplayName();
                Text_Name.setText(name);

                Uri photoUrl = user.getPhotoUrl();
                Picasso.with(this).load(photoUrl).fit().transform(new myTransformation()).into(Profile_Picture);
            }
            else {
                Text_Name.setText(s);
                BitmapDrawable drawable = (BitmapDrawable) Profile_Picture.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Bitmap converted_Bitmap = getRoundedRectBitmap(bitmap,250);
                Profile_Picture.setImageBitmap(converted_Bitmap);
            }
        }
        else{
            Text_Name.setText(s);
            BitmapDrawable drawable = (BitmapDrawable) Profile_Picture.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Bitmap converted_Bitmap = getRoundedRectBitmap(bitmap,250);
            Profile_Picture.setImageBitmap(converted_Bitmap);
        }

        //final String id = bundle.getString("UserID");
        final String id = user.getUid();
        EditProfile();
        UploadPicture();
        viewAssignedTasks(id);
        LearnAssignedProcedure();
        viewTrainingHistory(id);
        viewPracticeHistory(id);

        CheckNotifications();
    }

    public void  CheckNotifications(){
        RootURL.child("Approved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value.equals("Yes"))
                {
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.displayNotification(Profile.this,PACKAGE_NAME);
                }

                //Make Approved No here
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void viewAssignedTasks(final String id){
        TextView_ViewAssignedTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("UserID", id);

                Intent i = new Intent(Profile.this,ViewAssignedTasks.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    public void LearnAssignedProcedure(){
        TextView_LearnAssignedProcedures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,LearnAssignedProcedure.class));
            }
        });
    }

    public void viewTrainingHistory(final String id){
        TextView_ViewTrainingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("UserID", id);

                Intent i = new Intent(Profile.this,ViewTrainingHistory.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    public void viewPracticeHistory(final String id){
        TextView_VewPracticeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("UserID", id);

                Intent i = new Intent(new Intent(Profile.this,ViewPracticeHistory.class));
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    public void UploadPicture() {
        //checkFilePermissions();
        TextView_UploadPicture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(
                                Profile.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE_GALLERY
                        );
                    }
                }
        );

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if (user != null) {
            //Uri photoUrl = user.getPhotoUrl();
            //Profile_Picture.setDrawingCacheEnabled(false);
            //Picasso.with(this).load(photoUrl).fit().transform(new myTransformation()).into(Profile_Picture);
        //}
    }

    public void EditProfile() {
        TextView_EditProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Profile.this, EditProfile.class);
                        //i.putExtra("uname",s);
                        startActivity(i);
                        Profile.this.finish();
                    }
                }
        );
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

                Intent i = new Intent(Profile.this, ChangePassword.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                //closeOptionsMenu();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }

                Intent ii = new Intent(Profile.this, MainActivity.class);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url

            if(user.getDisplayName() != null) {
                String name = user.getDisplayName();
                Text_Name.setText(name);

                Uri photoUrl = user.getPhotoUrl();
                Picasso.with(this).load(photoUrl).fit().transform(new myTransformation()).into(Profile_Picture);
            }
            else
                Text_Name.setText(s);
        }
        else{
            Text_Name.setText(s);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url

            if(user.getDisplayName() != null) {
                String name = user.getDisplayName();
                Text_Name.setText(name);

                Uri photoUrl = user.getPhotoUrl();
                Picasso.with(this).load(photoUrl).fit().transform(new myTransformation()).into(Profile_Picture);
            }
            else
                Text_Name.setText(s);
        }
        else{
            Text_Name.setText(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);

            }
            else {
                Toast.makeText(getApplicationContext(), R.string.warning_permission, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();

                    user.updateProfile(profileUpdates);
                }
                Transformation transformation = new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int size = Math.min(source.getWidth(), source.getHeight());

                        int x = (source.getWidth() - size) / 2;
                        int y = (source.getHeight() - size) / 2;

                        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                        if (squaredBitmap != source) {
                            source.recycle();
                        }

                        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        BitmapShader shader = new BitmapShader(squaredBitmap,
                                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                        paint.setShader(shader);
                        paint.setAntiAlias(true);

                        float r = size / 2f;
                        canvas.drawCircle(r, r, r, paint);

                        squaredBitmap.recycle();
                        return bitmap;
                    }

                    @Override
                    public String key() {
                        return "circle";
                    }
                };
                Profile_Picture.refreshDrawableState();
                Picasso.with(this).load(uri).fit().transform(transformation).into(Profile_Picture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {//pass 100 px for circle im passing 250 for better circle
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;


        //previously using this function like this in on start and resume waghera pr
        /*
        * Uri photoUrl = user.getPhotoUrl();
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(photoUrl);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap converted_Bitmap = getRoundedRectBitmap(bitmap,250);
                Profile_Picture.setImageBitmap(converted_Bitmap);
*/
    }

    private void Init() {

        Text_Name = (TextView) findViewById(R.id.title_profile);
        TextView_EditProfile = (TextView) findViewById(R.id.edit_profile);
        Profile_Picture = (ImageView) findViewById(R.id.profile_picture);
        TextView_UploadPicture = (TextView) findViewById(R.id.upload_picture);
        TextView_ViewAssignedTasks = (TextView) findViewById(R.id.view_assigned_tasks);
        TextView_LearnAssignedProcedures = (TextView) findViewById(R.id.learn_assignd_procedure);
        TextView_PracticeAsssignedProcedure = (TextView) findViewById(R.id.practice_assigned_procedure);
        TextView_ViewTrainingHistory = (TextView) findViewById(R.id.view_training_history);
        TextView_VewPracticeHistory = (TextView) findViewById(R.id.view_practice_history);
    }

    public class UpdateAPK extends AsyncTask<Void,Void,String> {
        private Context context;
        public void setContext(Context contextf){
            context = contextf;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            NotificationUtils notificationUtils = new NotificationUtils();
            notificationUtils.displayNotification(Profile.this,PACKAGE_NAME);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Downloading updated apk file.\nPlease wait..");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                Thread.sleep(12000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public class myTransformation implements com.squareup.picasso.Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
/*
*   Transformation transformation = new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int size = Math.min(source.getWidth(), source.getHeight());

                        int x = (source.getWidth() - size) / 2;
                        int y = (source.getHeight() - size) / 2;

                        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                        if (squaredBitmap != source) {
                            source.recycle();
                        }

                        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        BitmapShader shader = new BitmapShader(squaredBitmap,
                                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                        paint.setShader(shader);
                        paint.setAntiAlias(true);

                        float r = size / 2f;
                        canvas.drawCircle(r, r, r, paint);

                        squaredBitmap.recycle();
                        return bitmap;
                    }

                    @Override
                    public String key() {
                        return "circle";
                    }
                };*/

}
