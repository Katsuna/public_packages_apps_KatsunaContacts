package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.domain.Address;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Email;
import gr.crystalogic.oldmen.domain.Name;
import gr.crystalogic.oldmen.domain.Phone;
import gr.crystalogic.oldmen.utils.DataAction;
import gr.crystalogic.oldmen.utils.ImageHelper;

public class EditContactActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText[] mTelephones;
    private EditText mName;
    private EditText mSurname;
    private EditText mEmail;
    private EditText mAddress;
    private ImageView mPhoto;
    private Bitmap mBitmap;
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
        EditText mTelephone1 = (EditText) findViewById(R.id.telephone1);
        EditText mTelephone2 = (EditText) findViewById(R.id.telephone2);
        EditText mTelephone3 = (EditText) findViewById(R.id.telephone3);
        mTelephones = new EditText[]{mTelephone1, mTelephone2, mTelephone3};
        mEmail = (EditText) findViewById(R.id.email);
        mAddress = (EditText) findViewById(R.id.address);

        mPhoto = (ImageView) findViewById(R.id.photo);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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

            Bitmap maskedBitmap = ImageHelper.getMaskedBitmap(getResources(), mContact.getPhoto(), R.drawable.avatar);

            mPhoto.setImageBitmap(maskedBitmap);
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
                mContact.setName(getNameForUpdate());
                mContact.setPhones(getPhonesForUpdate());
                mContact.setEmail(getEmailForUpdate());
                mContact.setAddress(getAddressForUpdate());
                mContact.setPhoto(mBitmap);

                ContactDao contactDao = new ContactDao(EditContactActivity.this);
                contactDao.updateContact(mContact);

                Intent intent = new Intent();
                intent.putExtra("contactId", mContact.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
            if (mContact.getPhone(i) == null) {
                if (!TextUtils.isEmpty(mTelephones[i].getText())) {
                    Phone phone = new Phone(mTelephones[i].getText().toString());
                    phone.setDataAction(DataAction.CREATE);
                    phones.add(phone);
                }
            } else {
                Phone phone = mContact.getPhone(i);
                if (TextUtils.isEmpty(mTelephones[i].getText())) {
                    phone.setDataAction(DataAction.DELETE);
                    phones.add(phone);
                } else {
                    phone.setNumber(mTelephones[i].getText().toString());
                    phone.setDataAction(DataAction.UPDATE);
                    phones.add(phone);
                }
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


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            Bitmap centeredBitmap = ImageHelper.centerCrop(bitmap);
            mBitmap = centeredBitmap;
            Bitmap maskedBitmap = ImageHelper.getMaskedBitmap(getResources(), centeredBitmap, R.drawable.avatar);
            mPhoto.setImageBitmap(maskedBitmap);
        }
    }

}
