package com.banana.projectapp.communication;

import android.location.Location;

import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.CouponInvalid;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.InsufficientCredits;
import com.banana.projectapp.exception.LocationInvalid;
import com.banana.projectapp.exception.PhotoInvalid;
import com.banana.projectapp.exception.SocialAccountInvalid;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public interface CommunicationCommercialInterface {

    String synchronizeCampaigns(String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException;

    void participateCampaign(int campaign, Location location, String ember_token)
            throws NullPointerException, CampaignInvalid, LocationInvalid, EmberTokenInvalid, IOException;

    String synchronizeCoupons(String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException;

    String requestCoupon(int coupon, String ember_token)
            throws NullPointerException, CouponInvalid, InsufficientCredits, EmberTokenInvalid, IOException;
}
