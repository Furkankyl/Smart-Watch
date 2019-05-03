package com.reeder.smartwatch.Adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.R;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Doctor> implements View.OnClickListener{

    private List<Doctor> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView textViewPersonName;
        TextView textViewPersonExplonation;
        ImageView imageViewPerson;
    }

    public CustomArrayAdapter(List<Doctor> data, Context context) {
        super(context, R.layout.family_simple_layout, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Doctor doctor=(Doctor)object;

        switch (v.getId())
        {
            case R.id.imageViewPerson:
                Snackbar.make(v, "Release date " +doctor.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Doctor doctor = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.family_simple_layout, parent, false);
            viewHolder.textViewPersonName = (TextView) convertView.findViewById(R.id.textViewPersonName);
            viewHolder.textViewPersonExplonation = (TextView) convertView.findViewById(R.id.textViewPersonExplonation);
            viewHolder.imageViewPerson = (ImageView) convertView.findViewById(R.id.imageViewPerson);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.textViewPersonName.setText(doctor.getName());
        viewHolder.textViewPersonExplonation.setText(doctor.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}