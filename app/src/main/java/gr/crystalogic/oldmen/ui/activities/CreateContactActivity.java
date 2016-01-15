package gr.crystalogic.oldmen.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import gr.crystalogic.oldmen.R;

public class CreateContactActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mSurname;
    private EditText mTelephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        initControls();
    }

    private void initControls() {
        mName = (EditText) findViewById(R.id.name);
        mSurname = (EditText) findViewById(R.id.surname);
        mTelephone = (EditText) findViewById(R.id.telephone);

        mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (mName.getText().length() == 0) {
                        mName.setError(getResources().getString(R.string.name_validation));
                    } else {
                        mName.setError(null);
                        mSurname.setVisibility(View.VISIBLE);
                        return false;
                    }
                }
                return true;
            }
        });

        mSurname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (mSurname.getText().length() == 0) {
                        mSurname.setError(getResources().getString(R.string.surname_validation));
                    } else {
                        mSurname.setError(null);
                        mTelephone.setVisibility(View.VISIBLE);
                        return false;
                    }
                }
                return true;
            }
        });

        mTelephone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //validate and save

                }
                return false;
            }
        });
    }
}
