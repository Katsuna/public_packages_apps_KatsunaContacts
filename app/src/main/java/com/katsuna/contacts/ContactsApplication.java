package com.katsuna.contacts;

import android.app.Application;

import com.google.firebase.crash.FirebaseCrash;

public class ContactsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // disable firebase crash collection for debug
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG);
    }

}

