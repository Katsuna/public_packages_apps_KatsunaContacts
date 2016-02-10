package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.utils.Constants;
import gr.crystalogic.oldmen.utils.DirectoryChooserDialog;
import gr.crystalogic.oldmen.utils.VCardHelper;

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

    private void initControls() {
        Button deleteButton = (Button) findViewById(R.id.deleteContacts);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SelectContactsActivity.class));
            }
        });

        Button exportButton = (Button) findViewById(R.id.exportContacts);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(SettingsActivity.this,
                                new DirectoryChooserDialog.ChosenDirectoryListener() {
                                    @Override
                                    public void onChosenDir(String chosenDir) {
                                        exportContacts(chosenDir);
                                    }
                                });

                directoryChooserDialog.setNewFolderEnabled(true);
                directoryChooserDialog.chooseDirectory();
            }

        });


        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.display_sort_group);
        mSurnameFirstRadioButton = (RadioButton) findViewById(R.id.surname_first_button);
        mNameFirstRadioButton = (RadioButton) findViewById(R.id.name_first_button);

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

    private void exportContacts(String directory) {
        IContactDao dao = new ContactDao(this);
        List<Contact> contactList = dao.getContactsForExport();

        List<VCard> vCards = new ArrayList<>();
        for (Contact contact : contactList) {
            VCard vCard = VCardHelper.getVCard(contact);
            vCards.add(vCard);
        }

        try {
            String fullPath = directory + File.separator + "contacts.vcf";
            File file = new File(fullPath);
            VCardWriter writer = new VCardWriter(file, VCardVersion.V4_0);
            try {
                for (VCard vcard : vCards) {
                    writer.write(vcard);
                }
            } finally {
                writer.close();
            }
            Toast.makeText(this, String.format(getResources().getString(R.string.contacts_export_completed), fullPath), Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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

}
