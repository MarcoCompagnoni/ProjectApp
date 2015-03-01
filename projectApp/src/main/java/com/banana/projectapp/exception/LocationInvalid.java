package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class LocationInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public LocationInvalid() {
        super();
    }

    public LocationInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public LocationInvalid(String arg0) {
        super(arg0);
    }

    public LocationInvalid(Throwable arg0) {
        super(arg0);
    }
}