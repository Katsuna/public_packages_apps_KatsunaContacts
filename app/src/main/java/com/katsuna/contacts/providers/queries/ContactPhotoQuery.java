package com.katsuna.contacts.providers.queries;

import android.provider.ContactsContract;

public class ContactPhotoQuery {
    public static final String[] _PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE
    };

    public static final int _ID = 0;
    public static final int NUMBER = 1;
    public static final int TYPE = 2;

}
