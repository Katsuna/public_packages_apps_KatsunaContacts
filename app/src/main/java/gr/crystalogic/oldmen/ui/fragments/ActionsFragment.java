package gr.crystalogic.oldmen.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.ui.controls.PressureButton;
import gr.crystalogic.oldmen.ui.listeners.IActionsFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IActionsFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ActionsFragment extends Fragment {

    private static final String TAG = "ActionsFragment";

    private IActionsFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actions, container, false);

        PressureButton searchButton = (PressureButton) view.findViewById(R.id.btn_search);
        searchButton.setRunnable(new Runnable() {
            @Override
            public void run() {
                mListener.search();
            }
        });

        PressureButton button = (PressureButton) view.findViewById(R.id.btn_new_contact);
        button.setRunnable(new Runnable() {
            @Override
            public void run() {
                mListener.addNewContact();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IActionsFragmentInteractionListener) {
            mListener = (IActionsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}