package com.angryscarf.contacts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jaime on 4/29/2018.
 */

public class Contact implements Parcelable {
    private String id, name, lastName, number, email, address, birthDate;
    private boolean favorite;

    public Contact(String name, String number, boolean favorite) {
        this.id = "";
        this.name = name;
        this.lastName = "";
        this.number = number;
        this.email = "";
        this.address = "";
        this.birthDate = "";
        this.favorite = favorite;
    }

    public Contact(String id, String name, String lastName, String number, String email, String address, String birthDate, boolean favorite) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.number = number;
        this.email = email;
        this.address = address;
        this.birthDate = birthDate;
        this.favorite = favorite;
    }

    protected Contact(Parcel in) {
        id = in.readString();
        name = in.readString();
        lastName = in.readString();
        number = in.readString();
        email = in.readString();
        address = in.readString();
        birthDate = in.readString();
        favorite = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(lastName);
        parcel.writeString(number);
        parcel.writeString(email);
        parcel.writeString(address);
        parcel.writeString(birthDate);
        parcel.writeByte((byte) (favorite?1:0));
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
