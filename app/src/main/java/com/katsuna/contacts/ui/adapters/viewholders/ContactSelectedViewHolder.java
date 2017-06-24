package com.katsuna.contacts.ui.adapters.viewholders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKey;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.commons.utils.ColorAdjuster;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.DrawUtils;
import com.katsuna.commons.utils.Shape;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalc;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

public class ContactSelectedViewHolder extends ContactViewHolderBase {
    private final ImageView mEditButton;
    private final Button mCallButton;
    private final View mCallButtonContainer;
    private final View mMessageButtonContainer;
    private final Button mMessageButton;

    public ContactSelectedViewHolder(View view, IContactInteractionListener listener) {
        super(view, listener);
        mEditButton = (ImageView) itemView.findViewById(R.id.edit_button);
        mCallButton = (Button) itemView.findViewById(R.id.call_button);
        mCallButtonContainer = itemView.findViewById(R.id.call_button_container);
        mMessageButton = (Button) itemView.findViewById(R.id.message_button);
        mMessageButtonContainer = itemView.findViewById(R.id.message_button_container);
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
        mCallButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callContact(contact);
            }
        });
        mMessageButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendSMS(contact);
            }
        });
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendSMS(contact);
            }
        });

        adjustProfile();
    }

    protected void adjustProfile() {
        super.adjustProfile();
        SizeProfile opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int photoSize = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            int callButtonDrawable = R.drawable.common_ic_call_black_24dp;
            int messageButtonDrawable = R.drawable.common_ic_message_white_24dp;
            int editButtonDrawable = R.drawable.edit_button_24dp;

            if (opticalSizeProfile == SizeProfile.ADVANCED) {
                photoSize = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
            } else if (opticalSizeProfile == SizeProfile.SIMPLE) {
                photoSize = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
                callButtonDrawable = R.drawable.common_ic_call_black_28dp;
                messageButtonDrawable = R.drawable.common_ic_message_white_28dp;
                editButtonDrawable = R.drawable.edit_button_28dp;
            }
            ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
            layoutParams.height = photoSize;
            layoutParams.width = photoSize;
            mPhoto.setLayoutParams(layoutParams);

            mCallButton.setCompoundDrawablesWithIntrinsicBounds(callButtonDrawable, 0, 0, 0);
            mMessageButton.setCompoundDrawablesWithIntrinsicBounds(messageButtonDrawable, 0, 0, 0);
            mEditButton.setImageResource(editButtonDrawable);

            OpticalParams opticalParams = SizeCalc.getOpticalParams(SizeProfileKey.ACTION_BUTTON,
                    opticalSizeProfile);
            SizeAdjuster.adjustText(itemView.getContext(), mCallButton, opticalParams);
            SizeAdjuster.adjustText(itemView.getContext(), mMessageButton, opticalParams);

            SizeAdjuster.adjustButtonContainer(itemView.getContext(), mCallButtonContainer,
                    opticalParams);
            SizeAdjuster.adjustButtonContainer(itemView.getContext(), mMessageButtonContainer,
                    opticalParams);
        }

        adjustColorProfile();
    }

    private void adjustColorProfile() {
        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
        // set action buttons background color
        ColorAdjuster.adjustButtons(itemView.getContext(), colorProfile,
                mCallButtonContainer, mCallButton, mMessageButtonContainer, mMessageButton);

        // set background color
        int bgColor = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.POP_UP_COLOR,
                colorProfile);
        mContactBasicContainer.setBackgroundColor(bgColor);
        mEditButton.setBackgroundColor(bgColor);
    }
}
