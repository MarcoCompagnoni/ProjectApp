package com.banana.projectapp.communication;

import com.banana.projectapp.exception.ActivationNeeded;
import com.banana.projectapp.exception.AuthenticationFailure;
import com.banana.projectapp.exception.EmailDuplicate;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.UserInvalid;
import com.banana.projectapp.exception.MailException;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.SocialTypeInvalid;

import java.io.IOException;

public interface CommunicationProfileInterface {

    void registration(String email, String password)
            throws NullPointerException, EmailDuplicate, MailException, IOException, NoConnectionException;

    String login(String email, String password)
            throws NullPointerException, UserInvalid, ActivationNeeded, AuthenticationFailure, IOException, NoConnectionException;

    String getUserInfo(String ember_token) throws NullPointerException, EmberTokenInvalid, IOException, NoConnectionException;

    void logout(String ember_token) throws NullPointerException, EmberTokenInvalid, IOException, NoConnectionException;

    void deleteYourAccount(String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException, NoConnectionException;

    void changeEmail(String new_mail, String ember_token)
            throws NullPointerException, UserInvalid, EmberTokenInvalid, IOException, NoConnectionException;

    void changePassword(String new_password, String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException, NoConnectionException;

    void addSocial(int socialType, String info, String ember_token)
            throws NullPointerException, SocialTypeInvalid, EmberTokenInvalid, IOException, NoConnectionException;

    void deleteSocial(int socialType, String ember_token)
            throws NullPointerException, SocialTypeInvalid, EmberTokenInvalid, IOException, NoConnectionException;
}
