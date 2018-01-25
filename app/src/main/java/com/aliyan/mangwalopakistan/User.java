package com.aliyan.mangwalopakistan;

import android.util.Base64;

/**
 * Created by Aliyan on 4/16/2017.
 */
public class User {

    public String firstName;
    public String lastName;
    public String address;
    public String phone;
    public String image;

    public User() {

    }

    public User(String firstName, String lastName, String address, String phone, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.image = image;
    }
}
