package com.katsuna.contacts.ui.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.utils.Log;
import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.domain.Phone;
import com.katsuna.contacts.providers.ContactProvider;
import com.katsuna.contacts.ui.adapters.ContactsRecyclerViewAdapter;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;
import com.katsuna.contacts.utils.ContactArranger;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends KatsunaActivity implements IContactInteractionListener {

    private final static String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static final int REQUEST_CODE_ASK_CALL_PERMISSION = 2;
    private static final int REQUEST_CODE_EDIT_CONTACT = 3;
    private static final int POPUP_INACTIVITY_THRESHOLD = 10000;
    private static final int POPUP_HANDLER_DELAY = 1000;
    private List<ContactListItemModel> mModels;
    private ContactsRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private DrawerLayout drawerLayout;
    private TextView mNoResultsView;
    private SearchView mSearchView;
    private Contact mSelectedContact;
    private FloatingActionButton mNewContactFab;
    private FloatingActionButton mSearchFab;
    private FrameLayout mPopupFrame;
    private long mLastTouchTimestamp;
    private Handler mPopupActionHandler;
    private boolean mPopupVisible;
    private boolean mSearchMode;
    private LinearLayout mFabContainer;
    private Button mNewContactButton;
    private Button mSearchContactsButton;
    private boolean mContactSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();
        initToolbar();
        setupDrawerLayout();
        setupFab();
    }

    @Override
    protected void onResume() {
        super.onResume();

        showPopup(false);
        if (mUserProfileChanged) {
            adjustFabPosition(false);
        }

        if (isChanged() || mUserProfileChanged) {
            loadContacts();
        }
    }

    private void adjustFabPosition(boolean verticalCenter) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabContainer.getLayoutParams();

        int verticalCenterGravity = verticalCenter ? Gravity.CENTER : Gravity.BOTTOM;

        if (mUserProfileContainer.isRightHanded()) {
            lp.gravity = Gravity.END | verticalCenterGravity;
        } else {
            lp.gravity = Gravity.START | verticalCenterGravity;
        }
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mLastTouchTimestamp = System.currentTimeMillis();
        initPopupActionHandler();

        mNoResultsView = (TextView) findViewById(R.id.no_results);

        mPopupFrame = (FrameLayout) findViewById(R.id.popup_frame);
        mPopupFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showPopup(false);
                return true;
            }
        });
        mFabContainer = (LinearLayout) findViewById(R.id.fab_container);

        mNewContactButton = (Button) findViewById(R.id.new_contact_button);
        mNewContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContact();
            }
        });
        mSearchContactsButton = (Button) findViewById(R.id.search_button);
    }

    private void showPopup(boolean show) {
        if (show) {
            //don't show popup if menu drawer is open or toolbar search is enabled
            // or contact is selected
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)
                    && !mSearchMode
                    && !mContactSelected) {
                mPopupFrame.setVisibility(View.VISIBLE);
                mNewContactButton.setVisibility(View.VISIBLE);
                mSearchContactsButton.setVisibility(View.VISIBLE);
                adjustFabPosition(true);
                mPopupVisible = true;
            }
        } else {
            mPopupFrame.setVisibility(View.GONE);
            mNewContactButton.setVisibility(View.GONE);
            mSearchContactsButton.setVisibility(View.GONE);
            adjustFabPosition(false);
            mPopupVisible = false;
            mLastTouchTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastTouchTimestamp = System.currentTimeMillis();
        return super.dispatchTouchEvent(ev);
    }

    private void initPopupActionHandler() {
        mPopupActionHandler = new Handler();
        mPopupActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now - POPUP_INACTIVITY_THRESHOLD > mLastTouchTimestamp && !mPopupVisible) {
                    showPopup(true);
                }
                mPopupActionHandler.postDelayed(this, POPUP_HANDLER_DELAY);
            }
        }, POPUP_HANDLER_DELAY);
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black87_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showPopup(false);
                search(newText);
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchMode = false;
                mAdapter.resetFilter();
                return false;
            }
        });
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchMode = true;
                showPopup(false);
            }
        });

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, e);
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

    private void setupDrawerLayout() {
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        assert view != null;
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.drawer_settings:
                        markChanged();
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case R.id.drawer_help:
                        break;
                    case R.id.drawer_info:
                        break;
                }

                return true;
            }
        });
    }

    private boolean isChanged() {
        return mModels == null;
    }

    private void markChanged() {
        mModels = null;
    }

    private void setupFab() {
        mNewContactFab = (FloatingActionButton) findViewById(R.id.new_contact_fab);
        assert mNewContactFab != null;
        mNewContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContact();
            }
        });

        int colorBlue = ContextCompat.getColor(this, R.color.katsuna_blue);
        mNewContactFab.setBackgroundTintList(ColorStateList.valueOf(colorBlue));

        mSearchFab = (FloatingActionButton) findViewById(R.id.search_fab);
        int colorPink = ContextCompat.getColor(this, R.color.katsuna_pink);
        mSearchFab.setBackgroundTintList(ColorStateList.valueOf(colorPink));
    }

    private void tintFabs(boolean flag) {
        int addContactColor;
        int searchContactsColor;
        if (flag) {
            addContactColor = ContextCompat.getColor(this, R.color.katsuna_blue_tinted);
            searchContactsColor = ContextCompat.getColor(this, R.color.katsuna_pink_tinted);
        } else {
            addContactColor = ContextCompat.getColor(this, R.color.katsuna_blue);
            searchContactsColor = ContextCompat.getColor(this, R.color.katsuna_pink);
        }
        mNewContactFab.setBackgroundTintList(ColorStateList.valueOf(addContactColor));
        mSearchFab.setBackgroundTintList(ColorStateList.valueOf(searchContactsColor));
    }

    private void createContact() {
        Intent i = new Intent(MainActivity.this, CreateContactActivity.class);
        startActivityForResult(i, REQUEST_CODE_EDIT_CONTACT);
    }

    private void loadContacts() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            return;
        }

        //get contacts from device
        ContactProvider contactProvider = new ContactProvider(this);
        List<Contact> contactList = contactProvider.getContacts();
        mModels = ContactArranger.getContactsProcessed(contactList);
        mAdapter = new ContactsRecyclerViewAdapter(mModels, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                showNoResultsView();
            }
        });
        showNoResultsView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.e(TAG, "read contacts permission granted");
                    loadContacts();
                }
                break;
            case REQUEST_CODE_ASK_CALL_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.e(TAG, "call contact permission granted");
                    callContact(mSelectedContact);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_CONTACT) {
            if (resultCode == RESULT_OK) {
                String contactId = data.getStringExtra("contactId");
                loadContacts();

                //invalidate cached photo
                Uri photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
                Picasso.with(this).invalidate(photoUri);

                int position = mAdapter.getPositionByContactId(contactId);
                if (position != -1) {
                    selectContact(position);
                }
            }
        }
    }

    @Override
    public void selectContact(int position) {
        if (mContactSelected) {
            mContactSelected = false;
            mAdapter.deselectContact();
            tintFabs(false);
        } else {
            mContactSelected = true;
            mAdapter.selectContactAtPosition(position);

            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int height = lm.findViewByPosition(position).getMeasuredHeight();

            ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .scrollToPositionWithOffset(position, (mRecyclerView.getHeight() / 2) - height);

            tintFabs(true);
        }
    }

    @Override
    public void editContact(String contactId) {
        Intent i = new Intent(this, EditContactActivity.class);
        i.putExtra("contactId", contactId);
        startActivityForResult(i, REQUEST_CODE_EDIT_CONTACT);
    }

    @Override
    public void callContact(Contact contact) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            mSelectedContact = contact;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE},
                    REQUEST_CODE_ASK_CALL_PERMISSION);
            return;
        }

        final List<Phone> phones = new ContactProvider(this).getPhones(contact.getId());
        if (phones.size() == 1) {
            callNumber(phones.get(0).getNumber());
        } else {
            phoneNumbersDialog(phones, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    callNumber(phones.get(i).getNumber());
                }
            });
        }
    }

    private void phoneNumbersDialog(List<Phone> phones, DialogInterface.OnClickListener listener) {
        final CharSequence phonesArray[] = new CharSequence[phones.size()];
        int i = 0;
        for (Phone phone : phones) {
            phonesArray[i++] = phone.getNumber();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_title, null);
        builder.setCustomTitle(view);
        builder.setItems(phonesArray, listener);
        builder.show();
    }

    private void callNumber(String number) {
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        startActivity(i);
    }

    @Override
    public void sendSMS(Contact contact) {
        final List<Phone> phones = new ContactProvider(this).getPhones(contact.getId());
        if (phones.size() == 1) {
            sendSmsToNumber(phones.get(0).getNumber());
        } else {
            phoneNumbersDialog(phones, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sendSmsToNumber(phones.get(i).getNumber());
                }
            });
        }

    }

    @Override
    public UserProfileContainer getUserProfileContainer() {
        return mUserProfileContainer;
    }

    private void sendSmsToNumber(String number) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }

    private void search(String query) {
        if (TextUtils.isEmpty(query)) {
            mAdapter.resetFilter();
        } else {
            mAdapter.getFilter().filter(query);
        }
    }

    private void showNoResultsView() {
        if (mAdapter.getItemCount() > 0) {
            mNoResultsView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoResultsView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }
}