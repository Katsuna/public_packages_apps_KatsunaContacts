package com.katsuna.contacts.ui.adapters;

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

import java.util.List;

public class ContactsGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int NO_CONTACT_POSITION = -1;

    private final List<ContactsGroup> mOriginalContacts;
    private List<ContactsGroup> mFilteredContacts;
    private int mSelectedContactsGroupPosition = NO_CONTACT_POSITION;

    private IContactsGroupListener mContactsGroupListener;
    private IContactListener mContactListener;
    private int mHighlightedContactsGroupPosition;
    private String mSelectedGroupLetter;
    private long mSelectedContactId;

    private ContactsGroupAdapter(List<ContactsGroup> models) {
        mOriginalContacts = models;
        mFilteredContacts = models;
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
        return mFilteredContacts.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_group, parent,
                false);
        return new ContactsGroupViewHolder(view, mContactsGroupListener, mContactListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ContactsGroup model = mFilteredContacts.get(position);

        boolean focused = mSelectedContactsGroupPosition == position;
        boolean unfocused = mSelectedContactsGroupPosition != NO_CONTACT_POSITION;
        boolean highlighted = mHighlightedContactsGroupPosition == position;

        ContactsGroupState state = new ContactsGroupState(model.premium, focused, unfocused,
                highlighted, position);
        state.setStartLetter(mSelectedGroupLetter);
        state.setContactId(mSelectedContactId);

        ContactsGroupViewHolder holder = (ContactsGroupViewHolder) viewHolder;
        holder.bind(model, position, state);
    }

    public int getPositionByStartingLetter(String letter) {
        int position = NO_CONTACT_POSITION;
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            //don't focus on premium contacts
            ContactsGroup model = mFilteredContacts.get(i);
            if (model.premium) {
                continue;
            }

            if (mFilteredContacts.get(i).firstLetter.equals(letter)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void resetFilter() {
        mFilteredContacts = mOriginalContacts;
        notifyDataSetChanged();
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
        int mPreviousSelectedContactsGroupPosition = mSelectedContactsGroupPosition;
        mSelectedContactsGroupPosition = position;

        // if we have a contact selected we must invalidate everything
        if (mSelectedContactId > 0) {
            mSelectedContactId = 0;
            notifyDataSetChanged();
        } else {
            notifyItemChanged(mPreviousSelectedContactsGroupPosition);
            notifyItemChanged(mSelectedContactsGroupPosition);
        }

        // unhighlight existing contact group if any
        if (mHighlightedContactsGroupPosition != NO_CONTACT_POSITION) {
            notifyItemChanged(mHighlightedContactsGroupPosition);
            mHighlightedContactsGroupPosition = NO_CONTACT_POSITION;
        }
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
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            for (Contact contact : mFilteredContacts.get(i).contactList) {
                if (contact.getId() == contactId) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

}