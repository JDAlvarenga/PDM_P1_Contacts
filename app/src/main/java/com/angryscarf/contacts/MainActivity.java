package com.angryscarf.contacts;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity  implements ContactListFragment.OnFragmentInteractionListener{

    //Intent request keys
    public static final int EDIT_CONTACT = 1;
    public static final int ADD_CONTACT = 2;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter vpAdapter;
    private ArrayList<Contact> mContacts;
    private ContactListFragment allContactsFrag;
    private ContactListFragment favContactsFrag;

    private Contact editing;
    private RVContactListAdapter editing_adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem =  menu.findItem(R.id.menu_search);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                ((SearchView)menuItem.getActionView()).setIconified(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                allContactsFrag.getAdapter().noFilterContacts();
                favContactsFrag.getAdapter().noFilterContacts();
                menuItem.getActionView().clearFocus();

                return true;
            }
        });



        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                allContactsFrag.getAdapter().filterContacts(s);
                favContactsFrag.getAdapter().filterContacts(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                favContactsFrag.getAdapter().filterContacts(s);
                allContactsFrag.getAdapter().filterContacts(s);
                return false;
            }

        });


        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //first time loading app
        if (savedInstanceState == null) {
        loadContacts();
        //Create new fragments
        allContactsFrag = ContactListFragment.newInstance(mContacts);
        favContactsFrag = ContactListFragment.newInstance(filterFavorites(mContacts));

        vpAdapter.addFragment(allContactsFrag, "");
        vpAdapter.addFragment(favContactsFrag, "");
        }
        else {
            /*ViewPagerAdapter uses the SupportFragmentManager to handle Fragments but, when activity is recreated (orientation change)
            * must create another ViewPagerAdapter but Fragments registered in support manager by the adapter are not unregistered so
            * the previously created fragments are obtained from the manager and reinserted into the adapter.
            * When inserting duplicated fragments in the manager they are replaced/ignored so that in the end the adapter and manager
            * have the same registered fragments after recreating the activity.
            *
            * NOTE: The fragments handle its own State Restoration procedures.
            */
            //Getting the fragments
            allContactsFrag = (ContactListFragment) getSupportFragmentManager().getFragments().get(0);
            favContactsFrag = (ContactListFragment) getSupportFragmentManager().getFragments().get(1);

            //adding the fragments to the new adapter
            vpAdapter.addFragment(allContactsFrag, "");
            vpAdapter.addFragment(favContactsFrag, "");
        }




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
        mContacts.add(new Contact("Tariza", "503 73033815", true));
        mContacts.add(new Contact("Moe", "503 73033815", false));
        mContacts.add(new Contact("Jose", "503 73033815", false));

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
    public void OnFavoriteToggle(RVContactListAdapter adapter, ArrayList<Contact> contactList, int position) {
        Contact c = contactList.get(position);

        RVContactListAdapter allAdapter = allContactsFrag.getAdapter();
        RVContactListAdapter favAdapter = favContactsFrag.getAdapter();
        //Event from favorites tab
        if(favAdapter.equals(adapter)) {
            favAdapter.removeContact(position);

            int i = allAdapter.contacts.indexOf(c);
            allAdapter.updateContact(i);
            //allAdapter.holders.get(i).img_favorite.setImageResource(c.isFavorite()? RVContactListAdapter.IS_FAV_RESOURCE: RVContactListAdapter.IS_NOT_FAV_RESOURCE);

        }
        else {
            //adapter.holders.get(position).img_favorite.setImageResource(c.isFavorite()? RVContactListAdapter.IS_FAV_RESOURCE: RVContactListAdapter.IS_NOT_FAV_RESOURCE);
            allAdapter.updateContact(position);
            //RVContactListAdapter favAdapter = ((ContactListFragment)vpAdapter.getItem(1)).getAdapter();
            if(c.isFavorite()) {
                favAdapter.addContact(c);
            }
            else {
                favAdapter.removeContact(favAdapter.contacts.indexOf(c));
            }

        }

    }

    @Override
    public void OnRequestEdit(RVContactListAdapter adapter, ArrayList<Contact> contactList, int position) {

        editing = contactList.get(position);
        editing_adapter = adapter;
        Intent addIntent = new Intent(this, EditActivity.class);
        addIntent.putExtra(EditActivity.EXTRA_CONTACT, editing);
        startActivityForResult(addIntent, EDIT_CONTACT);


    }

    @Override
    public void OnRequestDelete(RVContactListAdapter adapter, ArrayList<Contact> contactList, int position) {
        Contact c = contactList.get(position);
        allContactsFrag.getAdapter().removeContact(c);
        if(c.isFavorite()) {
            favContactsFrag.getAdapter().removeContact(c);
        }
        adapter.closeDialog();
    }

    public void AddContact(View view) {
        Intent addIntent = new Intent(this, EditActivity.class);
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
                break;


            case EDIT_CONTACT:
                if(resultCode == Activity.RESULT_OK) {
                    Contact c = data.getParcelableExtra(EditActivity.EXTRA_CONTACT);
                    Toast.makeText(this, c.getName()+" "+c.getLastName(), Toast.LENGTH_SHORT).show();
                    allContactsFrag.getAdapter().updateContact(editing, c);
                    if(editing.isFavorite()) {
                        favContactsFrag.getAdapter().updateContact(editing, c);
                    }
                    else if(c.isFavorite()){
                       favContactsFrag.getAdapter().addContact(c);
                    }

                    editing_adapter.updateDialog(c);
                }
                editing = null;
                editing_adapter = null;
                break;
        }
    }

}
