package com.banana.projectapp;

import android.location.Location;

import com.banana.projectapp.campagne.CompanyCampaign;
import com.banana.projectapp.profile.MyProfile;
import com.facebook.Session;

public class DataHolder {

    private static MyProfile myProfile;
    private static String code = null;
    public static boolean testing = true;
    private static float credits = 0;
    private static CompanyCampaign campaign;
    private static String authToken = null;
    private static Location location = null;
    private static Session session = null;

    public class SocialType{
        public final static int FACEBOOK = 1;
        public final static int TWITTER = 2;
        public final static int ERROR = -1;
    }

    public static Session getSession() {return session;}
    public static void setSession(Session session) {DataHolder.session = session;}
    public static MyProfile getMyProfile() {return myProfile;}
    public static void setMyProfile(MyProfile myProfile) {DataHolder.myProfile = myProfile;}
    public static String getAuthToken() {return authToken;}
    public static void setAuthToken(String authToken) {DataHolder.authToken = authToken;}
    public static String getUserName() {return myProfile.getFirstName()+" "+myProfile.getLastName();}
    public static float getCredits() {return credits;}
    public static void setCredits(float credits) {DataHolder.credits = credits;}
    public static CompanyCampaign getCampaign() {return campaign;}
    public static void setCampaign(CompanyCampaign campaign) {DataHolder.campaign = campaign;}
    public static String getCode() {return code;}
    public static void setCode(String code) {DataHolder.code = code;}
    public static Location getLocation() {return location;}
    public static void setLocation(Location location) {DataHolder.location = location;}
}