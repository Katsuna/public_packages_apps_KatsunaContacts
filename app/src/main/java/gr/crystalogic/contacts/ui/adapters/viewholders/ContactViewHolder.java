package gr.crystalogic.contacts.ui.adapters.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.contacts.R;
import gr.crystalogic.contacts.domain.Contact;
import gr.crystalogic.contacts.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.contacts.ui.listeners.IContactInteractionListener;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final LinearLayout mContactBasicContainer;
    private final TextView mSeparatorView;
    private final TextView mContentView;
    private final ImageView mPhoto;
    private final ImageView mSeparatorImage;
    private final IContactInteractionListener mListener;

    public ContactViewHolder(View view, IContactInteractionListener listener) {
        super(view);
        mView = view;
        mContactBasicContainer = (LinearLayout) view.findViewById(R.id.contact_basic_container);
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mContentView = (TextView) view.findViewById(R.id.contact_name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mListener = listener;
    }

    public void bind(final ContactListItemModel model, final int position) {
        initialize();

        final Contact contact = model.getContact();

        //load photo
        Picasso.with(mView.getContext())
                .load(contact.getPhotoUri())
                .fit()
                .into(mPhoto);

        mContentView.setText(contact.getDisplayName());

        switch (model.getSeparator()) {
            case FIRST_LETTER:
                mSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                mSeparatorView.setVisibility(View.VISIBLE);
                break;
            case STARRED:
                mSeparatorImage.setImageDrawable(ContextCompat.getDrawable(mView.getContext(), R.drawable.star));
                mSeparatorImage.setVisibility(View.VISIBLE);
                break;
            case NONE:
                mSeparatorView.setVisibility(View.GONE);
                break;
        }

        mContactBasicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectContact(position);
            }
        });
    }

    private void initialize() {
        mSeparatorView.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);
    }

}
