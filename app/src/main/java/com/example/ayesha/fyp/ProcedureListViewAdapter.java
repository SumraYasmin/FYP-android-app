package com.example.ayesha.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.example.ayesha.fyp.ApproveNewTechniques.verifyStoragePermissions;

/**
 * Created by Ayesha on 4/24/2018.
 */

public class ProcedureListViewAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Procedure> procedures_list;


    public ProcedureListViewAdapter(Context context, int layout, ArrayList<Procedure> procedureList) {
        this.context = context;
        this.layout = layout;
        this.procedures_list = procedureList;
    }


    @Override
    public int getCount() {
        return procedures_list.size();
    }

    @Override
    public Object getItem(int position) {
        return procedures_list.get(position);
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
        ViewHolder holder = new ViewHolder();

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
            holder = (ViewHolder) row.getTag();
        }

        Procedure procedure = procedures_list.get(position);

        holder.procName.setText(procedure.getName());

        holder.score.setText("Level: " + String.valueOf(procedure.getLevel()));

        if(procedure.getName().equals("Shock Treatment")){
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.shock_treatment);
        }
        else if(procedure.getName().equals("Cardio Pulmonary Resuscitation")){
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.drawable.cpr);
        }
        else if(procedure.getName().equals("Heimlich Maneuver")){
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.drawable.choking);
        }
        else if(procedure.getName().equals("Recovery Position")){
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.recovery_position);
        }
        else if(procedure.getName().equals("Drowning")){
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.drowning);
        }
        else if(procedure.getName().equals("Hyperventilation")){
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.hyperventilation);
        }
        else if(procedure.getName().equals("How to put on a dressing")){
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.dressing);
        }
        else if(procedure.getName().equals("How to bandage a Hand")){
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.bandage);
        }
        else {
            ImageView image = (ImageView) row.findViewById(R.id.icon_list);
            image.setImageResource(R.mipmap.defolt);
        }


        final ViewHolder holder1 = holder;
        final int pos = position;

        row.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(context, holder1.options);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.menu_context, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.edit:
                                        Procedure procedure = procedures_list.get(pos);
                                        Intent updateProcedure = new Intent(context, UpdateProcedure.class);
                                        updateProcedure.putExtra("ID", procedure.getId());
                                        context.startActivity(updateProcedure);
                                        ((Activity)context).finish();
                                        item.setEnabled(true);
                                        break;

                                    case R.id.view:
                                        Procedure proc = procedures_list.get(pos);
                                        if(proc.getName().equals("Shock Treatment")) {
                                            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Ayesha1.ShockTreatment")));
                                        }
                                        else if(proc.getName().equals("Cardio Pulmonary Resuscitation")) {
                                            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Ayesha2.CPR")));
                                        }
                                        else if(proc.getName().equals("Heimlich Maneuver")) {
                                            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Ayesha3.Choking")));
                                        }
                                        else if(proc.getName().equals("Recovery Position")) {
                                            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Ayesha4.RecoveryPosition")));
                                        }
                                        else
                                            context.startActivity(new Intent(context.getPackageManager().getLaunchIntentForPackage("com.Ayesha2.CPR")));
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
}
