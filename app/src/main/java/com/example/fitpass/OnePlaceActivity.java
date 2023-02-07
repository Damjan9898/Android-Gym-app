package com.example.fitpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.LinkedList;

public class OnePlaceActivity extends AppCompatActivity {

    String gymId;
    ProgressBar progressBar;
    TextView progressText;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_place);

        Intent intent = getIntent();
        gymId = intent.getStringExtra("id");

        initPost();

        Button buttonGymBack = findViewById(R.id.buttonGymBack);

        buttonGymBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }




    private void initPost() {

        API.getJSON("https://63c96fcc904f040a965e3c79.mockapi.io/fitpass/gyms", new ReadDataHandler(){

            @Override
            public void handleMessage(@NonNull Message msg) {

                String odgovor = getJson();

                try{

                    JSONArray array = new JSONArray(odgovor);
                    LinkedList<PostModel> posts = PostModel.parseJSONArray(array);

                    for( PostModel p : posts ){

                        if(p.getId().equals(gymId)){

                            ImageView imageGym = findViewById(R.id.imageGym);
                            Picasso.get().load(p.getImg()).fit().centerCrop().into(imageGym);

                            TextView textGymName = findViewById(R.id.textGymName);
                            textGymName.setText(p.getName());

                            TextView textGymAddress = findViewById(R.id.textGymAddress);
                            textGymAddress.setText(p.getAddress());

                            TextView textGymPhone = findViewById(R.id.textGymPhone);
                            textGymPhone.setText(p.getPhone());

                            textGymPhone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String contact = textGymPhone.getText().toString();

                                    Intent callIntent = new Intent(Intent.ACTION_CALL);

                                    callIntent.setData(Uri.parse("tel:"+contact));

                                    if(ActivityCompat.checkSelfPermission(OnePlaceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(OnePlaceActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 4001 );
                                    }

                                    startActivity(callIntent);
                                }
                            });


                            progressBar = findViewById(R.id.progress_bar);
                            progressText = findViewById(R.id.progress_bar_text);

                            double gymReview = p.getReview();

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(i <= (gymReview*10)){
                                        double iDouble = i;
                                        progressText.setText(""+iDouble/10);
                                        progressBar.setProgress(i);
                                        i++;
                                        handler.postDelayed(this, 20);
                                    }else{
                                        handler.removeCallbacks(this);
                                    }

                                }
                            }, 20);

                            TextView textGymReview = findViewById(R.id.textGymReview);
                            if(p.getReview() < 2.5){
                                textGymReview.setText("Bad!");
                            }else if(p.getReview() < 5){
                                textGymReview.setText("Good!");
                            }else if(p.getReview() < 7.5){
                                textGymReview.setText("Great!");
                            }else{
                                textGymReview.setText("Awesome!");
                            }

                            TextView textGymFacContent = findViewById(R.id.textGymFacContent);
                            textGymFacContent.setText(p.getType());


                            TextView textGymTime1 = findViewById(R.id.textGymTime1);
                            textGymTime1.setText(p.getWeekday_time());
                            TextView textGymTime2 = findViewById(R.id.textGymTime2);
                            textGymTime2.setText(p.getWeekday_time());
                            TextView textGymTime3 = findViewById(R.id.textGymTime3);
                            textGymTime3.setText(p.getWeekday_time());
                            TextView textGymTime4 = findViewById(R.id.textGymTime4);
                            textGymTime4.setText(p.getWeekday_time());
                            TextView textGymTime5 = findViewById(R.id.textGymTime5);
                            textGymTime5.setText(p.getWeekday_time());
                            TextView textGymTime6 = findViewById(R.id.textGymTime6);
                            textGymTime6.setText(p.getWeekday_time());
                            TextView textGymTime7 = findViewById(R.id.textGymTime7);
                            textGymTime7.setText(p.getSunday_time());


                            TextView textGymAboutContent = findViewById(R.id.textGymAboutContent);
                            textGymAboutContent.setText(p.getAbout_us());


                            break;


                        }

                    }

                }catch(Exception e){

                }



            }
        });

    }
}