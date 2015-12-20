package gr.crystalogic.oldmen.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.ContactsRecyclerViewAdapter;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;
import gr.crystalogic.oldmen.utils.ContactArranger;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link IContactsFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends Fragment implements IContactsFragmentInteractionListener {

    private static final int REQUEST_CODE_READ_CONTACTS = 123;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static String TAG = "ContactsFragment";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private IContactsFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private List<ContactListItemModel> mModels;
    private ContactsRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mListener.onTouchEvent();
                return false;
            }
        });

        mRecyclerView = (RecyclerView) view;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = view.getContext();

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        loadContacts();
    }

    private void loadContacts() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            return;
        }

        //get contacts from device
        IContactDao dao = new ContactDao(getActivity());
        Log.e(TAG, "-1-");
        List<Contact> contactList = dao.getContacts();
        Log.e(TAG, "-2-");

        //create some contacts for demo - test
        //TODO remove this before production
        if (contactList.size() == 0) {
            List<Contact> customList = new ArrayList<>();
            customList.add(new Contact("Thomas", "Walker", "07985677911"));
            customList.add(new Contact("Gianna", "Wizz", "07985677912"));
            customList.add(new Contact("John", "Wocker", "07985677913"));
            customList.add(new Contact("Dietrich", "Wonn", "07985677914"));
            customList.add(new Contact("Johannes", "Wyrting", "07985677915"));
            customList.add(new Contact("Thomas", "Xalker", "07985677916"));
            customList.add(new Contact("John", "Xocker", "07985677917"));
            customList.add(new Contact("Dietrich", "Xonn", "07985677918"));
            customList.add(new Contact("Johnannes", "Xyrting", "07985677919"));
            customList.add(new Contact("Gianna", "Yizz", "07985677926"));
            customList.add(new Contact("John", "Yocker", "07985677916"));

            for (Contact c : customList) {
                dao.addContact(c);
            }

            contactList = customList;
        }

        mModels = ContactArranger.sortContactsBySurname(contactList);
        Log.e(TAG, "-3-");

        mAdapter = new ContactsRecyclerViewAdapter(getDeepCopy(), this);
        Log.e(TAG, "-4-");

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.e(TAG, "permission granted");
                    loadContacts();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Contact access denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private List<ContactListItemModel> getDeepCopy() {
        //deep copy to keep initil list
        List<ContactListItemModel> mModelsCopy = new ArrayList<>();
        for (ContactListItemModel m : mModels) {
            mModelsCopy.add(new ContactListItemModel(m));
        }
        return mModelsCopy;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IContactsFragmentInteractionListener) {
            mListener = (IContactsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListFragmentInteraction(ContactListItemModel item) {
        // let parent know
        mListener.onListFragmentInteraction(item);
    }

    @Override
    public void onTouchEvent() {
        //let parent know
        mListener.onTouchEvent();
    }

    public void filterBySurnameStartLetter(String query) {
        List<ContactListItemModel> filteredModels = ContactArranger.queryContactsByFirstLetter(getDeepCopy(), query);
        mAdapter.animateTo(filteredModels);
        mRecyclerView.scrollToPosition(0);
    }

    public void resetContacts() {
        mAdapter.animateTo(getDeepCopy());
        mRecyclerView.scrollToPosition(0);
    }

}
