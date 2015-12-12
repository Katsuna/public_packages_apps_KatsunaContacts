package gr.crystalogic.oldmen.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.fragments.ContactsFragment;

public class ContactListItemViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;
    private final ContactsFragment.OnListFragmentInteractionListener mListener;

    public ContactListItemViewHolder(View view, ContactsFragment.OnListFragmentInteractionListener listener) {
        super(view);
        mView = view;
        mIdView = (TextView) view.findViewById(R.id.id);
        mContentView = (TextView) view.findViewById(R.id.content);
        mListener = listener;
    }

    public void bind(final ContactListItemModel model) {
        if (model.getSeparator() == null) {
            Contact contact = model.getContact();

            mIdView.setText(contact.getId());

            String cInfo = contact.getName().getFullName();
            if (contact.getPhones() != null && contact.getPhones().size() > 0) {
                cInfo += " " + contact.getPhones().get(0).getNumber();
            }

            mContentView.setText(cInfo);

        } else {
            mContentView.setText(model.getSeparator());
        }

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(model);
                }
            }
        });
    }
}
