package gr.crystalogic.contacts.ui.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.contacts.R;
import gr.crystalogic.contacts.dao.ContactDao;
import gr.crystalogic.contacts.domain.Address;
import gr.crystalogic.contacts.domain.Contact;
import gr.crystalogic.contacts.domain.Email;
import gr.crystalogic.contacts.domain.Name;
import gr.crystalogic.contacts.domain.Phone;
import gr.crystalogic.contacts.utils.DataAction;

public class EditContactActivity extends PhotoActivity {

    private EditText[] mTelephones;
    private EditText mName;
    private EditText mSurname;
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

    private void initControls() {
        mName = (EditText) findViewById(R.id.name);
        mSurname = (EditText) findViewById(R.id.surname);
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
        String contactId = getIntent().getStringExtra("contactId");
        ContactDao contactDao = new ContactDao(this);

        mContact = contactDao.getContact(contactId);

        //set data on fields
        mName.setText(mContact.getName().getName());
        mSurname.setText(mContact.getName().getSurname());

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
        FloatingActionButton mEditContactFab = (FloatingActionButton) findViewById(R.id.edit_contact_fab);
        mEditContactFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.greenLight)));
        mEditContactFab.setOnClickListener(new View.OnClickListener() {
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
            if (mPhoto.getDrawable() != null) {
                Bitmap bitmap = ((RoundedDrawable) mPhoto.getDrawable()).getSourceBitmap();
                mContact.setPhoto(bitmap);
            }

            ContactDao contactDao = new ContactDao(EditContactActivity.this);
            contactDao.updateContact(mContact);

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
