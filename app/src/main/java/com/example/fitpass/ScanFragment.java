package com.example.fitpass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class ScanFragment extends Fragment{

    Button btn_scan;
    View view;
    UserModel currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_scan, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Uzimam vrednost trenutnog Usera iz Shared preferences i dodeljujem ga varijabli currentUser
        if(sharedPref.getString("user_id", "") != ""){
            int identification  = Integer.parseInt(sharedPref.getString("user_id", ""));
            currentUser = showUser(identification);
        }

        btn_scan = (Button) view.findViewById(R.id.btn_scan);

        // Prikazivanje Scan-a QR koda
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });



        return view;

    }

    // Poziv QR scan-a
    private void scanCode() {

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    // Provera da li je QR kod dobar i prikaz poruke
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if(result.getContents() != null){

            AlertDialog.Builder builder = new AlertDialog.Builder(ScanFragment.this.getContext());
            //Ispravan QR kod
            if(result.getContents().equals("1")){
                // Proveravam da li User ima uplacenu clanarinu, ako nema neuspesno je skeniranje i izlazi dijalog
                if(currentUser.getEntries() == 0) {
                    builder.setTitle("Scan failed");
                    builder.setMessage("You have no entries left. Please pay the membership fee.");

                    builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    }).show();
                }else{

                    // Uspesno skeniranje i korisnik ima uplacenu clanarinu

                    LayoutInflater inflater = (LayoutInflater)ScanFragment.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View oneView = inflater.inflate(R.layout.profile_image, null);

                    // Prikaz informacija current User-a u dijalogu
                    try{
                        ImageView userImage = oneView.findViewById(R.id.singleProfileImage);
                        TextView userName = oneView.findViewById(R.id.textProfileName);

                        String profileFullName = currentUser.getFirst_name() + " " + currentUser.getLast_name();
                        userName.setText(profileFullName);

                        userImage.setImageBitmap(currentUser.getUserImage());
                        ImageView image = new ImageView(getContext());
                        image.setImageBitmap(currentUser.getUserImage());

                        builder.setView(oneView);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    }).show();

                    // Update u bazi za datog usera, smanjujem broj termina za 1
                    setTimeout(() -> scanUserUpdate(), 200);

                }

            }else{
                // Nije ispravan QR kod, izlazi dijalog

                builder.setTitle("Scan failed");
                builder.setMessage("There was a problem with code scanning. Please try again.");

                builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).show();
            }


        }

    });

    //Dolazak do usera po ID
    private UserModel showUser(int id){
        DataBase db = new DataBase(view.getContext());

        UserModel item = db.getUserById(id);

        return item;

    }

    // Update Usera nakon skeniranja QR koda
    private void updateUser(int id, String first_name, String last_name, String email, String password, Bitmap userImage, int entries, String expiring){
        DataBase db = new DataBase(view.getContext());
        db.editUser(id, first_name, last_name, email, password, userImage, entries, expiring);

    }


    private void scanUserUpdate(){
        int newEntries = currentUser.getEntries() - 1;

        // U slucaju da je User-u ostao samo 1 termin, nakon sto ga iskoristi umesto datuma isteka termina stavljam "/"
        if(currentUser.getEntries() == 1){
            updateUser(currentUser.getUser_id(), currentUser.getFirst_name(), currentUser.getLast_name(), currentUser.getEmail(), currentUser.getPassword(), currentUser.getUserImage(), newEntries, "/");
        }else{
            updateUser(currentUser.getUser_id(), currentUser.getFirst_name(), currentUser.getLast_name(), currentUser.getEmail(), currentUser.getPassword(), currentUser.getUserImage(), newEntries, currentUser.getExpiring());
        }
    }

    // TimeOut od 200 milisekundi
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }



}
