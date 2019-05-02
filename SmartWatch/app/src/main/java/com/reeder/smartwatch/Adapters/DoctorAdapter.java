package com.reeder.smartwatch.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.R;

import java.util.List;

public class DoctorAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Doctor> doctorList;
    private FragmentManager fragmentManager;
    public DoctorAdapter(List<Doctor> doctorList, Context context, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.doctorList = doctorList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return doctorList.size();
    }

    @Override
    public Object getItem(int i) {
        return doctorList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row_view;
        row_view= layoutInflater.inflate(R.layout.family_simple_layout,null);

        final TextView textViewPersonName = (TextView) row_view.findViewById(R.id.textViewPersonName1);
        TextView textViewExplonation = (TextView) row_view.findViewById(R.id.textViewPersonExplonation1);
        final ImageView imageViewPerson = (ImageView) row_view.findViewById(R.id.imageViewPerson1);
        Doctor doctor = doctorList.get(i);
        textViewPersonName.setText(doctor.getName());
        textViewExplonation.setText(doctor.getBio());

        /*row_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager
                        .beginTransaction()
                        .addSharedElement(imageViewPerson, "imageViewPerson")
                        .addSharedElement(textViewPersonName, "textViewPersonName")
                        .setCustomAnimations(R.anim.fade_in_animation,R.anim.fade_out_animation)
                        .replace(R.id.frameLayout, new FragmentDoctorDetail())
                        .addToBackStack(null)
                        .commit();
            }
        });
        */
        return row_view;
    }
}
