package gr.crystalogic.oldmen.dao;

import java.util.Collection;
import java.util.List;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Name;
import gr.crystalogic.oldmen.domain.Phone;

public interface IContactDao {

    Collection<Contact> getContacts();

    List<Phone> getPhones(String contactId);

    Name getName(String contactId);

    void addContact(Contact contact);

}
