package com.katsuna.contacts.providers.queries;

import android.provider.ContactsContract;

public class ContactNameQuery {
    public static final String[] _PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.StructuredName._ID,
            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
    };

    public static final int _ID = 0;
    public static final int GIVEN_NAME = 1;
    public static final int FAMILY_NAME = 2;
}
