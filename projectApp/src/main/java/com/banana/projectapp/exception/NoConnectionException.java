package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class NoConnectionException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoConnectionException() {
        super();
    }

    public NoConnectionException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public NoConnectionException(String arg0) {
        super(arg0);
    }

    public NoConnectionException(Throwable arg0) {
        super(arg0);
    }
}