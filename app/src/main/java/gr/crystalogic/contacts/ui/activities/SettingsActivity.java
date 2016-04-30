package gr.crystalogic.contacts.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import gr.crystalogic.contacts.R;
import gr.crystalogic.contacts.providers.ContactProvider;
import gr.crystalogic.contacts.domain.Contact;
import gr.crystalogic.contacts.utils.Constants;
import gr.crystalogic.contacts.utils.DirectoryChooserDialog;
import gr.crystalogic.contacts.utils.FileChooserDialog;
import gr.crystalogic.contacts.utils.VCardHelper;

public class SettingsActivity extends AppCompatActivity {

    private RadioButton mSurnameFirstRadioButton;
    private RadioButton mNameFirstRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initToolbar();
        initControls();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private ProgressDialog mProgressDialog;

    private void initControls() {

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
                FileChooserDialog fileChooserDialog =
                        new FileChooserDialog(SettingsActivity.this,
                                new FileChooserDialog.ChosenFileListener() {
                                    @Override
                                    public void onChosenFile(String chosenFile) {
                                        new ImportContactsAsyncTask().execute(chosenFile);
                                    }
                                });

                fileChooserDialog.choose();
            }

        });

        Button exportButton = (Button) findViewById(R.id.exportContacts);
        assert exportButton != null;
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                    setSetting(Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_SURNAME);
                }

                if (mNameFirstRadioButton == checkedRadioButton) {
                    setSetting(Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_NAME);
                }
            }
        });

        String displaySortSetting = readSetting(Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_SURNAME);
        switch (displaySortSetting) {
            case Constants.DISPLAY_SORT_SURNAME:
                mSurnameFirstRadioButton.setChecked(true);
                break;
            case Constants.DISPLAY_SORT_NAME:
                mNameFirstRadioButton.setChecked(true);
                break;
        }
    }

    private void setSetting(String key, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String readSetting(String key, String defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString(key, defaultValue);
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

            ContactProvider dao = new ContactProvider(SettingsActivity.this);
            List<Contact> contactList = dao.getContactsForExport();

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

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            return fullPath;
        }

        @Override
        protected void onPostExecute(String fullPath) {
            mProgressDialog.dismiss();
            Toast.makeText(SettingsActivity.this, String.format(getResources().getString(R.string.contacts_export_completed), fullPath), Toast.LENGTH_LONG).show();
        }
    }

    private class ImportContactsAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage(getResources().getString(R.string.importing_contacts));
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String fullPath = params[0];
            Log.e("TAG", fullPath);

            try {
                File file = new File(fullPath);
                List<VCard> vCards = Ezvcard.parse(file).all();

                if (vCards.size() == 0) {
                    Toast.makeText(SettingsActivity.this, R.string.invalid_vcf, Toast.LENGTH_SHORT).show();
                    return null;
                }

                ContactProvider dao = new ContactProvider(SettingsActivity.this);
                for (VCard vCard : vCards) {
                    Contact contact = VCardHelper.getContact(vCard);
                    dao.importContact(contact);
                }

            } catch (Exception e) {
                Log.e("TAG", e.getMessage());
                throw new RuntimeException(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressDialog.dismiss();
            Toast.makeText(SettingsActivity.this, R.string.import_completed, Toast.LENGTH_SHORT).show();
        }
    }


}
