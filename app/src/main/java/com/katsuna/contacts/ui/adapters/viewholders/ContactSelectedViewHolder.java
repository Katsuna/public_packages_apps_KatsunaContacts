package com.katsuna.contacts.ui.adapters.viewholders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.katsuna.commons.entities.ProfileType;
import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

public class ContactSelectedViewHolder extends ContactViewHolder {
    private final Button mEditButton;
    private final Button mCallButton;
    private final Button mMessageButton;

    public ContactSelectedViewHolder(View view, IContactInteractionListener listener) {
        super(view, listener);
        mEditButton = (Button) itemView.findViewById(R.id.edit_button);
        mCallButton = (Button) itemView.findViewById(R.id.call_button);
        mMessageButton = (Button) itemView.findViewById(R.id.message_button);
        adjustProfile();
    }

    public void bind(final ContactListItemModel model, int position) {
        super.bind(model, position);

        final Contact contact = model.getContact();

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
        ProfileType opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int photoSize = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
            int actionButtonHeight = itemView.getResources().getDimensionPixelSize(R.dimen.action_button_height_intemediate);

            if (opticalSizeProfile == ProfileType.ADVANCED) {
                photoSize = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
                actionButtonHeight = itemView.getResources().getDimensionPixelSize(R.dimen.action_button_height_advanced);
            } else if (opticalSizeProfile == ProfileType.SIMPLE) {
                photoSize = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
                actionButtonHeight = itemView.getResources().getDimensionPixelSize(R.dimen.action_button_height_simple);
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
