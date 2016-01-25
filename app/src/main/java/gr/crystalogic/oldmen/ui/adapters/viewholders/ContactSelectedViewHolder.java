package gr.crystalogic.oldmen.ui.adapters.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.listeners.IContactInteractionListener;
import gr.crystalogic.oldmen.utils.ImageHelper;

public class ContactSelectedViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mContactName;
    private final Button mEditButton;
    private final Button mCallButton;
    private final Button mMessageButton;
    private final ImageView mPhoto;
    private final IContactInteractionListener mListener;

    public ContactSelectedViewHolder(View view, IContactInteractionListener listener) {
        super(view);
        mView = view;
        mContactName = (TextView) view.findViewById(R.id.contact_name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mEditButton = (Button) view.findViewById(R.id.edit_button);
        mCallButton = (Button) view.findViewById(R.id.call_button);
        mMessageButton = (Button) view.findViewById(R.id.message_button);
        mListener = listener;
    }

    public void bind(final Contact contact) {
        mContactName.setText(contact.getDisplayName());

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editContact(contact.getId());
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

        new ImageLoader(mView.getContext()).execute(contact);
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
