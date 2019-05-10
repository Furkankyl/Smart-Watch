package com.reeder.smartwatch.Adapters;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.reeder.smartwatch.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class GenderAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<String> genderList;
    public GenderAdapter(List<String> genderList, Context context) {
        this.genderList = genderList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return genderList.size();
    }

    @Override
    public Object getItem(int i) {
        return genderList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row_view;
        row_view= layoutInflater.inflate(R.layout.simple_gender_layout,null);
        TextView textViewPersonName = (TextView) row_view.findViewById(R.id.textViewGender);
        ImageView genderImageView = (ImageView) row_view.findViewById(R.id.imageViewGender);
        textViewPersonName.setText(genderList.get(i));
        if(genderList.get(i).equals("Kadın")){
            genderImageView.setImageResource(R.drawable.ic_woman);
            textViewPersonName.setTextColor(Color.parseColor("#F05228"));
        }else{
            genderImageView.setImageResource(R.drawable.ic_man);
        }
        return row_view;
    }
}
