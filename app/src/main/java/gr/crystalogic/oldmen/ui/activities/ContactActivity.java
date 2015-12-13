package gr.crystalogic.oldmen.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Intent intent = getIntent();
        Contact contact = (Contact) intent.getExtras().getSerializable("contact");
        Log.e("TAG", contact.toString());

    }
}
