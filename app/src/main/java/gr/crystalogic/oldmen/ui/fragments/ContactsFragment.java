package gr.crystalogic.oldmen.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import gr.crystalogic.oldmen.utils.ContactArranger;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactsFragment newInstance(int columnCount) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
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

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            IContactDao dao = new ContactDao(getActivity());
            List<Contact> contactList = dao.getContacts();

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

            recyclerView.setAdapter(new ContactsRecyclerViewAdapter(ContactArranger.sortContactsBySurname(contactList), mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Contact item);
    }

}
