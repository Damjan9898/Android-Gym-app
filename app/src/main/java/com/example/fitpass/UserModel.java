package com.example.fitpass;

import java.sql.Blob;

public class UserModel {

    public static final String TABLE_NAME = "user";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_FIRSTNAME = "first_name";
    public static final String COLUMN_USER_LASTNAME = "last_name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";

    private int user_id;
    private String first_name, last_name, email, password;

    public UserModel(int user_id, String first_name, String last_name, String email, String password){
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
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

    @Override
    public String toString() {
        return "UserModel{" +
                "user_id=" + user_id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
