package com.example.ayesha.fyp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ayesha on 3/30/2018.
 */

public class AssignedTaskAdapter extends ArrayAdapter<AssignedTask> {

    Firebase RootURL;
    private Context context;
    private  int layout;
    private ArrayList<AssignedTask> assignedTaskList;
    private String UserId;
    private ArrayList<String> learntTasksList;
    private ArrayList<RecentTask> recentTaskList;

    public AssignedTaskAdapter(Context context, int textViewResourceId,ArrayList<AssignedTask> objects ,ArrayList<String> learntTasksList,ArrayList<RecentTask> recentTasksList,String id) {
        super(context, textViewResourceId, objects);

        this.context = context;
        this.layout = textViewResourceId;
        this.assignedTaskList = objects;
        this.learntTasksList = learntTasksList;
        this.recentTaskList = recentTasksList;
        this.UserId = id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View curView = convertView;

        if (curView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            curView = vi.inflate(R.layout.two_row_content, null);
        }

        final AssignedTask assignedTask = getItem(position);

        //sets the layouts for the item
        TextView statement = (TextView) curView.findViewById(R.id.procName);
        statement.setText(assignedTask.getStatement());

        TextView status = (TextView) curView.findViewById(R.id.second);
        status.setText(assignedTask.getStatus());
        //status.setTypeface(null, Typeface.BOLD);

        curView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doTask(assignedTask);
                    }
                }
        );

        if(assignedTask.getStatement().contains("Learn")){
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound("L", Color.parseColor("#2ECC71"));
            ImageView image = (ImageView) curView.findViewById(R.id.icon_list);
            image.setImageDrawable(drawable);
            return curView;
        }


        TextDrawable drawable = TextDrawable.builder()
                .buildRound("P", Color.RED);
        ImageView image = (ImageView) curView.findViewById(R.id.icon_list);

        image.setImageDrawable(drawable);

        return curView;

    }

    private void doTask(AssignedTask assignedTask)
    {
        String statement = assignedTask.getStatement();
        RootURL = new Firebase(FirebaseConstants.RootURL);
        //context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.MyCompany.CPR")));
        if(statement.contains("Learn"))
        {
            if(statement.contains("Shock Treatment")){
                doStuff(assignedTask, "Shock Treatment", "com.Ayesha1.ShockTreatment");
            }
            else if(statement.contains("Cardio Pulmonary Resuscitation")){
                doStuff(assignedTask, "Cardio Pulmonary Resuscitation", "com.Ayesha2.CPR");
            }
            else if(statement.contains("Heimlich Maneuver")){
                doStuff(assignedTask, "Heimlich Maneuver", "com.Ayesha3.Choking");
            }
            else if(statement.contains("Recovery Position")){
                doStuff(assignedTask, "Recovery Position", "com.Ayesha4.RecoveryPosition");
            }
            else//for future extension
                doStuff(assignedTask, "Cardio Pulmonary Resuscitation", "com.Ayesha2.CPR");
        }
        else if(statement.contains("Practice"))
        {
            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Server.LM")));
            /*((Activity)getContext()).finish();

            if(!isActivityRunning(context.getPackageManager().getLaunchIntentForPackage("com.Server.LM").getClass()))
            {
                Bundle bundle = new Bundle();
                bundle.putString("UserID", UserId);

                Intent i = new Intent(getContext(),ViewAssignedTasks.class);
                i.putExtras(bundle);
                context.startActivity(i);
            }*/
        }

    }

    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    private void doStuff(AssignedTask assignedTask, String procedureName, String PackageName)
    {
        context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage(PackageName)));
        ((Activity)getContext()).finish();

        String learntTask = "Learnt: " + procedureName;
        learntTasksList.add(learntTask);
        Firebase node = RootURL.child("Trainees").child(UserId).child("learntTasksList");
        node.removeValue();
        node.setValue(learntTasksList);

       RecentTask recentTask = new RecentTask(learntTask, "Click to Learn Again!");
       if(recentTaskList.size() >= 3) //we'll maintain its size to be always <= 3
           recentTaskList.remove(recentTaskList.size() -1); //remove from end
        recentTaskList.add(0,recentTask); //add always at start so that recent ones are at top
        Firebase node1 = RootURL.child("Trainees").child(UserId).child("recentTasksList");
        node1.removeValue();
        node1.setValue(recentTaskList);

        if(assignedTask.getStatus().equals("New Task!")){ //if its new task assign its practice now. Assigned again k case main uski practice already assign hui vi hai. is case amain it will just be removed from assigned tasks list
            AssignedTask assignedTask1 = new AssignedTask("New Task!","Practice: " + procedureName);
            assignedTaskList.add(assignedTask1);
        }
        assignedTaskList.remove(assignedTask);
        Firebase Node = RootURL.child("Trainees").child(UserId).child("assignedTasksList");
        Node.removeValue();
        Node.setValue(assignedTaskList);

        if(!isActivityRunning(context.getPackageManager().getLaunchIntentForPackage(PackageName).getClass()))
        {
            Bundle bundle = new Bundle();
            bundle.putString("UserID", UserId);

            Intent i = new Intent(getContext(),ViewAssignedTasks.class);
            i.putExtras(bundle);
            context.startActivity(i);
        }
    }

}


