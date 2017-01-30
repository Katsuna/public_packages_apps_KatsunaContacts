package com.katsuna.contacts.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.domain.Phone;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import ezvcard.VCard;
import ezvcard.parameter.ImageType;
import ezvcard.property.Address;
import ezvcard.property.Email;
import ezvcard.property.Photo;
import ezvcard.property.Revision;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.property.Uid;

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
        String name = "";
        if (vCard.getStructuredName().getGiven() != null) {
            name += vCard.getStructuredName().getGiven();
        }
        if (vCard.getStructuredName().getFamily() != null) {
            name += " " + vCard.getStructuredName().getFamily();
        }
        contact.setDisplayName(name.trim());

        List<Phone> phoneList = new ArrayList<>();
        for (Telephone telephone : vCard.getTelephoneNumbers()) {
            Phone phone = new Phone();
            phone.setNumber(telephone.getText());
            phoneList.add(phone);
        }
        contact.setPhones(phoneList);

        List<Address> addresses = vCard.getAddresses();
        if (!addresses.isEmpty()) {
            com.katsuna.commons.domain.Address address = new com.katsuna.commons.domain.Address();
            //we support only one address
            address.setFormattedAddress(addresses.get(0).getLabel());
            contact.setAddress(address);
        }

        List<Email> emails = vCard.getEmails();
        if (!emails.isEmpty()) {
            com.katsuna.commons.domain.Email email = new com.katsuna.commons.domain.Email();
            //we support only one email
            email.setAddress(emails.get(0).getValue());
            contact.setEmail(email);
        }

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
