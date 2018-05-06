package com.angryscarf.contacts;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.siyamed.shapeimageview.CircularImageView;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT = "com.angryscarf.contacts.EXTRA_CONTACT";

    public static int R_FAVORITE = R.drawable.ic_favorite;
    public static int R_NO_FAVORITE = R.drawable.ic_favorite_border;

    private EditText edit_name;
    private EditText edit_lastName;
    private EditText edit_phone;
    private EditText edit_id;
    private EditText edit_email;
    private EditText edit_address;
    private CircularImageView img_picture;
    private ImageView img_favorite;
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
        img_favorite = findViewById(R.id.img_edit_favorite);

        Intent intent = getIntent();
        contact = intent.hasExtra(EXTRA_CONTACT)? (Contact)intent.getParcelableExtra(EXTRA_CONTACT): (new Contact());

        img_favorite.setImageResource(contact.isFavorite()?R_FAVORITE:R_NO_FAVORITE);
        img_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact.setFavorite(!contact.isFavorite());
                img_favorite.setImageResource(contact.isFavorite()?R_FAVORITE:R_NO_FAVORITE);
            }
        });
        edit_name.setText(contact.getName());
        edit_lastName.setText(contact.getLastName());
        edit_phone.setText(contact.getNumber());
        edit_id.setText(contact.getId());
        edit_email.setText(contact.getEmail());
        edit_address.setText(contact.getAddress());
        img_picture.setImageResource(R.drawable.ic_account_circle);
    }

    public void saveContact(View view) {
        Contact c = new Contact();
        //Favorite is set on click
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
