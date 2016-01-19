package gr.crystalogic.oldmen.ui.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.domain.Contact;

public class EditContactActivity extends AppCompatActivity {

    private ImageView mPhoto;
    private EditText mName;
    private EditText mSurname;
    private EditText mTelephone1;
    private EditText mTelephone2;
    private EditText mTelephone3;
    private EditText mEmail;
    private EditText mAddress;

    private FloatingActionButton mEditContactFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        mPhoto = (ImageView) findViewById(R.id.photo);
        mName = (EditText) findViewById(R.id.name);
        mSurname = (EditText) findViewById(R.id.surname);
        mTelephone1 = (EditText) findViewById(R.id.telephone1);
        mTelephone2 = (EditText) findViewById(R.id.telephone2);
        mTelephone3 = (EditText) findViewById(R.id.telephone3);
        mEmail = (EditText) findViewById(R.id.email);
        mAddress = (EditText) findViewById(R.id.address);

        String contactId = getIntent().getStringExtra("contactId");
        ContactDao contactDao = new ContactDao(this);

        Contact contact = contactDao.getContact(contactId);

        //set data on fields
        mName.setText(contact.getName().getName());
        mSurname.setText(contact.getName().getSurname());
        mTelephone1.setText(contact.getPrimaryTelephone());
        mTelephone2.setText(contact.getSecondaryTelephone());
        mTelephone3.setText(contact.getTertiaryTelephone());
        mEmail.setText(contact.getEmail());
        mAddress.setText(contact.getAddress());

        if (contact.getPhoto() != null) {
            mPhoto.setImageBitmap(contact.getPhoto());
        }

        setupFab();
    }

    private void setupFab() {
        mEditContactFab = (FloatingActionButton) findViewById(R.id.edit_contact_fab);
        mEditContactFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.greenLight)));
        mEditContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
    }

}
