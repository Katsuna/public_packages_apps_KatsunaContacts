package com.katsuna.contacts.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.katsuna.commons.domain.Address;
import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.domain.Description;
import com.katsuna.commons.domain.Email;
import com.katsuna.commons.domain.Name;
import com.katsuna.commons.domain.Phone;
import com.katsuna.commons.providers.ContactProvider;
import com.katsuna.commons.utils.DataAction;
import com.katsuna.contacts.R;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditContactActivity extends PhotoActivity {

    private EditText[] mTelephones;
    private EditText mName;
    private EditText mSurname;
    private EditText mDescription;
    private EditText mTelephone1;
    private EditText mEmail;
    private EditText mAddress;
    private RoundedImageView mPhoto;
    private Contact mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        initControls();
        loadContact();
        setupFab();
    }

    @Override
    protected void showPopup(boolean b) {
        // no op here
    }

    private void initControls() {
        mName = (EditText) findViewById(R.id.name);
        mSurname = (EditText) findViewById(R.id.surname);
        mDescription = (EditText) findViewById(R.id.description);
        mTelephone1 = (EditText) findViewById(R.id.telephone1);
        EditText mTelephone2 = (EditText) findViewById(R.id.telephone2);
        EditText mTelephone3 = (EditText) findViewById(R.id.telephone3);
        mTelephones = new EditText[]{mTelephone1, mTelephone2, mTelephone3};
        mEmail = (EditText) findViewById(R.id.email);
        mAddress = (EditText) findViewById(R.id.address);

        mPhoto = (RoundedImageView) findViewById(R.id.photo);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void loadContact() {
        long contactId = getIntent().getLongExtra("contactId", 0);
        ContactProvider contactProvider = new ContactProvider(this);

        mContact = contactProvider.getContact(contactId);

        //set data on fields
        mName.setText(mContact.getName().getName());
        mSurname.setText(mContact.getName().getSurname());
        if (mContact.getDescription() != null) {
            mDescription.setText(mContact.getDescription().getDescription());
        }

        loadPhoneNumbers();
        loadEmail();
        loadAddress();
        if (mContact.getPhoto() != null) {
            loadPhoto(mContact.getPhotoUri());
        }
    }

    private void loadPhoneNumbers() {
        for (int i = 0; i < mTelephones.length; i++) {
            if (mContact.getPhone(i) != null) {
                mTelephones[i].setText(mContact.getPhone(i).getNumber());
            }
        }
    }

    private void loadEmail() {
        if (mContact.getEmail() != null) {
            mEmail.setText(mContact.getEmail().getAddress());
        }
    }

    private void loadAddress() {
        if (mContact.getAddress() != null) {
            mAddress.setText(mContact.getAddress().getFormattedAddress());
        }
    }

    private void setupFab() {
        mFab2 = (FloatingActionButton) findViewById(R.id.edit_contact_fab);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContact();
            }
        });
    }

    private void updateContact() {
        if (inputIsValid()) {
            mContact.setName(getNameForUpdate());
            mContact.setPhones(getPhonesForUpdate());
            mContact.setEmail(getEmailForUpdate());
            mContact.setAddress(getAddressForUpdate());
            mContact.setDescription(getDescriptionForUpdate());
            if (mPhoto.getDrawable() != null) {
                Bitmap bitmap = ((RoundedDrawable) mPhoto.getDrawable()).getSourceBitmap();
                mContact.setPhoto(bitmap);
            }

            ContactProvider contactProvider = new ContactProvider(EditContactActivity.this);
            contactProvider.updateContact(mContact);

            Intent intent = new Intent();
            intent.putExtra("contactId", mContact.getId());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean inputIsValid() {
        boolean output = true;
        if (mName.getText().length() == 0 && mSurname.getText().length() == 0) {
            mName.setError(getResources().getString(R.string.name_validation));
            output = false;
        }
        if (mTelephone1.getText().length() == 0) {
            mTelephone1.setError(getResources().getString(R.string.number_validation));
            output = false;
        }
        return output;
    }

    private Name getNameForUpdate() {
        Name name = mContact.getName();
        name.setName(mName.getText().toString());
        name.setSurname(mSurname.getText().toString());
        return name;
    }

    private List<Phone> getPhonesForUpdate() {
        List<Phone> phones = new ArrayList<>();

        for (int i = 0; i < mTelephones.length; i++) {
            Phone phone = null;
            if (mContact.getPhone(i) == null) {
                if (!TextUtils.isEmpty(mTelephones[i].getText())) {
                    phone = new Phone(mTelephones[i].getText().toString());
                    phone.setDataAction(DataAction.CREATE);
                }
            } else {
                phone = mContact.getPhone(i);
                if (TextUtils.isEmpty(mTelephones[i].getText())) {
                    phone.setDataAction(DataAction.DELETE);
                } else {
                    phone.setNumber(mTelephones[i].getText().toString());
                    phone.setDataAction(DataAction.UPDATE);
                }
            }

            //add only if phone is set
            if (phone != null) {
                //1st phone is always primary
                if (i == 0) {
                    phone.setPrimary(true);
                }
                phones.add(phone);
            }
        }

        return phones;
    }

    private Email getEmailForUpdate() {
        Email email = null;

        if (mContact.getEmail() == null) {
            if (!TextUtils.isEmpty(mEmail.getText())) {
                email = new Email();
                email.setDataAction(DataAction.CREATE);
                email.setAddress(mEmail.getText().toString());
            }
        } else {
            email = mContact.getEmail();
            if (TextUtils.isEmpty(mEmail.getText())) {
                email.setDataAction(DataAction.DELETE);
            } else {
                email.setDataAction(DataAction.UPDATE);
                email.setAddress(mEmail.getText().toString());
            }
        }

        return email;
    }

    private Address getAddressForUpdate() {
        Address address = null;

        if (mContact.getAddress() == null) {
            if (!TextUtils.isEmpty(mAddress.getText())) {
                address = new Address();
                address.setDataAction(DataAction.CREATE);
                address.setFormattedAddress(mAddress.getText().toString());
            }
        } else {
            address = mContact.getAddress();
            if (TextUtils.isEmpty(mAddress.getText())) {
                address.setDataAction(DataAction.DELETE);
            } else {
                address.setDataAction(DataAction.UPDATE);
                address.setFormattedAddress(mAddress.getText().toString());
            }
        }

        return address;
    }

    private Description getDescriptionForUpdate() {
        Description description = null;

        if (mContact.getDescription() == null) {
            if (!TextUtils.isEmpty(mDescription.getText())) {
                description = new Description(mDescription.getText().toString());
                description.setDataAction(DataAction.CREATE);
            }
        } else {
            description = mContact.getDescription();
            if (TextUtils.isEmpty(mDescription.getText())) {
                description.setDataAction(DataAction.DELETE);
            } else {
                description.setDataAction(DataAction.UPDATE);
                description.setDescription(mDescription.getText().toString());
            }
        }

        return description;
    }

    @Override
    void loadPhoto(Uri uri) {
        Picasso.with(this).load(uri).fit().centerCrop().into(mPhoto);
    }

    @Override
    void removePhoto() {
        mPhoto.setImageDrawable(null);
        mContact.setPhoto(null);
    }
}
