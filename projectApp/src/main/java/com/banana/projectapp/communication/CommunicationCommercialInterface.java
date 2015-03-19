package com.banana.projectapp.communication;

import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.CouponTypeInvalid;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.InsufficientCredits;
import com.banana.projectapp.exception.LocationInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.PostInvalid;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.exception.SocialTypeInvalid;

import java.io.IOException;

public interface CommunicationCommercialInterface {

    String synchronizeCampaigns(String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException;

    String participateCampaign(int campaignID, int socialTypeID, double latitude, double longitude
            , String authToken)
            throws AuthTokenInvalid, SocialAccountTokenInvalid, CampaignInvalid, LocationInvalid,
            PostInvalid, SocialTypeInvalid, IOException, NoConnectionException;

    String synchronizeCouponTypes(String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException;

    String requestCoupon(int couponTypeID, String authToken)
            throws CouponTypeInvalid, InsufficientCredits, AuthTokenInvalid, IOException, NoConnectionException;
}