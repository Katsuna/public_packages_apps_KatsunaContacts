package com.katsuna.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.ui.adapters.models.ContactsGroupState;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.viewholders.ContactViewHolder;
import com.katsuna.contacts.ui.listeners.IContactListener;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Contact> mContacts;
    private final IContactListener mContactListener;
    private final ContactsGroupState mContactsGroupState;

    public ContactsAdapter(List<Contact> models, IContactListener contactListener,
                           ContactsGroupState contactsGroupState) {
        mContacts = models;
        mContactListener = contactListener;
        mContactsGroupState = contactsGroupState;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean isRightHanded = mContactListener.getUserProfileContainer().isRightHanded();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contact_v2, parent, false);
        ViewGroup buttonsWrapper = view.findViewById(R.id.action_buttons_wrapper);
        View buttonsView = isRightHanded ?
                inflater.inflate(R.layout.contact_buttons_rh, buttonsWrapper, false) :
                inflater.inflate(R.layout.contact_buttons_lh, buttonsWrapper, false);

        buttonsWrapper.addView(buttonsView);

        return new ContactViewHolder(view, mContactListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
        contactViewHolder.bind(contact, position, mContactsGroupState);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }
}
