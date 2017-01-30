package com.katsuna.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.ui.adapters.ContactsAdapterBase;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.viewholders.ContactForSelectionViewHolder;

import java.util.List;

public class ContactsSelectionAdapter extends ContactsAdapterBase {

    public ContactsSelectionAdapter(List<ContactListItemModel> models) {
        mOriginalContacts = models;
        mFilteredContacts = models;
    }

    @Override
    public ContactForSelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_for_selection, parent, false);
        return new ContactForSelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((ContactForSelectionViewHolder) viewHolder).bind(mFilteredContacts.get(position));
    }

    public void removeItem(Contact contact) {
        int filteredPosition = -1;
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            if (mFilteredContacts.get(i).getContact().getId() == contact.getId()) {
                filteredPosition = i;
                break;
            }
        }
        //position found
        if (filteredPosition > -1) {
            mFilteredContacts.remove(filteredPosition);
            notifyItemRemoved(filteredPosition);
        }

        //remove also from original list
        int originalListPosition = -1;
        for (int i = 0; i < mOriginalContacts.size(); i++) {
            if (mOriginalContacts.get(i).getContact().getId() == contact.getId()) {
                originalListPosition = i;
                break;
            }
        }
        if (originalListPosition > -1) {
            mOriginalContacts.remove(originalListPosition);
        }
    }

    public List<ContactListItemModel> getModels() {
        return mFilteredContacts;
    }

}