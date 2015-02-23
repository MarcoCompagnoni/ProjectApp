package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class SocialAccountInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public SocialAccountInvalid() {
        super();
    }

    public SocialAccountInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SocialAccountInvalid(String arg0) {
        super(arg0);
    }

    public SocialAccountInvalid(Throwable arg0) {
        super(arg0);
    }
}