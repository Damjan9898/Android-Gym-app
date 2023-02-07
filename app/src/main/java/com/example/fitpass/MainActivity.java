package com.example.fitpass;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Button buttonLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonLogout = findViewById(R.id.buttonLogout);

        //Deo za fragmente:

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

    }

    //Za fragmente
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_location:
                            selectedFragment = new LocationFragment();
                            break;
                        case R.id.nav_scan:
                            selectedFragment = new ScanFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };




    @Override
    protected void onResume() {

        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        if(sharedPref.getString("user_id", "").isEmpty()){
            startActivity(new Intent(this, LoginActivity.class));
        }

//        Toast.makeText(this, sharedPref.getString("user_id", ""), Toast.LENGTH_SHORT).show();



    }
}