package gr.crystalogic.contacts.dao;

import java.util.List;

import gr.crystalogic.contacts.domain.Contact;

public interface IContactDao {

    List<Contact> getContacts();

    List<Contact> getContactsForExport();

    void addContact(Contact contact);

    void deleteContact(Contact contact);

}
