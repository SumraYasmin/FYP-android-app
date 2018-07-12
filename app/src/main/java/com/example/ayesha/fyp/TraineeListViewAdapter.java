package com.example.ayesha.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by Ayesha on 3/27/2018.
 */

public class TraineeListViewAdapter extends ArrayAdapter<Trainee>
{

    public TraineeListViewAdapter(Context context, int textViewResourceId, List<Trainee> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View curView = convertView;

        if (curView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            curView = vi.inflate(R.layout.component_list, null);
        }

        final Trainee trainee = getItem(position);

        //sets the layouts for the item
        TextView name = (TextView) curView.findViewById(R.id.name);
        name.setText(trainee.getName());

        final String id = trainee.getId();
        curView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString("UserID", id);

                        Intent i = new Intent(getContext(), PreviewTrainee.class);
                        i.putExtras(bundle);
                        getContext().startActivity(i);

                        ((Activity)getContext()).finish();
                    }
                }
        );

        //if(cp.getImage() == null) {
            String Name = trainee.getName();
            String Intials = "";
            int secondIndex = getIndexof2ndIntial(Name);
            if (secondIndex != -1) {
                Intials = Intials + Character.toUpperCase(Name.charAt(0)) + Character.toUpperCase(Name.charAt(secondIndex));
            } else
                Intials = Intials + Character.toUpperCase(Name.charAt(0));

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(trainee);

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(Intials, color); //getInitials //select color randomly
            ImageView image = (ImageView) curView.findViewById(R.id.icon_list);
            image.setImageDrawable(drawable);


        return curView;

    }

    private int getIndexof2ndIntial(String _name) {
        int i = 0;
        for (i = 0; i < _name.length(); i++) {
            if (_name.charAt(i) == ' ') {
                if (i + 1 == _name.length()) {
                    return -1;
                } else if (_name.charAt(i + 1) == ' ')
                    return -1;
                else
                    return i+1;
            }
        }
        return -1;
    }

}

