package com.angryscarf.contacts;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContactListFragment.OnFragmentInteractionListener {

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
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                ((SearchView) menuItem.getActionView()).setIconified(false);
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
            fetchContacts();
            //Create new fragments
            allContactsFrag = ContactListFragment.newInstance(mContacts);
            favContactsFrag = ContactListFragment.newInstance(filterFavorites(mContacts));

            vpAdapter.addFragment(allContactsFrag, "");
            vpAdapter.addFragment(favContactsFrag, "");
        } else {
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
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite);

        //remove shadow from action bar (did)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);


    }


    public ArrayList<Contact> filterFavorites(ArrayList<Contact> contacts) {
        ArrayList<Contact> filtered = new ArrayList<>();

        for (Contact c : contacts) {
            if (c.isFavorite()) {
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
        if (favAdapter.equals(adapter)) {
            favAdapter.removeContact(position);

            int i = allAdapter.contacts.indexOf(c);
            allAdapter.updateContact(i);
            //allAdapter.holders.get(i).img_favorite.setImageResource(c.isFavorite()? RVContactListAdapter.IS_FAV_RESOURCE: RVContactListAdapter.IS_NOT_FAV_RESOURCE);

        } else {
            //adapter.holders.get(position).img_favorite.setImageResource(c.isFavorite()? RVContactListAdapter.IS_FAV_RESOURCE: RVContactListAdapter.IS_NOT_FAV_RESOURCE);
            allAdapter.updateContact(position);
            //RVContactListAdapter favAdapter = ((ContactListFragment)vpAdapter.getItem(1)).getAdapter();
            if (c.isFavorite()) {
                favAdapter.addContact(c);
            } else {
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
        if (c.isFavorite()) {
            favContactsFrag.getAdapter().removeContact(c);
        }
        adapter.closeDialog();
        Toast.makeText(this, getResources().getText(R.string.onRemoveSuccessToast)+ " "+c.getName(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnRequestCall(Contact contact) {
        CallContact(contact);
    }

    @Override
    public void OnRequestShare(Contact contact) {
        ShareContact(contact);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    Contact c = data.getParcelableExtra(EditActivity.EXTRA_CONTACT);
                    if(!(c.getName() == c.getLastName() && c.getName() == "")){

                        allContactsFrag.getAdapter().addContact(c);
                        if (c.isFavorite()) {
                            favContactsFrag.getAdapter().addContact(c);
                        }

                        Toast.makeText(this, getResources().getText(R.string.onAddSuccessToast)+ " "+c.getName(),Toast.LENGTH_SHORT).show();
                    }
                }
                break;


            case EDIT_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    Contact c = data.getParcelableExtra(EditActivity.EXTRA_CONTACT);
                    allContactsFrag.getAdapter().updateContact(editing, c);
                    if (editing.isFavorite()) {
                        favContactsFrag.getAdapter().updateContact(editing, c);
                    } else if (c.isFavorite()) {
                        favContactsFrag.getAdapter().addContact(c);
                    }

                    editing_adapter.updateDialog(c);
                Toast.makeText(this, getResources().getText(R.string.onEditSuccessToast)+ " "+c.getName(),Toast.LENGTH_SHORT).show();
                }
                editing = null;
                editing_adapter = null;
                break;
        }
    }


    public void AddContact(View view) {
        Intent addIntent = new Intent(this, EditActivity.class);
        startActivityForResult(addIntent, ADD_CONTACT);
    }

    public void fetchContacts() {

        mContacts = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        //Cursor onto all main contacts
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact contact = new Contact();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                boolean favorite = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.STARRED)) == 1;


                //Cursor onto email addresses (get first only)
                Cursor mailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + "= ?",
                        new String[]{id}, null);

                while (mailCur.moveToNext()) {
                    String email = mailCur.getString(mailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    contact.setEmail(email);
                    break;
                }
                mailCur.close();

                //String CID = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Identity.CONTACT_ID));
                //String photo  = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI));
                //TODO: Implement Photo loading

                contact.setName(name);
                contact.setFavorite(favorite);

                //Has phone numbers
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )) > 0) {

                    //Cursor onto phone numbers
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact.setNumber(phoneNo);
                        break;//TODO: Implement showing multiple phone numbers
                    }
                    pCur.close();
                }


                mContacts.add(contact);
            }
        }
        cur.close();
    }

    //Dummy Data
    public void loadContacts() {
        mContacts = new ArrayList<>();
        mContacts.add(new Contact("Jaime", "503 73033815", false));
        mContacts.add(new Contact("Tarisa", "503 73033815", true));
        mContacts.add(new Contact("Moe", "503 73033815", false));
        mContacts.add(new Contact("Jose", "503 73033815", false));

    }


    public void CallContact(Contact contact) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contact.getNumber()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    public void ShareContact(Contact contact) {

        String info = contact.getName()+ " "+contact.getLastName();
        if(contact.getNumber() != ""){info += ("\n"+ contact.getNumber());}
        if(contact.getEmail() != ""){info += ("\n"+ contact.getEmail());}
        if(contact.getAddress() != ""){info += ("\n"+ contact.getAddress());}


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, info);
        shareIntent.setType("text/plain");

        if(shareIntent.resolveActivity(getPackageManager()) != null){
            startActivity(shareIntent);
        }
        else{
            Toast.makeText(this, "Could not find any app to share info",Toast.LENGTH_SHORT).show();
        }


    }
}
