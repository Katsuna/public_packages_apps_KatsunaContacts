package gr.crystalogic.oldmen.ui.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.ContactsSelectionAdapter;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.utils.ContactArranger;

public class SelectContactsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<ContactListItemModel> mModels;
    private ContactsSelectionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);

        initControls();
        loadContacts();
    }

    private void initControls() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<Contact> selectedContacts = getSelectedContacts();
                if (selectedContacts.size() > 0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectContactsActivity.this);
                    alertDialogBuilder
                            .setTitle(R.string.delete_contacts)
                            .setMessage(R.string.delete_contacts_approval)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    IContactDao dao = new ContactDao(SelectContactsActivity.this);
                                    for (Contact contact : selectedContacts) {
                                        dao.deleteContact(contact);
                                        mAdapter.removeItem(contact);
                                    }
                                    Toast.makeText(SelectContactsActivity.this, R.string.contacts_deleted, Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(SelectContactsActivity.this, R.string.no_contacts_selected, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
    }

    private List<Contact> getSelectedContacts() {
        List<Contact> output = new ArrayList<>();

        if (mModels != null) {
            for (ContactListItemModel model : mModels) {
                if (model.isSelected()) {
                    output.add(model.getContact());
                }
            }
        }

        return output;
    }

    private void loadContacts() {
        //get contacts from device
        IContactDao dao = new ContactDao(this);
        List<Contact> contactList = dao.getContacts();

        mModels = ContactArranger.sortContactsBySurname(contactList);
        mAdapter = new ContactsSelectionAdapter(mModels);
        mRecyclerView.setAdapter(mAdapter);
    }

}
