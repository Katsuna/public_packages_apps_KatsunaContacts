package com.katsuna.contacts.ui.adapters.viewholders;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;

import com.katsuna.commons.ui.adapters.models.ContactsGroup;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;

public class ContactsGroupViewHolder extends ContactViewHolderBase {

    private final CardView mContactsGroupContainerCard;
    private final View mContactsGroupContainerCardInner;

    public ContactsGroupViewHolder(View view, IContactInteractionListener listener) {
        super(view, listener);
        mContactsGroupContainerCard = view.findViewById(R.id.contacts_group_container_card);
        mContactsGroupContainerCardInner = view.findViewById(R.id.contacts_group_container_card_inner);
    }

    public void bind(final ContactsGroup model, final int position) {
        super.bind(model, position);

/*        switch (model.getSeparator()) {
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
        mDisplayName.setOnClickListener(focusContact);

        adjustProfile();
        */
    }

    public void adjustState(boolean isPremium, boolean isSelected) {
        if (isPremium) {
            adjustPremium(true);
        } else {
            adjustPremium(false);
        }
        if (isSelected) {

        }
    }

    private void adjustPremium(boolean premium) {

        // calc colors
        int cardColor = 0;
        int cardColorAlpha = 0;

        if (premium) {
            cardColor = R.color.priority_two;
            cardColorAlpha = R.color.priority_two_tone_one;
        } else {
            cardColor = R.color.priority_one;
            cardColorAlpha = R.color.priority_one_tone_one;
        }



        // set colors
        if (cardColor != 0) {
            mContactsGroupContainerCard.setCardBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), cardColor)));
            mContactsGroupContainerCardInner.setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), cardColorAlpha));
        }
    }
}
