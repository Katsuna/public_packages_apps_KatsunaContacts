package gr.crystalogic.oldmen.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.fragments.ActionsFragment;
import gr.crystalogic.oldmen.fragments.ContactsFragment;

public class MainActivity extends AppCompatActivity implements ContactsFragment.OnListFragmentInteractionListener, ActionsFragment.OnFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getName();

    private Step currentStep = Step.START;

    private enum Step {
        START,
        ZOOM1,
        ZOOM2,
        DETAIL
    }

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
        } else {
            currentStep = Step.START;
            setFragmentWeight(R.id.actions_fragment, 1f);
        }
    }

    @Override
    public void onListFragmentInteraction(Contact item) {
        Log.e(TAG, item.toString());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e(TAG, uri.toString());
    }
}
