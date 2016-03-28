package gr.crystalogic.contacts.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.contacts.R;
import gr.crystalogic.contacts.domain.Contact;
import gr.crystalogic.contacts.ui.adapters.models.ContactListItemModel;

public class ContactForSelectionViewHolder extends RecyclerView.ViewHolder {

    private final TextView mSeparatorView;
    private final TextView mContentView;
    private final ImageView mPhoto;
    private final CheckBox mCheckBox;

    public ContactForSelectionViewHolder(View itemView) {
        super(itemView);

        mSeparatorView = (TextView) itemView.findViewById(R.id.separator);
        mContentView = (TextView) itemView.findViewById(R.id.contact_name);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mCheckBox = (CheckBox) itemView.findViewById(R.id.contact_check);
    }

    public void bind(final ContactListItemModel model) {
        initialize();

        final Contact contact = model.getContact();

        mCheckBox.setChecked(model.isSelected());
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newValue = !model.isSelected();
                model.setSelected(newValue);
                mCheckBox.setChecked(newValue);
            }
        });

        //load photo
        Picasso.with(itemView.getContext())
                .load(contact.getPhotoUri())
                .fit()
                .into(mPhoto);

        mContentView.setText(contact.getDisplayName());

        switch (model.getSeparator()) {
            case FIRST_LETTER:
                mSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                mSeparatorView.setVisibility(View.VISIBLE);
                break;
            case NONE:
                mSeparatorView.setVisibility(View.INVISIBLE);
                break;
        }

    }

    private void initialize() {
        mSeparatorView.setVisibility(View.GONE);
    }

}
