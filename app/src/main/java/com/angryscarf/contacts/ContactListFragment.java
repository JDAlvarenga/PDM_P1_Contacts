package com.angryscarf.contacts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactListFragment extends Fragment {

    //State saving keys
    private static final String STATE_CONTACTS = "STATE_CONTACTS";
    private static final String STATE_SWAP_CONTACTS = "STATE_SWAP_CONTACTS";


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CONTACTS_LIST = "contacts_list";

    private RecyclerView recyclerView;
    private ArrayList<Contact> mContacts;
    private ArrayList<Contact> swap;
    private RVContactListAdapter adapter;

    public RVContactListAdapter getAdapter() {
        return adapter;
    }

    private OnFragmentInteractionListener mListener;

    public ContactListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contacts Contacts list to display.
     * @return A new instance of fragment ContactListFragment.
     */

    public static ContactListFragment newInstance(ArrayList<Contact> contacts) {
        ContactListFragment fragment = new ContactListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_CONTACTS_LIST, contacts);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContacts = getArguments().getParcelableArrayList(ARG_CONTACTS_LIST);
            swap = new ArrayList<>();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragLayout = inflater.inflate(R.layout.fragment_contact_list, container, false);
        recyclerView = fragLayout.findViewById(R.id.contact_list_recyclerView);

        if(savedInstanceState != null) {
            mContacts = (ArrayList<Contact>) savedInstanceState.getSerializable(STATE_CONTACTS);
            swap = (ArrayList<Contact>) savedInstanceState.getSerializable(STATE_SWAP_CONTACTS);
        }

        adapter = new RVContactListAdapter(getContext(), mContacts, swap) {
            @Override
            public void OnToggleFavorite(ArrayList<Contact> contactList, int position) {

                mListener.OnFavoriteToggle(this, contactList, position);
            }

            @Override
            public void OnClickEdit(ArrayList<Contact> contactList, int position) {
                mListener.OnRequestEdit(this, contactList, position);
            }

            @Override
            public void OnClickDelete(ArrayList<Contact> contactList, int position) {
                mListener.OnRequestDelete(this, contactList, position);
            }

            @Override
            public void OnClickCall(ArrayList<Contact> contactList, int position) {
                mListener.OnRequestCall(contactList.get(position));
            }
        };


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return fragLayout;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_CONTACTS, mContacts);
        outState.putSerializable(STATE_SWAP_CONTACTS, swap);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnFavoriteToggle(RVContactListAdapter adapter, ArrayList<Contact> contactList, int position);
        void OnRequestEdit(RVContactListAdapter adapter, ArrayList<Contact> contactList, int position);
        void OnRequestDelete(RVContactListAdapter adapter, ArrayList<Contact> contactList, int position);
        void OnRequestCall(Contact contact);
    }
}
