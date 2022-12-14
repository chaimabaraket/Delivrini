package com.example.delevrini.Chef;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;

import com.example.delevrini.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChefActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);


        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        // set the bottom  navigation  item click action
        bottomNavigation.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    replace(new ChefHome());
                    break;
                case R.id.add:
                    replace(new ChefAddd());
                    break;
                case R.id.profile:
                    replace(new ChefProfil());
                    break;
            }
            return true;
        });
        // set the home fragment as the first attached fragment in the view

        bottomNavigation.setSelectedItemId(R.id.home);

    }

    // for setting the fragment in the FrameLayout
    private void replace(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commitAllowingStateLoss();

    }


}