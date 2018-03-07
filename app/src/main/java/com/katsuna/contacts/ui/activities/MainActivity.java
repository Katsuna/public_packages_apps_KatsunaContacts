package com.katsuna.contacts.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.katsuna.commons.controls.KatsunaNavigationView;
import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.domain.Phone;
import com.katsuna.commons.entities.KatsunaConstants;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.providers.ContactProvider;
import com.katsuna.commons.ui.SearchBarActivity;
import com.katsuna.commons.ui.adapters.models.ContactsGroup;
import com.katsuna.commons.utils.Constants;
import com.katsuna.commons.utils.ContactArranger;
import com.katsuna.commons.utils.KatsunaAlertBuilder;
import com.katsuna.contacts.R;
import com.katsuna.contacts.data.FetchContactsInfoAsyncTask;
import com.katsuna.contacts.ui.adapters.ContactsGroupAdapter;
import com.katsuna.contacts.ui.listeners.IContactListener;
import com.katsuna.contacts.ui.listeners.IContactsGroupListener;
import com.konifar.fab_transformation.FabTransformation;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.katsuna.commons.utils.Constants.KATSUNA_PRIVACY_URL;

public class MainActivity extends SearchBarActivity implements IContactsGroupListener,
        IContactListener {

    private final static String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static final int REQUEST_CODE_ASK_CALL_PERMISSION = 2;
    private static final int REQUEST_CODE_EDIT_CONTACT = 3;
    private static final int REQUEST_CODE_ADD_NUMBER_TO_CONTACT = 4;
    private List<ContactsGroup> mModels;
    private ContactsGroupAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private DrawerLayout drawerLayout;
    private TextView mNoResultsView;
    private Contact mSelectedContact;
    private FrameLayout mPopupFrame;
    private int mSelectedPosition;
    private boolean mReadContactsPermissionDontAsk = false;
    private boolean reloadData = true;
    private String numberToAddToExistingContact;
    private boolean mSearchMode;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAnalytics.getInstance(this);
        initControls();
        setupDrawerLayout();
        setupFab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        if (searchManager != null) {
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return false;
                }
            });
            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    mSearchMode = false;
                    if (mAdapter != null) {
                        mAdapter.resetFilter();
                    }
                    return false;
                }
            });
            mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSearchMode = true;
                    showPopup(false);
                    showFabToolbar(false);
                }
            });
        }

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

    private void search(String query) {
        if (mAdapter == null) return;
        if (TextUtils.isEmpty(query)) {
            mAdapter.resetFilter();
        } else {
            mAdapter.filter(query);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        showPopup(false);

        if (reloadData) {
            loadContacts();
        } else {
            // we don't reload after edit contact activity.
            // set default value to reenable reloading
            reloadData = true;
        }

        // keep contact selected after an outgoing call
        if (mItemSelected) {
            selectContactsGroup(mSelectedPosition);
        } else {
            deselectItem();
        }

        handleIntent();
    }

    @Override
    protected void onStop() {
        super.onStop();
        deselectItem();
    }

    private void initControls() {
        initToolbar(R.drawable.common_ic_menu_black_24dp);
        mLettersList = findViewById(R.id.letters_list);

        mRecyclerView = findViewById(R.id.contacts_list);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                // findPosition to highlight

                LinearLayoutManager lm = ((LinearLayoutManager) mRecyclerView.getLayoutManager());
                int firstVisibleItemPosition = lm.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();

                if (firstVisibleItemPosition < lastVisibleItemPosition) {
                    View firstVisibleView = lm.findViewByPosition(firstVisibleItemPosition);
                    Rect outR = new Rect();
                    firstVisibleView.getHitRect(outR);

                    int positionToHighlight;
                    if (outR.bottom - 300 < 0) {
                        positionToHighlight = firstVisibleItemPosition + 1;

                        // order highlight
                        if (mAdapter != null) {
                            mAdapter.highlightContactsGroup(positionToHighlight);
                        }
                    }
                }
            }
        });


        drawerLayout = findViewById(R.id.drawer_layout);

        mLastTouchTimestamp = System.currentTimeMillis();
        initPopupActionHandler();

        initDeselectionActionHandler();

        mNoResultsView = findViewById(R.id.no_results);

        mPopupFrame = findViewById(R.id.popup_frame);
        mPopupFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(false);
            }
        });
        mFabContainer = findViewById(R.id.fab_container);

        mPopupButton2 = findViewById(R.id.new_contact_button);
        mPopupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContact();
            }
        });

        mPopupButton1 = findViewById(R.id.search_button);
        mPopupButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });

        mFabToolbar = findViewById(R.id.fab_toolbar);
        mNextButton = findViewById(R.id.next_page_button);
        mNextButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            final Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    mLettersList.scrollBy(0, 30);
                    mHandler.postDelayed(this, 10);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.performClick();
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 10);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }
        });

        mPrevButton = findViewById(R.id.prev_page_button);
        mPrevButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            final Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    mLettersList.scrollBy(0, -30);
                    mHandler.postDelayed(this, 10);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.performClick();
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 10);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }
        });

        mViewPagerContainer = findViewById(R.id.viewpager_container);
        mButtonsContainer2 = findViewById(R.id.new_contact_buttons_container);
        mButtonsContainer1 = findViewById(R.id.search_buttons_container);
        mFabToolbarContainer = findViewById(R.id.fab_toolbar_container);
    }

    @Override
    protected void showPopup(boolean show) {
        if (show) {
            //don't show popup if menu drawer is open or toolbar search is enabled
            // or contact is selected or search with letters is shown.
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)
                    && !mSearchMode
                    && !mItemSelected
                    && !mFabToolbarOn) {
                mPopupFrame.setVisibility(View.VISIBLE);
                mPopupButton1.setVisibility(View.VISIBLE);
                mPopupButton2.setVisibility(View.VISIBLE);
                mPopupVisible = true;
            }
        } else {
            mPopupFrame.setVisibility(View.GONE);
            mPopupButton1.setVisibility(View.GONE);
            mPopupButton2.setVisibility(View.GONE);
            mPopupVisible = false;
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (Constants.ADD_TO_CONTACT_ACTION.equals(action)) {
                numberToAddToExistingContact =
                        intent.getStringExtra(Constants.ADD_TO_CONTACT_ACTION_NUMBER);
                mFab2.post(new Runnable() {
                    @Override
                    public void run() {
                        showFabToolbar(true);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        refreshLastTouchTimestamp();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (mFabToolbarOn) {
            showFabToolbar(false);
        } else if (mPopupVisible) {
            showPopup(false);
        } else if (mItemSelected) {
            deselectItem();
        } else if (mSearchMode) {
            mSearchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }
    }

    private void showFabToolbar(boolean show) {
        if (!readContactsPermissionGranted()) {
            return;
        }

        if (show) {
            mSearchView.onActionViewCollapsed();

            FabTransformation.with(mFab2).duration(Constants.FAB_TRANSFORMATION_DURATION)
                    .transformTo(mFabToolbar);

            if (mPopupVisible) {
                showPopup(false);
            }
            if (mItemSelected) {
                deselectItem();
            }

            mFab1.setVisibility(View.INVISIBLE);
        } else {
            FabTransformation.with(mFab2).duration(Constants.FAB_TRANSFORMATION_DURATION)
                    .transformFrom(mFabToolbar);
            mAdapter.deselectContactsGroup();
            mFab1.setVisibility(View.VISIBLE);
        }
        mFabToolbarOn = show;
    }

    @Override
    public void selectItemByStartingLetter(String letter) {
        if (mAdapter != null) {
            deselectItem();
            int position = mAdapter.getPositionByStartingLetter(letter);
            scrollToPositionWithOffset(position, 0);
            mAdapter.selectContactsGroup(position);
        }
    }

    @Override
    public UserProfile getUserProfile() {
        return mUserProfileContainer.getActiveUserProfile();
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
    }

    private void markChanged() {
        mModels = null;
    }

    private void setupFab() {
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

    private void createContact() {
        if (!readContactsPermissionGranted()) {
            return;
        }

        Intent i = new Intent(MainActivity.this, EditContactActivity.class);
        startActivityForResult(i, REQUEST_CODE_EDIT_CONTACT);
    }

    private void loadContacts() {

        if (!readContactsPermissionGranted()) {
            return;
        }

        //get contacts from device
        ContactProvider contactProvider = new ContactProvider(this);
        List<Contact> contactList = contactProvider.getContacts();

        // load contants info cache
        Contact[] contactsArray = new Contact[contactList.size()];
        new FetchContactsInfoAsyncTask(new WeakReference<Context>(this))
                .execute(contactList.toArray(contactsArray));

        mModels = ContactArranger.getContactsGroups(contactList);
        mAdapter = new ContactsGroupAdapter(mModels, this, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                showNoResultsView();
            }
        });

        initializeFabToolbarWithContactGroups(mModels);

        showNoResultsView();
    }

    private boolean readContactsPermissionGranted() {
        boolean output;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (mReadContactsPermissionDontAsk) {
                Toast.makeText(MainActivity.this, R.string.common_go_to_settings_permissions,
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            }
            output = false;
        } else {
            output = true;
        }
        return output;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "read contacts permission granted");
                } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                    Log.d(TAG, "read contacts permission never ask again");
                    // User selected the Never Ask Again Option
                    mReadContactsPermissionDontAsk = true;
                } else {
                    Log.d(TAG, "read contacts permission denied");
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
                long contactId = data.getLongExtra("contactId", 0);
                loadContacts();

                //invalidate cached photo
                Uri photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                        contactId);
                Picasso.with(this).invalidate(photoUri);

                int position = mAdapter.getPositionByContactId(contactId);
                if (position != -1) {
                    focusOnContactGroup(position, 0);
                }
                reloadData = false;
            }
        } else if (requestCode == REQUEST_CODE_ADD_NUMBER_TO_CONTACT) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void selectContact(int contactGroupPosition, String letter, long contactId, int pos) {
        showFabToolbar(false);
        tintFabs(true);
        adjustFabPosition(false);
        mItemSelected = true;
        refreshLastSelectionTimestamp();

        if (!TextUtils.isEmpty(numberToAddToExistingContact)) {
            Intent i = new Intent(this, EditContactActivity.class);
            i.setAction(Constants.ADD_TO_CONTACT_ACTION);
            i.putExtra("contactId", contactId);
            i.putExtra(Constants.ADD_TO_CONTACT_ACTION_NUMBER, numberToAddToExistingContact);
            startActivityForResult(i, REQUEST_CODE_ADD_NUMBER_TO_CONTACT);
            return;
        }

        mAdapter.selectContactInGroup(contactGroupPosition, letter, contactId);
        // scroll to contact by using its position inside inner list
        // only for not starred contacts
        if (contactGroupPosition > 0) {
            int contactHeight = getResources().getDimensionPixelSize(R.dimen.contact_item_height);
            scrollToPositionWithOffset(contactGroupPosition, -pos * contactHeight);
        }
    }

    @Override
    protected void deselectItem() {
        mItemSelected = false;
        if (mAdapter != null) {
            mAdapter.deselectContactsGroup();
        }
        tintFabs(false);
        adjustFabPosition(true);

        //deselection mechanism
        refreshLastTouchTimestamp();
    }

    @Override
    public void selectContactsGroup(int position) {
        focusOnContactGroup(position, 0);
    }

    private void focusOnContactGroup(int position, int offset) {
        if (mFabToolbarOn) {
            showFabToolbar(false);
        }

        mSelectedPosition = position;

        mAdapter.selectContactsGroup(position);
        scrollToPositionWithOffset(position, offset);

        tintFabs(true);
        adjustFabPosition(false);
        mItemSelected = true;
        refreshLastSelectionTimestamp();
    }

    private void scrollToPositionWithOffset(int position, int offset) {
        ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .scrollToPositionWithOffset(position, offset);
    }

    @Override
    public void editContact(long contactId) {
        Intent i = new Intent(this, EditContactActivity.class);
        i.putExtra("contactId", contactId);
        startActivityForResult(i, REQUEST_CODE_EDIT_CONTACT);
    }

    @Override
    public void deleteContact(final Contact contact) {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(getString(R.string.common_delete_contact));
        String message = getString(R.string.common_delete_contact_approval,
                contact.getDisplayName());
        builder.setMessage(message);
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactProvider provider = new ContactProvider(MainActivity.this);
                provider.deleteContact(contact);
                loadContacts();
                Toast.makeText(MainActivity.this, R.string.contacts_deleted, Toast.LENGTH_LONG)
                        .show();
            }
        });
        builder.create().show();
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

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.alert_title, null);
            builder.setCustomTitle(view);
            builder.setItems(phonesArray, listener);
            builder.show();
        }
    }

    private void callNumber(String number) {
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(i);
    }

    @Override
    public void sendSMS(final Contact contact) {
        final List<Phone> phones = new ContactProvider(this).getPhones(contact.getId());
        if (phones.size() == 1) {
            sendSmsToNumber(phones.get(0).getNumber(), contact.getDisplayName());
        } else {
            phoneNumbersDialog(phones, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sendSmsToNumber(phones.get(i).getNumber(), contact.getDisplayName());
                }
            });
        }

    }

    @Override
    public UserProfileContainer getUserProfileContainer() {
        return mUserProfileContainer;
    }

    private void sendSmsToNumber(String number, String name) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
        i.putExtra(KatsunaConstants.EXTRA_DISPLAY_NAME, name);
        startActivity(i);
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