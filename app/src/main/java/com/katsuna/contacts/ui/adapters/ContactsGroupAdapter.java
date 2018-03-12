package com.katsuna.contacts.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.ui.adapters.models.ContactsGroup;
import com.katsuna.commons.ui.adapters.models.ContactsGroupState;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.viewholders.ContactsGroupViewHolder;
import com.katsuna.contacts.ui.listeners.IContactListener;
import com.katsuna.contacts.ui.listeners.IContactsGroupListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int NO_CONTACT_POSITION = -1;

    private final List<ContactsGroup> mOriginalContactsGroups;
    private List<ContactsGroup> mFilteredContactsGroups;
    private int mSelectedContactsGroupPosition = NO_CONTACT_POSITION;

    private IContactsGroupListener mContactsGroupListener;
    private IContactListener mContactListener;
    private int mHighlightedContactsGroupPosition;
    private String mSelectedGroupLetter;
    private long mSelectedContactId;

    private ContactsGroupAdapter(List<ContactsGroup> models) {
        mOriginalContactsGroups = models;
        mFilteredContactsGroups = models;
    }

    public ContactsGroupAdapter(List<ContactsGroup> models,
                                IContactsGroupListener contactsGroupListener,
                                IContactListener contactListener) {
        this(models);
        mContactsGroupListener = contactsGroupListener;
        mContactListener = contactListener;
    }

    @Override
    public int getItemCount() {
        return mFilteredContactsGroups.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_group, parent,
                false);
        return new ContactsGroupViewHolder(view, mContactsGroupListener, mContactListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final ContactsGroup model = mFilteredContactsGroups.get(position);

        boolean focused = mSelectedContactsGroupPosition == position;
        boolean focusModeOn = mSelectedContactsGroupPosition != NO_CONTACT_POSITION;
        boolean highlighted = mHighlightedContactsGroupPosition == position;

        ContactsGroupState state = new ContactsGroupState(model.premium, focused, focusModeOn,
                highlighted, position);
        state.setStartLetter(mSelectedGroupLetter);
        state.setContactId(mSelectedContactId);
        state.setSelectedGroupPosition(mSelectedContactsGroupPosition);

        ContactsGroupViewHolder holder = (ContactsGroupViewHolder) viewHolder;
        holder.bind(model, position, state);
    }

    public int getPositionByStartingLetter(String letter) {
        int position = NO_CONTACT_POSITION;
        for (int i = 0; i < mFilteredContactsGroups.size(); i++) {
            //don't focus on premium contacts
            ContactsGroup model = mFilteredContactsGroups.get(i);
            if (model.premium) {
                continue;
            }

            if (mFilteredContactsGroups.get(i).firstLetter.equals(letter)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void highlightContactsGroup(int position) {
        // while we have a selected contact group no highlighting is made
        if (mSelectedContactsGroupPosition != NO_CONTACT_POSITION) {
            if (mHighlightedContactsGroupPosition != NO_CONTACT_POSITION) {
                notifyItemChanged(mHighlightedContactsGroupPosition);
            }
            mHighlightedContactsGroupPosition = NO_CONTACT_POSITION;
            return;
        }

        // refresh only if we have a change
        if (mHighlightedContactsGroupPosition != position) {
            if (mHighlightedContactsGroupPosition != NO_CONTACT_POSITION) {
                notifyItemChanged(mHighlightedContactsGroupPosition);
            }
            mHighlightedContactsGroupPosition = position;
            notifyItemChanged(position);
        }
    }

    public void selectContactsGroup(int position) {
        mSelectedContactsGroupPosition = position;

        if (mSelectedContactId > 0) {
            mSelectedContactId = 0;
            notifyDataSetChanged();
        }

        // unhighlight existing contact group if any
        if (mHighlightedContactsGroupPosition != NO_CONTACT_POSITION) {
            mHighlightedContactsGroupPosition = NO_CONTACT_POSITION;
        }

        notifyDataSetChanged();
    }

    public void deselectContactsGroup() {
        mSelectedContactsGroupPosition = NO_CONTACT_POSITION;
        mSelectedContactId = 0;
        notifyDataSetChanged();
    }

    public void selectContactInGroup(int contactGroupPosition, String letter, long contactId) {
        mSelectedGroupLetter = letter;
        mSelectedContactId = contactId;
        mSelectedContactsGroupPosition = contactGroupPosition;
        notifyDataSetChanged();
    }

    public int getPositionByContactId(long contactId) {
        int position = NO_CONTACT_POSITION;
        for (int i = 0; i < mFilteredContactsGroups.size(); i++) {
            for (Contact contact : mFilteredContactsGroups.get(i).contactList) {
                if (contact.getId() == contactId) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public void resetFilter() {
        mFilteredContactsGroups = mOriginalContactsGroups;
        notifyDataSetChanged();
    }

    public void filter(String query) {
        query = query.toLowerCase();

        List<ContactsGroup> filteredGroups = new ArrayList<>();
        for (ContactsGroup cg : mOriginalContactsGroups) {
            //exclude premium contacts
            if (cg.premium) continue;

            List<Contact> contactsFound = new ArrayList<>();
            for(Contact contact: cg.contactList) {
                final String name = contact.getDisplayName().toLowerCase();

                if (name.contains(query)) {
                    contactsFound.add(new Contact(contact));
                }
            }

            if (contactsFound.size() > 0) {
                ContactsGroup contactsGroupFound = new ContactsGroup();
                contactsGroupFound.contactList = contactsFound;
                contactsGroupFound.firstLetter = cg.firstLetter;
                filteredGroups.add(contactsGroupFound);
            }
        }

        mFilteredContactsGroups = filteredGroups;
        notifyDataSetChanged();
    }
}