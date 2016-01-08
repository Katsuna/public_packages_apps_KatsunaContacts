package gr.crystalogic.oldmen.ui.adapters.viewholders;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;
import gr.crystalogic.oldmen.utils.Step;

public class ContactListItemViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mLargeSeparatorView;
    private final LinearLayout mContactBasicContainer;
    private final LinearLayout mContactDetails;
    private final TextView mSeparatorView;
    private final TextView mContentView;
    private final ImageView mPhoto;
    private final IContactsFragmentInteractionListener mListener;

    public ContactListItemViewHolder(View view, IContactsFragmentInteractionListener listener) {
        super(view);
        mView = view;
        mLargeSeparatorView = (TextView) view.findViewById(R.id.largeSeparator);
        mContactBasicContainer = (LinearLayout) view.findViewById(R.id.contact_basic_container);
        mContactDetails = (LinearLayout) view.findViewById(R.id.contact_details);
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mContentView = (TextView) view.findViewById(R.id.content);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mListener = listener;
    }

    public void bind(final ContactListItemModel model, Step step, final int position, Contact selectedContact) {
        final Contact contact = model.getContact();

        //initialize
        mLargeSeparatorView.setText("");
        mContactBasicContainer.setOnClickListener(null);
        mContactDetails.setVisibility(View.GONE);
        mLargeSeparatorView.setVisibility(View.GONE);
        mSeparatorView.setText("");
        mContentView.setText(contact.getDisplayName());

        switch (step){
            case S1:
                mSeparatorView.setTextSize(20);
                mContentView.setTextSize(20);
                mPhoto.getLayoutParams().width = 40;

                if (model.isSeparator()) {
                    mSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                }

                mContactBasicContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onContactSelected(position, contact);
                    }
                });
                break;
            case S2:
                mSeparatorView.setTextSize(12);
                mContentView.setTextSize(12);
                mPhoto.getLayoutParams().width = 25;
                if (model.isSeparator()) {
                    mLargeSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                    mLargeSeparatorView.setVisibility(View.VISIBLE);
                    mLargeSeparatorView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.onSeparatorClick(position);
                            }
                        }
                    });
                }
                break;
            case S5:
                if (contact.getId() == selectedContact.getId()) {
                    mContactDetails.setVisibility(View.VISIBLE);
                    mSeparatorView.setTextSize(20);
                    mContentView.setTextSize(20);
                    mPhoto.getLayoutParams().width = 40;
                } else {
                    if (model.isSeparator()) {
                        mSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                    }
                    mSeparatorView.setTextSize(12);
                    mContentView.setTextSize(12);
                    mPhoto.getLayoutParams().width = 25;
                }
                break;
        }

        Bitmap photo = null;
        if (contact.isPhotoChecked()) {
            photo = contact.getPhoto();
        } else {
            contact.setPhotoChecked(true);

            Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contact.getId());
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(mView.getContext().getContentResolver(),
                    contactUri, false);

            if (inputStream != null) {
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                int drawable = R.drawable.shape_circle;
                photo = getMaskedBitmap(mView.getContext().getResources(), bmp, drawable);
                contact.setPhoto(photo);
            }
        }
        mPhoto.setImageBitmap(photo);
    }

    private static Bitmap getMaskedBitmap(Resources res, Bitmap source, int maskResId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = source;
        bitmap.setHasAlpha(true);

        Bitmap mask = BitmapFactory.decodeResource(res, maskResId);
        bitmap = Bitmap.createScaledBitmap(bitmap, mask.getWidth(), mask.getHeight(), false);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mask, 0, 0, paint);
        mask.recycle();
        return bitmap;
    }
}
