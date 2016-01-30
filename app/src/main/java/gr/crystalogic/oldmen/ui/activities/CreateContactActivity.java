package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Phone;

public class CreateContactActivity extends PhotoActivity {

    private RoundedImageView mPhoto;
    private EditText mName;
    private TextInputLayout mSurnameLayout;
    private EditText mSurname;
    private TextInputLayout mTelephoneLayout;
    private EditText mTelephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        initControls();
    }

    private void initControls() {
        mPhoto = (RoundedImageView) findViewById(R.id.photo);
        mName = (EditText) findViewById(R.id.name);
        mSurnameLayout = (TextInputLayout) findViewById(R.id.surnameLayout);
        mSurname = (EditText) findViewById(R.id.surname);
        mTelephoneLayout = (TextInputLayout) findViewById(R.id.telephoneLayout);
        mTelephone = (EditText) findViewById(R.id.telephone);

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mSurnameLayout.setVisibility(View.VISIBLE);
                    return false;
                }
                return true;
            }
        });

        mSurname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (mName.getText().length() == 0 && mSurname.getText().length() == 0) {
                        mSurname.setError(getResources().getString(R.string.name_validation));
                    } else {
                        mSurname.setError(null);
                        mTelephoneLayout.setVisibility(View.VISIBLE);
                        return false;
                    }
                }
                return true;
            }
        });

        mTelephone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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

                        ContactDao contactDao = new ContactDao(CreateContactActivity.this);
                        contactDao.addContact(c);

                        Intent intent = new Intent();
                        intent.putExtra("contactId", c.getId());
                        setResult(RESULT_OK, intent);

                        finish();
                    }
                }
                return false;
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
    }

    @Override
    void removePhoto() {
        mPhoto.setImageDrawable(null);
    }

}
