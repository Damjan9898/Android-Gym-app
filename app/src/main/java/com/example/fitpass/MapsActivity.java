package com.example.fitpass;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.fitpass.databinding.ActivityMapsBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Location currentLocation = null;
    LocationManager locationManager;
    LocationListener locationListener;
    int initialLocationFind = 0;
    private ArrayList<String> listaNazivaLokacija = new ArrayList<>();


    private ArrayList<LatLng> listaLokacija = new ArrayList<>();


    private boolean areLocationTheSame(Location a, Location b){
        return a.getLatitude() == b.getLatitude() && a.getLongitude() == b.getLongitude();
    }

    public void centreMapLocation(Location location, String title){
        if(currentLocation != null && areLocationTheSame(currentLocation, location)){
            return;
        }
        if(location == null){
            return;
        }

        // Dolazak do trenutne lokacije User-a i priblizavanje kamere ka njegovoj lokaciji
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

        // Dodavanje Naziva i adrese lokacije u mapu iz API-ja
        for(int i = 0; i < listaLokacija.size(); i++){
            mMap.addMarker(new MarkerOptions().position(listaLokacija.get(i)).title(listaNazivaLokacija.get(i)));
        }

        // Priblizavanje Mape trenutnom useru
        mMap.animateCamera( CameraUpdateFactory.zoomTo(11.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        currentLocation = location;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000,0,locationListener);

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000,0,locationListener);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Poziv ka apiju, dolazak do svih lokacija
        filterPost();

        Button buttonMapClose = findViewById(R.id.buttonMapClose);

        // Gasenje mape, idem na prethodni fragment
        buttonMapClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        filterPost();
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

            Intent intent = getIntent();

            if(intent.getIntExtra("Place Number", 0) == 0){
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {


                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        if(initialLocationFind == 0){
                            centreMapLocation(location, "Your location");
                            initialLocationFind++;
                        }else{
                            setTimeout(() ->  centreMapLocation(location, "Your location"), 1000000);
                        }

                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {

                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {

                    }
                };

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
                }else{
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

            }


    }



    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }



    //Poziv ka API-ju
    private void filterPost() {


        LayoutInflater inflater = (LayoutInflater)MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        API.getJSON("https://63c96fcc904f040a965e3c79.mockapi.io/fitpass/gyms", new ReadDataHandler(){

            @Override
            public void handleMessage(@NonNull Message msg) {

                String odgovor = getJson();

                try{

                    JSONArray array = new JSONArray(odgovor);

                    LinkedList<PostModel> postsSortByReview = PostModel.parseJSONArray(array);

                    for( PostModel p : postsSortByReview ){


                        // Uzimanje Lat I Lng vrednosti iz API-ja
                        LatLng Lokacija = new LatLng(p.getLat(), p.getLng());
                        listaLokacija.add(Lokacija);
                        // Uzimanje Naziva objekta i adrese iz API-ja
                        String fullAddress = p.getName() + ", " + p.getAddress();
                        listaNazivaLokacija.add(fullAddress);

                    }

                }catch(Exception e){

                }



            }
        });

    }
}