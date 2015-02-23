package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class MailException extends Exception{
    private static final long serialVersionUID = 1L;

    public MailException() {
        super();
    }

    public MailException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public MailException(String arg0) {
        super(arg0);
    }

    public MailException(Throwable arg0) {
        super(arg0);
    }
}
