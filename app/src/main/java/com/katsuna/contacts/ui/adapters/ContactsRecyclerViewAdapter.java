package com.katsuna.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.ui.adapters.ContactsAdapterBase;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.viewholders.ContactGreyedViewHolder;
import com.katsuna.contacts.ui.adapters.viewholders.ContactSelectedViewHolder;
import com.katsuna.contacts.ui.adapters.viewholders.ContactViewHolder;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

import java.util.List;

public class ContactsRecyclerViewAdapter extends ContactsAdapterBase {

    private static final int CONTACT_NOT_SELECTED = 1;
    private static final int CONTACT_SELECTED = 2;
    private static final int CONTACT_GREYED_OUT = 3;

    private IContactInteractionListener mListener;

    private ContactsRecyclerViewAdapter(List<ContactListItemModel> models) {
        mOriginalContacts = models;
        mFilteredContacts = models;
    }

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models, IContactInteractionListener listener) {
        this(models);
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = CONTACT_NOT_SELECTED;
        if (position == mSelectedContactPosition) {
            viewType = CONTACT_SELECTED;
        } else if (mSelectedContactPosition != NO_CONTACT_POSITION) {
            viewType = CONTACT_GREYED_OUT;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case CONTACT_NOT_SELECTED:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
                viewHolder = new ContactViewHolder(view, mListener);
                break;
            case CONTACT_SELECTED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selected, parent, false);
                viewHolder = new ContactSelectedViewHolder(view, mListener);
                break;
            case CONTACT_GREYED_OUT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_greyed, parent, false);
                viewHolder = new ContactGreyedViewHolder(view, mListener);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ContactListItemModel model = mFilteredContacts.get(position);

        boolean focused = position == mSelectedFromSearchPosition;

        switch (viewHolder.getItemViewType()) {
            case CONTACT_NOT_SELECTED:
                ContactViewHolder contactViewHolder = (ContactViewHolder) viewHolder;
                contactViewHolder.bind(model, position);
                contactViewHolder.searchFocus(focused);
                break;
            case CONTACT_GREYED_OUT:
                ContactGreyedViewHolder greyedViewHolder = (ContactGreyedViewHolder) viewHolder;
                greyedViewHolder.bind(model, position);
                greyedViewHolder.searchFocus(focused);
                break;
            case CONTACT_SELECTED:
                ContactSelectedViewHolder contactSelectedViewHolder = (ContactSelectedViewHolder) viewHolder;
                contactSelectedViewHolder.bind(model, position);
                break;
        }
    }
}
