package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class CouponTypeInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public CouponTypeInvalid() {
        super();
    }

    public CouponTypeInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public CouponTypeInvalid(String arg0) {
        super(arg0);
    }

    public CouponTypeInvalid(Throwable arg0) {
        super(arg0);
    }
}