package com.katsuna.contacts.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.katsuna.commons.ui.SettingsKatsunaActivity;
import com.katsuna.commons.utils.SettingsManager;
import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.providers.ContactProvider;
import com.katsuna.contacts.utils.Constants;
import com.katsuna.contacts.utils.DirectoryChooserDialog;
import com.katsuna.contacts.utils.FileChooserDialog;
import com.katsuna.contacts.utils.VCardHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;

public class SettingsActivity extends SettingsKatsunaActivity {

    private static final String TAG = "SettingsActivity";

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;

    private RadioButton mSurnameFirstRadioButton;
    private RadioButton mNameFirstRadioButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initControls();
    }

    private void initControls() {
        initToolbar();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        Button deleteButton = (Button) findViewById(R.id.deleteContacts);
        assert deleteButton != null;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SelectContactsActivity.class));
            }
        });

        Button importButton = (Button) findViewById(R.id.importContacts);
        assert importButton != null;
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importContacts();
            }
        });

        Button exportButton = (Button) findViewById(R.id.exportContacts);
        assert exportButton != null;
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportContacts();
            }
        });


        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.display_sort_group);
        mSurnameFirstRadioButton = (RadioButton) findViewById(R.id.surname_first_button);
        mNameFirstRadioButton = (RadioButton) findViewById(R.id.name_first_button);

        assert mRadioGroup != null;
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(checkedId);

                if (mSurnameFirstRadioButton == checkedRadioButton) {

                    SettingsManager.setSetting(SettingsActivity.this, Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_SURNAME);
                }

                if (mNameFirstRadioButton == checkedRadioButton) {
                    SettingsManager.setSetting(SettingsActivity.this, Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_NAME);
                }
            }
        });

        String displaySortSetting = SettingsManager.readSetting(SettingsActivity.this, Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_SURNAME);
        switch (displaySortSetting) {
            case Constants.DISPLAY_SORT_SURNAME:
                mSurnameFirstRadioButton.setChecked(true);
                break;
            case Constants.DISPLAY_SORT_NAME:
                mNameFirstRadioButton.setChecked(true);
                break;
        }

        initSizeProfiles();
        initColorProfiles();
        initRightHand();
    }

    private void importContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        FileChooserDialog fileChooserDialog =
                new FileChooserDialog(this,
                        new FileChooserDialog.ChosenFileListener() {
                            @Override
                            public void onChosenFile(String chosenFile) {
                                new ImportContactsAsyncTask().execute(chosenFile);
                            }
                        });

        fileChooserDialog.choose();
    }

    private void exportContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        DirectoryChooserDialog directoryChooserDialog =
                new DirectoryChooserDialog(SettingsActivity.this,
                        new DirectoryChooserDialog.ChosenDirectoryListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                new ExportContactsAsyncTask().execute(chosenDir);
                            }
                        });

        directoryChooserDialog.setNewFolderEnabled(true);
        directoryChooserDialog.chooseDirectory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    importContacts();
                }
                break;
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    exportContacts();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class ExportContactsAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage(getResources().getString(R.string.exporting_contacts));
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String directory = params[0];

            String fullPath;

            ContactProvider contactProvider = new ContactProvider(SettingsActivity.this);
            List<Contact> contactList = contactProvider.getContactsForExport();

            List<VCard> vCards = new ArrayList<>();
            for (Contact contact : contactList) {
                VCard vCard = VCardHelper.getVCard(contact);
                vCards.add(vCard);
            }

            try {
                fullPath = directory + File.separator + "contacts.vcf";
                File file = new File(fullPath);
                try (VCardWriter writer = new VCardWriter(file, VCardVersion.V3_0)) {
                    for (VCard vcard : vCards) {
                        writer.write(vcard);
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                throw new RuntimeException(e);
            }

            return fullPath;
        }

        @Override
        protected void onPostExecute(String fullPath) {
            mProgressDialog.dismiss();
            Toast.makeText(SettingsActivity.this, String.format(getResources().getString(R.string.contacts_export_completed), fullPath), Toast.LENGTH_LONG).show();
        }
    }

    private class ImportContactsAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage(getResources().getString(R.string.importing_contacts));
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String fullPath = params[0];

            try {
                File file = new File(fullPath);
                List<VCard> vCards = Ezvcard.parse(file).all();

                if (vCards.size() == 0) {
                    return R.string.invalid_vcf;
                }

                ContactProvider contactProvider = new ContactProvider(SettingsActivity.this);
                for (VCard vCard : vCards) {
                    Contact contact = VCardHelper.getContact(vCard);
                    contactProvider.importContact(contact);
                }

                return R.string.import_completed;

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Integer resultResourceId) {
            mProgressDialog.dismiss();
            Toast.makeText(SettingsActivity.this, resultResourceId, Toast.LENGTH_SHORT).show();
        }
    }
}
