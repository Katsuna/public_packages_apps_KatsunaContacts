package com.katsuna.contacts.ui.adapters.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKeyV2;
import com.katsuna.commons.ui.adapters.models.ContactsGroupState;
import com.katsuna.commons.utils.ColorAdjusterV2;
import com.katsuna.commons.utils.ColorCalcV2;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalcV2;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.listeners.IContactListener;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    private final TextView mContactName;
    private final TextView mContactDesc;
    private final View mActionButtonsContainer;
    private final IContactListener mListener;
    private final Button mCallButton;
    private final Button mMessageButton;
    private final TextView mMoreText;
    private final View mMoreActionsContainer;
    private final View mEditContactContainer;
    private final View mDeleteContactContainer;
    private final TextView mEditContactText;
    private final TextView mDeleteContactText;

    public ContactViewHolder(View itemView, IContactListener contactListener) {
        super(itemView);
        mContactName = itemView.findViewById(R.id.contact_name);
        mContactDesc = itemView.findViewById(R.id.contact_desc);
        mActionButtonsContainer = itemView.findViewById(R.id.action_buttons_container);
        mListener = contactListener;

        mCallButton = itemView.findViewById(R.id.button_call);
        mMessageButton = itemView.findViewById(R.id.button_message);
        mMoreText = itemView.findViewById(R.id.txt_more);
        mMoreActionsContainer = itemView.findViewById(R.id.more_actions_container);
        mEditContactContainer = itemView.findViewById(R.id.edit_contact_container);
        mDeleteContactContainer = itemView.findViewById(R.id.delete_contact_container);
        mEditContactText = itemView.findViewById(R.id.edit_contact_text);
        mDeleteContactText = itemView.findViewById(R.id.delete_contact_text);
    }

    public void bind(final Contact contact, final int position,
                     final ContactsGroupState contactsGroupState) {
        mContactName.setText(contact.getDisplayName());
        String contactDesc = contact.showDescription();
        if (TextUtils.isEmpty(contactDesc)) {
            mContactDesc.setVisibility(View.GONE);
        } else {
            mContactDesc.setText(contactDesc);
            mContactDesc.setVisibility(View.VISIBLE);
        }

        if (contact.getId() == contactsGroupState.getContactId() &&
                contactsGroupState.isFocused()) {
            mActionButtonsContainer.setVisibility(View.VISIBLE);
        } else {
            mActionButtonsContainer.setVisibility(View.GONE);
        }

        ColorProfile colorProfile = mListener.getUserProfileContainer().getActiveUserProfile().colorProfile;
        int secondaryColor1 = ColorCalcV2.getColor(itemView.getContext(),
                ColorProfileKeyV2.SECONDARY_COLOR_1, colorProfile);
        int secondaryColor2 = ColorCalcV2.getColor(itemView.getContext(),
                ColorProfileKeyV2.SECONDARY_COLOR_2, colorProfile);
        int greyColor2 = ColorCalcV2.getColor(itemView.getContext(),
                ColorProfileKeyV2.SECONDARY_GREY_2, colorProfile);

        int colorForTextFields;
        int colorForBackground;
        if (contactsGroupState.isFocused()) {
            if (contact.getId() == contactsGroupState.getContactId() ||
                    contactsGroupState.getContactId() == 0) {
                // we have a contact selected in this contacts group and this is our contact
                colorForTextFields = R.color.common_black87;
                if (contactsGroupState.isPremium()) {
                    colorForBackground = secondaryColor2;
                } else {
                    colorForBackground = secondaryColor1;
                }
            } else {
                // we have a contact selected in this contacts group but not this contact
                colorForTextFields = R.color.common_black34;
                colorForBackground = ContextCompat.getColor(itemView.getContext(),
                        R.color.common_transparent);
            }
        } else {
            if (contactsGroupState.getContactId() > 0) {
                // we have a contact selected but not in this contacts group
                colorForTextFields = R.color.common_black34;
                colorForBackground = ContextCompat.getColor(itemView.getContext(),
                        R.color.common_transparent);
            } else {
                // we have no contact selected
                colorForTextFields = R.color.common_black87;
                if (contactsGroupState.isPremium()) {
                    colorForBackground = secondaryColor2;
                } else {
                    colorForBackground = greyColor2;
                }
            }
        }
        mContactName.setTextColor(ContextCompat.getColor(itemView.getContext(),colorForTextFields));
        mContactDesc.setTextColor(ContextCompat.getColor(itemView.getContext(),colorForTextFields));
        itemView.setBackgroundColor(colorForBackground);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.selectContact(contactsGroupState.getContactGroupPosition(),
                        contact.getFirstLetterNormalized(), contact.getId(), position);
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
        mMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreActionsContainer.getVisibility() == View.VISIBLE) {
                    expandMoreActions(false);
                } else {
                    expandMoreActions(true);
                }
            }
        });

        mEditContactContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editContact(contact.getId());
            }
        });

        mDeleteContactContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteContact(contact);
            }
        });

        adjustSizeProfile();
        adjustColorProfile();
    }

    private void adjustSizeProfile() {
        SizeProfile sizeProfile = mListener.getUserProfileContainer().getOpticalSizeProfile();

        // display name
        OpticalParams opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.TITLE, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mContactName, opticalParams);

        // contact description
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.SUBHEADING_1, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mContactDesc, opticalParams);

        // adjust buttons
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BUTTON, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mCallButton, opticalParams);
        SizeAdjuster.adjustText(itemView.getContext(), mMessageButton, opticalParams);

        // more text
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BUTTON, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mMoreText, opticalParams);

        // contact description
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.SUBHEADING_2, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mEditContactText, opticalParams);
        SizeAdjuster.adjustText(itemView.getContext(), mDeleteContactText, opticalParams);
    }

    private void adjustColorProfile() {
        ColorAdjusterV2.adjustButtons(itemView.getContext(),
                mListener.getUserProfileContainer().getActiveUserProfile(),
                mCallButton, mMessageButton, mMoreText);
    }

    private void expandMoreActions(boolean flag) {
        if (flag) {
            mMoreActionsContainer.setVisibility(View.VISIBLE);
            mMoreText.setText(R.string.common_less);
        } else {
            mMoreActionsContainer.setVisibility(View.GONE);
            mMoreText.setText(R.string.common_more);
        }
    }

}
