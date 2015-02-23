package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class ActivationNeeded extends Exception {

    private static final long serialVersionUID = 1L;

    public ActivationNeeded() {
        super();
    }

    public ActivationNeeded(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ActivationNeeded(String arg0) {
        super(arg0);
    }

    public ActivationNeeded(Throwable arg0) {
        super(arg0);
    }
}