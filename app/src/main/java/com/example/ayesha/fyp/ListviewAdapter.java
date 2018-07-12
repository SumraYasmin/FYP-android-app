package com.example.ayesha.fyp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


//import com.MyCompany.CPR.UnityPlayerActivity;

/**
 * Created by Ayesha on 12/3/2017.
 */

public class ListviewAdapter extends BaseAdapter {   //for LearntTasks

    Firebase RootURL;
    private Context context;
    private  int layout;
    private ArrayList<String> List;
    private String id;

    public ListviewAdapter(Context context, int layout, ArrayList<String> List, String userID) {
        this.context = context;
        this.layout = layout;
        this.List = List;
        this.id = userID;
    }

    @Override
    public int getCount() {
        return List.size();
    }

    @Override
    public Object getItem(int position) {
        return List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView learntTask;
        TextView learn_again;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.learntTask = (TextView) row.findViewById(R.id.list_content);
            holder.learn_again = (TextView) row.findViewById(R.id.learn_again);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if(!(user.getEmail().equals("interactive.first.aid.training@gmail.com")))
                    holder.learn_again.setVisibility(View.VISIBLE);
            }
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        final String content = List.get(position);
        holder.learntTask.setText(content);

        holder.learn_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (content.contains("Shock Treatment")) {
                    AddtoTrainingHistory(content, "com.Ayesha1.ShockTreatment");
                } else if (content.contains("Cardio Pulmonary Resuscitation")) {
                    AddtoTrainingHistory(content, "com.Ayesha2.CPR");
                } else if (content.contains("Heimlich Maneuver")) {
                    AddtoTrainingHistory(content, "com.Ayesha3.Choking");
                } else if (content.contains("Recovery Position")) {
                    AddtoTrainingHistory(content, "com.Ayesha4.RecoveryPosition");
                }
                else
                    AddtoTrainingHistory(content, "com.Ayesha2.CPR");
            }
        });

        //row.setonclick listener

        TextDrawable drawable = TextDrawable.builder()
                .buildRound("L", Color.parseColor("#2ECC71"));
        ImageView image = (ImageView) row.findViewById(R.id.icon_list);
        image.setImageDrawable(drawable);

        return row;
    }

    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    private void AddtoTrainingHistory(String learntTask,String PackageName) {
        context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage(PackageName)));
        ((Activity)context).finish();

        RootURL = new Firebase(FirebaseConstants.RootURL);
        List.add(learntTask);
        Firebase node = RootURL.child("Trainees").child(id).child("learntTasksList");
        node.removeValue();
        node.setValue(List);

        if(!isActivityRunning(context.getPackageManager().getLaunchIntentForPackage(PackageName).getClass()))
        {
            Bundle bundle = new Bundle();
            bundle.putString("UserID", id);

            Intent i = new Intent(context,ViewTrainingHistory.class);
            i.putExtras(bundle);
            context.startActivity(i);
        }
    }

}


