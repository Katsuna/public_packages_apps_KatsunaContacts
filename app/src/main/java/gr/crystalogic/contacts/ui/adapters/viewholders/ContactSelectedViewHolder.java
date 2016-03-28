package gr.crystalogic.contacts.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.contacts.R;
import gr.crystalogic.contacts.domain.Contact;
import gr.crystalogic.contacts.ui.listeners.IContactInteractionListener;

public class ContactSelectedViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mContactName;
    private final Button mEditButton;
    private final Button mCallButton;
    private final Button mMessageButton;
    private final ImageView mPhoto;
    private final IContactInteractionListener mListener;

    public ContactSelectedViewHolder(View view, IContactInteractionListener listener) {
        super(view);
        mView = view;
        mContactName = (TextView) view.findViewById(R.id.contact_name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mEditButton = (Button) view.findViewById(R.id.edit_button);
        mCallButton = (Button) view.findViewById(R.id.call_button);
        mMessageButton = (Button) view.findViewById(R.id.message_button);
        mListener = listener;
    }

    public void bind(final Contact contact) {
        Picasso.with(mView.getContext())
                .load(contact.getPhotoUri())
                .fit()
                .into(mPhoto);

        mContactName.setText(contact.getDisplayName());

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editContact(contact.getId());
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callContact(contact);
            }
        });
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendSMS(contact);
            }
        });
    }

}
