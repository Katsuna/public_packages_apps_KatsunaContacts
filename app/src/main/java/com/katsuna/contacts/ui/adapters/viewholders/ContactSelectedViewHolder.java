package com.katsuna.contacts.ui.adapters.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.Shape;
import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

public class ContactSelectedViewHolder extends ContactViewHolderBase {
    private final View mEditButton;
    private final Button mCallButton;
    private final Button mMessageButton;

    public ContactSelectedViewHolder(View view, IContactInteractionListener listener) {
        super(view, listener);
        mEditButton = itemView.findViewById(R.id.edit_button);
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
            int photoSize = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            int actionButtonHeight = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_action_button_height_intemediate);

            if (opticalSizeProfile == ProfileType.ADVANCED) {
                photoSize = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
                actionButtonHeight = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_action_button_height_advanced);
            } else if (opticalSizeProfile == ProfileType.SIMPLE) {
                photoSize = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
                actionButtonHeight = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_action_button_height_simple);
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

        adjustColorProfile();
    }

    private void adjustColorProfile() {
        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
        int color1 = ColorCalc.getColor(ColorProfileKey.ACCENT1_COLOR, colorProfile);
        Shape.setRoundedBackground(mCallButton, ContextCompat.getColor(itemView.getContext(),
                color1));

        int color2 = ColorCalc.getColor(ColorProfileKey.ACCENT2_COLOR, colorProfile);
        Shape.setRoundedBackground(mMessageButton, ContextCompat.getColor(itemView.getContext(),
                color2));
    }
}
