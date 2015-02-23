package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class UserInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public UserInvalid() {
        super();
    }

    public UserInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public UserInvalid(String arg0) {
        super(arg0);
    }

    public UserInvalid(Throwable arg0) {
        super(arg0);
    }
}