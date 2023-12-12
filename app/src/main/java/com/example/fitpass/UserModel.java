package com.example.fitpass;

import android.graphics.Bitmap;

import java.sql.Blob;
import java.util.ArrayList;

public class UserModel {

    public static final String TABLE_NAME = "user";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_FIRSTNAME = "first_name";
    public static final String COLUMN_USER_LASTNAME = "last_name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_IMAGE = "image";
    public static final String COLUMN_USER_ENTRIES = "entries";
    public static final String COLUMN_USER_EXPIRING = "expiring";

    private int user_id, entries;
    private String first_name, last_name, email, password, expiring;
    Bitmap userImage;


    public UserModel(int user_id, String first_name, String last_name, String email, String password, Bitmap bitmap, int entries, String expiring){
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.userImage = bitmap;
        this.entries = entries;
        this.expiring = expiring;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public int getEntries() { return entries; }

    public String getExpiring() {
        return expiring;
    }


    @Override
    public String toString() {
        return "UserModel{" +
                "user_id=" + user_id +
                ", entries=" + entries +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", expiring='" + expiring + '\'' +
                ", userImage=" + userImage +
                '}';
    }
}
