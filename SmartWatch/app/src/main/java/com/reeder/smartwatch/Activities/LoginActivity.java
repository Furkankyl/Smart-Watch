package com.reeder.smartwatch.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.reeder.smartwatch.Fragments.FastLoginFragment;
import com.reeder.smartwatch.Fragments.LoginEmailFragment;
import com.reeder.smartwatch.Helpers.ViewPagerAdapter;
import com.reeder.smartwatch.R;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginEmailFragment(),"Email");
        adapter.addFragment(new FastLoginFragment(), "Hızlı Giriş");
        viewPager.setAdapter(adapter);
    }
}
