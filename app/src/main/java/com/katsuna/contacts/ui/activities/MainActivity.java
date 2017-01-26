package com.katsuna.contacts.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.Log;
import com.katsuna.commons.utils.Shape;
import com.katsuna.contacts.R;
import com.katsuna.contacts.domain.Contact;
import com.katsuna.contacts.domain.Phone;
import com.katsuna.contacts.providers.ContactProvider;
import com.katsuna.contacts.ui.adapters.ContactsRecyclerViewAdapter;
import com.katsuna.contacts.ui.adapters.TabsPagerAdapter;
import com.katsuna.contacts.ui.adapters.models.ContactListItemModel;
import com.katsuna.contacts.ui.fragments.SearchBarFragment;
import com.katsuna.contacts.ui.listeners.IContactInteractionListener;
import com.katsuna.contacts.utils.ContactArranger;
import com.katsuna.contacts.utils.Separator;
import com.konifar.fab_transformation.FabTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends KatsunaActivity implements IContactInteractionListener,
        SearchBarFragment.OnFragmentInteractionListener {

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
    private FrameLayout mPopupFrame;
    private long mLastTouchTimestamp;
    private Handler mPopupActionHandler;
    private boolean mPopupVisible;
    private boolean mSearchMode;
    private Button mNewContactButton;
    private Button mSearchContactsButton;
    private boolean mContactSelected;
    private View mFabToolbar;
    private boolean mFabToolbarOn;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private ViewPager mViewPager;
    private LinearLayout mFabContainer;
    private LinearLayout mViewPagerContainer;
    private LinearLayout mNewContactButtonsContainer;
    private LinearLayout mSearchButtonsContainer;
    private FrameLayout mFabToolbarContainer;

    // chops a list into non-view sublists of length L
    private static <T> List<ArrayList<T>> chopped(List<T> list, final int L) {
        List<ArrayList<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();
        setupDrawerLayout();
        setupFab();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showPopup(false);

        if (isChanged() || mUserProfileChanged) {
            loadContacts();
        }

        if (mUserProfileChanged) {
            // color profile adjustments
            ColorProfile profile = mUserProfileContainer.getColorProfile();
            adjustPopupButtons(profile);
            adjustSearchBar(profile);

            adjustRightHand();
        }
    }

    private void adjustRightHand() {
        if (mUserProfileContainer.isRightHanded()) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabToolbarContainer.getLayoutParams();
            lp.gravity = Gravity.END;
            mFabContainer.setGravity(Gravity.END | Gravity.CENTER);

            //set shadow
            mFabToolbar.setBackground(getDrawable(R.drawable.search_bar_bg));
            int shadowPixels = getResources().getDimensionPixelSize(R.dimen.search_shadow);
            mFabToolbar.setPadding(shadowPixels, 0, 0, 0);

            positionFabsToLeft(false);
        } else {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabToolbarContainer.getLayoutParams();
            lp.gravity = Gravity.START;
            mFabContainer.setGravity(Gravity.START | Gravity.CENTER);

            mFabToolbar.setBackground(getDrawable(R.drawable.search_bar_bg_left_handed));
            int shadowPixels = getResources().getDimensionPixelSize(R.dimen.search_shadow);
            mFabToolbar.setPadding(0, 0, shadowPixels, 0);

            positionFabsToLeft(true);
        }
    }

    private void positionFabsToLeft(boolean flag) {
        mNewContactButtonsContainer.removeAllViews();
        mSearchButtonsContainer.removeAllViews();

        if (flag) {
            mNewContactButtonsContainer.addView(mFab2);
            mNewContactButtonsContainer.addView(mNewContactButton);

            mSearchButtonsContainer.addView(mFab1);
            mSearchButtonsContainer.addView(mSearchContactsButton);
        } else {
            mNewContactButtonsContainer.addView(mNewContactButton);
            mNewContactButtonsContainer.addView(mFab2);

            mSearchButtonsContainer.addView(mSearchContactsButton);
            mSearchButtonsContainer.addView(mFab1);
        }
    }

    private void adjustSearchBar(ColorProfile profile) {
        int accentColor1 = ColorCalc.getColor(this, ColorProfileKey.ACCENT1_COLOR, profile);
        mViewPagerContainer.setBackgroundColor(accentColor1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        deselectContact();
    }

    private void adjustFabPosition(boolean verticalCenter) {
        int verticalCenterGravity = verticalCenter ? Gravity.CENTER : Gravity.BOTTOM;
        if (mUserProfileContainer.isRightHanded()) {
            mFabContainer.setGravity(verticalCenterGravity | Gravity.END);
        } else {
            mFabContainer.setGravity(verticalCenterGravity | Gravity.START);
        }
    }

    private void adjustPopupButtons(ColorProfile profile) {
        int color1 = ColorCalc.getColor(this, ColorProfileKey.ACCENT1_COLOR, profile);
        Shape.setRoundedBackground(mSearchContactsButton, color1);

        int color2 = ColorCalc.getColor(this, ColorProfileKey.ACCENT2_COLOR, profile);
        Shape.setRoundedBackground(mNewContactButton, color2);
    }

    private void initControls() {
        initToolbar(R.drawable.common_ic_menu_black_24dp);

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
        mSearchContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });

        mFabToolbar = findViewById(R.id.fab_toolbar);
        mNextButton = (ImageButton) findViewById(R.id.next_page_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                adjustFabToolbarNavButtonsVisibility();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_page_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                adjustFabToolbarNavButtonsVisibility();
            }
        });

        mViewPagerContainer = (LinearLayout) findViewById(R.id.viewpager_container);
        mNewContactButtonsContainer = (LinearLayout)
                findViewById(R.id.new_contact_buttons_container);
        mSearchButtonsContainer = (LinearLayout) findViewById(R.id.search_buttons_container);
        mFabToolbarContainer = (FrameLayout) findViewById(R.id.fab_toolbar_container);
    }

    private void showPopup(boolean show) {
        if (show) {
            //don't show popup if menu drawer is open or toolbar search is enabled
            // or contact is selected or search with letters is shown.
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)
                    && !mSearchMode
                    && !mContactSelected
                    && !mFabToolbarOn) {
                mPopupFrame.setVisibility(View.VISIBLE);
                mNewContactButton.setVisibility(View.VISIBLE);
                mSearchContactsButton.setVisibility(View.VISIBLE);
                mPopupVisible = true;
            }
        } else {
            mPopupFrame.setVisibility(View.GONE);
            mNewContactButton.setVisibility(View.GONE);
            mSearchContactsButton.setVisibility(View.GONE);
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
        } else if (mFabToolbarOn) {
            showFabToolbar(false);
        } else {
            super.onBackPressed();
        }
    }

    private void showFabToolbar(boolean show) {
        int duration = 550;
        if (show) {
            FabTransformation.with(mFab1).duration(duration)
                    .transformTo(mFabToolbar);

            if (mPopupVisible) {
                showPopup(false);
            }
        } else {
            FabTransformation.with(mFab1).duration(duration)
                    .transformFrom(mFabToolbar);
        }
        mFabToolbarOn = show;
    }

    @Override
    public void selectContactByStartingLetter(String letter) {
        if (mAdapter != null) {
            int position = mAdapter.getPositionByStartingLetter(letter);
            focusOnContact(position);
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
        mFab1 = (FloatingActionButton) findViewById(R.id.search_fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });

        mFab2 = (FloatingActionButton) findViewById(R.id.new_contact_fab);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContact();
            }
        });
    }

    private void tintFabs(boolean flag) {
        int addContactColor;
        int searchContactsColor;
        if (flag) {
            addContactColor = ContextCompat.getColor(this, R.color.common_blue_tinted);
            searchContactsColor = ContextCompat.getColor(this, R.color.common_pink_tinted);
        } else {
            ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
            int color1 = ColorCalc.getColor(this, ColorProfileKey.ACCENT1_COLOR, colorProfile);
            int color2 = ColorCalc.getColor(this, ColorProfileKey.ACCENT2_COLOR, colorProfile);
            searchContactsColor = color1;
            addContactColor = color2;
        }
        mFab1.setBackgroundTintList(ColorStateList.valueOf(searchContactsColor));
        mFab2.setBackgroundTintList(ColorStateList.valueOf(addContactColor));
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

        initializeFabToolbar();

        showNoResultsView();
    }

    private void initializeFabToolbar() {
        List<String> letters = new ArrayList<>();

        for (ContactListItemModel contactListItemModel : mModels) {
            if (!contactListItemModel.isPremium()) {
                if (contactListItemModel.getSeparator() == Separator.FIRST_LETTER) {
                    letters.add(contactListItemModel.getContact().getDisplayName().substring(0, 1));
                }
            }
        }

        List<ArrayList<String>> lettersLists = chopped(letters, 20);

        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        for (ArrayList<String> lettersList : lettersLists) {
            fragmentArrayList.add(SearchBarFragment.newInstance(lettersList));
        }

        TabsPagerAdapter mLetterAdapter = new TabsPagerAdapter(getSupportFragmentManager(),
                fragmentArrayList);
        mViewPager.setAdapter(mLetterAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                adjustFabToolbarNavButtonsVisibility();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        adjustFabToolbarNavButtonsVisibility();
    }

    private void adjustFabToolbarNavButtonsVisibility() {
        int pages = mViewPager.getChildCount();
        int currentPage = mViewPager.getCurrentItem();

        if (pages == currentPage + 1) {
            mNextButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setVisibility(View.VISIBLE);
        }

        if (currentPage == 0) {
            mPrevButton.setVisibility(View.INVISIBLE);
        } else {
            mPrevButton.setVisibility(View.VISIBLE);
        }
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
                    focusOnContact(position);
                }
            }
        }
    }

    @Override
    public void selectContact(int position) {
        if (mContactSelected) {
            deselectContact();
        } else {
            focusOnContact(position);
        }
    }

    private void deselectContact() {
        mContactSelected = false;
        mAdapter.deselectContact();
        tintFabs(false);
        adjustFabPosition(true);
    }

    @Override
    public void focusContact(int position) {
        focusOnContact(position);
    }

    private void focusOnContact(int position) {
        mAdapter.selectContactAtPosition(position);
        ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .scrollToPositionWithOffset(position, (mRecyclerView.getHeight() / 2) - 170);

        tintFabs(true);

        //hide fabToolbar if shown
        if (mFabToolbarOn) {
            showFabToolbar(false);
        }
        adjustFabPosition(false);
        mContactSelected = true;
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.alert_title, null);
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