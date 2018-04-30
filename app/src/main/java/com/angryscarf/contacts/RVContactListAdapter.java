package com.angryscarf.contacts;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Jaime on 4/29/2018.
 */

public class RVContactListAdapter extends RecyclerView.Adapter<RVContactListAdapter.ContactListViewHolder> {

    private Context mContext;
    private ArrayList<Contact> contacts;
    private Dialog detailsDialog;

    public RVContactListAdapter(Context mContext, ArrayList<Contact> contacts) {
        this.mContext = mContext;
        this.contacts = contacts;
    }

    @Override
    public ContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        final ContactListViewHolder vHolder = new ContactListViewHolder(v);

        //set-up dialog
        detailsDialog = new Dialog(mContext);
        detailsDialog.setContentView(R.layout.dialog_contact_details);

        //TODO: Add custom image or default to dialog
        //show dialog on click
        vHolder.item_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView name = detailsDialog.findViewById(R.id.txt_dialog_name);
                TextView id = detailsDialog.findViewById(R.id.txt_dialog_id);
                TextView number = detailsDialog.findViewById(R.id.txt_dialog_number);
                TextView address = detailsDialog.findViewById(R.id.txt_dialog_address);
                CircularImageView img = detailsDialog.findViewById(R.id.img_dialog_picture);

                name.setText(contacts.get(vHolder.getAdapterPosition()).getName());
                id.setText(contacts.get(vHolder.getAdapterPosition()).getId());
                number.setText(contacts.get(vHolder.getAdapterPosition()).getNumber());
                address.setText(contacts.get(vHolder.getAdapterPosition()).getAddress());
                img.setImageResource(R.drawable.ic_account_circle);

                detailsDialog.show();
            }
        });
        return vHolder;
    }

    @Override
    public void onBindViewHolder(ContactListViewHolder holder, int position) {
        Contact cont = contacts.get(position);

        holder.txt_name.setText(cont.getName() + cont.getLastName());
        holder.txt_number.setText(cont.getNumber());
        holder.img_favorite.setImageResource(cont.isFavorite()? R.drawable.ic_star : R.drawable.ic_star_border);

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactListViewHolder extends RecyclerView.ViewHolder {

        private View item_contact;
        private TextView txt_name;
        private TextView txt_number;
        private  ImageView img_favorite;
        private ImageView img_call;

        public ContactListViewHolder(View itemView) {
            super(itemView);
            item_contact = itemView.findViewById(R.id.relLay_contact_item);
            txt_name = itemView.findViewById(R.id.txt_contact_name);
            txt_number = itemView.findViewById(R.id.txt_contact_number);
            img_favorite = itemView.findViewById(R.id.img_contact_favorite);
            img_call = itemView.findViewById(R.id.img_contact_call);


        }
    }
}
