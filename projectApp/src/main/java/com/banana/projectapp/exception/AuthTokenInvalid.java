package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class AuthTokenInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public AuthTokenInvalid() {
        super();
    }

    public AuthTokenInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public AuthTokenInvalid(String arg0) {
        super(arg0);
    }

    public AuthTokenInvalid(Throwable arg0) {
        super(arg0);
    }
}