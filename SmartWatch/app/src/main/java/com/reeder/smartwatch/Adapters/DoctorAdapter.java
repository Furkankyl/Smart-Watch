package com.reeder.smartwatch.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.R;

import org.w3c.dom.Text;

import java.util.List;

public class DoctorAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Doctor> doctorList;

    public DoctorAdapter(List<Doctor> doctorList, Context context) {
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row_view = layoutInflater.inflate(R.layout.family_simple_layout,null);

        TextView textViewName = (TextView) row_view.findViewById(R.id.textViewPersonName);
        TextView textViewExplonation = (TextView) row_view.findViewById(R.id.textViewPersonExplonation);
        Doctor doctor = doctorList.get(i);
        textViewName.setText(doctor.getName());
        textViewExplonation.setText(doctor.getBio());
        return row_view;
    }
}
