package com.katsuna.contacts.ui.adapters.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import com.katsuna.commons.entities.Profile;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final LinearLayout mContactBasicContainer;
    private final LinearLayout mSeparatorWrapper;
    private final TextView mSeparatorView;
    private final TextView mContentView;
    private final ImageView mPhoto;
    private final ImageView mSeparatorImage;
    private final IContactInteractionListener mListener;
    private final Profile mProfile;

    public ContactViewHolder(View view, IContactInteractionListener listener, Profile profile) {
        super(view);
        mView = view;
        mContactBasicContainer = (LinearLayout) view.findViewById(R.id.contact_basic_container);
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mSeparatorWrapper = (LinearLayout) view.findViewById(R.id.separator_wrapper);
        mContentView = (TextView) view.findViewById(R.id.contact_name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mListener = listener;
        mProfile = profile;
        adjustProfile();
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
                mSeparatorWrapper.setVisibility(View.VISIBLE);
                break;
            case STARRED:
                mSeparatorImage.setImageDrawable(ContextCompat.getDrawable(mView.getContext(), R.drawable.ic_star_grey800_24dp));
                mSeparatorImage.setVisibility(View.VISIBLE);
                mSeparatorWrapper.setVisibility(View.VISIBLE);
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
        mSeparatorWrapper.setVisibility(View.GONE);
        mSeparatorView.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);
    }

    private void adjustProfile() {
        if (mProfile != null) {
            int size = mView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
            if (mProfile.getType() == ProfileType.ADVANCED.getNumVal()) {
                size = mView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
            } else if (mProfile.getType() == ProfileType.SIMPLE.getNumVal()) {
                size = mView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            mPhoto.setLayoutParams(layoutParams);
        }
    }

}