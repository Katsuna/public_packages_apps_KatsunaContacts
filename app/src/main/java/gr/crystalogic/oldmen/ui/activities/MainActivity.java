package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.fragments.ActionsFragment;
import gr.crystalogic.oldmen.ui.fragments.ContactsFragment;
import gr.crystalogic.oldmen.ui.listeners.IActionsFragmentInteractionListener;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;
import gr.crystalogic.oldmen.utils.Constants;

public class MainActivity extends AppCompatActivity implements IContactsFragmentInteractionListener, IActionsFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getName();


    private Step currentStep = Step.START;
    private ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addActionsFragment();
        contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.contacts_fragment);
    }

    private void addActionsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment actionsFragment = new ActionsFragment();

        fragmentTransaction.replace(R.id.bottom_container, actionsFragment);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void setFragmentWeight(int id, float weight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = weight;
        findViewById(id).setLayoutParams(params);
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

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            setActionsVisible(true);
        }
    };

    private void setActionsVisible(boolean show) {
        if (show) {
            setFragmentWeight(R.id.bottom_container, 2f);
        } else {
            setFragmentWeight(R.id.bottom_container, 0f);
        }
    }

    @Override
    public void onTouchEvent() {
        setActionsVisible(false);
        if (currentStep == Step.START) {
            currentStep = Step.ZOOM1;
        }
        //start timer to show again
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, Constants.ACTIONS_DIALOG_OFF_TIMEOUT);
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
