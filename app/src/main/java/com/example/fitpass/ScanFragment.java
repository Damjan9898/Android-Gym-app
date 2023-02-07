package com.example.fitpass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class ScanFragment extends Fragment{

    Button btn_scan;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        btn_scan = (Button) view.findViewById(R.id.btn_scan);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });



        return view;

    }

    private void scanCode() {

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }


    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if(result.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(ScanFragment.this.getContext());
            builder.setTitle("Result");
            SharedPreferences sharedPref = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
//            builder.setMessage(sharedPref.getString("user_id", ""));
//            builder.setMessage(result.getContents());
            String x = "Damjan\nBelojevic";
            builder.setMessage(x);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();

                }
            }).show();
        }

    });


}
