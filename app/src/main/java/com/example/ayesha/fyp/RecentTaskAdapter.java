package com.example.ayesha.fyp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
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

public class RecentTaskAdapter  extends ArrayAdapter<RecentTask> {

    Firebase RootURL;
    private Context context;
    private  int layout;
    private ArrayList<RecentTask> recentTaskList;
    private String UserId;
    private ArrayList<String> learntTasksList;

    public RecentTaskAdapter(Context context, int textViewResourceId,ArrayList<RecentTask> objects ,ArrayList<String> learntTasksList,String id) {
        super(context, textViewResourceId, objects);

        this.context = context;
        this.layout = textViewResourceId;
        this.recentTaskList = objects;
        this.learntTasksList = learntTasksList;
        this.UserId = id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View curView = convertView;

        if (curView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            curView = vi.inflate(R.layout.two_row_content, null);
        }

        final RecentTask recentTask = getItem(position);

        //sets the layouts for the item
        TextView statement = (TextView) curView.findViewById(R.id.procName);
        statement.setText(recentTask.getStatement());

        TextView click = (TextView) curView.findViewById(R.id.second);
        click.setText(recentTask.getClick());

        //programmatically setting style
        click.setTypeface(null, Typeface.NORMAL);
        int textSizeInSp = (int) context.getResources().getDimension(R.dimen.result_font);
        click.setTextSize(convertSpToPixels(textSizeInSp , getContext()));

        curView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doTask(recentTask);
                    }
                }
        );

        if(recentTask.getStatement().contains("Learn")){
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound("L", Color.parseColor("#2ECC71"));
            ImageView image = (ImageView) curView.findViewById(R.id.icon_list);
            image.setImageDrawable(drawable);

            click.setTextColor(Color.parseColor("#D11919"));
            return curView;
        }

        click.setTextColor(Color.parseColor("#2ECC71"));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("P", Color.RED);
        ImageView image = (ImageView) curView.findViewById(R.id.icon_list);

        image.setImageDrawable(drawable);


        return curView;

    }

    private void doTask(RecentTask recentTask)
    {
        String statement = recentTask.getStatement();
        RootURL = new Firebase(FirebaseConstants.RootURL);
        //context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.MyCompany.CPR")));
        if(statement.contains("Learn"))
        {
            if(statement.contains("Shock Treatment")){
                doStuff(recentTask, "Shock Treatment", "com.Ayesha1.ShockTreatment");
            }
            else if(statement.contains("Cardio Pulmonary Resuscitation")){
                doStuff(recentTask, "Cardio Pulmonary Resuscitation", "com.Ayesha2.CPR");
            }
            else if(statement.contains("Heimlich Maneuver")){
                doStuff(recentTask, "Heimlich Maneuver", "com.Ayesha3.Choking");
            }
            else if(statement.contains("Recovery Position")){
                doStuff(recentTask, "Recovery Position", "com.Ayesha4.RecoveryPosition");
            }
            else//for future extension
                doStuff(recentTask, "Cardio Pulmonary Resuscitation", "com.Ayesha2.CPR");
        }
        else if(statement.contains("Practice"))
        {
            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Server.LM")));
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

    private void doStuff(RecentTask recentTask, String procedureName, String PackageName)
    {
        context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage(PackageName)));
        ((Activity)getContext()).finish();

        String learntTask = "Learnt: " + procedureName;
        learntTasksList.add(learntTask);
        Firebase node = RootURL.child("Trainees").child(UserId).child("learntTasksList");
        node.removeValue();
        node.setValue(learntTasksList);


        if(!isActivityRunning(context.getPackageManager().getLaunchIntentForPackage(PackageName).getClass()))
        {
            Bundle bundle = new Bundle();
            bundle.putString("UserID", UserId);

            Intent i = new Intent(getContext(),ViewAssignedTasks.class);
            i.putExtras(bundle);
            context.startActivity(i);
        }
    }

    public static float convertSpToPixels(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

}



