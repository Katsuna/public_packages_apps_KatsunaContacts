package gr.crystalogic.oldmen.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import gr.crystalogic.oldmen.utils.ImageHelper;

public class CreateContactActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_FILE = 2;

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
                            Bitmap bitmap = ((RoundedDrawable)mPhoto.getDrawable()).getSourceBitmap();
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

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo),
                getString(R.string.choose_from_gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    dispatchTakePictureIntent();
                } else if (items[item].equals(getString(R.string.choose_from_gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), SELECT_FILE);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                Bitmap centeredBitmap = ImageHelper.centerCrop(bitmap);
                mPhoto.setImageBitmap(centeredBitmap);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();

                Picasso.with(this).load(selectedImageUri).fit().centerCrop().into(mPhoto);
            }
        }
    }

}
