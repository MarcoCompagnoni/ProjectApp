package com.banana.projectapp.communication;

import android.location.Location;

import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.CouponInvalid;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.InsufficientCredits;
import com.banana.projectapp.exception.LocationInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.SocialTypeInvalid;

import java.io.IOException;

public interface CommunicationCommercialInterface {

    String synchronizeCampaigns(String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException, NoConnectionException;

    void participateCampaign(int campaign, int socialType, double latitude, double longitude
            , String ember_token)
            throws Exception;

    String synchronizeCoupons(String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException, NoConnectionException;

    String requestCoupon(int couponType, String ember_token)
            throws NullPointerException, CouponInvalid, InsufficientCredits, EmberTokenInvalid, IOException, NoConnectionException;
}