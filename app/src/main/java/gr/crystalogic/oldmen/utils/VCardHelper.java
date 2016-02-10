package gr.crystalogic.oldmen.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import ezvcard.VCard;
import ezvcard.parameter.ImageType;
import ezvcard.property.Address;
import ezvcard.property.Photo;
import ezvcard.property.Revision;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.property.Uid;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Phone;

public class VCardHelper {
    public static VCard getVCard(Contact contact) {
        VCard vcard = new VCard();

        StructuredName n = new StructuredName();
        n.setFamily(contact.getName().getSurname());
        n.setGiven(contact.getName().getName());
        vcard.setStructuredName(n);

        for (Phone phone : contact.getPhones()) {
            vcard.addTelephoneNumber(phone.getNumber());
        }

        if (contact.getEmail() != null) {
            vcard.addEmail(contact.getEmail().getAddress());
        }

        if (contact.getAddress() != null) {
            Address adr = new Address();
            adr.setLabel(contact.getAddress().getFormattedAddress());
            vcard.addAddress(adr);
        }

        if (contact.getPhoto() != null) {
            Photo photo = new Photo(getPhotoBytes(contact.getPhoto()), ImageType.JPEG);
            vcard.addPhoto(photo);
        }

        vcard.setUid(Uid.random());

        vcard.setRevision(Revision.now());

        return vcard;
    }

    public static Contact getContact(VCard vCard) {
        Contact contact = new Contact();
        String name = vCard.getStructuredName().getGiven() + " " + vCard.getStructuredName().getFamily();
        contact.setDisplayName(name.trim());

        List<Phone> phoneList = new ArrayList<>();
        for (Telephone telephone : vCard.getTelephoneNumbers()) {
            Phone phone = new Phone();
            phone.setNumber(telephone.getText());
            phoneList.add(phone);
        }
        contact.setPhones(phoneList);

        for (Photo photo : vCard.getPhotos()) {
            byte[] bitmapdata = photo.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            contact.setPhoto(bitmap);
        }

        return contact;
    }

    private static byte[] getPhotoBytes(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
