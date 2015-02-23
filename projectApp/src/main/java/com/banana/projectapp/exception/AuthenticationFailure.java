package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class AuthenticationFailure extends Exception {

    private static final long serialVersionUID = 1L;

    public AuthenticationFailure() {
        super();
    }

    public AuthenticationFailure(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public AuthenticationFailure(String arg0) {
        super(arg0);
    }

    public AuthenticationFailure(Throwable arg0) {
        super(arg0);
    }
}