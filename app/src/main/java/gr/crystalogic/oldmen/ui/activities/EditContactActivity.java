package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Name;
import gr.crystalogic.oldmen.utils.ImageHelper;

public class EditContactActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText mName;
    private EditText mSurname;
    private EditText mTelephone1;
    private EditText mTelephone2;
    private EditText mTelephone3;
    private EditText mEmail;
    private EditText mAddress;
    private ImageView mPhoto;
    private Bitmap mBitmap;
    private String mContactId;

    private FloatingActionButton mEditContactFab;

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
        mTelephone2 = (EditText) findViewById(R.id.telephone2);
        mTelephone3 = (EditText) findViewById(R.id.telephone3);
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
        mContactId = getIntent().getStringExtra("contactId");
        ContactDao contactDao = new ContactDao(this);

        Contact contact = contactDao.getContact(mContactId);

        //set data on fields
        mName.setText(contact.getName().getName());
        mSurname.setText(contact.getName().getSurname());
        mTelephone1.setText(contact.getPrimaryTelephone());
        mTelephone2.setText(contact.getSecondaryTelephone());
        mTelephone3.setText(contact.getTertiaryTelephone());
        mEmail.setText(contact.getEmail());
        mAddress.setText(contact.getAddress());

        if (contact.getPhoto() != null) {

            Bitmap maskedBitmap = ImageHelper.getMaskedBitmap(getResources(), contact.getPhoto(), R.drawable.avatar);

            mPhoto.setImageBitmap(maskedBitmap);
        }
    }

    private void setupFab() {
        mEditContactFab = (FloatingActionButton) findViewById(R.id.edit_contact_fab);
        mEditContactFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.greenLight)));
        mEditContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = new Contact();
                contact.setId(mContactId);
                contact.setName(new Name(mName.getText().toString(), mSurname.getText().toString()));
                ContactDao contactDao = new ContactDao(EditContactActivity.this);
                contactDao.updateContact(contact);
                finish();
            }
        });
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
