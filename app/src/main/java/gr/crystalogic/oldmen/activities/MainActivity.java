package gr.crystalogic.oldmen.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.fragments.ActionsFragment;
import gr.crystalogic.oldmen.fragments.ContactsFragment;
import gr.crystalogic.oldmen.fragments.dummy.DummyContent;

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

        Button buttonTest = (Button) findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentStep = Step.ZOOM1;

                setFragmentWeight(R.id.actions_fragment, 0f);

                Log.e(TAG, "I was here.");
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
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.e(TAG, item.toString());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e(TAG, uri.toString());
    }
}
