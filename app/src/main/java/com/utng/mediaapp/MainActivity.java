package com.utng.mediaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public boolean onSearchRequested() {
        startActivity(new Intent(this, MediaSearchActivity.class));
        return true;
    }
}