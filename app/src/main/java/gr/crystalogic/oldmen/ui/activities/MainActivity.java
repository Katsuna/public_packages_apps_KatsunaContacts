package gr.crystalogic.oldmen.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.domain.Phone;
import gr.crystalogic.oldmen.ui.adapters.ContactsRecyclerViewAdapter;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;
import gr.crystalogic.oldmen.utils.ContactArranger;
import gr.crystalogic.oldmen.utils.Step;

public class MainActivity extends AppCompatActivity implements IContactsFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static final int REQUEST_CODE_ASK_CALL_PERMISSION = 2;

    private FloatingActionButton mNewContactFab;
    private ContactsRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private Step mStep;
    private Contact mSelectedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFab();
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        //always resume on INITIAL
        mStep = Step.INITIAL;
        showButtons(mStep);
        loadContacts();
    }

    private void setupFab() {
        mNewContactFab = (FloatingActionButton) findViewById(R.id.new_contact_fab);
        mNewContactFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pink)));

        mNewContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CreateContactActivity.class);
                startActivity(i);
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

        List<ContactListItemModel> models = ContactArranger.getContactsProcessed(contactList);
        Log.e(TAG, "-3-");

        mAdapter = new ContactsRecyclerViewAdapter(models, this, mStep);
        Log.e(TAG, "-4-");

        mRecyclerView.setAdapter(mAdapter);
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

    private List<ContactListItemModel> getDeepCopy(List<ContactListItemModel> models) {
        //deep copy to keep initial list
        List<ContactListItemModel> output = new ArrayList<>();
        for (ContactListItemModel m : models) {
            output.add(new ContactListItemModel(m));
        }
        return output;
    }

    @Override
    public void onContactSelected(int position) {
        goToStep(Step.CONTACT_SELECTION, position);
        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, (mRecyclerView.getHeight() / 2) - 60);
    }

    @Override
    public void onLostFocusContactClick() {
        goToStep(Step.INITIAL);
    }

    @Override
    public void onSeparatorClick(int position) {
        goToStep(Step.INITIAL);
        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void onBackPressed() {
        if (mStep == Step.CONTACT_SELECTION) {
            goToStep(Step.INITIAL);
        } else {
            super.onBackPressed();
        }
    }

    private void goToStep(Step step) {
        goToStep(step, null);
    }

    private void goToStep(Step step, Integer position) {
        mStep = step;
        showButtons(step);
        switch (step) {
            case INITIAL:
                mAdapter.goToStep(mStep);
                break;
            case CONTACT_SELECTION:
                mAdapter.goToStepWithContactSelection(mStep, position);
                break;
        }
    }

    private void showButtons(Step step) {
        switch (step) {
            case INITIAL:
                mNewContactFab.setVisibility(View.VISIBLE);
                break;
            case CONTACT_SELECTION:
                mNewContactFab.setVisibility(View.GONE);
                break;
        }
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

    private String getContactPhone(Contact contact) {
        List<Phone> phones = new ContactDao(this).getPhones(contact.getId());
        return phones.get(0).getNumber();
    }

    @Override
    public void sendSMS(Contact contact) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", getContactPhone(contact), null)));
    }

}