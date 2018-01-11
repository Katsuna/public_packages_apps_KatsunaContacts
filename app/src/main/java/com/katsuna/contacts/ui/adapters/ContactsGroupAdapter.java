package com.katsuna.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.ui.adapters.ContactsGroupAdapterBase;
import com.katsuna.commons.ui.adapters.models.ContactsGroup;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.viewholders.ContactSelectedViewHolder;
import com.katsuna.contacts.ui.adapters.viewholders.ContactsGroupViewHolder;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

import java.util.List;

public class ContactsGroupAdapter extends ContactsGroupAdapterBase {

    private static final int CONTACT_NOT_SELECTED = 1;
    private static final int CONTACT_SELECTED = 2;
    private static final int CONTACT_GREYED_OUT = 3;

    private IContactInteractionListener mListener;

    private ContactsGroupAdapter(List<ContactsGroup> models) {
        mOriginalContacts = models;
        mFilteredContacts = models;
    }

    public ContactsGroupAdapter(List<ContactsGroup> models, IContactInteractionListener listener) {
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

        boolean isRightHanded = mListener.getUserProfileContainer().isRightHanded();
        View view;

        switch (viewType) {
            case CONTACT_NOT_SELECTED:
            case CONTACT_GREYED_OUT:
                if (isRightHanded) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_group, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_lh, parent, false);
                }
                viewHolder = new ContactsGroupViewHolder(view, mListener);
                break;
            case CONTACT_SELECTED:
                if (isRightHanded) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selected, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selected_lh, parent, false);
                }
                viewHolder = new ContactSelectedViewHolder(view, mListener);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ContactsGroup model = mFilteredContacts.get(position);

        boolean focused = position == mSelectedFromSearchPosition;

        switch (viewHolder.getItemViewType()) {
            case CONTACT_NOT_SELECTED:
                ContactsGroupViewHolder holder = (ContactsGroupViewHolder) viewHolder;
                holder.bind(model, position);
                holder.showPopupFrame(false);
                holder.adjustState(model.premium, false);
                break;
            case CONTACT_GREYED_OUT:
                ContactsGroupViewHolder greyOutContactsGroupViewHolder = (ContactsGroupViewHolder) viewHolder;
                greyOutContactsGroupViewHolder.bind(model, position);
                greyOutContactsGroupViewHolder.showPopupFrame(true);
                break;
            case CONTACT_SELECTED:
                ContactSelectedViewHolder contactSelectedViewHolder = (ContactSelectedViewHolder) viewHolder;
                contactSelectedViewHolder.bind(model, position);
                break;
        }
    }
}
