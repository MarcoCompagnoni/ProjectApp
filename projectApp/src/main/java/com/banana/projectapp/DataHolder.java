package com.banana.projectapp;

import android.location.Location;
import com.banana.projectapp.campagne.Campaign;
import com.banana.projectapp.profile.MyProfile;
import com.facebook.Session;

public class DataHolder {

    //abilita la connessione al server, se è falso il testing è tutto in locale
    public static boolean testing_with_server = true;
    private static String serverIP = "10.20.6.101";

    //profilo dell'utente
    private static MyProfile myProfile;

    //codice per il buono richiesto
    private static String code = null;

    //crediti dell'utente
    private static float credits = 0;

    //campagna selezionata
    private static Campaign campaign;

    //token di autenticazione al server Friendz
    private static String authToken = null;

    //sessione attiva di Facebook dell'utente
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
    public static Campaign getCampaign() {return campaign;}
    public static void setCampaign(Campaign campaign) {DataHolder.campaign = campaign;}
    public static String getCode() {return code;}
    public static void setCode(String code) {DataHolder.code = code;}
    public static String getServerIP() {
        return serverIP;
    }
}