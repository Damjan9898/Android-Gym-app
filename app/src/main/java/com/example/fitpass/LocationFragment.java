package com.example.fitpass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class LocationFragment extends Fragment {

    View view;
    TextView textNoSearch;
    SearchView searchView;
    RelativeLayout popupBlack;
    Button buttonFilterOpen;
    Boolean noItemsSearch;
    String cityFilterValue = "";
    String facilityFilterValue = "";
    Boolean radioButtonValue = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_location, container, false);

        textNoSearch = view.findViewById(R.id.textNoSearch);
        searchView = view.findViewById(R.id.searchView);
        searchView.setIconified(false);
        textNoSearch.setVisibility(View.GONE);
        buttonFilterOpen = view.findViewById(R.id.buttonFilterOpen);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                initPost(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    initPost("");
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                initPost("");
                return false;
            }
        });


        initPost("");

        initComponents();


        return view;

    }

    private void initComponents() {

        //Otvaranje i zatvaranje popup-a
        Button buttonGymBack = view.findViewById(R.id.buttonGymBack);
        popupBlack = view.findViewById(R.id.popupBlack);
        popupBlack.setVisibility(View.GONE);

        buttonFilterOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupBlack.setVisibility(View.VISIBLE);
            }
        });

        buttonGymBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupBlack.setVisibility(View.GONE);
            }
        });

        //Drop down za city i facility

        Spinner cityFilter = view.findViewById(R.id.cityFilterSpinner);
        Spinner facFilterSpinner = view.findViewById(R.id.facFilterSpinner);

        ArrayList<String> listCity = new ArrayList<>();
        listCity.add("Select city...");
        listCity.add("Belgrade");
        listCity.add("Novi Sad");
        listCity.add("Kragujevac");
        listCity.add("Niš");

        ArrayList<String> listFacility = new ArrayList<>();
        listFacility.add("Select facility...");
        listFacility.add("Swimming pool");
        listFacility.add("Yoga");
        listFacility.add("Gym");
        listFacility.add("For children");

        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, listCity);
        ArrayAdapter<String> adapterFacility = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, listFacility);

        cityFilter.setAdapter(adapterCity);
        facFilterSpinner.setAdapter(adapterFacility);


        //Apply filter dugme

        Button buttonSubmitFilter = view.findViewById(R.id.buttonSubmitFilter);
        Button buttonResetFilter = view.findViewById(R.id.buttonResetFilter);
        RadioButton radioButtonReview = view.findViewById(R.id.radioButtonReview);




        buttonSubmitFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"Select city...".equals(cityFilter.getSelectedItem().toString())){
                    cityFilterValue = cityFilter.getSelectedItem().toString().trim();
                }else{
                    cityFilterValue = "";
                }
                if(!"Select facility...".equals(facFilterSpinner.getSelectedItem().toString())){
                    facilityFilterValue = facFilterSpinner.getSelectedItem().toString().trim();
                }else{
                    facilityFilterValue = "";
                }
                if(radioButtonReview.isChecked()){
                    radioButtonValue = true;
                }

                filterPost(cityFilterValue, facilityFilterValue, radioButtonValue);
                popupBlack.setVisibility(View.GONE);

            }
        });

        buttonResetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityFilter.setSelection(0);
                facFilterSpinner.setSelection(0);
                radioButtonReview.setChecked(false);
                popupBlack.setVisibility(View.GONE);
                initPost("");

            }
        });

    }


    private void initPost(String arr) {

        noItemsSearch = true;

        //DEO KOJI SAM OBRISAO
        LinearLayout locationScrollView = view.findViewById(R.id.locationScrollView);

        locationScrollView.removeAllViews();

        LayoutInflater inflater = (LayoutInflater)LocationFragment.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        API.getJSON("https://63c96fcc904f040a965e3c79.mockapi.io/fitpass/gyms", new ReadDataHandler(){

            @Override
            public void handleMessage(@NonNull Message msg) {

                String odgovor = getJson();

                try{

                    JSONArray array = new JSONArray(odgovor);
                    LinkedList<PostModel> posts = PostModel.parseJSONArray(array);

                    for( PostModel p : posts ){

                        if(p.getName().toLowerCase().contains(arr)){

                            textNoSearch.setVisibility(View.GONE);

                            ConstraintLayout item = (ConstraintLayout) inflater.inflate(R.layout.single_location, null);

                            TextView textLocationTitle  = item.findViewById(R.id.textLocationTitle);
                            textLocationTitle.setText(p.getName());

                            TextView textLocationAddress  = item.findViewById(R.id.textLocationAddress);
                            textLocationAddress.setText(p.getAddress());

                            TextView textLocationReview  = item.findViewById(R.id.textLocationReview);
                            textLocationReview.setText(Double.toString(p.getReview()));


                            ImageView imageLocation = item.findViewById(R.id.imageLocation);
                            Picasso.get().load(p.getImg()).fit().centerCrop().into(imageLocation);

                            CardView borderLocation = item.findViewById(R.id.borderLocation);

                            borderLocation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), OnePlaceActivity.class);
                                    intent.putExtra("id", p.getId());
                                    startActivity(intent);
                                }
                            });
                            noItemsSearch = false;

                            locationScrollView.addView(item);
                        }

                    }

                    if(noItemsSearch){
                        textNoSearch.setVisibility(View.VISIBLE);
                    }

                }catch(Exception e){

                }



            }
        });

    }




    private void filterPost(String city, String facility, Boolean review) {

        noItemsSearch = true;

        //DEO KOJI SAM OBRISAO
        LinearLayout locationScrollView = view.findViewById(R.id.locationScrollView);

        locationScrollView.removeAllViews();

        LayoutInflater inflater = (LayoutInflater)LocationFragment.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        API.getJSON("https://63c96fcc904f040a965e3c79.mockapi.io/fitpass/gyms", new ReadDataHandler(){

            @Override
            public void handleMessage(@NonNull Message msg) {

                String odgovor = getJson();

                try{

                    JSONArray array = new JSONArray(odgovor);

                    LinkedList<PostModel> postsSortByReview = PostModel.parseJSONArray(array);

                    if(review){
                        PostModel postReview;

                        //Sortiram posts po review
                            for (int i = 0; i < postsSortByReview.size(); i++) {
                                for (int j = i + 1; j < postsSortByReview.size(); j++) {
                                    if (postsSortByReview.get(i).getReview() < postsSortByReview.get(j).getReview()) {
                                        postReview = postsSortByReview.get(i);
                                        postsSortByReview.set(i, postsSortByReview.get(j));
                                        postsSortByReview.set(j, postReview);
                                    }
                                }
                            }

                    }


                    for( PostModel p : postsSortByReview ){

                        if(p.getCity().trim().contains(city) && p.getType().trim().contains(facility)){

                            textNoSearch.setVisibility(View.GONE);

                            ConstraintLayout item = (ConstraintLayout) inflater.inflate(R.layout.single_location, null);

                            TextView textLocationTitle  = item.findViewById(R.id.textLocationTitle);
                            textLocationTitle.setText(p.getName());

                            TextView textLocationAddress  = item.findViewById(R.id.textLocationAddress);
                            textLocationAddress.setText(p.getAddress());

                            TextView textLocationReview  = item.findViewById(R.id.textLocationReview);
                            textLocationReview.setText(Double.toString(p.getReview()));


                            ImageView imageLocation = item.findViewById(R.id.imageLocation);
                            Picasso.get().load(p.getImg()).fit().centerCrop().into(imageLocation);

                            CardView borderLocation = item.findViewById(R.id.borderLocation);

                            borderLocation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), OnePlaceActivity.class);
                                    intent.putExtra("id", p.getId());
                                    startActivity(intent);
                                }
                            });

                            noItemsSearch = false;

                            locationScrollView.addView(item);
                        }

                    }

                    if(noItemsSearch){
                        textNoSearch.setVisibility(View.VISIBLE);
                    }

                }catch(Exception e){

                }



            }
        });

    }



}
