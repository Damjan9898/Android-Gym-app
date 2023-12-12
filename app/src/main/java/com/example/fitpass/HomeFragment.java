package com.example.fitpass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.LinkedList;

public class HomeFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        //Pozivam API koji vraca sve popular sportske objekte
        initPost();

        //Prikazujem sve popuste sa logoima
        getDiscounts();

        //Prikazujem kartice kategorija
        getFacilities();


        return view;

    }




    private void initPost() {
        LinearLayout mainScrollView = view.findViewById(R.id.mainScrollView);

        LayoutInflater inflater = (LayoutInflater)HomeFragment.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        API.getJSON("https://63c96fcc904f040a965e3c79.mockapi.io/fitpass/gyms", new ReadDataHandler(){

            @Override
            public void handleMessage(@NonNull Message msg) {

                String odgovor = getJson();

                try{

                    JSONArray array = new JSONArray(odgovor);
                    LinkedList<PostModel> posts = PostModel.parseJSONArray(array);

                    for( PostModel p : posts ){
                        //Proveravam ocenu od review-a dobijen iz API-ja
                        if(p.getReview() >= 9.0){
                            ConstraintLayout item = (ConstraintLayout) inflater.inflate(R.layout.main_item, null);

                            TextView textName  = item.findViewById(R.id.textName);
                            textName.setText(p.getName());

                            ImageView imageImage = item.findViewById(R.id.imageImage);
                            Picasso.get().load(p.getImg()).fit().centerCrop().into(imageImage);

                            //Pozivam Activity koji predstavlja 1 dati objekat sa odredjenim ID
                            imageImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), OnePlaceActivity.class);
                                    intent.putExtra("id", p.getId());
                                    startActivity(intent);
                                }
                            });

                            //Dodajem karticu u scroll view
                            mainScrollView.addView(item);
                        }

                    }

                }catch(Exception e){

                }



            }
        });

    }


    //Prikaz popusta
    private void getDiscounts() {

        LinkedList<Discount> lista = DiscountList.getMyDiscounts();
        LinearLayout discountScrollView = view.findViewById(R.id.discountScrollView);

        LayoutInflater inflater = (LayoutInflater)HomeFragment.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for (Discount discount : lista) {

            ConstraintLayout item = (ConstraintLayout) inflater.inflate(R.layout.single_discount, null);

            TextView textDiscount = item.findViewById(R.id.textFacility);
            textDiscount.setText(discount.getDiscount());

            ImageView imageDiscount = item.findViewById(R.id.imageFacility);

            //Dodajem odredjenu sliku iz drawable foldera u zavisnosti od firme
            switch ( discount.getFirma() ) {
                case "nike":
                    imageDiscount.setImageResource(R.drawable.nike);
                    break;
                case "a1":
                    imageDiscount.setImageResource(R.drawable.a1);
                    break;
                case "adidas":
                    imageDiscount.setImageResource(R.drawable.adidas);
                    break;
                case "puma":
                    imageDiscount.setImageResource(R.drawable.puma);
                    break;
            }
            //Ubacujem karticu u scroll view
            discountScrollView.addView(item);

        }
    }


    //Prikaz svih kategorija sportskih objekata
    private void getFacilities() {

        LinkedList<Facility> lista = FacilityList.getMyFacilities();
        LinearLayout facilityScrollView = view.findViewById(R.id.facilityScrollView);

        LayoutInflater inflater = (LayoutInflater)HomeFragment.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for (Facility facility : lista) {

            ConstraintLayout item = (ConstraintLayout) inflater.inflate(R.layout.single_facility, null);

            TextView textFacility= item.findViewById(R.id.textFacility);
            textFacility.setText(facility.getName());

            ImageView imageFacility = item.findViewById(R.id.imageFacility);

            //Prikaz odredjene slike u zavisnosti od kategorije objekta
            switch ( facility.getName()) {
                case "Swimming pool":
                    imageFacility.setImageResource(R.drawable.pool);
                    break;
                case "Yoga":
                    imageFacility.setImageResource(R.drawable.yoga);
                    break;
                case "Gym":
                    imageFacility.setImageResource(R.drawable.gym);
                    break;
                case "For children":
                    imageFacility.setImageResource(R.drawable.children);
                    break;
            }


            imageFacility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Fragmentu Locations saljem podatak kategorije objekta koji koristim za filtriranje lokacija
                    SharedPreferences sharedPref = view.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("locationfacility", facility.getName());
                    editor.apply();

                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new LocationFragment()).commit();

                }
            });


            // Ubacujem karticu u scroll view
            facilityScrollView.addView(item);

        }


    }


}
