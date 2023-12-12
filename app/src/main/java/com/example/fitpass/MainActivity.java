package com.example.fitpass;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
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


        //Navigacija na dnu koja sluzi da menja fragmente
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Prvo prikazujem Home fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

    }

    //Klikom na odredjeno dugme iz menija zamenjujem fragment container sa datim fragmentom
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



    //Ukoliko nisam loginovan (nije setovan user_id) odmah se vracam na Login activity
    //U suprotnom ostajem u Main Activity-ju
    @Override
    protected void onResume() {

        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        if(sharedPref.getString("user_id", "").isEmpty()){
            startActivity(new Intent(this, LoginActivity.class));
        }


    }

    //Triger za dialog u slucaju da izlazim iz aplikacije
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leaving the app")
                .setMessage("Do you want to leave the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        finish();
                        System.exit(0);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}