package gr.crystalogic.oldmen.ui.adapters.models;

import gr.crystalogic.oldmen.domain.Contact;

public class ContactListItemModel {

    private Contact contact;
    private String separator;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
