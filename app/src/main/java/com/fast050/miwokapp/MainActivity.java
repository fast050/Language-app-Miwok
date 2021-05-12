package com.fast050.miwokapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout=findViewById(R.id.tab_layout);

        ViewPager2 viewPager2=findViewById(R.id.pagerView);

        CustomPagerAdapter customPagerAdapter=new CustomPagerAdapter(getSupportFragmentManager(),getLifecycle());

        viewPager2.setAdapter(customPagerAdapter);

        //tabs
        new TabLayoutMediator(tabLayout,viewPager2,((tab, position) ->
        {
            switch (position){
                case 0:
                tab.setText(R.string.NumbersFragment_name);
                        break;
                case 1:
                    tab.setText(R.string.FamilyMembersFragment_name);
                    break;
                case 2:
                    tab.setText(R.string.ColorsFragment_name);
                    break;
                case 3:
                    tab.setText(R.string.PhrasesFragment_name);
                    break;
            }
        })).attach();

    }
}