package com.katsuna.contacts.ui.adapters.viewholders;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalc;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;
import com.squareup.picasso.Picasso;

abstract class ContactViewHolderBase extends RecyclerView.ViewHolder {
    final ImageView mPhoto;
    final IContactInteractionListener mListener;
    final UserProfileContainer mUserProfileContainer;
    final TextView mDisplayName;
    final View mContactBasicContainer;
    private final TextView mSeparatorView;
    private final LinearLayout mSeparatorWrapper;
    private final ImageView mSeparatorImage;
    private final View mPopupFrame;

    ContactViewHolderBase(View view, IContactInteractionListener listener) {
        super(view);
        mContactBasicContainer = view.findViewById(R.id.contact_basic_container);
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mSeparatorWrapper = (LinearLayout) view.findViewById(R.id.separator_wrapper);
        mDisplayName = (TextView) view.findViewById(R.id.contact_name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mPopupFrame = view.findViewById(R.id.popup_frame);
        mListener = listener;
        mUserProfileContainer = listener.getUserProfileContainer();
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
                mSeparatorView.setText(contact.getFirstLetterNormalized());
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

    protected void adjustProfile() {
        SizeProfile opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

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

            ViewGroup.LayoutParams lp = mPhoto.getLayoutParams();
            lp.width = size;
            lp.height = size;

            // display name
            OpticalParams opticalParams = SizeCalc.getOpticalParams(SizeProfileKey.TITLE,
                    opticalSizeProfile);
            SizeAdjuster.adjustText(itemView.getContext(), mDisplayName, opticalParams);
        }
    }

    public void searchFocus(boolean flag) {
        if (mSeparatorView != null) {

            if (flag) {
                ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
                // set action buttons background color
                int color1 = ColorCalc.getColor(itemView.getContext(),
                        ColorProfileKey.ACCENT1_COLOR, colorProfile);

                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(
                        itemView.getContext(), R.drawable.circle_black_36dp);
                circle.setColor(color1);

                mSeparatorView.setBackground(circle);
            } else {
                mSeparatorView.setBackground(null);
            }

        }
    }

    public void showPopupFrame(boolean enabled) {
        if (enabled) {
            mPopupFrame.setVisibility(View.VISIBLE);
        } else {
            mPopupFrame.setVisibility(View.GONE);
        }

    }
}
