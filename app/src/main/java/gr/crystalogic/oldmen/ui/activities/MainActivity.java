package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.fragments.ActionsFragment;
import gr.crystalogic.oldmen.ui.fragments.ContactsFragment;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements IContactsFragmentInteractionListener, ActionsFragment.OnFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getName();

    private Step currentStep = Step.START;
    private ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentStep = Step.ZOOM1;

                setFragmentWeight(R.id.actions_fragment, 0f);

                Log.e(TAG, "Search button pressed.");
            }
        });

        Button button = (Button) findViewById(R.id.btn_new_contact);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Add new contact pressed.");
            }
        });

        contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.contacts_fragment);
    }

    private void setFragmentWeight(int id, float weight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = weight;
        findViewById(id).setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        if (currentStep == Step.START) {
            super.onBackPressed();
        } else if (currentStep == Step.ZOOM1) {
            currentStep = Step.START;
            setFragmentWeight(R.id.actions_fragment, 2f);
        } else if (currentStep == Step.ZOOM2) {
            currentStep = Step.ZOOM1;
            contactsFragment.resetContacts();
        }
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

            if (currentStep == Step.START) {
                setFragmentWeight(R.id.actions_fragment, 0f);
            }
            contactsFragment.filterBySurnameStartLetter(item.getSeparator());
            currentStep = Step.ZOOM2;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e(TAG, uri.toString());
    }

    private enum Step {
        START,
        ZOOM1,
        ZOOM2
    }
}
