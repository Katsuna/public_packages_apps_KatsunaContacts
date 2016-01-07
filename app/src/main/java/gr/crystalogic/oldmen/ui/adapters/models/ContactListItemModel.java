package gr.crystalogic.oldmen.ui.adapters.models;

import gr.crystalogic.oldmen.domain.Contact;

public class ContactListItemModel {

    private Contact contact;
    private boolean separator;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public boolean isSeparator() {
        return separator;
    }

    public void setSeparator(boolean separator) {
        this.separator = separator;
    }

    public ContactListItemModel() {}

    public ContactListItemModel(ContactListItemModel model) {
        setSeparator(model.isSeparator());
        if (model.getContact() != null) {
            contact = new Contact(model.getContact());
        }
    }
}
