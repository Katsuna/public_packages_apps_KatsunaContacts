package gr.crystalogic.contacts.dao;

import android.graphics.Bitmap;

import java.util.List;

import gr.crystalogic.contacts.domain.Contact;
import gr.crystalogic.contacts.domain.Name;
import gr.crystalogic.contacts.domain.Phone;

public interface IContactDao {

    List<Contact> getContacts();

    List<Contact> getContactsForExport();

    Contact getContact(String contactId);

    List<Phone> getPhones(String contactId);

    Name getName(String contactId);

    Bitmap getImage(String contactId, boolean preferHighres);

    void addContact(Contact contact);

    void updateContact(Contact contact);

    void deleteContact(Contact contact);

}
