package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class SocialTypeInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public SocialTypeInvalid() {
        super();
    }

    public SocialTypeInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SocialTypeInvalid(String arg0) {
        super(arg0);
    }

    public SocialTypeInvalid(Throwable arg0) {
        super(arg0);
    }
}