package gr.crystalogic.oldmen.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.viewholders.ContactListItemViewHolder;
import gr.crystalogic.oldmen.ui.fragments.ContactsFragment;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Contact} and makes a call to the
 * specified {@link ContactsFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactListItemViewHolder> {

    private final List<Contact> mValues;
    private final ContactsFragment.OnListFragmentInteractionListener mListener;

    public ContactsRecyclerViewAdapter(List<Contact> items, ContactsFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ContactListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts, parent, false);
        return new ContactListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactListItemViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId());

        Contact c = mValues.get(position);

        String cInfo = c.getName().getFullName();
        if (c.getPhones() != null && c.getPhones().size() > 0) {
            cInfo += " " + c.getPhones().get(0).getNumber();
        }

        holder.mContentView.setText(cInfo);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
