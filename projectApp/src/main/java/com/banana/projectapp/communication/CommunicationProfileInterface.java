package com.banana.projectapp.communication;

import com.banana.projectapp.exception.ActivationNeeded;
import com.banana.projectapp.exception.AuthenticationFailure;
import com.banana.projectapp.exception.EmailDuplicate;
import com.banana.projectapp.exception.UserInvalid;
import com.banana.projectapp.exception.MailException;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.SocialAccountInvalid;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;

import java.io.IOException;

    public interface CommunicationProfileInterface {

    void registration(String email, String password)
            throws NullPointerException, EmailDuplicate, MailException, IOException;

    String login(String email, String password)
            throws NullPointerException, UserInvalid, ActivationNeeded, AuthenticationFailure, IOException;

    void logout(String email) throws NullPointerException, UserInvalid, IOException;

    void deleteYourAccount(String email, String ember_token)
            throws NullPointerException, UserInvalid, EmberTokenInvalid, IOException;

    void changeEmail(String new_mail, String ember_token)
            throws NullPointerException, UserInvalid, EmberTokenInvalid, IOException;

    void changePassword(String new_password, String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException;

    void addSocial(int social_account, String token_social_account, String ember_token)
            throws NullPointerException, SocialAccountInvalid, SocialAccountTokenInvalid, EmberTokenInvalid, IOException;

    void deleteSocial(int social_account, String ember_token)
            throws NullPointerException, SocialAccountInvalid, EmberTokenInvalid, IOException;
}
