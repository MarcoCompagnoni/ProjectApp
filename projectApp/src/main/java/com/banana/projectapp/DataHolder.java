package com.banana.projectapp;

public class DataHolder {

    public class SocialType{
        public final static int FACEBOOK = 1;
        public final static int TWITTER = 2;
        public final static int ERROR = -1;
    }

    public static boolean testing = false;
    private static String email = null;
    private static String token = null;
    public static String getToken() {return token;}
    public static void setToken(String token) {DataHolder.token = token;}
    public static String getEmail() {return email;}
    public static void setEmail(String email) {DataHolder.email = email;}
}