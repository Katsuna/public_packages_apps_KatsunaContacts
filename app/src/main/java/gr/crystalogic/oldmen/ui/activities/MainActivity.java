package gr.crystalogic.oldmen.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.ContactsRecyclerViewAdapter;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;
import gr.crystalogic.oldmen.utils.ContactArranger;
import gr.crystalogic.oldmen.utils.Step;

public class MainActivity extends AppCompatActivity  implements IContactsFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE_READ_CONTACTS = 123;

    private FloatingActionButton searchFab;
    private FloatingActionButton newContactFab;
    private List<ContactListItemModel> mModels;
    private ContactsRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private Step mStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFabs();
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);

        mStep = Step.S1;
        loadContacts();
    }

    private void setupFabs() {
        searchFab = (FloatingActionButton) findViewById(R.id.search_fab);
        searchFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.indigo_blue)));
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStep = Step.S2;
                mAdapter.goToStep(mStep);
            }
        });

        newContactFab = (FloatingActionButton) findViewById(R.id.new_contact_fab);
        newContactFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pink)));

        newContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditContactActivity.class);
                startActivity(i);
            }
        });
    }

    private void loadContacts() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            return;
        }

        //get contacts from device
        IContactDao dao = new ContactDao(this);
        Log.e(TAG, "-1-");
        List<Contact> contactList = dao.getContacts();
        Log.e(TAG, "-2-");

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
            customList.add(new Contact("John", "Yocker", "07985677916"));

            for (Contact c : customList) {
                dao.addContact(c);
            }

            contactList = customList;
        }

        mModels = ContactArranger.sortContactsBySurname(contactList);
        Log.e(TAG, "-3-");

        mAdapter = new ContactsRecyclerViewAdapter(getDeepCopy(), this, mStep);
        Log.e(TAG, "-4-");

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.e(TAG, "permission granted");
                    loadContacts();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Contact access denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private List<ContactListItemModel> getDeepCopy() {
        //deep copy to keep initil list
        List<ContactListItemModel> mModelsCopy = new ArrayList<>();
        for (ContactListItemModel m : mModels) {
            mModelsCopy.add(new ContactListItemModel(m));
        }
        return mModelsCopy;
    }

    @Override
    public void onListFragmentInteraction(ContactListItemModel item) {
        Log.e(TAG, item.toString());
    }

    @Override
    public void onTouchEvent() {

    }

    @Override
    public void onBackPressed() {
        if (mStep == Step.S2) {
            mStep = Step.S1;
            mAdapter.goToStep(mStep);
        } else {
            super.onBackPressed();
        }
    }
}