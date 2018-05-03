package com.angryscarf.contacts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity  implements ContactListFragment.OnFragmentInteractionListener{

    public static final int EDIT_CONTACT = 1;
    public static final int ADD_CONTACT = 2;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter vpAdapter;
    private ArrayList<Contact> mContacts;
    private ContactListFragment allContactsFrag;
    private ContactListFragment favContactsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        loadContacts();

        //Add fragments
        allContactsFrag = ContactListFragment.newInstance(mContacts);
        favContactsFrag = ContactListFragment.newInstance(filterFavorites(mContacts));
        vpAdapter.addFragment(allContactsFrag, "");
        vpAdapter.addFragment(favContactsFrag, "");


        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //set icons
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_contacts);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tabs_favorite);

        //remove shadow from action bar (did)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);



    }

    //TODO: Load contacts from device
    public void loadContacts() {
        mContacts = new ArrayList<>();
        mContacts.add(new Contact("Jaime", "503 73033815", false));
        mContacts.add(new Contact("Jaime", "503 73033815", true));
        mContacts.add(new Contact("Jaime", "503 73033815", false));
        mContacts.add(new Contact("Jaime", "503 73033815", false));

    }

    public ArrayList<Contact> filterFavorites (ArrayList<Contact> contacts) {
        ArrayList<Contact> filtered = new ArrayList<>();

        for(Contact c:contacts) {
            if(c.isFavorite()) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    @Override
    public void OnFavoriteToggle(RVContactListAdapter adapter, RVContactListAdapter.ContactListViewHolder holder, ArrayList<Contact> contactList, int position) {
        Contact c = contactList.get(position);
        //Event from favorites tab
        if(((ContactListFragment)vpAdapter.getItem(1)).getAdapter().equals(adapter)) {
            adapter.removeContact(position);

            RVContactListAdapter allAdapter = ((ContactListFragment)vpAdapter.getItem(0)).getAdapter();
            int i = allAdapter.contacts.indexOf(c);
            allAdapter.holders.get(i).img_favorite.setImageResource(c.isFavorite()? RVContactListAdapter.IS_FAV_RESOURCE: RVContactListAdapter.IS_NOT_FAV_RESOURCE);

        }
        else {
            adapter.holders.get(position).img_favorite.setImageResource(c.isFavorite()? RVContactListAdapter.IS_FAV_RESOURCE: RVContactListAdapter.IS_NOT_FAV_RESOURCE);
            RVContactListAdapter favAdapter = ((ContactListFragment)vpAdapter.getItem(1)).getAdapter();
            if(c.isFavorite()) {
                favAdapter.addContact(c);
            }
            else {
                favAdapter.removeContact(favAdapter.contacts.indexOf(c));
            }

        }

    }

    public void AddContact(View view) {
        Intent addIntent = new Intent(this, EditActivity.class);
        //addIntent.putExtra(EditActivity.EXTRA_CONTACT, new Contact("Dan","73294872",false));
        startActivityForResult(addIntent, ADD_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_CONTACT:
                if(resultCode == Activity.RESULT_OK) {
                    Contact c = data.getParcelableExtra(EditActivity.EXTRA_CONTACT);
                    Toast.makeText(this, c.getName()+" "+c.getLastName(), Toast.LENGTH_SHORT).show();
                    allContactsFrag.getAdapter().addContact(c);
                    if(c.isFavorite()) {
                        favContactsFrag.getAdapter().addContact(c);
                    }

                }
        }
    }
}
