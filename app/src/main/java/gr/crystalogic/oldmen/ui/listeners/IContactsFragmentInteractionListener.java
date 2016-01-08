package gr.crystalogic.oldmen.ui.listeners;

import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;

public interface IContactsFragmentInteractionListener {
    void onListFragmentInteraction(ContactListItemModel item);
    void onSeparatorClick(int position);
    void onContactSelected(int position, Contact contact);
    void onTouchEvent();
}
