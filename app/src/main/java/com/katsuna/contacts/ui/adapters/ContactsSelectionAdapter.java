package com.katsuna.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.adapters.viewholders.ContactForSelectionViewHolder;
import com.katsuna.contacts.utils.Separator;

import java.util.ArrayList;
import java.util.List;

public class ContactsSelectionAdapter extends RecyclerView.Adapter<ContactForSelectionViewHolder>
        implements Filterable {

    private final List<ContactListItemModel> mOriginalContacts;
    private final ContactSelectionFilter mFilter = new ContactSelectionFilter();
    private List<ContactListItemModel> mFilteredContacts;

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
    public void onBindViewHolder(ContactForSelectionViewHolder holder, int position) {
        holder.bind(mFilteredContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mFilteredContacts.size();
    }

    public void removeItem(Contact contact) {
        int filteredPosition = -1;
        for (int i = 0; i < mFilteredContacts.size(); i++) {
            if (mFilteredContacts.get(i).getContact().getId().equals(contact.getId())) {
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
            if (mOriginalContacts.get(i).getContact().getId().equals(contact.getId())) {
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

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void resetFilter() {
        mFilteredContacts = mOriginalContacts;
        notifyDataSetChanged();
    }

    private class ContactSelectionFilter extends Filter {
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
                    if (text.contains(query)) {
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