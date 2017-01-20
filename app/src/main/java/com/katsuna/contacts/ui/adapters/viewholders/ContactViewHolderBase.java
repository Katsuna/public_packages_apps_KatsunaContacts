package com.katsuna.contacts.ui.adapters.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;
import com.squareup.picasso.Picasso;

abstract class ContactViewHolderBase extends RecyclerView.ViewHolder {
    final ImageView mPhoto;
    final IContactInteractionListener mListener;
    final UserProfileContainer mUserProfileContainer;
    final TextView mDisplayName;
    private final View mContactBasicContainer;
    private final LinearLayout mSeparatorWrapper;
    private final TextView mSeparatorView;
    private final ImageView mSeparatorImage;

    ContactViewHolderBase(View view, IContactInteractionListener listener) {
        super(view);
        mContactBasicContainer = view.findViewById(R.id.contact_basic_container);
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mSeparatorWrapper = (LinearLayout) view.findViewById(R.id.separator_wrapper);
        mDisplayName = (TextView) view.findViewById(R.id.contact_name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mListener = listener;
        mUserProfileContainer = listener.getUserProfileContainer();
        adjustProfile();
    }

    void bind(final ContactListItemModel model, final int position) {
        initialize();

        final Contact contact = model.getContact();

        //load photo
        Picasso.with(itemView.getContext())
                .load(contact.getPhotoUri())
                .fit()
                .into(mPhoto);

        mDisplayName.setText(contact.getDisplayName());

        switch (model.getSeparator()) {
            case FIRST_LETTER:
                mSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                mSeparatorView.setVisibility(View.VISIBLE);
                mSeparatorWrapper.setVisibility(View.VISIBLE);
                break;
            case STARRED:
                mSeparatorImage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_star_grey800_24dp));
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
        mSeparatorWrapper.setVisibility(View.INVISIBLE);
        mSeparatorView.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);
    }

    private void adjustProfile() {
        ProfileType opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            if (opticalSizeProfile == ProfileType.ADVANCED) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
            } else if (opticalSizeProfile == ProfileType.SIMPLE) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
            }

            ViewGroup.LayoutParams lp = mPhoto.getLayoutParams();
            lp.width = size;
            lp.height = size;
        }
    }

}
