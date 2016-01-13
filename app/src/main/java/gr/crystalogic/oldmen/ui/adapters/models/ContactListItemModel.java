package gr.crystalogic.oldmen.ui.adapters.models;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.utils.Separator;

public class ContactListItemModel {

    private Contact contact;
    private Separator separator = Separator.NONE;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Separator getSeparator() {
        return separator;
    }

    public void setSeparator(Separator separator) {
        this.separator = separator;
    }

    public ContactListItemModel() {}

    public ContactListItemModel(ContactListItemModel model) {
        setSeparator(model.getSeparator());
        if (model.getContact() != null) {
            contact = new Contact(model.getContact());
        }
    }
}
