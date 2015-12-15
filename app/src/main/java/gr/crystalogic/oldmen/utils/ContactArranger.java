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

            if (displayName != null) {
                if (!displayName.startsWith(s)) {
                    s = displayName.subSequence(0, 1).toString();
                    ContactListItemModel separatorModel = new ContactListItemModel();
                    separatorModel.setSeparator(s);
                    output.add(separatorModel);
                }
            }

            ContactListItemModel model = new ContactListItemModel();
            model.setContact(c);

            output.add(model);
        }

        return output;
    }

    public static List<ContactListItemModel> queryContactsByFirstLetter(List<ContactListItemModel> models, String query) {
        List<ContactListItemModel> output = new ArrayList<>();

        //add matching models
        for (ContactListItemModel model : models) {
            if (model.getSeparator() == null) {
                if (model.getContact().getDisplayName().startsWith(query)) {
                    output.add(model);
                }
            } else {
                if (model.getSeparator().startsWith(query)) {
                    output.add(model);
                }
            }
        }

        return output;
    }

}
