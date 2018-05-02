package com.angryscarf.contacts;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity  implements ContactListFragment.OnFragmentInteractionListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter vpAdapter;
    private ArrayList<Contact> mContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        loadContacts();

        //Add fragments
        vpAdapter.addFragment(ContactListFragment.newInstance(mContacts), "");
        vpAdapter.addFragment(ContactListFragment.newInstance(filterFavorites(mContacts)), "");


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
}
