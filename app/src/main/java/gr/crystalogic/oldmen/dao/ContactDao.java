package gr.crystalogic.oldmen.dao;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Name;
import gr.crystalogic.oldmen.domain.Phone;
import gr.crystalogic.oldmen.utils.ImageHelper;

public class ContactDao implements IContactDao {

    private static final String TAG = "ContactDao";

    private final ContentResolver cr;

    public ContactDao(Context context) {
        cr = context.getContentResolver();
    }

    @Override
    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();

        Uri baseUri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE,
                ContactsContract.Contacts.TIMES_CONTACTED,
                ContactsContract.Contacts.LAST_TIME_CONTACTED,
                ContactsContract.Contacts.STARRED
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
        String orderBy = ContactsContract.Contacts.DISPLAY_NAME + " ASC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int displayNameAltIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE);
            int timesContactedIndex = cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED);
            int lastTimeContactedIndex = cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED);
            int starredIndex = cursor.getColumnIndex(ContactsContract.Contacts.STARRED);

            do {
                Contact contact = new Contact();

                contact.setId(cursor.getString(idIndex));
                contact.setDisplayName(cursor.getString(displayNameAltIndex));
                contact.setTimesContacted(cursor.getInt(timesContactedIndex));
                contact.setLastTimeContacted(cursor.getLong(lastTimeContactedIndex));
                int starred = cursor.getInt(starredIndex);
                contact.setStarred(starred == 1);

                //this is slow....
                //List<Phone> phones = getPhones(contact.getId());
                //contact.setNumber(phones.get(0).getNumber());

                //contact.setPhones(getPhones(contact.getId()));

                contacts.add(contact);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contacts;
    }

    //TODO consider deletion
    //@Override
    public List<Contact> getContactsFast() {
        List<Contact> contacts = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE
        };

        Cursor cursor = cr.query(baseUri, projection, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Contact contact = new Contact();
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String displayNameAlternative = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contact.setId(contactId);
                contact.setDisplayName(displayNameAlternative);
                contact.setNumber(number);

                contacts.add(contact);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contacts;
    }


    @Override
    public List<Phone> getPhones(String contactId) {
        List<Phone> phones = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };
        String selection = ContactsContract.Data.CONTACT_ID + "=" + contactId;

        Cursor cursor = cr.query(baseUri, projection, selection, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                Phone phone = new Phone();
                phone.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                phone.setType(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                phones.add(phone);

            } while (cursor.moveToNext());
            cursor.close();
        }

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

        if (cursor != null && cursor.moveToFirst()) {
            name = new Name();
            name.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
            name.setSurname(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));

            cursor.close();
        }

        return name;
    }

    @Override
    public Bitmap getImage(String contactId, boolean preferHighres) {
        Bitmap output = null;

        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactUri, preferHighres);
        if (inputStream != null) {
            output = BitmapFactory.decodeStream(inputStream);
        }
        return output;
    }

    @Override
    public void addContact(Contact contact) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getDisplayName())
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

        if (contact.getPhoto() != null) {
            byte[] photo = ImageHelper.bitmapToByteArray(contact.getPhoto());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo)
                    .build());
        }

        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
