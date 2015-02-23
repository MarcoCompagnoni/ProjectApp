package com.banana.projectapp.exception;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class CampaignInvalid extends Exception {

    private static final long serialVersionUID = 1L;

    public CampaignInvalid() {
        super();
    }

    public CampaignInvalid(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public CampaignInvalid(String arg0) {
        super(arg0);
    }

    public CampaignInvalid(Throwable arg0) {
        super(arg0);
    }
}