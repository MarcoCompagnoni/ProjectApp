package com.banana.projectapp;

import com.banana.projectapp.campagne.CompanyCampaign;

public class DataHolder {

    public static int getCredits() {
        return credits;
    }

    public static void setCredits(int credits) {
        DataHolder.credits = credits;
    }

    public static CompanyCampaign getCampaign() {
        return campaign;
    }

    public static void setCampaign(CompanyCampaign campaign) {
        DataHolder.campaign = campaign;
    }

    public class SocialType{
        public final static int FACEBOOK = 1;
        public final static int TWITTER = 2;
        public final static int ERROR = -1;
    }

    public static boolean testing = false;
    private static String email = null;
    private static int credits = 0;
    private static CompanyCampaign campaign;
    private static String token = null;
    public static String getToken() {return token;}
    public static void setToken(String token) {DataHolder.token = token;}
    public static String getEmail() {return email;}
    public static void setEmail(String email) {DataHolder.email = email;}
}