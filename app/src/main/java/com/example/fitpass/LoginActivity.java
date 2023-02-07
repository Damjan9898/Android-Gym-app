package com.example.fitpass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class LoginActivity extends AppCompatActivity {


    Button forgot_password;
    Button buttonCreateAccount, buttonLogin;
    EditText inputEmail, inputPassword;
    TextView errorMessage;
    int activeUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        forgot_password = findViewById(R.id.buttonForgot);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        buttonLogin = findViewById(R.id.buttonLogin);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        errorMessage = findViewById(R.id.errorMessage);

        ImageView password_eye = findViewById(R.id.imageEye);
        password_eye.setImageResource(R.drawable.password_hidden);
        password_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText)findViewById(R.id.inputPassword)).getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Ukoliko je paswword vidljiv
                    ((EditText)findViewById(R.id.inputPassword)).setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Menjnje ikonice
                    password_eye.setImageResource(R.drawable.password_hidden);
                }else{
                    ((EditText)findViewById(R.id.inputPassword)).setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password_eye.setImageResource(R.drawable.password_show);
                }
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = inputEmail.getText().toString();
                String lozinka = inputPassword.getText().toString();
                String poruka;

                poruka = checkEmailAndPassword(mail, lozinka);

                if(poruka.equals("OK")){
                    // First, get a reference to the SharedPreferences object
                    SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

                    // Next, get a reference to the SharedPreferences.Editor object
                    SharedPreferences.Editor editor = sharedPref.edit();

                    // Now, store the user ID in the SharedPreferences
                    String userId = Integer.toString(activeUserId);
                    editor.putString("user_id", userId);
                    editor.apply();

                    startActivity(new Intent(v.getContext(), MainActivity.class));



                }else{
                    errorMessage.setText(poruka);
                }


//
//                Toast.makeText(LoginActivity.this.getApplicationContext(), poruka, Toast.LENGTH_SHORT).show();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ForgotPasswordActivity.class));
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SignupActivity.class));
            }
        });



    }

    private String checkEmailAndPassword(String email, String password){

        String response = "";

        DataBase db = new DataBase(this);
        List<UserModel> lista = db.getAllUsers();
        for(UserModel item : lista){
            if(email.isEmpty() || password.isEmpty()){
                response = "You need to fill in all the fields";
                break;
            }else{
                if (item.getEmail().equals(email)) {
                    if (item.getPassword().equals(password)) {
                        response = "OK";
                        activeUserId = item.getUser_id();
                        break;
                    }else{
                        response = "Your password is incorrect";
                        break;
                    }

                }else{
                    response = "There is no account associated with this email";
                }
            }

        }
        return response;

    }


}