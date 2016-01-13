package gr.crystalogic.oldmen.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;

public class ContactArranger {

    private static List<ContactListItemModel> sortContactsBySurname(List<Contact> contacts) {
        Collections.sort(contacts);

        List<ContactListItemModel> output = new ArrayList<>();

        String s = "-";

        for (Contact c : contacts) {
            String displayName = c.getDisplayName();

            //check if contact is separator
            Separator separator = Separator.NONE;
            if (displayName != null) {
                if (!displayName.startsWith(s)) {
                    s = displayName.subSequence(0, 1).toString();
                    separator = Separator.FIRST_LETTER;
                }
            }

            ContactListItemModel model = new ContactListItemModel();
            model.setContact(c);
            model.setSeparator(separator);

            output.add(model);
        }

        return output;
    }

    public static List<ContactListItemModel> getContactsProcessed(List<Contact> contacts) {
        List<ContactListItemModel> topContacts = getTopContacts(contacts);

        //sort contacts
        List<ContactListItemModel> contactsSorted = sortContactsBySurname(contacts);

        List<ContactListItemModel> output = new ArrayList<>();
        output.addAll(topContacts);
        output.addAll(contactsSorted);

        return output;
    }

    private static List<ContactListItemModel> getTopContacts(List<Contact> contacts) {
        //select top contacts
        Contact[] frequentContacted = getFrequentContacted(contacts);
        Contact[] latestContacted = getLatestContacted(contacts);
        List<Contact> starredContacts = getStarredContacts(contacts);

        //add 3 most frequent
        LinkedHashMap<String, Contact> map = new LinkedHashMap <>();
        int i = 0;
        for (Contact c : frequentContacted) {
            map.put(c.getId(), c);
            i++;
            if (i == 3) {
                break;
            }
        }

        //add 2 latest
        i = 0;
        for (Contact c : latestContacted) {
            if (!map.containsKey(c.getId())) {
                map.put(c.getId(), c);
                i++;
                if (i == 2) {
                    break;
                }
            }
        }

        //add starred
        for (Contact c : starredContacts) {
            if (!map.containsKey(c.getId())) {
                map.put(c.getId(), c);
            }
        }

        //get topContacts
        List<ContactListItemModel> topContacts = new ArrayList<>();
        boolean firstItemSet = false;
        for (Map.Entry<String, Contact> entry : map.entrySet())
        {
            ContactListItemModel model = new ContactListItemModel();
            model.setContact(entry.getValue());
            if (!firstItemSet) {
                model.setSeparator(Separator.STARRED);
                firstItemSet = true;
            }
            topContacts.add(model);
        }

        return topContacts;
    }

    private static Contact[] getLatestContacted(List<Contact> contacts) {
        Contact[] output = getDeepCopy(contacts);
        bubbleSort4LatestContacted(output);
        return output;
    }

    private static Contact[] getFrequentContacted(List<Contact> contacts) {
        Contact[] output = getDeepCopy(contacts);
        bubbleSort4FrequentContacted(output);
        return output;
    }

    private static List<Contact> getStarredContacts(List<Contact> contacts) {
        List<Contact> output = new ArrayList<>();

        for (Contact c : contacts) {
            if (c.isStarred()) {
                output.add(new Contact(c));
            }
        }

        return output;
    }

    private static Contact[] getDeepCopy(List<Contact> contacts) {
        Contact[] output = new Contact[contacts.size()];
        for (int i = 0; i < contacts.size(); i++) {
            output[i] = new Contact(contacts.get(i));
        }
        return output;
    }

    private static void bubbleSort4LatestContacted(Contact[] contacts) {

        /*
         * In bubble sort, we basically traverse the array from first
         * to array_length - 1 position and compare the element with the next one.
         * Element is swapped with the next element if the next element is smaller.
         *
         * Bubble sort steps are as follows.
         *
         * 1. Compare array[0] & array[1]
         * 2. If array[0] < array [1] swap it.
         * 3. Compare array[1] & array[2]
         * 4. If array[1] < array[2] swap it.
         * ...
         * 5. Compare array[n-1] & array[n]
         * 6. if [n-1] < array[n] then swap it.
         *
         * After this step we will have smallest element at the last index.
         *
         * Repeat the same steps for array[1] to array[n-1]
         *
         */

        int n = contacts.length;
        Contact temp;

        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {

                if (contacts[j - 1].getLastTimeContacted() < contacts[j].getLastTimeContacted()) {
                    //swap the elements!
                    temp = contacts[j - 1];
                    contacts[j - 1] = contacts[j];
                    contacts[j] = temp;
                }

            }
        }
    }

    private static void bubbleSort4FrequentContacted(Contact[] contacts) {

        int n = contacts.length;
        Contact temp;

        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {

                if (contacts[j - 1].getTimesContacted() < contacts[j].getTimesContacted()) {
                    //swap the elements!
                    temp = contacts[j - 1];
                    contacts[j - 1] = contacts[j];
                    contacts[j] = temp;
                }

            }
        }
    }

}
