package com.banana.projectapp.profile;

import android.graphics.Bitmap;

public class MyProfile {
    private String firstName;
    private String lastName;
    private Bitmap photo;

    public MyProfile(String firstName, String lastName, Bitmap photo){
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
