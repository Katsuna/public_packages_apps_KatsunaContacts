package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup mRadioGroup;
    private RadioButton mSurnameFirstRadioButton;
    private RadioButton mNameFirstRadioButton;
    private Spinner mContactFilter;

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

        mRadioGroup = (RadioGroup) findViewById(R.id.display_sort_group);
        mSurnameFirstRadioButton = (RadioButton) findViewById(R.id.surname_first_button);
        mNameFirstRadioButton = (RadioButton) findViewById(R.id.name_first_button);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(checkedId);

                boolean isChecked = checkedRadioButton.isChecked();
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

        mContactFilter = (Spinner) findViewById(R.id.spinner_contacts_filters);
        mContactFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                handleSpinnerSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String contactFilter = readSetting(Constants.CONTACTS_FILTER_KEY, Constants.CONTACTS_FILTER_ALL);
        mContactFilter.setSelection(Integer.parseInt(contactFilter));
    }

    private void handleSpinnerSelection(String selection) {
        String valueToSet = null;
        if (selection.equals(getResources().getString(R.string.contacts_filter_all))) {
            valueToSet = Constants.CONTACTS_FILTER_ALL;
        } else if (selection.equals(getResources().getString(R.string.contacts_filter_sim))) {
            valueToSet = Constants.CONTACTS_FILTER_SIM;
        } else if (selection.equals(getResources().getString(R.string.contacts_filter_phone))) {
            valueToSet = Constants.CONTACTS_FILTER_PHONE;
        } else if (selection.equals(getResources().getString(R.string.contacts_filter_google))) {
            valueToSet = Constants.CONTACTS_FILTER_GOOGLE;
        } else if (selection.equals(getResources().getString(R.string.contacts_filter_skype))) {
            valueToSet = Constants.CONTACTS_FILTER_SKYPE;
        } else if (selection.equals(getResources().getString(R.string.contacts_filter_viber))) {
            valueToSet = Constants.CONTACTS_FILTER_VIBER;
        }

        setSetting(Constants.CONTACTS_FILTER_KEY, valueToSet);
    }

    private void setSetting(String key, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String readSetting(String key, String defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString(key, defaultValue);
    }

}
