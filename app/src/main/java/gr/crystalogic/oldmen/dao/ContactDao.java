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
import gr.crystalogic.oldmen.domain.Name;
import gr.crystalogic.oldmen.domain.Phone;

public class ContactDao implements IContactDao {

    private final ContentResolver cr;

    public ContactDao(Context context) {
        cr = context.getContentResolver();
    }

    @Override
    public Collection<Contact> getContacts() {
        Collection<Contact> contacts = new ArrayList<>();

        Uri baseUri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
        String orderBy = ContactsContract.Contacts.DISPLAY_NAME + " ASC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);
        cursor.moveToFirst();
        do {
            Contact contact = new Contact();

            contact.setId(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
            //Log.e("DAO", cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
            contact.setName(getName(contact.getId()));
            contact.setPhones(getPhones(contact.getId()));

            Log.e("DAO", contact.toString());

            contacts.add(contact);

        } while (cursor.moveToNext());

        cursor.close();

        return contacts;
    }

    @Override
    public Collection<Phone> getPhones(String contactId) {
        List<Phone> phones = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };
        String selection = ContactsContract.Data.CONTACT_ID + "=" + contactId;

        Cursor cursor = cr.query(baseUri, projection, selection, null, null);
        cursor.moveToFirst();
        do {
            Phone phone = new Phone();
            phone.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            phone.setType(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
            phones.add(phone);

        } while (cursor.moveToNext());
        cursor.close();

        return phones;
    }

    @Override
    public Name getName(String contactId) {
        Name name = null;

        Uri baseUri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
        };

        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionParameters = new String[]{contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

        Cursor cursor = cr.query(baseUri, projection, selection, selectionParameters, null);

        if (cursor.moveToFirst()) {
            name = new Name();
            name.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
            name.setSurname(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
        }
        cursor.close();

        return name;
    }

}
