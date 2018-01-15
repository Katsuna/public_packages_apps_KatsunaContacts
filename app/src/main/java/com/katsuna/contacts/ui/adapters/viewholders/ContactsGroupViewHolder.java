package com.katsuna.contacts.ui.adapters.viewholders;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.adapters.models.ContactsGroup;
import com.katsuna.commons.ui.adapters.models.ContactsGroupState;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.ContactsAdapter;
import com.katsuna.contacts.ui.listeners.IContactListener;
import com.katsuna.contacts.ui.listeners.IContactsGroupListener;

public class ContactsGroupViewHolder extends RecyclerView.ViewHolder {

    private final IContactsGroupListener mContactsGroupListener;
    private final IContactListener mContactListener;
    private final UserProfileContainer mUserProfileContainer;
    private final CardView mContactsGroupContainerCard;
    private final View mContactsGroupContainerCardInner;
    private final View mPopupFrame;

    private final TextView mStartLetter;
    private final ImageView mStarIcon;
    private final TextView mStarDesc;
    private final RecyclerView mContactsList;

    public ContactsGroupViewHolder(View view,
                                   IContactsGroupListener contactsGroupListener,
                                   IContactListener contactListener) {
        super(view);
        mPopupFrame = view.findViewById(R.id.popup_frame);
        mContactsGroupListener = contactsGroupListener;
        mContactListener = contactListener;
        mUserProfileContainer = contactsGroupListener.getUserProfileContainer();

        mStartLetter = view.findViewById(R.id.start_letter);
        mStarIcon = view.findViewById(R.id.star_icon);
        mStarDesc = view.findViewById(R.id.star_desc);
        mContactsList = view.findViewById(R.id.contacts_list);

        mContactsGroupContainerCard = view.findViewById(R.id.contacts_group_container_card);
        mContactsGroupContainerCardInner = view.findViewById(R.id.contacts_group_container_card_inner);
    }

    public void bind(final ContactsGroup model, final int position,
                     final ContactsGroupState contactsGroupState) {
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

        ContactsAdapter adapter = new ContactsAdapter(model.contactList, mContactListener,
                contactsGroupState);
        mContactsList.setAdapter(adapter);

        adjustState(contactsGroupState);

        // direct focus on non selected contact if photo or name is clicked
        View.OnClickListener focusContact = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactsGroupListener.selectContactsGroup(position);
            }
        };
        mContactsGroupContainerCard.setOnClickListener(focusContact);

        // adjustProfile();
        //showPopupFrame(true);
    }

    private void adjustState(ContactsGroupState state) {
        // calc colors
        int cardColor;
        int cardColorAlpha;

        if (state.isPremium()) {
            cardColor = R.color.priority_two;
            cardColorAlpha = R.color.priority_two_tone_one;
        } else if (state.isFocused()) {
            cardColor = R.color.priority_three;
            cardColorAlpha = R.color.priority_three_tone_one;
        } else {
            cardColor = R.color.priority_one;
            cardColorAlpha = R.color.priority_one_tone_one;
        }

        if (state.getContactId() > 0) {
            cardColorAlpha = R.color.common_unfocused;
        }

        // set colors
        if (cardColor != 0) {
            mContactsGroupContainerCard.setCardBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), cardColor)));
            mContactsGroupContainerCardInner.setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), cardColorAlpha));
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
