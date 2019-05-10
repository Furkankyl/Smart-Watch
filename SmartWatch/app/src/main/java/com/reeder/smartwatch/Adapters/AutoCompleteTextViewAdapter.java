package com.reeder.smartwatch.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteTextViewAdapter extends ArrayAdapter<Doctor> {
    private List<Doctor> doctorListFull;

    public AutoCompleteTextViewAdapter(Context context, List<Doctor> doctorList) {
        super(context, 0, doctorList);
        doctorListFull = new ArrayList<>(doctorList);

    }

    @NonNull
    @Override
    public Filter getFilter() {
        return doctorFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.family_row_layout, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        ImageView imageView = convertView.findViewById(R.id.imageViewPhoto);
        textViewName.setText(getItem(position).getName());

        Picasso.get().load(getItem(position).getPhotoUrl()).into(imageView);


        return convertView;

    }

    private Filter doctorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<Doctor> suggestions = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                suggestions.addAll(doctorListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Doctor item : doctorListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Doctor) resultValue).getName();
        }
    };
}
