package com.fast050.miwokapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Phrases extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new PhrasesFragment()).commit();

        }


    }


