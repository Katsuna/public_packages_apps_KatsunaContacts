package com.katsuna.contacts.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.contacts.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchBarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchBarFragment extends Fragment {
    private static final String ARG_LETTERS_LIST = "letters";

    private List<String> mLetters;

    private OnFragmentInteractionListener mListener;
    private View mVerticalDivider;

    public SearchBarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param letters Letters list.
     * @return A new instance of fragment SearchBarFragment.
     */
    public static SearchBarFragment newInstance(ArrayList<String> letters) {
        SearchBarFragment fragment = new SearchBarFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_LETTERS_LIST, letters);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLetters = getArguments().getStringArrayList(ARG_LETTERS_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_search_bar, container, false);

        LinearLayout mLettersContainerA = (LinearLayout) layout.findViewById(R.id.letters_container_a);
        LinearLayout mLettersContainerB = (LinearLayout) layout.findViewById(R.id.letters_container_b);
        mVerticalDivider = layout.findViewById(R.id.vertical_divider);

        int lettersAdded = 0;

        for (String letter : mLetters) {
            if (lettersAdded < 10) {
                addLetterView(inflater, letter, mLettersContainerA);
            } else {
                addLetterView(inflater, letter, mLettersContainerB);
            }
            lettersAdded++;
        }

        return layout;
    }


    private void addLetterView(LayoutInflater inflater, final String letter, ViewGroup container) {
        View layout = inflater.inflate(R.layout.textview_letter, container, false);

        TextView searchLetter = (TextView) layout.findViewById(R.id.search_letter);
        searchLetter.setText(letter);
        searchLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectContactByStartingLetter(letter);
            }
        });

        container.addView(layout);
    }

    @Override
    public void onResume() {
        super.onResume();
        applyColorProfile();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    private void applyColorProfile() {
        if (mListener != null) {
            ColorProfile profile = mListener.getUserProfileContainer().getColorProfile();
            int color = ColorCalc.getColor(getActivity(), ColorProfileKey.DIVIDERS_OPACITY, profile);
            mVerticalDivider.setBackgroundColor(color);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void selectContactByStartingLetter(String letter);

        UserProfileContainer getUserProfileContainer();
    }
}
