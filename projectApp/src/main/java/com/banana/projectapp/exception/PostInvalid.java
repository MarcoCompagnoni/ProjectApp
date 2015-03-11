package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class PostInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public PostInvalid() {
        super();
    }

    public PostInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public PostInvalid(String arg0) {
        super(arg0);
    }

    public PostInvalid(Throwable arg0) {
        super(arg0);
    }
}