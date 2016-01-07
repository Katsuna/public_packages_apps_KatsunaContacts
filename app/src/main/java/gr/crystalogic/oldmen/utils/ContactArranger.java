package gr.crystalogic.oldmen.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;

public class ContactArranger {

    public static List<ContactListItemModel> sortContactsBySurname(List<Contact> contacts) {
        Collections.sort(contacts);

        List<ContactListItemModel> output = new ArrayList<>();

        String s = "-";

        for (Contact c : contacts) {
            String displayName = c.getDisplayName();

            //check if contact is separator
            boolean separator = false;
            if (displayName != null) {
                if (!displayName.startsWith(s)) {
                    s = displayName.subSequence(0, 1).toString();
                    separator = true;
                }
            }

            ContactListItemModel model = new ContactListItemModel();
            model.setContact(c);
            model.setSeparator(separator);

            output.add(model);
        }

        return output;
    }
}
