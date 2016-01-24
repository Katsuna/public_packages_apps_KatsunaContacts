package gr.crystalogic.oldmen.ui.adapters.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.listeners.IContactInteractionListener;
import gr.crystalogic.oldmen.utils.ImageHelper;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final LinearLayout mContactBasicContainer;
    private final TextView mSeparatorView;
    private final TextView mContentView;
    private final ImageView mPhoto;
    private final ImageView mSeparatorImage;
    private final IContactInteractionListener mListener;

    public ContactViewHolder(View view, IContactInteractionListener listener) {
        super(view);
        mView = view;
        mContactBasicContainer = (LinearLayout) view.findViewById(R.id.contact_basic_container);
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mContentView = (TextView) view.findViewById(R.id.content);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mListener = listener;
    }

    public void bind(final ContactListItemModel model, final int position) {
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

        mContactBasicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectContact(position);
            }
        });

        new ImageLoader(mView.getContext()).execute(contact);
    }

    private void initialize() {
        mSeparatorView.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);
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
