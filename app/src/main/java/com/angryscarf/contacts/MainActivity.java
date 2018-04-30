package com.angryscarf.contacts;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

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
        vpAdapter.addFragment(ContactListFragment.newInstance(mContacts), "");


        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //set icons
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_contacts);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_contacts);

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
