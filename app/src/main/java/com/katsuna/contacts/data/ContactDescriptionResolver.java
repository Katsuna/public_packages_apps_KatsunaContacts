package com.katsuna.contacts.data;

import android.content.Context;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.domain.Description;
import com.katsuna.commons.providers.ContactProvider;

import java.util.List;

public class ContactDescriptionResolver {


    public static String getDescription(Context context, Contact contact) {
        String output = ContactsInfoCache.DescriptionsMap.get(contact.getId());
        if (output == null) {
            // get Description from db
            ContactProvider provider = new ContactProvider(context);

            // Read all description and use the first one.
            List<Description> descriptions = provider.getDescriptions(contact.getId());
            if (descriptions.size() > 0) {
                output = descriptions.get(0).getDescription();
            } else {
                // this will mark that we have no description set
                output = "";
            }
            ContactsInfoCache.DescriptionsMap.put(contact.getId(), output);
        }

        return output;
    }
}
