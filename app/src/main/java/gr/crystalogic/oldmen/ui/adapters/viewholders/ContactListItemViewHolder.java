package gr.crystalogic.oldmen.ui.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.activities.EditContactActivity;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;
import gr.crystalogic.oldmen.utils.ImageHelper;
import gr.crystalogic.oldmen.utils.Step;

public class ContactListItemViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final LinearLayout mContactBasicContainer;
    private final LinearLayout mContactDetails;
    private final TextView mSeparatorView;
    private final TextView mContentView;
    private final ImageButton mEditButton;
    private final Button mCallButton;
    private final Button mMessageButton;
    private final View mTopRuler;
    private final View mBottomRuler;
    private final ImageView mPhoto;
    private final ImageView mSeparatorImage;
    private final IContactsFragmentInteractionListener mListener;

    public ContactListItemViewHolder(View view, IContactsFragmentInteractionListener listener) {
        super(view);
        mView = view;
        mContactBasicContainer = (LinearLayout) view.findViewById(R.id.contact_basic_container);
        mContactDetails = (LinearLayout) view.findViewById(R.id.contact_details);
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mContentView = (TextView) view.findViewById(R.id.content);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mEditButton = (ImageButton) view.findViewById(R.id.edit_button);
        mCallButton = (Button) view.findViewById(R.id.call_button);
        mMessageButton = (Button) view.findViewById(R.id.message_button);
        mTopRuler = view.findViewById(R.id.top_ruler);
        mBottomRuler = view.findViewById(R.id.bottom_ruler);
        mListener = listener;
    }

    public void bind(final ContactListItemModel model, Step step, final int position, int selectedContactPosition) {
        initialize();

        final Contact contact = model.getContact();
        mContentView.setText(contact.getDisplayName());

        switch (model.getSeparator()) {
            case FIRST_LETTER:
                mSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                mSeparatorView.setVisibility(View.VISIBLE);
                break;
            case STARRED:
                mSeparatorImage.setImageDrawable(ContextCompat.getDrawable(mView.getContext(), R.drawable.star));
                mSeparatorImage.setVisibility(View.VISIBLE);
                break;
            case NONE:
                mSeparatorView.setVisibility(View.INVISIBLE);
                break;
        }

        switch (step) {
            case INITIAL:
                mContactBasicContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onContactSelected(position);
                    }
                });
                break;
            case CONTACT_SELECTION:
                if (position == selectedContactPosition) {
                    mTopRuler.setVisibility(View.VISIBLE);
                    mBottomRuler.setVisibility(View.VISIBLE);
                    mContactDetails.setVisibility(View.VISIBLE);
                    mContentView.setTextSize(25);
                    mEditButton.setVisibility(View.VISIBLE);
                    mEditButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(mView.getContext(), EditContactActivity.class);
                            i.putExtra("contactId", contact.getId());
                            mView.getContext().startActivity(i);
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
                } else {
                    mContactBasicContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onLostFocusContactClick();
                        }
                    });
                }
                break;
        }

        new ImageLoader(mView.getContext()).execute(contact);
    }

    private void initialize() {
        mTopRuler.setVisibility(View.GONE);
        mBottomRuler.setVisibility(View.GONE);

        mContactDetails.setVisibility(View.GONE);
        mEditButton.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);

        mSeparatorView.setText("");
        mSeparatorView.setTextSize(18);
        mSeparatorView.setVisibility(View.GONE);

        mContentView.setText("");
        mContentView.setTextSize(18);

        mContactBasicContainer.setOnClickListener(null);
        mCallButton.setOnClickListener(null);
        mMessageButton.setOnClickListener(null);
        mPhoto.setImageBitmap(null);
    }

    private class ImageLoader extends AsyncTask<Contact, Void, Bitmap> {

        private final Context context;

        public ImageLoader(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(Contact... params) {
            Contact contact = params[0];

            Bitmap output = null;
            if (contact.isPhotoChecked()) {
                output = contact.getPhoto();
            } else {
                IContactDao contactDao = new ContactDao(context);
                Bitmap image = contactDao.getImage(contact.getId(), false);

                if (image != null) {
                    int drawable = R.drawable.shape_circle;
                    output = ImageHelper.getMaskedBitmap(context.getResources(), image, drawable);
                    //cache for reuse
                    contact.setPhoto(output);
                }
                contact.setPhotoChecked(true);
            }

            return output;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mPhoto.setImageBitmap(result);
        }
    }

}
