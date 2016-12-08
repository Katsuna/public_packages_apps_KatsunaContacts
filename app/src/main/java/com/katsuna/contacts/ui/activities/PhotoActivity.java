package com.katsuna.contacts.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.katsuna.contacts.R;

public abstract class PhotoActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 3;

    private Uri mUri;

    void selectImage() {

        final CharSequence[] items = {getString(R.string.take_photo),
                getString(R.string.choose_from_gallery), getString(R.string.remove_photo), getString(android.R.string.cancel)};
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
                } else if (items[item].equals(getString(R.string.remove_photo))) {
                    removePhoto();
                } else if (items[item].equals(getString(android.R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //TODO handle exceptions
                Log.e("TAG", ex.toString());
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mUri = getUriFromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private Uri getUriFromFile(File photoFile) {

        if (android.os.Build.VERSION.SDK_INT >= 24)
        {
            return FileProvider.getUriForFile(PhotoActivity.this, "com.katsuna.contacts.provider",
                    photoFile);
        }
        else
        {
            return Uri.fromFile(photoFile);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timeStamp = sdf.format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                loadPhoto(mUri);
            } else if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                loadPhoto(uri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    dispatchTakePictureIntent();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    abstract void loadPhoto(Uri uri);

    abstract void removePhoto();
}
