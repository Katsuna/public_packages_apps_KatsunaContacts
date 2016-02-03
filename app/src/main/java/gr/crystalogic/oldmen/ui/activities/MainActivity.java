package gr.crystalogic.oldmen.ui.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Phone;
import gr.crystalogic.oldmen.ui.adapters.ContactsRecyclerViewAdapter;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.listeners.IContactInteractionListener;
import gr.crystalogic.oldmen.utils.ContactArranger;

public class MainActivity extends AppCompatActivity implements IContactInteractionListener {

    private final static String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static final int REQUEST_CODE_ASK_CALL_PERMISSION = 2;
    private static final int REQUEST_CODE_EDIT_CONTACT = 3;

    private List<ContactListItemModel> mModels;
    private ContactsRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private DrawerLayout drawerLayout;
    private TextView mNoResultsView;
    private SearchView mSearchView;
    private FloatingActionsMenu mFabMenu;

    private Contact mSelectedContact;

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
        if (isChanged()) {
            loadContacts();
        }
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNoResultsView = (TextView) findViewById(R.id.no_results);
        mFabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
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
                search(newText);
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.animateTo(getDeepCopy(mModels));
                showNoResultsView();
                return false;
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
            search(query);
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
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerLayout() {
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

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
        FloatingActionButton searchFab = (FloatingActionButton) findViewById(R.id.search_fab);
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //focus on search
                mFabMenu.collapse();
                mSearchView.setIconified(false);
            }
        });


        FloatingActionButton mNewContactFab = (FloatingActionButton) findViewById(R.id.new_contact_fab);
        mNewContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CreateContactActivity.class);
                startActivityForResult(i, REQUEST_CODE_EDIT_CONTACT);
            }
        });
    }

    private void loadContacts() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            return;
        }

        //get contacts from device
        IContactDao dao = new ContactDao(this);
        List<Contact> contactList = dao.getContacts();

        //create some contacts for demo - test
        //TODO remove this before production
        if (contactList.size() == 0) {
            List<Contact> customList = new ArrayList<>();
            customList.add(new Contact("Thomas", "Walker", "07985677911"));
            customList.add(new Contact("Gianna", "Wizz", "07985677912"));
            customList.add(new Contact("John", "Wocker", "07985677913"));
            customList.add(new Contact("Dietrich", "Wonn", "07985677914"));
            customList.add(new Contact("Johannes", "Wyrting", "07985677915"));
            customList.add(new Contact("Thomas", "Xalker", "07985677916"));
            customList.add(new Contact("John", "Xocker", "07985677917"));
            customList.add(new Contact("Dietrich", "Xonn", "07985677918"));
            customList.add(new Contact("Johnannes", "Xyrting", "07985677919"));
            customList.add(new Contact("Gianna", "Yizz", "07985677926"));
            customList.add(new Contact("John", "Pocker", "07985677916"));
            customList.add(new Contact("Thomas", "Salker", "07985677911"));
            customList.add(new Contact("Gianna", "Sizz", "07985677912"));
            customList.add(new Contact("John", "Socker", "07985677913"));
            customList.add(new Contact("Dietrich", "Sonn", "07985677914"));
            customList.add(new Contact("Johannes", "Syrting", "07985677915"));
            customList.add(new Contact("Thomas", "Talker", "07985677916"));
            customList.add(new Contact("John", "Tocker", "07985677917"));
            customList.add(new Contact("Dietrich", "Tonn", "07985677918"));
            customList.add(new Contact("Johnannes", "Tyrting", "07985677919"));
            customList.add(new Contact("Gianna", "Tizz", "07985677926"));
            customList.add(new Contact("John", "Kocker", "07985677916"));
            customList.add(new Contact("Thomas", "Kalker", "07985677911"));
            customList.add(new Contact("Gianna", "Kizz", "07985677912"));
            customList.add(new Contact("John", "Kocker", "07985677913"));
            customList.add(new Contact("Dietrich", "Lonn", "07985677914"));
            customList.add(new Contact("Johannes", "Ryrting", "07985677915"));
            customList.add(new Contact("Thomas", "Ralker", "07985677916"));
            customList.add(new Contact("John", "Rocker", "07985677917"));
            customList.add(new Contact("Dietrich", "Ronn", "07985677918"));
            customList.add(new Contact("Johnannes", "Hyrting", "07985677919"));
            customList.add(new Contact("Gianna", "Hizz", "07985677926"));
            customList.add(new Contact("John", "Hocker", "07985677916"));

            for (Contact c : customList) {
                dao.addContact(c);
            }

            contactList = customList;
        }

        mModels = ContactArranger.getContactsProcessed(contactList);
        mAdapter = new ContactsRecyclerViewAdapter(getDeepCopy(mModels), this);
        mRecyclerView.setAdapter(mAdapter);
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
        mAdapter.selectContactAtPosition(position);
        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, (mRecyclerView.getHeight() / 2) - 130);
    }

    @Override
    public void editContact(String contactId) {
        Intent i = new Intent(this, EditContactActivity.class);
        i.putExtra("contactId", contactId);
        startActivityForResult(i, REQUEST_CODE_EDIT_CONTACT);
    }

    @Override
    public void callContact(Contact contact) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            mSelectedContact = contact;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_CALL_PERMISSION);
            return;
        }

        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getContactPhone(contact)));
        startActivity(i);
    }

    @Override
    public void sendSMS(Contact contact) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", getContactPhone(contact), null)));
    }

    private String getContactPhone(Contact contact) {
        List<Phone> phones = new ContactDao(this).getPhones(contact.getId());
        return phones.get(0).getNumber();
    }

    private List<ContactListItemModel> filter(List<ContactListItemModel> models, String query) {
        query = query.toLowerCase();

        final List<ContactListItemModel> filteredModelList = new ArrayList<>();
        for (ContactListItemModel model : models) {
            final String text = model.getContact().getDisplayName().toLowerCase();
            if (text.contains(query)) {
                //exclude premium contacts
                if (!model.isPremium()) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    private void search(String query) {
        if (TextUtils.isEmpty(query)) {
            mAdapter.animateTo(getDeepCopy(mModels));
        } else {
            final List<ContactListItemModel> filteredModelList = filter(mModels, query);
            mAdapter.animateTo(getDeepCopy(filteredModelList));
            mRecyclerView.scrollToPosition(0);
        }
        showNoResultsView();
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

    private List<ContactListItemModel> getDeepCopy(List<ContactListItemModel> contactListItemModels) {
        List<ContactListItemModel> output = new ArrayList<>();
        for (ContactListItemModel model : contactListItemModels) {
            output.add(new ContactListItemModel(model));
        }
        return output;
    }
}