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
                contacts.add(contact);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contacts;
    }

    @Override
    public Contact getContact(String contactId) {
        Contact contact = new Contact();

        contact.setId(contactId);

        //get name
        contact.setName(getName(contactId));

        //get phones order by default flag or first three if no default flag is set
        List<Phone> phones = getPhones(contactId);
        for (int i = 0; i < phones.size(); i++) {
            switch (i) {
                case 0:
                    contact.setPrimaryTelephone(phones.get(i).getNumber());
                    break;
                case 1:
                    contact.setSecondaryTelephone(phones.get(i).getNumber());
                    break;
                case 2:
                    contact.setTertiaryTelephone(phones.get(i).getNumber());
                    break;
            }
            if (i == 3) {
                break;
            }
        }

        //get photo
        contact.setPhoto(getImage(contactId, true));

        //use default email or first found
        List<String> emails = getEmails(contactId);
        if (emails.size() > 0) {
            contact.setEmail(emails.get(0));
        }

        //use default address or first found
        List<String> addresses = getAddresses(contactId);
        if (addresses.size() > 0) {
            contact.setAddress(addresses.get(0));
        }

        return contact;
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
        String orderBy = ContactsContract.CommonDataKinds.Phone.IS_PRIMARY + " DESC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int typeIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            do {
                Phone phone = new Phone();
                phone.setNumber(cursor.getString(numberIndex));
                phone.setType(cursor.getString(typeIndex));
                phones.add(phone);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return phones;
    }

    private List<String> getEmails(String contactId) {
        List<String> emails = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Email.ADDRESS
        };
        String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId;
        String orderBy = ContactsContract.CommonDataKinds.Email.IS_PRIMARY + " DESC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            do {
                String email = cursor.getString(emailIndex);
                emails.add(email);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return emails;
    }

    private List<String> getAddresses(String contactId) {
        List<String> addresses = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS
        };
        String selection = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=" + contactId;
        String orderBy = ContactsContract.CommonDataKinds.StructuredPostal.IS_PRIMARY + " DESC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            int addressIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
            do {
                String address = cursor.getString(addressIndex);
                addresses.add(address);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return addresses;
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
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPrimaryTelephone())
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
