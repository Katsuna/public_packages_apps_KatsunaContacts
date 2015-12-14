package gr.crystalogic.oldmen.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = "ContactActivity";
    private static final int REQUEST_CODE_ASK_CALL_PERMISSION = 123;

    private Contact contact;
    private TextView number;
    private TextView name;
    private TextView surname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Intent intent = getIntent();
        contact = (Contact) intent.getExtras().getSerializable("contact");

        number = (TextView) findViewById(R.id.contact_number);
        name = (TextView) findViewById(R.id.contact_name);
        surname = (TextView) findViewById(R.id.contact_surname);

        Button callButton = (Button) findViewById(R.id.btn_call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_CALL_PERMISSION);
                    return;
                }

                Log.e(TAG, "call immediately");

                callContact();
            }
        });

        Button smsButton = (Button) findViewById(R.id.btn_sms);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        showContactInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_CALL_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.e(TAG, "permission granted");
                    callContact();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Phone Call denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void showContactInfo() {
        if (contact != null) {
            if (contact.getName() != null) {
                name.setText(contact.getName().getName());
                surname.setText(contact.getName().getSurname());
            }

            if (contact.getPhones() != null && contact.getPhones().size() > 0) {
                number.setText(contact.getPhones().get(0).getNumber());
            }
        }
    }


    private void callContact() {
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhones().get(0).getNumber()));
        startActivity(i);
    }

    private void sendSMS()
    {
        String number = contact.getPhones().get(0).getNumber();  // The number on which you want to send SMS
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }
}
