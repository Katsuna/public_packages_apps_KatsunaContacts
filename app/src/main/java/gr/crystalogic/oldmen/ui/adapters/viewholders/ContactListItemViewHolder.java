package gr.crystalogic.oldmen.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;

public class ContactListItemViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mSeparatorView;
    private final TextView mContentView;
    private final IContactsFragmentInteractionListener mListener;

    public ContactListItemViewHolder(View view, IContactsFragmentInteractionListener listener) {
        super(view);
        mView = view;
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mContentView = (TextView) view.findViewById(R.id.content);
        mListener = listener;
    }

    public void bind(final ContactListItemModel model) {
        Contact contact = model.getContact();

        if (model.isSeparator()) {
            mSeparatorView.setText(contact.getDisplayName().substring(0, 1));
        }
        mContentView.setText(contact.getDisplayName());

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
