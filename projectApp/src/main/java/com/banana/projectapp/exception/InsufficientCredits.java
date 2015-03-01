package com.banana.projectapp.exception;

public class InsufficientCredits extends Exception {

    private static final long serialVersionUID = 1L;

    public InsufficientCredits() {
        super();
    }

    public InsufficientCredits(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public InsufficientCredits(String arg0) {
        super(arg0);
    }

    public InsufficientCredits(Throwable arg0) {
        super(arg0);
    }
}