package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class EmberTokenInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public EmberTokenInvalid() {
        super();
    }

    public EmberTokenInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EmberTokenInvalid(String arg0) {
        super(arg0);
    }

    public EmberTokenInvalid(Throwable arg0) {
        super(arg0);
    }
}