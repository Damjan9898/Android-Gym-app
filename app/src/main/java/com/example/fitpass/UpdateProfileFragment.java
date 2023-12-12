package com.example.fitpass;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UpdateProfileFragment extends Fragment {

    View view;
    UserModel currentUser;
    int identification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_update_profile, container, false);

        // Dolazim do trenutnog Usera
        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        if(sharedPref.getString("user_id", "") != ""){
            identification  = Integer.parseInt(sharedPref.getString("user_id", ""));
            currentUser = showUser(identification);
        }

        initComponent();

        return view;
    }

    private void initComponent() {

        EditText updateEmail = view.findViewById(R.id.updateEmail);
        updateEmail.setText(currentUser.getEmail());

        EditText updatePassword = view.findViewById(R.id.updatePassword);
        updatePassword.setText(currentUser.getPassword());

        EditText updateName = view.findViewById(R.id.updateName);
        updateName.setText(currentUser.getFirst_name());

        EditText updateLastName = view.findViewById(R.id.updateLastName);
        updateLastName.setText(currentUser.getLast_name());


        Button buttonBackProfile = view.findViewById(R.id.buttonBackProfile);
        // Vracanje nazad na Profile fragment klikom na X
        buttonBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        Button buttonUpdateBack = view.findViewById(R.id.buttonUpdateBack);
        // Vracanje nazad na Profile fragment klikom na dugme back to profile
        buttonUpdateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        Button buttonUpdate = view.findViewById(R.id.buttonUpdate);

        // Radim Update usera i izbacujem dijalog da je uspesno Update-ovan
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser(identification, updateName.getText().toString(), updateLastName.getText().toString(), updateEmail.getText().toString(), updatePassword.getText().toString(), currentUser.getUserImage(), currentUser.getEntries(), currentUser.getExpiring());

                Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();

            }
        });

    }

    // Dolazim do trenutnog usera
    private UserModel showUser(int id){
        DataBase db = new DataBase(view.getContext());

        UserModel item = db.getUserById(id);

        return item;

    }

    // Update usera uzimajuci unete vrednosti iz inputa
    private void updateUser(int id, String first_name, String last_name,String email, String password, Bitmap userImage, int entries, String expiring){
        DataBase db = new DataBase(view.getContext());
        db.editUser(id, first_name, last_name,email, password, userImage, entries, expiring);

    }
}
