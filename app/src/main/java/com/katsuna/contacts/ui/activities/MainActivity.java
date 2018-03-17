package com.katsuna.contacts.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.katsuna.commons.controls.KatsunaNavigationView;
import com.katsuna.commons.ui.ContactsActivity;
import com.katsuna.commons.ui.listeners.IContactListener;
import com.katsuna.commons.ui.listeners.IContactsGroupListener;
import com.katsuna.commons.utils.Constants;
import com.katsuna.contacts.R;

import static com.katsuna.commons.utils.Constants.KATSUNA_PRIVACY_URL;

public class MainActivity extends ContactsActivity implements IContactsGroupListener,
        IContactListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAnalytics.getInstance(this);
    }

    protected void setupDrawerLayout() {
        KatsunaNavigationView mKatsunaNavigationView = findViewById(R.id.katsuna_navigation_view);
        mKatsunaNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.drawer_settings:
                        if (readContactsPermissionGranted()) {
                            markChanged();
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        }
                        break;
                    case R.id.drawer_info:
                        startActivity(new Intent(MainActivity.this, InfoActivity.class));
                        break;
                    case R.id.drawer_privacy:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(KATSUNA_PRIVACY_URL));
                        startActivity(browserIntent);
                        break;
                }

                return true;
            }
        });
        mKatsunaNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setupToolbar() {
        initToolbar(R.drawable.common_ic_menu_black_24dp);
    }

    protected void createContact() {
        if (writeContactsPermissionMissing()) {
            createContactRequestPending = true;
            return;
        } else {
            createContactRequestPending = false;
        }

        Intent i = new Intent(MainActivity.this, EditContactActivity.class);
        startActivityForResult(i, REQUEST_CODE_EDIT_CONTACT);
    }

    @Override
    protected void addNumberToExistingContact(long contactId, String numberToAddToExistingContact) {
        Intent i = new Intent(this, EditContactActivity.class);
        i.setAction(Constants.ADD_TO_CONTACT_ACTION);
        i.putExtra("contactId", contactId);
        i.putExtra(Constants.ADD_TO_CONTACT_ACTION_NUMBER, numberToAddToExistingContact);
        startActivityForResult(i, REQUEST_CODE_ADD_NUMBER_TO_CONTACT);
    }

    @Override
    public void editContact(long contactId) {
        if (writeContactsPermissionMissing()) {
            mContactIdForEdit = contactId;
            return;
        } else {
            mContactIdForEdit = 0;
        }

        Intent i = new Intent(this, EditContactActivity.class);
        i.putExtra("contactId", contactId);
        startActivityForResult(i, REQUEST_CODE_EDIT_CONTACT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                showPopup(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void setupFab() {
        mButtonsContainer2 = findViewById(com.katsuna.commons.R.id.new_contact_buttons_container);
        mButtonsContainer2.setVisibility(View.VISIBLE);
        mButtonsContainer1 = findViewById(com.katsuna.commons.R.id.search_buttons_container);

        mFab2 = findViewById(R.id.new_contact_fab);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContact();
            }
        });

        mFab1 = findViewById(R.id.search_fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });
    }

}