package gr.crystalogic.oldmen.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private static String TAG = "ContactsFragment";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
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

        //get contacts from device
        IContactDao dao = new ContactDao(getActivity());
        Log.e(TAG, "-1-");
        List<Contact> contactList = dao.getContacts();
        Log.e(TAG, "-2-");

        //create some contacts for demo - test
        //TODO remove this before production
        if (contactList.size() == 0) {
            List<Contact> customList = new ArrayList<>();
            customList.add(new Contact("Thomas", "Walker", "07985677916"));
            customList.add(new Contact("Gianna", "Wizz", "07985677916"));
            customList.add(new Contact("John", "Wocker", "07985677916"));
            customList.add(new Contact("Dietrich", "Wonn", "07985677916"));
            customList.add(new Contact("Johannes", "Wyrting", "07985677916"));
            customList.add(new Contact("Thomas", "Xalker", "07985677916"));
            customList.add(new Contact("John", "Xocker", "07985677916"));
            customList.add(new Contact("Dietrich", "Xonn", "07985677916"));
            customList.add(new Contact("Johnannes", "Xyrting", "07985677916"));
            customList.add(new Contact("Gianna", "Yizz", "07985677916"));
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

    private List<ContactListItemModel> getDeepCopy() {
        //deep copy to keep initil list
        List<ContactListItemModel> mModelsCopy = new ArrayList<>();
        for(ContactListItemModel m: mModels) {
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
