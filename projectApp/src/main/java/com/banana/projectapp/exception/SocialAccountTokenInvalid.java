package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class SocialAccountTokenInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public SocialAccountTokenInvalid() {
        super();
    }

    public SocialAccountTokenInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SocialAccountTokenInvalid(String arg0) {
        super(arg0);
    }

    public SocialAccountTokenInvalid(Throwable arg0) {
        super(arg0);
    }
}