package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.fragments.ContactsFragment;
import gr.crystalogic.oldmen.ui.listeners.IActionsFragmentInteractionListener;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements IContactsFragmentInteractionListener, IActionsFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getName();


    private Step currentStep = Step.START;
    private ContactsFragment contactsFragment;
    private FloatingActionButton searchFab;
    private FloatingActionButton newContactFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.contacts_fragment);
        setupFabs();
    }

    private void setupFabs() {
        searchFab = (FloatingActionButton) findViewById(R.id.search_fab);
        searchFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.indigo_blue)));
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    @Override
    public void onListFragmentInteraction(ContactListItemModel item) {
        if (item.getSeparator() == null) {
            Log.e(TAG, item.getContact().toString());
            Intent i = new Intent(this, ContactActivity.class);
            i.putExtra("contact", item.getContact());
            startActivity(i);
        } else {
            Log.e(TAG, item.getSeparator());

            contactsFragment.filterBySurnameStartLetter(item.getSeparator());
            currentStep = Step.ZOOM2;

            onTouchEvent();
        }
    }

    @Override
    public void onTouchEvent() {
        if (currentStep == Step.START) {
            currentStep = Step.ZOOM1;
        }
    }

    private enum Step {
        START,
        ZOOM1,
        ZOOM2
    }

    @Override
    public void search() {
        if (currentStep == Step.ZOOM2) {
            currentStep = Step.ZOOM1;
            contactsFragment.resetContacts();
        } else {
            currentStep = Step.ZOOM1;
        }

        onTouchEvent();
        Log.e(TAG, "Search button pressed.");
    }

    @Override
    public void addNewContact() {
        Intent i = new Intent(MainActivity.this, EditContactActivity.class);
        startActivity(i);
    }
}
