package gr.crystalogic.contacts.providers;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.contacts.domain.Address;
import gr.crystalogic.contacts.domain.Contact;
import gr.crystalogic.contacts.domain.Email;
import gr.crystalogic.contacts.domain.Name;
import gr.crystalogic.contacts.domain.Phone;
import gr.crystalogic.contacts.utils.Constants;
import gr.crystalogic.contacts.utils.ImageHelper;

public class ContactProvider {

    private static final String TAG = "ContactProvider";

    private final ContentResolver cr;
    private final Context mContext;

    public ContactProvider(Context context) {
        cr = context.getContentResolver();
        mContext = context;
    }

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

        String displaySort = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_SURNAME);

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);

            int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE);
            if (displaySort.equals(Constants.DISPLAY_SORT_NAME)) {
                displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
            }

            int timesContactedIndex = cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED);
            int lastTimeContactedIndex = cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED);
            int starredIndex = cursor.getColumnIndex(ContactsContract.Contacts.STARRED);

            do {
                Contact contact = new Contact();

                contact.setId(cursor.getString(idIndex));
                contact.setDisplayName(cursor.getString(displayNameIndex));
                contact.setTimesContacted(cursor.getInt(timesContactedIndex));
                contact.setLastTimeContacted(cursor.getLong(lastTimeContactedIndex));
                int starred = cursor.getInt(starredIndex);
                contact.setStarred(starred == 1);
                contacts.add(contact);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contacts;
    }

    public List<Contact> getContactsForExport() {
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

        String displaySort = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_SURNAME);

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);

            int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE);
            if (displaySort.equals(Constants.DISPLAY_SORT_NAME)) {
                displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
            }

            int timesContactedIndex = cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED);
            int lastTimeContactedIndex = cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED);
            int starredIndex = cursor.getColumnIndex(ContactsContract.Contacts.STARRED);

            do {
                Contact contact = new Contact();

                contact.setId(cursor.getString(idIndex));
                contact.setDisplayName(cursor.getString(displayNameIndex));
                contact.setTimesContacted(cursor.getInt(timesContactedIndex));
                contact.setLastTimeContacted(cursor.getLong(lastTimeContactedIndex));
                int starred = cursor.getInt(starredIndex);
                contact.setStarred(starred == 1);


                //name
                String contactId = contact.getId();
                contact.setName(getName(contactId));
                contact.setPhones(getPhones(contactId));
                contact.setPhoto(getImage(contactId, true));

                //use default email or first found
                List<Email> emails = getEmails(contactId);
                if (emails.size() > 0) {
                    contact.setEmail(emails.get(0));
                }

                //use default address or first found
                List<Address> addresses = getAddresses(contactId);
                if (addresses.size() > 0) {
                    contact.setAddress(addresses.get(0));
                }

                contacts.add(contact);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contacts;
    }


    public Contact getContact(String contactId) {
        Contact contact = new Contact();

        contact.setId(contactId);

        //get name
        contact.setName(getName(contactId));

        //get phones order by default flag
        List<Phone> phones = getPhones(contactId);
        contact.setPhones(phones);

        //get photo
        contact.setPhoto(getImage(contactId, true));

        //use default email or first found
        List<Email> emails = getEmails(contactId);
        if (emails.size() > 0) {
            contact.setEmail(emails.get(0));
        }

        //use default address or first found
        List<Address> addresses = getAddresses(contactId);
        if (addresses.size() > 0) {
            contact.setAddress(addresses.get(0));
        }

        return contact;
    }


    public List<Phone> getPhones(String contactId) {
        List<Phone> phones = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };
        String selection = ContactsContract.Data.CONTACT_ID + "=" + contactId;
        String orderBy = ContactsContract.CommonDataKinds.Phone.IS_PRIMARY + " DESC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int typeIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            do {
                Phone phone = new Phone();
                phone.setId(cursor.getString(idIndex));
                phone.setNumber(cursor.getString(numberIndex));
                phone.setType(cursor.getString(typeIndex));
                phones.add(phone);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return phones;
    }

    private List<Email> getEmails(String contactId) {
        List<Email> emails = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Email._ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS
        };
        String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId;
        String orderBy = ContactsContract.CommonDataKinds.Email.IS_PRIMARY + " DESC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID);
            int addressIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            do {
                Email email = new Email();
                email.setId(cursor.getString(idIndex));
                email.setAddress(cursor.getString(addressIndex));
                emails.add(email);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return emails;
    }

    private List<Address> getAddresses(String contactId) {
        List<Address> addresses = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.StructuredPostal._ID,
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS
        };
        String selection = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=" + contactId;
        String orderBy = ContactsContract.CommonDataKinds.StructuredPostal.IS_PRIMARY + " DESC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal._ID);
            int addressIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
            do {
                Address address = new Address();
                address.setId(cursor.getString(idIndex));
                address.setFormattedAddress(cursor.getString(addressIndex));
                addresses.add(address);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return addresses;
    }

    private Name getName(String contactId) {
        Name name = null;

        Uri baseUri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.StructuredName._ID,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
        };

        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] selectionParameters = new String[]{contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

        Cursor cursor = cr.query(baseUri, projection, selection, selectionParameters, null);

        if (cursor != null && cursor.moveToFirst()) {
            name = new Name();
            name.setId(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName._ID)));
            name.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
            name.setSurname(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));

            cursor.close();
        }

        return name;
    }

    private Bitmap getImage(String contactId, boolean preferHighres) {
        Bitmap output = null;

        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactUri, preferHighres);
        if (inputStream != null) {
            output = BitmapFactory.decodeStream(inputStream);
        }
        return output;
    }

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
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhones().get(0).getNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, 1)
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
            ContentProviderResult[] res = cr.applyBatch(ContactsContract.AUTHORITY, ops);

            // get generated contactId from rawContactId
            long rawContactId = ContentUris.parseId(res[0].uri);
            String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID};
            String selection = ContactsContract.RawContacts._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(rawContactId)};

            Cursor c = cr.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);

            if (c != null && c.moveToFirst()) {
                String contactId = c.getString(c.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
                contact.setId(String.valueOf(contactId));
                c.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void updateContact(Contact contact) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // Get first available raw_contact_id for creations
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(contact.getId())};

        Cursor c = cr.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        int rawContactId = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
            }
            c.close();
        }

        //update name
        String where = ContactsContract.Data._ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? ";
        String[] params = new String[]{contact.getName().getId(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, params)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.getName().getName())
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact.getName().getSurname())
                .build());

        //process phones
        for (Phone phone : contact.getPhones()) {

            switch (phone.getDataAction()) {
                case CREATE:
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getNumber())
                            .withValue(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, phone.isPrimary() ? 1 : 0)
                            .build());
                    break;
                case UPDATE:
                    where = ContactsContract.Data._ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? ";
                    params = new String[]{phone.getId(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, params)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getNumber())
                            .withValue(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, phone.isPrimary() ? 1 : 0)
                            .build());
                    break;
                case DELETE:
                    where = ContactsContract.CommonDataKinds.Phone._ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? ";
                    params = new String[]{phone.getId(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
                    ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, params)
                            .build());
                    break;
            }
        }

        //process Email
        Email email = contact.getEmail();
        if (email != null) {
            switch (email.getDataAction()) {
                case CREATE:
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email.getAddress())
                            .build());
                    break;
                case UPDATE:
                    where = ContactsContract.Data._ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? ";
                    params = new String[]{email.getId(), ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, params)
                            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email.getAddress())
                            .build());
                    break;
                case DELETE:
                    where = ContactsContract.CommonDataKinds.Email._ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? ";
                    params = new String[]{email.getId(), ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                    ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, params)
                            .build());
                    break;
            }
        }

        //process addresses
        Address address = contact.getAddress();
        if (address != null) {
            switch (address.getDataAction()) {
                case CREATE:
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.getFormattedAddress())
                            .build());
                    break;
                case UPDATE:
                    where = ContactsContract.Data._ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? ";
                    params = new String[]{address.getId(), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, params)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.getFormattedAddress())
                            .build());
                    break;
                case DELETE:
                    where = ContactsContract.CommonDataKinds.Email._ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? ";
                    params = new String[]{address.getId(), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, params)
                            .build());
                    break;
            }
        }

        //delete photo always and insert if new
        where = ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? ";
        params = new String[]{String.valueOf(rawContactId), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, params)
                .build());

        if (contact.getPhoto() != null) {
            byte[] photo = ImageHelper.bitmapToByteArray(contact.getPhoto());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
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

    public void deleteContact(Contact contact) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        String where = ContactsContract.RawContacts.CONTACT_ID + " = ? ";
        String[] params = new String[]{contact.getId()};

        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(where, params)
                .build());

        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void importContact(Contact contact) {
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

        //process phones
        for (Phone phone : contact.getPhones()) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, phone.isPrimary() ? 1 : 0)
                    .build());
        }

        //process Email
        Email email = contact.getEmail();
        if (email != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email.getAddress())
                    .build());
        }

        //process address
        Address address = contact.getAddress();
        if (address != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.getFormattedAddress())
                    .build());
        }


        if (contact.getPhoto() != null) {
            byte[] photo = ImageHelper.bitmapToByteArray(contact.getPhoto());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo)
                    .build());
        }

        try {
            ContentProviderResult[] res = cr.applyBatch(ContactsContract.AUTHORITY, ops);

            // get generated contactId from rawContactId
            long rawContactId = ContentUris.parseId(res[0].uri);
            String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID};
            String selection = ContactsContract.RawContacts._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(rawContactId)};

            Cursor c = cr.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);

            if (c != null && c.moveToFirst()) {
                String contactId = c.getString(c.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
                contact.setId(String.valueOf(contactId));
                c.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}