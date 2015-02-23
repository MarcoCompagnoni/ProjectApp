package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class EmailDuplicate extends Exception {

    private static final long serialVersionUID = 1L;

    public EmailDuplicate() {
        super();
    }

    public EmailDuplicate(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EmailDuplicate(String arg0) {
        super(arg0);
    }

    public EmailDuplicate(Throwable arg0) {
        super(arg0);
    }
}