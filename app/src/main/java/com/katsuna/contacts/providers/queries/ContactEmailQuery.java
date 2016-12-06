package com.katsuna.contacts.providers.queries;

import android.provider.ContactsContract;

public class ContactEmailQuery {
    public static final String[] _PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Email._ID,
            ContactsContract.CommonDataKinds.Email.ADDRESS
    };

    public static final int _ID = 0;
    public static final int ADDRESS = 1;
}
