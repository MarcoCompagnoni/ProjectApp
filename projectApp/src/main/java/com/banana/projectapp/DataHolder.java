package com.banana.projectapp;

public class DataHolder {
    public static boolean testing = false;
    private static String email;
    private static String token;
    public static String getToken() {return token;}
    public static void setToken(String token) {DataHolder.token = token;}
    public static String getEmail() {return email;}
    public static void setEmail(String email) {DataHolder.email = email;}
}
