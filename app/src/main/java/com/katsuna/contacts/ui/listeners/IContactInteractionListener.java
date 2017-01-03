package com.katsuna.contacts.ui.listeners;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.contacts.domain.Contact;

public interface IContactInteractionListener {
    void selectContact(int position);

    void editContact(String contactId);

    void callContact(Contact contact);

    void sendSMS(Contact contact);

    UserProfileContainer getUserProfileContainer();
}
