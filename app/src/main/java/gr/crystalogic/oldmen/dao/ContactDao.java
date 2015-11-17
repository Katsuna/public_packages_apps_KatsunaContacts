package gr.crystalogic.oldmen.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gr.crystalogic.oldmen.domain.Contact;

/**
 * Created by akafkis on 17/11/2015.
 */
public class ContactDao implements IContactDao {

    private Context context;

    public ContactDao(Context context) {
        this.context = context;
    }

    @Override
    public Collection<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        String[] projection = new String[]{ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        do {
            Contact contact = new Contact();

            String mime = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
            switch (mime) {
                case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                    contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
                    contact.setSurname(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
/*                    result.put(CITY, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)));
                    result.put(STREET, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)));
                    result.put(ZIP, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)));*/
                    break;
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    contact.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
/*                    if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
                        result.put(MOBILE, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    }*/
                    break;
            }

            Log.e("DAO", contact.toString());

            contacts.add(contact);

        } while (cursor.moveToNext());

        return contacts;
    }
}
