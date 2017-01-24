package com.katsuna.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.adapters.viewholders.ContactGreyedViewHolder;
import com.katsuna.contacts.ui.adapters.viewholders.ContactSelectedViewHolder;
import com.katsuna.contacts.ui.adapters.viewholders.ContactViewHolder;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;
import com.katsuna.contacts.utils.Separator;

import java.util.ArrayList;
import java.util.List;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private static final int NO_CONTACT_POSITION = -1;
    private static final int CONTACT_NOT_SELECTED = 1;
    private static final int CONTACT_SELECTED = 2;
    private static final int CONTACT_GREYED_OUT = 3;

    private final List<ContactListItemModel> mOriginalContacts;
    private final IContactInteractionListener mListener;
    private final ContactFilter mFilter = new ContactFilter();
    private List<ContactListItemModel> mFilteredContacts;
    private int mSelectedContactPosition = NO_CONTACT_POSITION;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models, IContactInteractionListener listener) {
        mOriginalContacts = models;
        mFilteredContacts = models;
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

        switch (viewHolder.getItemViewType()) {
            case CONTACT_NOT_SELECTED:
                ContactViewHolder contactViewHolder = (ContactViewHolder) viewHolder;
                contactViewHolder.bind(model, position);
                break;
            case CONTACT_GREYED_OUT:
                ContactGreyedViewHolder greyedViewHolder = (ContactGreyedViewHolder) viewHolder;
                greyedViewHolder.bind(model, position);
                break;
            case CONTACT_SELECTED:
                ContactSelectedViewHolder contactSelectedViewHolder = (ContactSelectedViewHolder) viewHolder;
                contactSelectedViewHolder.bind(model, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredContacts.size();
    }

    public void selectContactAtPosition(int position) {
        mSelectedContactPosition = position;
        notifyItemChanged(position);
    }

    public void deselectContact() {
        selectContactAtPosition(NO_CONTACT_POSITION);
    }

    public int getPositionByContactId(String contactId) {
        int position = NO_CONTACT_POSITION;
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            if (mFilteredContacts.get(i).getContact().getId().equals(contactId)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public int getPositionByStartingLetter(String letter) {
        int position = NO_CONTACT_POSITION;
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            //don't focus on premium contacts
            ContactListItemModel model = mFilteredContacts.get(i);
            if (model.isPremium()) {
                continue;
            }

            if (mFilteredContacts.get(i).getContact().getDisplayName().startsWith(letter)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void resetFilter() {
        mFilteredContacts = mOriginalContacts;
        notifyDataSetChanged();
    }

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ContactListItemModel> filteredContacts = filter(mOriginalContacts, constraint);
            FilterResults results = new FilterResults();
            results.values = filteredContacts;
            results.count = filteredContacts.size();
            return results;
        }

        private List<ContactListItemModel> filter(List<ContactListItemModel> models,
                                                  CharSequence query) {
            query = query.toString().toLowerCase();

            final List<ContactListItemModel> filteredModelList = new ArrayList<>();
            for (ContactListItemModel model : models) {
                final String text = model.getContact().getDisplayName().toLowerCase();
                if (text.contains(query)) {
                    //exclude premium contacts
                    if (!model.isPremium()) {
                        model.setSeparator(Separator.NONE);
                        filteredModelList.add(model);
                    }
                }
            }
            return filteredModelList;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredContacts = (ArrayList<ContactListItemModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
