package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class PhotoInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public PhotoInvalid() {
        super();
    }

    public PhotoInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public PhotoInvalid(String arg0) {
        super(arg0);
    }

    public PhotoInvalid(Throwable arg0) {
        super(arg0);
    }
}