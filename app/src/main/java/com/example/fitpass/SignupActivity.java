package com.example.fitpass;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SignupActivity extends AppCompatActivity {

    TextView ispis;
    Button buttonSignup, buttonHaveAccount;
    EditText inputName, inputLastName, inputEmail4, inputPassword2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ispis = (TextView)findViewById(R.id.ispis);
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonHaveAccount = findViewById(R.id.buttonHaveAccount);
        inputName = findViewById(R.id.inputName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail4 = findViewById(R.id.inputEmail4);
        inputPassword2 = findViewById(R.id.inputPassword2);

        DataBase db = new DataBase(this);

//        db.deleteUser(1);


        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                errorMessage("");

                String ime = inputName.getText().toString();
                String prezime = inputLastName.getText().toString();
                String mail = inputEmail4.getText().toString();
                String lozinka = inputPassword2.getText().toString();


                if(ime.isEmpty() || prezime.isEmpty() || mail.isEmpty() || lozinka.isEmpty()){
                    errorMessage("You need to fill in all the fields");
                }else{
                    if(ime.matches("[A-Z][a-z]+") && prezime.matches("[A-Z][a-z]+") && mail.matches("^(.+)@(.+)$") ){

                        if(ifEmailExists(mail)){
                            errorMessage("Account with this email already exists");
                        }else{
                            Toast.makeText(SignupActivity.this.getApplicationContext(), "Account made", Toast.LENGTH_SHORT).show();



                            addSomeUsers(ime, inputLastName.getText().toString(), inputEmail4.getText().toString(), inputPassword2.getText().toString());



                            errorMessage("");
                        }


                    }else{

                        if(!ime.matches("[A-Z][a-z]+")){
                            errorMessage("First name is not in the right format");
                        }else if(!prezime.matches("[A-Z][a-z]+")){
                            errorMessage("Last name is not in the right format");
                        }else{
                            errorMessage("Email is not in the right format");
                        }


                    }
                }


            }

        });


        buttonHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), LoginActivity.class));
            }
        });


    }



    private void errorMessage(String message){
        ispis.setText(message);
    }

    private boolean ifEmailExists(String email){
        boolean emailExists = false;
        DataBase db = new DataBase(this);
        List<UserModel> lista = db.getAllUsers();
        for(UserModel item : lista){
            if (item.getEmail().equals(email)) {
                emailExists = true;
                break;
            }
        }
        return emailExists;

    }




    private void addSomeUsers(String firstname, String lastname, String email, String password){
        DataBase db = new DataBase(this);

        db.addUser(firstname, lastname, email, password);
    }


    //
//    private void showAllUsers(){
//        LinearLayout contacts = findViewById(R.id.contacts);
//        DataBase db = new DataBase(this);
//        List<UserModel> lista = db.getAllUsers();
//        for(UserModel item : lista){
//            contacts.addView(new SingleUserItem(this, item));
//        }
//    }
//
//    private void showOneContact(){
//        LinearLayout contacts = findViewById(R.id.contacts);
//        DataBase db = new DataBase(this);
//        UserModel item = db.getUserById(1);
//        contacts.addView(new SingleUserItem(this, item));
//    }
//
//    private void initComponents() {
//
//        showAllUsers();
//    }
}