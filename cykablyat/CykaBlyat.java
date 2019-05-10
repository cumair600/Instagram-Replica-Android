package com.example.cykablyat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class CykaBlyat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
