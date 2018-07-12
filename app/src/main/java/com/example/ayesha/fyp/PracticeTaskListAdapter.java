package com.example.ayesha.fyp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by Ayesha on 4/23/2018.
 */

public class PracticeTaskListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<PracticedTask> practicedTaskArrayList;


    public PracticeTaskListAdapter(Context context, int layout, ArrayList<PracticedTask> practicedTaskArrayList) {
        this.context = context;
        this.layout = layout;
        this.practicedTaskArrayList = practicedTaskArrayList;
    }


    @Override
    public int getCount() {
        return practicedTaskArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return practicedTaskArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView procName;
        TextView score;
        TextView practice_again;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.procName = (TextView) row.findViewById(R.id.procName);
            holder.score = (TextView) row.findViewById(R.id.second);
            holder.practice_again = (TextView) row.findViewById(R.id.practice_again);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (!(user.getEmail().equals("interactive.first.aid.training@gmail.com")))
                    holder.practice_again.setVisibility(View.VISIBLE);
            }
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        PracticedTask practicedTask= practicedTaskArrayList.get(position);

        holder.procName.setText("Practiced: " + practicedTask.procedureName);
        holder.score.setText("Score: "+ practicedTask.score);

        holder.practice_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Server.LM")));
            }
        });

        TextDrawable drawable = TextDrawable.builder()
                .buildRound("P", Color.RED);
        ImageView image = (ImageView) row.findViewById(R.id.icon_list);

        image.setImageDrawable(drawable);


        return row;

    }
}
