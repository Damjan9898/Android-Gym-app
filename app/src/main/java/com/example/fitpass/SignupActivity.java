package com.example.fitpass;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    TextView ispis;
    Button buttonSignup, buttonHaveAccount;
    EditText inputName, inputLastName, inputEmail4, inputPassword2;
    ImageView imageProfile;
    String pickImagePath = "";
    String imagePath = "";
    String cropImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ispis = (TextView) findViewById(R.id.ispis);
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonHaveAccount = findViewById(R.id.buttonHaveAccount);
        inputName = findViewById(R.id.inputName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail4 = findViewById(R.id.inputEmail4);
        inputPassword2 = findViewById(R.id.inputPassword2);
        imageProfile = findViewById(R.id.imageProfile);

        DataBase db = new DataBase(this);


        //Izbacivanje dijaloga prilikom biranja profilne slike (kamera ili galerija)
        imageProfile.setOnClickListener(view -> {
            ImagePickBottomDialog bottomDialog = new ImagePickBottomDialog(type -> {
                if (type == 0)
                    pickCamera();
                else
                    pickGallery();

            });
            bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
        });

        //Signup
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                errorMessage("");

                String ime = inputName.getText().toString();
                String prezime = inputLastName.getText().toString();
                String mail = inputEmail4.getText().toString();
                String lozinka = inputPassword2.getText().toString();

                //Korisnik nije popunio sva polja
                if (ime.isEmpty() || prezime.isEmpty() || mail.isEmpty() || lozinka.isEmpty()) {
                    errorMessage("You need to fill in all the fields");
                } else {
                    //Korisnik nije izabrao profilnu sliku
                    if (pickImagePath.isEmpty()) {
                        errorMessage("Please set profile image");
                    } else {
                        //Validacija tekstualnih vrednosti
                        if (ime.matches("[A-Z][a-z]+") && prezime.matches("[A-Z][a-z]+") && mail.matches("^(.+)@(.+)$")) {

                            //Profil sa datim email-om vec postoji
                            if (ifEmailExists(mail)) {
                                errorMessage("Account with this email already exists");
                            } else {
                                //Uspesno kreiran account
                                Toast.makeText(SignupActivity.this.getApplicationContext(), "Account made", Toast.LENGTH_SHORT).show();
                                addSomeUsers(ime, inputLastName.getText().toString(), inputEmail4.getText().toString(), inputPassword2.getText().toString(), imagePath, 0, "/");
                                errorMessage("");

                                startActivity(new Intent(v.getContext(), LoginActivity.class));
                            }
                        } else {
                            //Ako nije ispunjena validacija proveravam koje polje nije ispunilo uslov i ispisujem adekvatnu error poruku
                            if (!ime.matches("[A-Z][a-z]+")) {
                                errorMessage("First name is not in the right format");
                            } else if (!prezime.matches("[A-Z][a-z]+")) {
                                errorMessage("Last name is not in the right format");
                            } else {
                                errorMessage("Email is not in the right format");
                            }
                        }
                    }
                }
            }
        });

        //Odlazak na Login u slucaju da imam account
        buttonHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), LoginActivity.class));
            }
        });


    }


    //Biranje slike iz galerije
    private void pickGallery() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            pickImageActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    //Slika iz kamere
    private void pickCamera() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            File newDir = new File(getFilesDir().toString());
                            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis());
                            pickImagePath = newDir.getPath() + File.separator + "Img_" + timeStamp + ".png";
                            Log.e("pickCamera", "imagePath==>> " + pickImagePath);


                            if (!newDir.exists()) newDir.mkdirs();
                            Uri uri =
                                    FileProvider.getUriForFile(SignupActivity.this, getPackageName() + ".provider", new File(pickImagePath));

                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            pickCameraActivityResultLauncher.launch(cameraIntent);

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        pickImagePath = ImageFilePath.getPath(this, result.getData().getData());
                        Log.e("pickImageA", "imagePath==>> " + pickImagePath);
                        openCropImage();
//                        showUserImage();
                    }
                }
            });

    ActivityResultLauncher<Intent> pickCameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    scanFile();
                    File file = new File(pickImagePath);
                    if (!file.exists()) {
                        pickImagePath = "";
                    }
                    openCropImage();
//                    showUserImage();
                } else {
                    File file = new File(pickImagePath);
                    if (file.exists()) {
                        file.delete();
                        scanFile();
                    }
                    imagePath = "";
                    showUserImage();
                }

            });


    //Kropovanje slike nakon odabira iz galerije/kamere
    private void openCropImage() {

        UCrop.Options options = new UCrop.Options();
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.orange));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.orange));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.white));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.white));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.white));
//

        cropImagePath =
                getCacheDir().getPath() + File.separator + "Img_" + System.currentTimeMillis() + ".png";

        UCrop.of(Uri.fromFile(new File(pickImagePath)), Uri.fromFile(new File(cropImagePath)))
                .withAspectRatio(1f, 1f)
                .withOptions(options)
                .start(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Log.e("","cropImagePath=>> " + cropImagePath);
            imagePath  = ImageCompression.compressImage(cropImagePath, this, false);
            showUserImage();
            Log.e("onActivityResult", "resultUri ==>> $resultUri");
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }


    private void scanFile() {
        MediaScannerConnection.scanFile(SignupActivity.this, new String[]{pickImagePath}, null, (path, uri) -> {
            // Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
        });
    }

    private void showUserImage() {
        if (cropImagePath.isEmpty())
            imageProfile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_add_a_photo_24));
        else
            Glide.with(this).load(cropImagePath)
                    .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_baseline_add_a_photo_24))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageProfile);
    }


    //Prikaz error poruke nakon validacije
    private void errorMessage(String message) {
        ispis.setText(message);
    }

    //Provera jel uneti email postoji u bazi
    private boolean ifEmailExists(String email) {
        boolean emailExists = false;
        DataBase db = new DataBase(this);
        List<UserModel> lista = db.getAllUsers();
        for (UserModel item : lista) {
            if (item.getEmail().equals(email)) {
                emailExists = true;
                break;
            }
        }
        return emailExists;

    }

    //Dodavanje usera nakon uspesnog signup-a
    private void addSomeUsers(String firstname, String lastname, String email, String password, String imagePath, int entries, String expiring) {
        DataBase db = new DataBase(this);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagePath);
            byte[] image = new byte[fis.available()];
            fis.read(image);
            db.addUser(firstname, lastname, email, password, image, entries, expiring);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}