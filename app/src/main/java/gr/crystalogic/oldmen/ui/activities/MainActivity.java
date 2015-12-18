package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
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
    private final static long ACTIONS_DIALOG_OFF_TIMEOUT = 2000;
    private final static long PRESSURE_SENSITIVITY_TIMEOUT = 500;

    private Step currentStep = Step.START;
    private ContactsFragment contactsFragment;

    private boolean mBooleanIsPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.btn_search);

        searchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    pressureHandler.postDelayed(pressureRunnable, PRESSURE_SENSITIVITY_TIMEOUT);
                    mBooleanIsPressed = true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(mBooleanIsPressed) {
                        mBooleanIsPressed = false;
                        pressureHandler.removeCallbacks(pressureRunnable);
                    }
                }

                return false;
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
            setFragmentWeight(R.id.actions_fragment, 2f);
        } else {
            setFragmentWeight(R.id.actions_fragment, 0f);
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
        handler.postDelayed(runnable, ACTIONS_DIALOG_OFF_TIMEOUT);
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

    private final Handler pressureHandler = new Handler();
    private final Runnable pressureRunnable = new Runnable() {
        public void run() {
            search();
        }
    };

    private void search() {
        if (currentStep == Step.ZOOM2) {
            currentStep = Step.ZOOM1;
            contactsFragment.resetContacts();
        } else {
            currentStep = Step.ZOOM1;
        }

        onTouchEvent();
        Log.e(TAG, "Search button pressed.");
    }
}
