package com.banana.projectapp;

import android.location.Location;

import com.banana.projectapp.campagne.CompanyCampaign;

public class DataHolder {

    public class SocialType{
        public final static int FACEBOOK = 1;
        public final static int TWITTER = 2;
        public final static int ERROR = -1;
    }

    private static String code = null;
    public static boolean testing = false;
    private static String email = null;
    private static int credits = 0;
    private static CompanyCampaign campaign;
    private static String token = null;
    private static Location location = null;

    public static String getToken() {return token;}
    public static void setToken(String token) {DataHolder.token = token;}
    public static String getEmail() {return email;}
    public static void setEmail(String email) {DataHolder.email = email;}
    public static int getCredits() {return credits;}
    public static void setCredits(int credits) {DataHolder.credits = credits;}
    public static CompanyCampaign getCampaign() {return campaign;}
    public static void setCampaign(CompanyCampaign campaign) {DataHolder.campaign = campaign;}
    public static String getCode() {return code;}
    public static void setCode(String code) {DataHolder.code = code;}
    public static Location getLocation() {return location;}
    public static void setLocation(Location location) {DataHolder.location = location;}
}