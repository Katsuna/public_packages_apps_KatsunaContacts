package com.katsuna.contacts.providers.queries;

import android.provider.ContactsContract;

public class ContactQuery {
    public static final String[] _PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE,
            ContactsContract.Contacts.TIMES_CONTACTED,
            ContactsContract.Contacts.LAST_TIME_CONTACTED,
            ContactsContract.Contacts.STARRED
    };

    public static final int _ID = 0;
    public static final int DISPLAY_NAME_PRIMARY = 1;
    public static final int DISPLAY_NAME_ALTERNATIVE = 2;
    public static final int TIMES_CONTACTED = 3;
    public static final int LAST_TIME_CONTACTED = 4;
    public static final int STARRED = 5;

}
