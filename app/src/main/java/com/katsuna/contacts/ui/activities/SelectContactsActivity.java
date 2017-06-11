package com.katsuna.contacts.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.providers.ContactProvider;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.commons.utils.ContactArranger;
import com.katsuna.commons.utils.KatsunaAlertBuilder;
import com.katsuna.contacts.R;
import com.katsuna.contacts.ui.adapters.ContactsSelectionAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectContactsActivity extends KatsunaActivity {

    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private List<ContactListItemModel> mModels;
    private ContactsSelectionAdapter mAdapter;
    private TextView mNoResultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);

        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void initControls() {
        initToolbar();

        mFab1 = (FloatingActionButton) findViewById(R.id.fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Contact> selectedContacts = getSelectedContacts();
                if (selectedContacts.size() > 0) {
                    deleteContacts(selectedContacts);
                } else {
                    Toast.makeText(SelectContactsActivity.this, R.string.no_contacts_selected, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        mNoResultsView = (TextView) findViewById(R.id.no_results);
    }

    private void deleteContacts(final List<Contact> selectedContacts) {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(R.string.delete_contacts);
        builder.setMessage(R.string.delete_contacts_approval);
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfileContainer(mUserProfileContainer);
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactProvider contactProvider = new ContactProvider(SelectContactsActivity.this);
                for (Contact contact : selectedContacts) {
                    contactProvider.deleteContact(contact);
                    mAdapter.removeItem(contact);
                }
                Toast.makeText(SelectContactsActivity.this, R.string.contacts_deleted,
                        Toast.LENGTH_LONG).show();
            }
        });

        List<String> contactNames = new ArrayList<>();
        for (Contact contact : selectedContacts) {
            contactNames.add(contact.getDisplayName());
        }

        builder.setScrollViewItems(contactNames);

        builder.create().show();
    }

    @Override
    protected void showPopup(boolean b) {
        // no op here
    }

    private List<Contact> getSelectedContacts() {
        List<Contact> output = new ArrayList<>();

        if (mModels != null) {
            for (ContactListItemModel model : mAdapter.getModels()) {
                if (model.isSelected()) {
                    output.add(model.getContact());
                }
            }
        }

        return output;
    }

    private void loadContacts() {
        //get contacts from device
        ContactProvider contactProvider = new ContactProvider(this);
        List<Contact> contactList = contactProvider.getContacts();

        mModels = ContactArranger.sortContactsBySurname(contactList);
        mAdapter = new ContactsSelectionAdapter(mModels,
                mUserProfileContainer.getActiveUserProfile());
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
                mAdapter.resetFilter();
                return false;
            }
        });

        return true;
    }

    private void search(String query) {
        if (TextUtils.isEmpty(query)) {
            mAdapter.resetFilter();
        } else {
            mAdapter.getFilter().filter(query);
        }
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
