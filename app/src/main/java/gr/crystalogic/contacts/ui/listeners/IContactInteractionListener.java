package gr.crystalogic.contacts.ui.listeners;

import gr.crystalogic.contacts.domain.Contact;

public interface IContactInteractionListener {
    void selectContact(int position);

    void editContact(String contactId);

    void callContact(Contact contact);

    void sendSMS(Contact contact);
}