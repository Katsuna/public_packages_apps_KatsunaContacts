/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.contacts.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.providers.ContactProvider;
import com.katsuna.commons.ui.SettingsActivityBase;
import com.katsuna.commons.utils.ColorAdjusterV2;
import com.katsuna.commons.utils.ColorCalcV2;
import com.katsuna.commons.utils.Constants;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.commons.utils.SettingsManager;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.contacts.R;
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

public class SettingsActivity extends SettingsActivityBase {

    private static final String TAG = "SettingsActivity";

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;

    private RadioButton mSurnameFirstRadioButton;
    private RadioButton mNameFirstRadioButton;
    private ProgressDialog mProgressDialog;
    private TextView mImportContacts;
    private TextView mExportContacts;
    private CardView mContactsIoCard;
    private CardView mContactsDisplayCard;
    private View mContactsIoCardInner;
    private View mContactsDisplayCardInner;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyProfiles();
        formatDisplayNameSortingColor();
        initControlsListeners();
    }

    @Override
    protected void initControls() {
        super.initControls();
        initToolbar();
        mScrollViewContainer = findViewById(R.id.scroll_view_container);

        mContactsIoCard = findViewById(R.id.contacts_io_card);
        mContactsIoCardInner = findViewById(R.id.contacts_io_card_inner);

        mContactsDisplayCard = findViewById(R.id.contacts_display_card);
        mContactsDisplayCardInner = findViewById(R.id.contacts_display_card_inner);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mImportContacts = findViewById(R.id.import_contacts);
        mExportContacts = findViewById(R.id.export_contacts);

        mRadioGroup = findViewById(R.id.display_sort_group);
        mSurnameFirstRadioButton = findViewById(R.id.surname_first_button);
        mNameFirstRadioButton = findViewById(R.id.name_first_button);

        String displaySortSetting = SettingsManager.readSetting(SettingsActivity.this, Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_SURNAME);
        switch (displaySortSetting) {
            case Constants.DISPLAY_SORT_SURNAME:
                mSurnameFirstRadioButton.setChecked(true);
                break;
            case Constants.DISPLAY_SORT_NAME:
                mNameFirstRadioButton.setChecked(true);
                break;
        }
    }

    private void initControlsListeners() {
        assert mImportContacts != null;
        mImportContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importContacts();
            }
        });

        assert mExportContacts != null;
        mExportContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportContacts();
            }
        });

        assert mRadioGroup != null;
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = rGroup.findViewById(checkedId);

                if (mSurnameFirstRadioButton == checkedRadioButton) {

                    SettingsManager.setSetting(SettingsActivity.this, Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_SURNAME);
                }

                if (mNameFirstRadioButton == checkedRadioButton) {
                    SettingsManager.setSetting(SettingsActivity.this, Constants.DISPLAY_SORT_KEY, Constants.DISPLAY_SORT_NAME);
                }
                formatDisplayNameSortingColor();
            }
        });
    }

    private void formatDisplayNameSortingColor() {
        if (mUserProfileContainer == null) return;

        UserProfile profile = mUserProfileContainer.getActiveUserProfile();
        int colorChecked = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_2, profile.colorProfile);
        int colorBlack54 = ContextCompat.getColor(this, R.color.common_black54);
        if (mSurnameFirstRadioButton.isChecked()) {
            mSurnameFirstRadioButton.setTextColor(colorChecked);
        } else {
            mSurnameFirstRadioButton.setTextColor(colorBlack54);
        }
        if (mNameFirstRadioButton.isChecked()) {
            mNameFirstRadioButton.setTextColor(colorChecked);
        } else {
            mNameFirstRadioButton.setTextColor(colorBlack54);
        }
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

    @Override
    protected void applyColorProfile(ColorProfile colorProfile) {
        ColorProfile profile = colorProfile;
        if (colorProfile == ColorProfile.AUTO) {
            if (mUserProfileContainer.hasKatsunaServices()) {
                profile = ProfileReader.getUserProfileFromKatsunaServices(this).colorProfile;
            } else {
                profile = ColorProfile.COLOR_IMPAIREMENT;
            }
        }
        int primaryGrey1 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_GREY_1,
                profile);

        int secondaryGrey2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.SECONDARY_GREY_2,
                profile);

        int primary2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_2, profile);

        mContactsIoCard.setCardBackgroundColor(primaryGrey1);
        mContactsDisplayCard.setCardBackgroundColor(primaryGrey1);
        mContactsIoCardInner.setBackgroundColor(secondaryGrey2);
        mContactsDisplayCardInner.setBackgroundColor(secondaryGrey2);
        ColorAdjusterV2.setTextViewDrawableColor(mImportContacts, primary2);
        ColorAdjusterV2.setTextViewDrawableColor(mExportContacts, primary2);
        ColorAdjusterV2.adjustRadioButton(this, profile, mNameFirstRadioButton, 0, false);
        ColorAdjusterV2.adjustRadioButton(this, profile, mSurnameFirstRadioButton, 0, false);
        formatDisplayNameSortingColor();
    }

    @Override
    protected void applySizeProfile(SizeProfile profile) {
        ViewGroup topViewGroup = findViewById(android.R.id.content);
        SizeAdjuster.applySizeProfile(this, topViewGroup, profile);
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
