package gr.crystalogic.oldmen.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.adapters.viewholders.ContactListItemViewHolder;
import gr.crystalogic.oldmen.ui.fragments.ContactsFragment;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Contact} and makes a call to the
 * specified {@link ContactsFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactListItemViewHolder> {

    private final List<ContactListItemModel> mValues;
    private final ContactsFragment.OnListFragmentInteractionListener mListener;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> items, ContactsFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ContactListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts, parent, false);
        return new ContactListItemViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(final ContactListItemViewHolder holder, int position) {
        final ContactListItemModel model = mValues.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
