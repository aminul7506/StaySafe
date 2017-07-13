package com.example.sojal.staysafe;

import java.util.ArrayList;

/**
 * Created by Sojal on 19-Apr-17.
 */

class Contact{
    String contactName;
    String contactNumber;

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}

public class ContactList {
    ArrayList<Contact> contactLists = new ArrayList<>();

    public void addToContactList(Contact contact){
        contactLists.add(contact);
    }
    public ArrayList<Contact> getContactLists() {
        return contactLists;
    }
}
