package gr.crystalogic.oldmen.ui.listeners;

import gr.crystalogic.oldmen.domain.Contact;

public interface IContactInteractionListener {
    void selectContact(int position);

    void editContact(String contactId);

    void callContact(Contact contact);

    void sendSMS(Contact contact);
}
