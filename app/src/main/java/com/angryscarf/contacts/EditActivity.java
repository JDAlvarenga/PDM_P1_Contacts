package com.angryscarf.contacts;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.github.siyamed.shapeimageview.CircularImageView;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT = "com.angryscarf.contacts.EXTRA_CONTACT";

    private EditText edit_name;
    private EditText edit_lastName;
    private EditText edit_phone;
    private EditText edit_id;
    private EditText edit_email;
    private EditText edit_address;
    private CircularImageView img_picture;
    private Contact contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        edit_name = findViewById(R.id.edit_edit_name);
        edit_lastName = findViewById(R.id.edit_edit_lastName);
        edit_phone = findViewById(R.id.edit_edit_phone);
        edit_id = findViewById(R.id.edit_edit_id);
        edit_email = findViewById(R.id.edit_edit_email);
        edit_address = findViewById(R.id.edit_edit_address);
        img_picture = findViewById(R.id.img_edit_picture);

        Intent intent = getIntent();
        contact = intent.hasExtra(EXTRA_CONTACT)? (Contact)intent.getParcelableExtra(EXTRA_CONTACT): (new Contact());


        edit_name.setText(contact.getName());
        edit_name.setText(contact.getLastName());
        edit_name.setText(contact.getId());
        edit_name.setText(contact.getEmail());
        edit_name.setText(contact.getAddress());
        img_picture.setImageResource(R.drawable.ic_account_circle);
    }

    public void saveContact(View view) {
        Contact c = new Contact();
        contact.setName(edit_name.getText().toString());
        contact.setLastName(edit_lastName.getText().toString());
        contact.setNumber(edit_phone.getText().toString());
        contact.setId(edit_id.getText().toString());
        contact.setEmail(edit_email.getText().toString());
        contact.setAddress(edit_address.getText().toString());

        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_CONTACT, contact);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
