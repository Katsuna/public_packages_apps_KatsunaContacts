package gr.crystalogic.oldmen.utils;

import ezvcard.VCard;
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


/*        Address adr = new Address();
        adr.s
        adr.setStreetAddress("123 Wall St.");
        adr.setLocality("New York");
        adr.setRegion("NY");
        adr.setPostalCode("12345");
        adr.setCountry("USA");
        adr.setLabel("123 Wall St.\nNew York, NY 12345\nUSA");
        adr.addType(AddressType.WORK);
        vcard.addAddress(adr);

        adr = new Address();
        adr.setStreetAddress("123 Main St.");
        adr.setLocality("Albany");
        adr.setRegion("NY");
        adr.setPostalCode("54321");
        adr.setCountry("USA");
        adr.setLabel("123 Main St.\nAlbany, NY 54321\nUSA");
        adr.addType(AddressType.HOME);
        vcard.addAddress(adr);



        vcard.addEmail("johndoe@hotmail.com", EmailType.HOME);
        vcard.addEmail("doe.john@acme.com", EmailType.WORK);

        File file = new File("portrait.jpg");
        Photo photo = new Photo(file, ImageType.JPEG);
        vcard.addPhoto(photo);
*/

        vcard.setUid(Uid.random());

        vcard.setRevision(Revision.now());

        return vcard;
    }
}
