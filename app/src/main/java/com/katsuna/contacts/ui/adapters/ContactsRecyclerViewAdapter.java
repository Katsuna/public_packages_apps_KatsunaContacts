package com.katsuna.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.katsuna.commons.entities.Profile;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.adapters.viewholders.ContactSelectedViewHolder;
import com.katsuna.contacts.ui.adapters.viewholders.ContactViewHolder;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;
import com.katsuna.contacts.utils.Separator;

import java.util.ArrayList;
import java.util.List;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private static final int CONTACT_NOT_SELECTED = 1;
    private static final int CONTACT_SELECTED = 2;

    private final List<ContactListItemModel> mOriginalContacts;
    private final IContactInteractionListener mListener;
    private final Profile mProfile;
    private final ContactFilter mFilter = new ContactFilter();
    private List<ContactListItemModel> mFilteredContacts;
    private int mSelectedContactPosition = -1;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models, IContactInteractionListener listener, Profile profile) {
        mOriginalContacts = models;
        mFilteredContacts = models;
        mListener = listener;
        mProfile = profile;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = CONTACT_NOT_SELECTED;
        if (position == mSelectedContactPosition) {
            viewType = CONTACT_SELECTED;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case CONTACT_NOT_SELECTED:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
                viewHolder = new ContactViewHolder(view, mListener, mProfile);
                break;
            case CONTACT_SELECTED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selected, parent, false);
                viewHolder = new ContactSelectedViewHolder(view, mListener, mProfile);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ContactListItemModel model = mFilteredContacts.get(position);

        switch (viewHolder.getItemViewType()) {

            case CONTACT_NOT_SELECTED:
                ContactViewHolder imageViewHolder = (ContactViewHolder) viewHolder;
                imageViewHolder.bind(model, position);
                break;

            case CONTACT_SELECTED:
                ContactSelectedViewHolder contactSelectedViewHolder = (ContactSelectedViewHolder) viewHolder;
                contactSelectedViewHolder.bind(model.getContact());
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

    public int getPositionByContactId(String contactId) {
        int position = -1;
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            if (mFilteredContacts.get(i).getContact().getId().equals(contactId)) {
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
