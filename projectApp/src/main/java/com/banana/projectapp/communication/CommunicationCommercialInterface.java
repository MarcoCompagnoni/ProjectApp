package com.banana.projectapp.communication;

import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.CouponInvalid;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.PhotoInvalid;
import com.banana.projectapp.exception.SocialAccountInvalid;

import java.io.IOException;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public interface CommunicationCommercialInterface {

    void synchronizeCampaigns(String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException;

    void participateCampaign(int campaign, int social_account, String photo_url, String ember_token)
            throws NullPointerException, CampaignInvalid, SocialAccountInvalid, PhotoInvalid, EmberTokenInvalid, IOException;

    void synchronizeCoupons(String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException;

    void requestCoupon(int coupon, String ember_token)
            throws NullPointerException, CouponInvalid, EmberTokenInvalid, IOException;
}
