package com.example.fitpass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.braintreepayments.cardform.view.CardForm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BuyMembershipFragment extends Fragment {

    View view;
    UserModel currentUser;
    int identification;
    int numberEntries;
    String expiringDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_buy_membership, container, false);

        //DOlazim do trenutnog User-a
        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        if(sharedPref.getString("user_id", "") != ""){
            identification  = Integer.parseInt(sharedPref.getString("user_id", ""));
            currentUser = showUser(identification);
        }

        initComponent();

        return view;
    }

    private void initComponent() {

        Button buttonBuyBack = view.findViewById(R.id.buttonBuyBack);
        // Vracanje na prethodni fragment klikom na X
        buttonBuyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        Button buttonBackProfilePay = view.findViewById(R.id.buttonBackProfilePay);
        // Vracanje na prethodni fragment klikom na dugme Back to Profile
        buttonBackProfilePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        //Odabir polja iz biblioteke za karticno placanje
        CardForm cardForm = (CardForm) view.findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(false)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")
                .setup(getActivity());

        Spinner payFilter = view.findViewById(R.id.payFilterSpinner);

        // Lista dostupnih paketa
        ArrayList<String> listPay = new ArrayList<>();
        listPay.add("Choose a package...");
        listPay.add("Studio Basic (12 entries) - 3500 rsd");
        listPay.add("Classic (16 entries) - 3600 rsd");
        listPay.add("Studio Classic (20 entries) - 4000 rsd");
        listPay.add("Studio Classic (30 entries) - 5200 rsd");

        ArrayAdapter<String> adapterPay = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, listPay);

        payFilter.setAdapter(adapterPay);

        LinearLayout linearLayoutPriceTotal = view.findViewById(R.id.linearLayoutPriceTotal);
        linearLayoutPriceTotal.setVisibility(View.GONE);

        //Provera jel je izabrao vrednost
        payFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,int position, long id) {
                LinearLayout linearLayoutPriceTotal = view.findViewById(R.id.linearLayoutPriceTotal);

                // Provera da li je izabran paket
                if(!adapter.getItemAtPosition(position).toString().equals("Choose a package...")){

                    linearLayoutPriceTotal.setVisibility(View.VISIBLE);

                    TextView textPackageValue = view.findViewById(R.id.textPackageValue);
                    textPackageValue.setText(adapter.getItemAtPosition(position).toString());

                    TextView textTotalValue = view.findViewById(R.id.textTotalValue);
                    if(adapter.getItemAtPosition(position).toString().equals("Studio Basic (12 entries) - 3500 rsd")){
                        textTotalValue.setText("3500rsd");
                        numberEntries = 12;
                    }else if(adapter.getItemAtPosition(position).toString().equals("Classic (16 entries) - 3600 rsd")){
                        textTotalValue.setText("3600rsd");
                        numberEntries = 16;
                    }else if(adapter.getItemAtPosition(position).toString().equals("Studio Classic (20 entries) - 4000 rsd")){
                        textTotalValue.setText("4000rsd");
                        numberEntries = 20;
                    }else{
                        textTotalValue.setText("5200rsd");
                        numberEntries = 30;
                    }

                }else{
                    linearLayoutPriceTotal.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        Button buttonPay = view.findViewById(R.id.buttonPay);
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editTextCSV = view.findViewById(R.id.editTextCSV);

                // U slucaju da nisu popunjena sva polja izbacujem gresku
                if(payFilter.getSelectedItem() == null || payFilter.getSelectedItem().equals("Choose a package...") || cardForm.getCardholderName().equals("") || cardForm.getCardNumber().equals("") || cardForm.getExpirationYear().equals("") || editTextCSV.getText().toString().equals("")){

                    if(payFilter.getSelectedItem() == null || payFilter.getSelectedItem().equals("Choose a package...")){
                        Toast.makeText(getContext(), "Pick a package", Toast.LENGTH_SHORT).show();

                        Toast.makeText(getContext(), "Pick a package", Toast.LENGTH_SHORT).show();
                    }else if(cardForm.getCardholderName().equals("")){
                        Toast.makeText(getContext(), "Enter the card holder name", Toast.LENGTH_SHORT).show();
                    }else if(cardForm.getCardNumber().equals("")){
                        Toast.makeText(getContext(), "Enter the card number", Toast.LENGTH_SHORT).show();
                    }else if(cardForm.getExpirationYear().equals("")){
                        Toast.makeText(getContext(), "Enter the expiration date", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "Enter the CVV number", Toast.LENGTH_SHORT).show();
                    }



                }else{

                    // Ispravno popunena sva polja i prikacuje se success dijalog

                    AlertDialog.Builder builder = new AlertDialog.Builder(BuyMembershipFragment.this.getContext());
                    builder.setTitle("Payment successful");
                    builder.setMessage("You can check your profile for new entries.");

                    builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DATE, 31);
                            Date date = cal.getTime();
                            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                            String inActiveDate = null;
                            try {
                                inActiveDate = format1.format(date);

                                expiringDate = inActiveDate;

                            } catch (ParseException e1) {

                                e1.printStackTrace();
                            }

                            // Updatujem usera, dodajem mu termine i novi datum isteka termina
                            updateUser(currentUser.getUser_id(), currentUser.getFirst_name(), currentUser.getLast_name(), currentUser.getEmail(), currentUser.getPassword(), currentUser.getUserImage(), numberEntries, expiringDate);


                            dialogInterface.dismiss();

                        }
                    }).show();
                }
            }
        });


    }

    // DOlazim do User-a preko ID
    private UserModel showUser(int id){
        DataBase db = new DataBase(view.getContext());

        UserModel item = db.getUserById(id);

        return item;

    }

    // Update Usera
    private void updateUser(int id, String first_name, String last_name,String email, String password, Bitmap userImage, int entries, String expiring){
        DataBase db = new DataBase(view.getContext());
        db.editUser(id, first_name, last_name,email, password, userImage, entries, expiring);

    }
}
