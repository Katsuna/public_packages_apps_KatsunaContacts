package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.utils.ImageHelper;

public class CreateContactActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView mPhoto;
    private EditText mName;
    private EditText mSurname;
    private EditText mTelephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        initControls();
    }

    private void initControls() {
        mPhoto = (ImageView) findViewById(R.id.photo);
        mName = (EditText) findViewById(R.id.name);
        mSurname = (EditText) findViewById(R.id.surname);
        mTelephone = (EditText) findViewById(R.id.telephone);

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (mName.getText().length() == 0) {
                        mName.setError(getResources().getString(R.string.name_validation));
                    } else {
                        mName.setError(null);
                        mSurname.setVisibility(View.VISIBLE);
                        return false;
                    }
                }
                return true;
            }
        });

        mSurname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (mSurname.getText().length() == 0) {
                        mSurname.setError(getResources().getString(R.string.surname_validation));
                    } else {
                        mSurname.setError(null);
                        mTelephone.setVisibility(View.VISIBLE);
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

                    //validate and save

                }
                return false;
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
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap centerCropped = ImageHelper.centerCrop(imageBitmap);
            Bitmap maskedBitmap = ImageHelper.getMaskedBitmap(getResources(), centerCropped, R.drawable.avatar);
            //mPhoto.setImageBitmap(imageBitmap);
            mPhoto.setImageBitmap(maskedBitmap);
        }
    }
}
