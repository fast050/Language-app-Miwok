package com.fast050.miwokapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FamilyMembers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new FamilyMembersFragment()).commit();


    }


}