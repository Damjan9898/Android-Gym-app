package com.example.fitpass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
public class ProfileFragment extends Fragment {

    Button buttonLogout, buttonUpdateProfile;
    View view;
    UserModel currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        buttonLogout = view.findViewById(R.id.buttonLogout);

        //Dolazim do ID od trenutnog User-a
        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        if(sharedPref.getString("user_id", "") != ""){
            int identification  = Integer.parseInt(sharedPref.getString("user_id", ""));
            currentUser = showUser(identification);
        }

        // user_id postavljam na prazan String, i vracam se na Main activity koji zatim automatski prelazi na Login Activity jer je vrednost user_id prazan String
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("user_id", "");
                editor.apply();

                startActivity(new Intent(v.getContext(), MainActivity.class));

            }
        });

        buttonUpdateProfile = view.findViewById(R.id.buttonUpdateProfile);
        // Otvaram fragment Update profile
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UpdateProfileFragment()).commit();
            }
        });

        Button buttonBuyMembership = view.findViewById(R.id.buttonBuyMembership);
        // Otvaram fragment Buy Membership
        buttonBuyMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BuyMembershipFragment()).commit();
            }
        });

        initComponent();

        return view;
    }

    private void initComponent() {

        ImageView imageProf = view.findViewById(R.id.imageProf);
        imageProf.setImageBitmap(currentUser.getUserImage());

        TextView textProfName = view.findViewById(R.id.textProfName);
        String fullName = currentUser.getFirst_name() + " " + currentUser.getLast_name();
        textProfName.setText(fullName);

        TextView textProfEmail = view.findViewById(R.id.textProfEmail);
        textProfEmail.setText(currentUser.getEmail());

        TextView textEntriesValue = view.findViewById(R.id.textEntriesValue);
        textEntriesValue.setText(String.valueOf(currentUser.getEntries()));

        TextView textExpiringValue = view.findViewById(R.id.textExpiringValue);
        textExpiringValue.setText(currentUser.getExpiring());



    }


    private UserModel showUser(int id){
        DataBase db = new DataBase(view.getContext());

        UserModel item = db.getUserById(id);

        return item;

    }

    @Override
    public void onResume() {
        initComponent();
        super.onResume();

    }
}
