package com.banana.projectapp;

public class DataHolder {
    public static boolean production = false;
    private static String token;
    public static String getToken() {return token;}
    public static void setToken(String token) {DataHolder.token = token;}
}
