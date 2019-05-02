package com.reeder.smartwatch.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reeder.smartwatch.Adapters.DoctorAdapter;
import com.reeder.smartwatch.Model.Doctor;
import com.reeder.smartwatch.R;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private ListView listViewDoctors;
    private List<Doctor> doctorList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_doctors);
        listViewDoctors = (ListView) findViewById(R.id.listViewMember);
        doctorList = new ArrayList<>();
        doctorList.add(new Doctor("John Doe","Kalp cerrahı",""));
        doctorList.add(new Doctor("Ervin Howell","Beyin cerrahı",""));
        doctorList.add(new Doctor("Clementine Bauch","Genel cerrahi",""));
        DoctorAdapter doctorAdapter = new DoctorAdapter(doctorList,TestActivity.this,getSupportFragmentManager());
        listViewDoctors.setAdapter(doctorAdapter);
        Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        listViewDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(TestActivity.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
