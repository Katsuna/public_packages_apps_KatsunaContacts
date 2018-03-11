package com.katsuna.contacts.ui.listeners;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.entities.UserProfileContainer;

public interface IContactListener {
    void selectContact(int contactGroupPosition, String letter, long contactId, int position);

    void editContact(long contactId);

    void deleteContact(Contact contact);

    void callContact(Contact contact);

    void sendSMS(Contact contact);

    UserProfileContainer getUserProfileContainer();
}
