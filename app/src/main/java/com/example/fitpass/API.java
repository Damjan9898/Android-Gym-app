package com.example.fitpass;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class API {

    public static void getJSON(String url, final ReadDataHandler rdh){

        AsyncTask<String,Void,String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {

                String response = "";

                try{

                    URL link = new URL(strings[0]);

                    HttpURLConnection con = (HttpsURLConnection)link.openConnection();

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String red;

                    while (( red = br.readLine() ) != null ){

                        response += red + "\n";

                    }
                    br.close();
                    con.disconnect();


                }catch(Exception e){

                }

                return response;
            }


            @Override
            protected void onPostExecute(String response) {
                rdh.setJson(response);
                //U slucaju da je prazan string:
                rdh.sendEmptyMessage(0 );
            }
        };


        task.execute(url);


    }

}
