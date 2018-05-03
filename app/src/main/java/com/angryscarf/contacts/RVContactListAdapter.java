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

public abstract class RVContactListAdapter extends RecyclerView.Adapter<RVContactListAdapter.ContactListViewHolder> {

    public static int IS_FAV_RESOURCE = R.drawable.ic_star;
    public static int IS_NOT_FAV_RESOURCE = R.drawable.ic_star_border;

    private Context mContext;
    public ArrayList<Contact> contacts;
    public ArrayList<ContactListViewHolder> holders;
    private Dialog detailsDialog;

    public RVContactListAdapter(Context mContext, ArrayList<Contact> contacts) {
        this.mContext = mContext;
        this.contacts = contacts;
        holders = new ArrayList<>();
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

                Contact ct = contacts.get(vHolder.getAdapterPosition());
                name.setText(ct.getName() + " " + ct.getLastName());
                id.setText(ct.getId());
                number.setText(ct.getNumber());
                address.setText(ct.getAddress());
                img.setImageResource(R.drawable.ic_account_circle);

                detailsDialog.show();
            }
        });
        return vHolder;
    }

    @Override
    public void onBindViewHolder(ContactListViewHolder holder, int position) {
        Contact cont = contacts.get(position);

        holder.txt_name.setText(cont.getName() + " " + cont.getLastName());
        holder.txt_number.setText(cont.getNumber());
        holder.img_favorite.setImageResource(cont.isFavorite()? IS_FAV_RESOURCE : IS_NOT_FAV_RESOURCE);

        holder.img_favorite.setOnClickListener(new FavOnClickListener(holder, contacts, position));
        holders.add(holder);

    }

    public class FavOnClickListener implements View.OnClickListener {
        ContactListViewHolder holder;
        ArrayList<Contact> contactList;
        int position;

        public FavOnClickListener(ContactListViewHolder holder, ArrayList<Contact> contactList, int position) {
            this.holder = holder;
            this.contactList = contactList;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            Contact c = contactList.get(position);
            c.setFavorite(! c.isFavorite());
            holder.img_favorite.setImageResource(c.isFavorite()? IS_FAV_RESOURCE : IS_NOT_FAV_RESOURCE);
            OnToggleFavorite(holder, contactList, position);

        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactListViewHolder extends RecyclerView.ViewHolder {

         View item_contact;
        TextView txt_name;
        TextView txt_number;
        ImageView img_favorite;
        ImageView img_call;

        public ContactListViewHolder(View itemView) {
            super(itemView);
            item_contact = itemView.findViewById(R.id.relLay_contact_item);
            txt_name = itemView.findViewById(R.id.txt_contact_name);
            txt_number = itemView.findViewById(R.id.txt_contact_number);
            img_favorite = itemView.findViewById(R.id.img_contact_favorite);
            img_call = itemView.findViewById(R.id.img_contact_call);


        }
    }

    public void removeContact(int position) {
        contacts.remove(position);
        holders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, contacts.size()+1);
    }

    public void addContact(Contact c) {
        contacts.add(c);
        notifyItemInserted(contacts.size()-1);

    }



    public abstract void OnToggleFavorite (ContactListViewHolder holder, ArrayList<Contact> contactList, int position);
}
