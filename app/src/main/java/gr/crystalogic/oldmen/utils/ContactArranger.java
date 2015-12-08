package gr.crystalogic.oldmen.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Name;

public class ContactArranger {

    public static List<Contact> sortContactsBySurname(List<Contact> contacts) {
        Collections.sort(contacts);

        List<Contact> output = new ArrayList<>();

        String s = "-";

        for (Contact c : contacts) {
            String surname = c.getName().getSurname();

            if (!surname.startsWith(s)) {
                s = surname.subSequence(0,1).toString();
                Contact headContact = new Contact();
                headContact.setName(new Name("", s));
                output.add(headContact);
            }
            output.add(c);
        }

        return output;
    }


}
