package gr.crystalogic.oldmen.dao;

import android.graphics.Bitmap;

import java.util.List;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Name;
import gr.crystalogic.oldmen.domain.Phone;

public interface IContactDao {

    List<Contact> getContacts();

    Contact getContact(String contactId);

    List<Phone> getPhones(String contactId);

    Name getName(String contactId);

    Bitmap getImage(String contactId, boolean preferHighres);

    void addContact(Contact contact);

}
