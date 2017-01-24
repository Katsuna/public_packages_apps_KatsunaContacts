package com.katsuna.contacts.ui.adapters.viewholders;

import android.view.View;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

public class ContactGreyedViewHolder extends ContactViewHolder {

    private final View mPopupFrame;

    public ContactGreyedViewHolder(View view, IContactInteractionListener listener) {
        super(view, listener);
        mPopupFrame = view.findViewById(R.id.popup_frame);
        adjustProfileLocal();
    }

    private void adjustProfileLocal() {
        ColorProfile profile = mUserProfileContainer.getColorProfile();
        int bgColor = ColorCalc.getColor(itemView.getContext(),
                ColorProfileKey.DISABLED_TEXT_OPACITY, profile);
        mPopupFrame.setBackgroundColor(bgColor);
    }

}