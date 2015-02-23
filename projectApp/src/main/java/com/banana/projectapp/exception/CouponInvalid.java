package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class CouponInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public CouponInvalid() {
        super();
    }

    public CouponInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public CouponInvalid(String arg0) {
        super(arg0);
    }

    public CouponInvalid(Throwable arg0) {
        super(arg0);
    }
}