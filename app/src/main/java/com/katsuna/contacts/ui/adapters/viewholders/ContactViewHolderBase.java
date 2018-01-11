package com.katsuna.contacts.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.adapters.models.ContactsGroup;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.ContactsAdapter;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

abstract class ContactViewHolderBase extends RecyclerView.ViewHolder {
    final IContactInteractionListener mListener;
    final UserProfileContainer mUserProfileContainer;
    final TextView mDisplayName;
    final View mContactBasicContainer;
    private final View mPopupFrame;

    private final TextView mStartLetter;
    private final ImageView mStarIcon;
    private final TextView mStarDesc;
    private final RecyclerView mContactsList;

    ContactViewHolderBase(View view, IContactInteractionListener listener) {
        super(view);
        mContactBasicContainer = view.findViewById(R.id.contact_basic_container);
        mDisplayName = (TextView) view.findViewById(R.id.contact_name);
        mPopupFrame = view.findViewById(R.id.popup_frame);
        mListener = listener;
        mUserProfileContainer = listener.getUserProfileContainer();

        mStartLetter = view.findViewById(R.id.start_letter);
        mStarIcon = view.findViewById(R.id.star_icon);
        mStarDesc = view.findViewById(R.id.star_desc);
        mContactsList = view.findViewById(R.id.contacts_list);
    }

    void bind(final ContactsGroup model, final int position) {

        if (model.premium) {
            mStarIcon.setVisibility(View.VISIBLE);
            mStarDesc.setVisibility(View.VISIBLE);
            mStartLetter.setVisibility(View.GONE);
        } else {
            mStarIcon.setVisibility(View.GONE);
            mStarDesc.setVisibility(View.GONE);
            mStartLetter.setVisibility(View.VISIBLE);
            mStartLetter.setText(model.firstLetter);
        }

        ContactsAdapter adapter = new ContactsAdapter(model.contactList);
        mContactsList.setAdapter(adapter);
/*
        mContactBasicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectContact(position);
            }
        });
*/
    }

    protected void adjustProfile() {
/*        SizeProfile opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            if (opticalSizeProfile == SizeProfile.ADVANCED) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
            } else if (opticalSizeProfile == SizeProfile.SIMPLE) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
            }

            // display name
            OpticalParams opticalParams = SizeCalc.getOpticalParams(SizeProfileKey.TITLE,
                    opticalSizeProfile);
            SizeAdjuster.adjustText(itemView.getContext(), mDisplayName, opticalParams);
        }*/
    }

    public void showPopupFrame(boolean enabled) {
        if (enabled) {
            mPopupFrame.setVisibility(View.VISIBLE);
        } else {
            mPopupFrame.setVisibility(View.GONE);
        }

    }
}
