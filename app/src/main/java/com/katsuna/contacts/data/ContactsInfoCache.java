package com.katsuna.contacts.data;

import android.support.v4.util.LongSparseArray;

public class ContactsInfoCache {

    public final static LongSparseArray<String> DescriptionsMap = new LongSparseArray<>();


    public static void invalidateContact(long contactId) {
        DescriptionsMap.remove(contactId);
    }
}
