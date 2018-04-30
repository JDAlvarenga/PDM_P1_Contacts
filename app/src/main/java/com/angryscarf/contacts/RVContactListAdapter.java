package com.angryscarf.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jaime on 4/29/2018.
 */

public class RVContactListAdapter extends RecyclerView.Adapter<RVContactListAdapter.ContactListViewHolder> {

    private Context mContext;
    private ArrayList<Contact> contacts;

    public RVContactListAdapter(Context mContext, ArrayList<Contact> contacts) {
        this.mContext = mContext;
        this.contacts = contacts;
    }

    @Override
    public ContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        ContactListViewHolder vHolder = new ContactListViewHolder(v);
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

        public TextView txt_name;
        public TextView txt_number;
        public ImageView img_favorite;
        public ImageView img_call;

        public ContactListViewHolder(View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_contact_name);
            txt_number = itemView.findViewById(R.id.txt_contact_number);
            img_favorite = itemView.findViewById(R.id.img_contact_favorite);
            img_call = itemView.findViewById(R.id.img_contact_call);


        }
    }
}
