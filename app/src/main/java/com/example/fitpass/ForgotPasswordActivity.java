package com.example.fitpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ForgotPasswordActivity extends AppCompatActivity {

    TextView errorMessage2;
    Button buttonBack;
    EditText inputEmail2;
    Button buttonResetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        findUserByEmail("damjanbelojevic98@gmail.com");


        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        buttonBack = findViewById(R.id.buttonBack);
        inputEmail2 = findViewById(R.id.inputEmail2);
        errorMessage2 = findViewById(R.id.errorMessage2);


        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String savedEmail = inputEmail2.getText().toString();
                if(savedEmail.isEmpty()){
                    errorMessage2.setText("You need to fill in all the fields");
                }else{
                    if(findUserByEmail(savedEmail)){
                        //Pronadjen email
                        errorMessage2.setText("");

                        OkHttpClient client = new OkHttpClient();

                        String url = "https://aylmao.com/index.php?email=" +  savedEmail;

                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if(response.isSuccessful()){
                                    final String myResponse = response.body().string().trim();

                                    ForgotPasswordActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateUserPasswordFromEmail(savedEmail, myResponse);
                                            Toast.makeText(ForgotPasswordActivity.this.getApplicationContext(), "Email sent", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }else{
                        //Nema tog email-a u bazi
                        errorMessage2.setText("There's no account associated with this email");
                    }
                }


            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), LoginActivity.class));
            }
        });


    }


    private boolean findUserByEmail(String email){
        DataBase db = new DataBase(this);
        UserModel item = db.getUserByEmail(email);

        if(item != null){
            return true;
        }else{
            return false;
        }

    }

    private void updateUserPasswordFromEmail(String email, String password){
        DataBase db = new DataBase(this);
        UserModel item = db.getUserByEmail(email);

        db.editUser(item.getUser_id(), item.getFirst_name(), item.getLast_name(), item.getEmail(), password);
    }


}
