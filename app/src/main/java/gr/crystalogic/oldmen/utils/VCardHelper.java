package gr.crystalogic.oldmen.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import ezvcard.VCard;
import ezvcard.parameter.ImageType;
import ezvcard.property.Address;
import ezvcard.property.Photo;
import ezvcard.property.Revision;
import ezvcard.property.StructuredName;
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

    private static byte[] getPhotoBytes(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
