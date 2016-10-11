package com.katsuna.contacts.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import com.katsuna.commons.entities.Profile;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

public class ContactSelectedViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mContactName;
    private final Button mEditButton;
    private final Button mCallButton;
    private final Button mMessageButton;
    private final ImageView mPhoto;
    private final IContactInteractionListener mListener;
    private final Profile mProfile;

    public ContactSelectedViewHolder(View view, IContactInteractionListener listener, Profile profile) {
        super(view);
        mView = view;
        mContactName = (TextView) view.findViewById(R.id.contact_name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mEditButton = (Button) view.findViewById(R.id.edit_button);
        mCallButton = (Button) view.findViewById(R.id.call_button);
        mMessageButton = (Button) view.findViewById(R.id.message_button);
        mListener = listener;
        mProfile = profile;
        adjustProfile();
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

    private void adjustProfile() {
        if (mProfile != null) {
            int photoSize = mView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
            int actionButtonHeight = mView.getResources().getDimensionPixelSize(R.dimen.action_button_height_intemediate);

            if (mProfile.getType() == ProfileType.ADVANCED.getNumVal()) {
                photoSize = mView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
                actionButtonHeight = mView.getResources().getDimensionPixelSize(R.dimen.action_button_height_advanced);
            } else if (mProfile.getType() == ProfileType.SIMPLE.getNumVal()) {
                photoSize = mView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
                actionButtonHeight = mView.getResources().getDimensionPixelSize(R.dimen.action_button_height_simple);
            }
            ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
            layoutParams.height = photoSize;
            layoutParams.width = photoSize;
            mPhoto.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams callButtonParams = mCallButton.getLayoutParams();
            callButtonParams.height = actionButtonHeight;

            ViewGroup.LayoutParams messageButtonParams = mMessageButton.getLayoutParams();
            messageButtonParams.height = actionButtonHeight;

            mCallButton.setLayoutParams(callButtonParams);
            mMessageButton.setLayoutParams(messageButtonParams);
        }
    }
}