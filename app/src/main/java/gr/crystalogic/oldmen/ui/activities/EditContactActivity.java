package gr.crystalogic.oldmen.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.domain.Name;
import gr.crystalogic.oldmen.domain.Phone;

public class EditContactActivity extends AppCompatActivity {

    private ImageView mPhoto;
    private EditText mName;
    private EditText mSurname;
    private EditText mTelephone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        mPhoto = (ImageView) findViewById(R.id.photo);
        mName = (EditText) findViewById(R.id.name);
        mSurname = (EditText) findViewById(R.id.surname);
        mTelephone = (EditText) findViewById(R.id.telephone1);

        String contactId = getIntent().getStringExtra("contactId");
        ContactDao contactDao = new ContactDao(this);
        Name name = contactDao.getName(contactId);
        List<Phone> phones = contactDao.getPhones(contactId);
        Bitmap photo = contactDao.getImage(contactId, true);

        mName.setText(name.getName());
        mSurname.setText(name.getSurname());
        mTelephone.setText(phones.get(0).getNumber());
        if (photo != null) {
            mPhoto.setImageBitmap(photo);
        }

    }
}
