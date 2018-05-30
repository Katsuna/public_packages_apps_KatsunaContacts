package com.katsuna.contacts.ui.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.contacts.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class PhotoActivity extends KatsunaActivity {

    private static final String TAG = "PhotoActivity";
    private static final String AOSP_CAMERA_PACKAGE = "com.android.camera2";
    private static final String AOSP_CAMERA_ACTIVITY = "com.android.camera.CaptureActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 3;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 4;

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
                    dispatchSelectPictureIntent();
                } else if (items[item].equals(getString(R.string.remove_photo))) {
                    removePhoto();
                } else if (items[item].equals(getString(android.R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchSelectPictureIntent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)),
                SELECT_FILE);
    }

    private void dispatchTakePictureIntent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // use explicitly aosp app if possible
        if (aospCameraIsInstalledAndEnabled()) {
            takePictureIntent.setComponent(new ComponentName(AOSP_CAMERA_PACKAGE,
                    AOSP_CAMERA_ACTIVITY));
        }
        ComponentName name = takePictureIntent.resolveActivity(getPackageManager());

        // Ensure that there's a camera activity to handle the intent
        if (name != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //TODO handle exceptions
                Log.e(TAG, ex.toString());
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mUri = getUriFromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, R.string.no_camera_app, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean aospCameraIsInstalledAndEnabled() {
        boolean output = false;
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(AOSP_CAMERA_PACKAGE, 0);
            output = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, " Exception: " + e);
        }
        return output;
    }

    private Uri getUriFromFile(File photoFile) {

        if (android.os.Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(PhotoActivity.this, "com.katsuna.contacts.provider",
                    photoFile);
        } else {
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
            case REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    dispatchSelectPictureIntent();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    abstract void loadPhoto(Uri uri);

    abstract void removePhoto();
}
