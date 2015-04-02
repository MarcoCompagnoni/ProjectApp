package com.banana.projectapp.communication;

import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.exception.UserInvalid;
import com.banana.projectapp.exception.AuthTokenInvalid;

import java.io.IOException;

public interface CommunicationProfileInterface {

    String login(String facebookAccessToken)
            throws UserInvalid, SocialAccountTokenInvalid, IOException, NoConnectionException;

    String getUserInfo(String authToken) throws AuthTokenInvalid, IOException, NoConnectionException;
    float getCreditAmount(String authToken) throws AuthTokenInvalid, IOException, NoConnectionException;

    void logout(String authToken) throws AuthTokenInvalid, IOException, NoConnectionException;

    void deleteYourAccount(String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException;
}
