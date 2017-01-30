package com.katsuna.contacts.ui.listeners;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.entities.UserProfileContainer;

public interface IContactInteractionListener {
    void selectContact(int position);

    void focusContact(int position);

    void editContact(long contactId);

    void callContact(Contact contact);

    void sendSMS(Contact contact);

    UserProfileContainer getUserProfileContainer();
}
