package gr.crystalogic.oldmen.dao;

import java.util.Collection;

import gr.crystalogic.oldmen.domain.Contact;

public interface IContactDao {

    Collection<Contact> getContacts();

}
