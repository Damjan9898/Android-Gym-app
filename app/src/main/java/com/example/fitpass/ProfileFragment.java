package com.example.fitpass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    Button buttonLogout;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        buttonLogout = view.findViewById(R.id.buttonLogout);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("user_id", "");
                editor.apply();

                startActivity(new Intent(v.getContext(), MainActivity.class));

            }
        });

        return view;
    }
}
