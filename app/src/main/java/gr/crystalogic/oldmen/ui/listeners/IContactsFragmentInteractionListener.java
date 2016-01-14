package gr.crystalogic.oldmen.ui.listeners;

import gr.crystalogic.oldmen.domain.Contact;

public interface IContactsFragmentInteractionListener {
    void onSeparatorClick(int position);
    void onContactSelected(int position);
    void onLostFocusContactClick();
    void callContact(Contact contact);
    void sendSMS(Contact contact);
}
