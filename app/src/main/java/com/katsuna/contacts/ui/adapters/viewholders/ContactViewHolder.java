package com.katsuna.contacts.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.katsuna.commons.domain.Contact;
import com.katsuna.contacts.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    private final TextView mContactName;
    private final TextView mContactDesc;

    public ContactViewHolder(View itemView) {
        super(itemView);
        mContactName = itemView.findViewById(R.id.contact_name);
        mContactDesc = itemView.findViewById(R.id.contact_desc);
    }

    public void bind(Contact contact, int position) {
        mContactName.setText(contact.getDisplayName());
        String contactDesc = contact.showDescription();
        if (TextUtils.isEmpty(contactDesc)) {
            mContactDesc.setVisibility(View.GONE);
        } else {
            mContactDesc.setText(contactDesc);
            mContactDesc.setVisibility(View.VISIBLE);
        }
    }

}
