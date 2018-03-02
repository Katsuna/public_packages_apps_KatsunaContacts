package com.katsuna.contacts.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.domain.Description;
import com.katsuna.commons.domain.Phone;
import com.katsuna.commons.providers.ContactProvider;
import com.katsuna.commons.utils.Constants;
import com.katsuna.contacts.R;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CreateContactActivity extends PhotoActivity {

    private RoundedImageView mPhoto;
    private EditText mName;
    private EditText mSurname;
    private EditText mDescription;
    private EditText mTelephone;
    private LinearLayout mNoPhotoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        initControls();

        Intent incomingIntent = getIntent();
        if (incomingIntent.getAction() != null &&
                incomingIntent.getAction().equals(Constants.CREATE_CONTACT_ACTION)) {
            mTelephone.setText(incomingIntent.getStringExtra("number"));
        }
        if (incomingIntent.getAction() != null &&
                incomingIntent.getAction().equals(Intent.ACTION_INSERT)) {
            mTelephone.setText(incomingIntent.getStringExtra(ContactsContract.Intents.Insert.PHONE));
            mName.setText(incomingIntent.getStringExtra(ContactsContract.Intents.Insert.NAME));
        }
    }

    @Override
    protected void showPopup(boolean b) {
        // no op here
    }

    private void initControls() {
        mPhoto = findViewById(R.id.photo);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        mName = findViewById(R.id.name);
        mSurname = findViewById(R.id.surname);
        mTelephone = findViewById(R.id.telephone);
        mDescription = findViewById(R.id.description);

        mFab1 = findViewById(R.id.new_contact_fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputIsValid()) {
                    Contact c = new Contact();
                    c.setDisplayName(mName.getText() + " " + mSurname.getText());

                    List<Phone> phones = new ArrayList<>();
                    phones.add(new Phone(mTelephone.getText().toString()));
                    c.setPhones(phones);

                    if (mPhoto.getDrawable() != null) {
                        Bitmap bitmap = ((RoundedDrawable) mPhoto.getDrawable()).getSourceBitmap();
                        c.setPhoto(bitmap);
                    }

                    Description description = new Description(mDescription.getText().toString());
                    c.setDescription(description);

                    ContactProvider contactProvider = new ContactProvider(CreateContactActivity.this);
                    contactProvider.addContact(c);

                    Intent intent = new Intent();
                    intent.putExtra("contactId", c.getId());
                    intent.putExtra("number", mTelephone.getText());
                    setResult(RESULT_OK, intent);

                    finish();
                }
            }
        });

        mNoPhotoContainer = findViewById(R.id.noPhotoContainer);
        mNoPhotoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private boolean inputIsValid() {
        boolean output = true;
        if (mName.getText().length() == 0 && mSurname.getText().length() == 0) {
            mName.setError(getResources().getString(R.string.name_validation));
            output = false;
        }
        if (mTelephone.getText().length() == 0) {
            mTelephone.setError(getResources().getString(R.string.number_validation));
            output = false;
        }
        return output;
    }

    @Override
    void loadPhoto(Uri uri) {
        Picasso.with(this).load(uri).fit().centerCrop().into(mPhoto);
        mNoPhotoContainer.setVisibility(View.GONE);
        mPhoto.setVisibility(View.VISIBLE);
    }

    @Override
    void removePhoto() {
        mPhoto.setImageDrawable(null);
        mNoPhotoContainer.setVisibility(View.VISIBLE);
        mPhoto.setVisibility(View.GONE);
    }

}
