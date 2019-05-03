package com.reeder.smartwatch.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.Model.FamilyMember;
import com.reeder.smartwatch.R;

import java.util.List;

public class FamilyMemberAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<FamilyMember> familyMemberList;

    public FamilyMemberAdapter(List<FamilyMember> familyMemberList, Context context) {
        this.familyMemberList = familyMemberList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return familyMemberList.size();
    }

    @Override
    public Object getItem(int i) {
        return familyMemberList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row_view =null;
        row_view = layoutInflater.inflate(R.layout.doctor_row_layout,null);
        final TextView textViewPersonName = (TextView) row_view.findViewById(R.id.textViewTitle);
        TextView textViewExplonation = (TextView) row_view.findViewById(R.id.textViewContent);
        //final ImageView imageViewPerson = (ImageView) row_view.findViewById(R.id.imageViewPerson);
        FamilyMember familyMember = familyMemberList.get(i);
        textViewPersonName.setText(familyMember.getName());
        textViewExplonation.setText(familyMember.getBio());
        return row_view;
    }
}
