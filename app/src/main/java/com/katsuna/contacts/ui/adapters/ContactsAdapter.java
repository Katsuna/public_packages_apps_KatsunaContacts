package com.katsuna.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.domain.Contact;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.viewholders.ContactViewHolder;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Contact> mContacts;

    public ContactsAdapter(List<Contact> models) {
        mContacts = models;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_v2, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
        contactViewHolder.bind(contact, position);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }
}
