package com.example.fitpass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private Context context;

    private static final String DATABASE_FILE_NAME = "user_database";

    public DataBase(@NonNull Context context) {
        super(context, DATABASE_FILE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format(
                "CREATE TABLE IF NOT EXISTS %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT,%s " +
                        "TEXT,%s TEXT," +
                        "%s TEXT ,%s BLOB NOT NULL,%s INTEGER,%s TEXT)",
                UserModel.TABLE_NAME,
                UserModel.COLUMN_USER_ID,
                UserModel.COLUMN_USER_FIRSTNAME,
                UserModel.COLUMN_USER_LASTNAME,
                UserModel.COLUMN_USER_EMAIL,
                UserModel.COLUMN_USER_PASSWORD,
                UserModel.COLUMN_USER_IMAGE,
                UserModel.COLUMN_USER_ENTRIES,
                UserModel.COLUMN_USER_EXPIRING
        );
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = String.format("DROP TABLE IF EXISTS %s", UserModel.TABLE_NAME);
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    public void addUser(String firstname, String lastname, String email, String password, byte[] image, int entries, String expiring) {

        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues cv = new ContentValues();

        cv.put(UserModel.COLUMN_USER_FIRSTNAME, firstname);
        cv.put(UserModel.COLUMN_USER_LASTNAME, lastname);
        cv.put(UserModel.COLUMN_USER_EMAIL, email);
        cv.put(UserModel.COLUMN_USER_PASSWORD, password);
        cv.put(UserModel.COLUMN_USER_IMAGE, image);
        cv.put(UserModel.COLUMN_USER_ENTRIES, entries);
        cv.put(UserModel.COLUMN_USER_EXPIRING, expiring);


        db.insert((UserModel.TABLE_NAME), null, cv);

    }

    public void editUser(int userid, String firstname, String lastname, String email, String password, Bitmap userImage, int entries, String expiring) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(UserModel.COLUMN_USER_FIRSTNAME, firstname);
        cv.put(UserModel.COLUMN_USER_LASTNAME, lastname);
        cv.put(UserModel.COLUMN_USER_EMAIL, email);
        cv.put(UserModel.COLUMN_USER_PASSWORD, password);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        userImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        userImage.recycle();
        cv.put(UserModel.COLUMN_USER_IMAGE, byteArray);
        cv.put(UserModel.COLUMN_USER_ENTRIES, entries);
        cv.put(UserModel.COLUMN_USER_EXPIRING, expiring);

        db.update(UserModel.TABLE_NAME, cv, UserModel.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userid)});
    }

    public int deleteUser(int userid) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(UserModel.TABLE_NAME, UserModel.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userid)});
    }

    public UserModel getUserById(int userid) {

        Bitmap bt = null;


        SQLiteDatabase db = this.getReadableDatabase();

        // SELECT * FROM contacts WHERE contact_id = ?
        String query = String.format("SELECT * FROM %s WHERE %s = ?", UserModel.TABLE_NAME, UserModel.COLUMN_USER_ID);
        Cursor rezultat = db.rawQuery(query, new String[]{String.valueOf(userid)});

        if (rezultat.moveToFirst()) {
            String firstname =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_FIRSTNAME));
            String lastname =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_LASTNAME));
            String email =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_EMAIL));
            String password =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_PASSWORD));

            byte[] image = rezultat.getBlob(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_IMAGE));
            Bitmap userImage = BitmapFactory.decodeByteArray(image, 0, image.length);

            int entries = rezultat.getInt(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_ENTRIES));

            String expiring =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_EXPIRING));


            return new UserModel(userid, firstname, lastname, email, password, userImage, entries, expiring);
        } else {
            return null;
        }
    }


    public UserModel getUserByEmail(String mail) {
        SQLiteDatabase db = this.getReadableDatabase();

        // SELECT * FROM users WHERE email = ?
        String query = String.format("SELECT * FROM %s WHERE %s = ?", UserModel.TABLE_NAME, UserModel.COLUMN_USER_EMAIL);
        Cursor rezultat = db.rawQuery(query, new String[]{String.valueOf(mail)});

        if (rezultat.moveToFirst()) {
            int userid =
                    rezultat.getInt(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_ID));
            String firstname =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_FIRSTNAME));
            String lastname =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_LASTNAME));
            String email =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_EMAIL));
            String password =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_PASSWORD));
            byte[] image = rezultat.getBlob(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_IMAGE));
            Bitmap userImage = BitmapFactory.decodeByteArray(image, 0, image.length);

            int entries = rezultat.getInt(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_ENTRIES));

            String expiring =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_EXPIRING));

            return new UserModel(userid, firstname, lastname, email, password, userImage, entries, expiring);
        } else {
            return null;
        }
    }

    public List<UserModel> getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format("SELECT * FROM %s", UserModel.TABLE_NAME);
        Cursor rezultat = db.rawQuery(query, null);

        rezultat.moveToFirst();

        List<UserModel> lista = new ArrayList<>(rezultat.getCount());

        while (!rezultat.isAfterLast()) {
            int userid =
                    rezultat.getInt(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_ID));
            String firstname =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_FIRSTNAME));
            String lastname =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_LASTNAME));
            String email =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_EMAIL));
            String password =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_PASSWORD));

            byte[] image = rezultat.getBlob(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_IMAGE));
            Bitmap userImage = null;

            int entries = rezultat.getInt(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_ENTRIES));

            try {
                userImage = BitmapFactory.decodeByteArray(image, 0, image.length);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String expiring =
                    rezultat.getString(rezultat.getColumnIndexOrThrow(UserModel.COLUMN_USER_EXPIRING));

            lista.add(new UserModel(userid, firstname, lastname, email, password, userImage, entries, expiring));
            rezultat.moveToNext();
        }

        return lista;
    }

}
