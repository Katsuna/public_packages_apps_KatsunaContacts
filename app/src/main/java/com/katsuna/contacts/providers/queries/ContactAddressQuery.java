package com.katsuna.contacts.providers.queries;

import android.provider.ContactsContract;

public class ContactAddressQuery {
    public static final String[] _PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.StructuredPostal._ID,
            ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS
    };

    public static final int _ID = 0;
    public static final int FORMATTED_ADDRESS = 1;
}
