package gr.crystalogic.oldmen.dao;

import java.util.Collection;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Name;
import gr.crystalogic.oldmen.domain.Phone;

public interface IContactDao {

    Collection<Contact> getContacts();

    Collection<Phone> getPhones(String contactId);

    Name getName(String contactId);

}
