package com.katsuna.contacts.ui.adapters.viewholders;

import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

public class ContactViewHolder extends ContactViewHolderBase {

    private final View mGroupDivider;

    public ContactViewHolder(View view, IContactInteractionListener listener) {
        super(view, listener);
        mGroupDivider = view.findViewById(R.id.group_divider);
        adjustProfile();
    }

    public void bind(final ContactListItemModel model, final int position) {
        super.bind(model, position);

        initialize();
        switch (model.getSeparator()) {
            case FIRST_LETTER:
                // show group divider
                mGroupDivider.setVisibility(View.VISIBLE);
                break;
            case STARRED:
            case NONE:
                break;
        }

        // direct focus on non selected contact if photo or name is clicked
        View.OnClickListener focusContact = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.focusContact(position);
            }
        };
        mPhoto.setOnClickListener(focusContact);
        mDisplayName.setOnClickListener(focusContact);
    }

    private void initialize() {
        mGroupDivider.setVisibility(View.GONE);
    }

    private void adjustProfile() {
        SizeProfile opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int photoSize = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            if (opticalSizeProfile == SizeProfile.ADVANCED) {
                photoSize = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
            } else if (opticalSizeProfile == SizeProfile.SIMPLE) {
                photoSize = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
            }

            ViewGroup.MarginLayoutParams lp =
                    (ViewGroup.MarginLayoutParams) mGroupDivider.getLayoutParams();
            int currentMargin = lp.getMarginStart();
            int halfPhotoSize = photoSize / 2;
            lp.setMarginStart(currentMargin + halfPhotoSize);
        }

        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
        int dividerColor = ColorCalc.getColor(itemView.getContext(),
                ColorProfileKey.DIVIDERS_OPACITY, colorProfile);
        mGroupDivider.setBackgroundColor(dividerColor);
    }

}
