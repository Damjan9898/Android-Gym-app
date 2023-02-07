package com.example.fitpass;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SingleUserItem extends ConstraintLayout {


    public SingleUserItem(@NonNull Context context, UserModel um){
        super(context);

        LayoutInflater li = LayoutInflater.from(context);
        li.inflate(R.layout.single_contact, this, true);

        ((TextView) this.findViewById(R.id.firstname)).setText(um.getLast_name());
        ((TextView) this.findViewById(R.id.lastname)).setText(um.getLast_name());
        ((TextView) this.findViewById(R.id.email)).setText(um.getEmail());
        ((TextView) this.findViewById(R.id.password)).setText(um.getPassword());
    }


}
