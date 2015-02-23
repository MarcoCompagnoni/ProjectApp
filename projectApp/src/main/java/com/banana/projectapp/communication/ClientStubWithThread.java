package com.banana.projectapp.communication;

import android.util.Log;

import com.banana.projectapp.exception.ActivationNeeded;
import com.banana.projectapp.exception.AuthenticationFailure;
import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.CouponInvalid;
import com.banana.projectapp.exception.EmailDuplicate;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.PhotoInvalid;
import com.banana.projectapp.exception.SocialAccountInvalid;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.exception.UserInvalid;
import com.banana.projectapp.exception.MailException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Compagnoni on 15/01/2015.
 */
public class ClientStubWithThread implements CommunicationProfileInterface, CommunicationCommercialInterface{

    private String TAG = "client";
    private static InetAddress ADDRESS;
    private static int PORT;

    private Socket sock;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String ember_token;
    private String campaign_json;
    private String coupon_json;

    public ClientStubWithThread() throws UnknownHostException {
        ADDRESS = InetAddress.getByName("10.10.3.8");
        PORT = 9000;
    }

    private void initialize() throws IOException {
        sock = new Socket(ADDRESS, PORT);
        out = new ObjectOutputStream(new BufferedOutputStream(sock.getOutputStream()));
        out.flush();
        in = new ObjectInputStream(new BufferedInputStream(sock.getInputStream()));
    }

    private void close() throws IOException {
        sock.close();
        sock = null;
    }

    @Override
    public void registration(final String email, final String password)
            throws NullPointerException, EmailDuplicate, MailException, IOException {

        if (email == null) { throw new NullPointerException("missing email."); }
        if (password == null) { throw new NullPointerException("missing password."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("registration");
                    out.writeObject(email);
                    out.writeObject(password);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof EmailDuplicate) {throw (EmailDuplicate)result;}
                        else if (result instanceof MailException) {throw (MailException)result;}
                        if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG, "registrazione effettuata con successo");
                            else
                                Log.i(TAG, "problemi con la registrazione");
                        } else {
                            throw (Exception)result;
                        }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {


                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

    }

    @Override
    public String login(final String email, final String password)
            throws NullPointerException, UserInvalid, ActivationNeeded, AuthenticationFailure, IOException {

        if (email == null) { throw new NullPointerException("missing email."); }
        if (password == null) { throw new NullPointerException("missing password."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("login");
                    out.writeObject(email);
                    out.writeObject(password);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof UserInvalid) {
                            throw (UserInvalid) result;
                        } else if (result instanceof ActivationNeeded) {
                            throw (ActivationNeeded) result;
                        } else if (result instanceof AuthenticationFailure) {
                            throw (AuthenticationFailure) result;
                        } else if (result instanceof String) {
                            ember_token = (String) result;
                        } else {
                            throw new IOException("input/output error");
                        }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        return null;
    }

    @Override
    public void logout(final String email) throws NullPointerException, IOException, UserInvalid {

        if (email == null) { throw new NullPointerException("missing email."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("logout");
                    out.writeObject(email);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof UserInvalid) { throw (UserInvalid) result; }
                        else if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG,"logout successfull");
                            else
                                Log.i(TAG,"problemi con il logout");
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void deleteYourAccount(final String email, final String ember_token)
            throws NullPointerException, UserInvalid, EmberTokenInvalid, IOException {

        if (email == null) { throw new NullPointerException("missing email."); }
        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("deleteYourAccount");
                    out.writeObject(email);
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof UserInvalid) { throw (UserInvalid) result; }
                        else if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG,"account cancellato con successo");
                            else
                                Log.i(TAG,"problemi con la cancellazione");
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void changeEmail(final String new_mail, final String ember_token)
            throws NullPointerException, UserInvalid, EmberTokenInvalid, IOException {

        if (new_mail == null) { throw new NullPointerException("missing email."); }
        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("changeEmail");
                    out.writeObject(new_mail);
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof UserInvalid) { throw (UserInvalid) result; }
                        else if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG,"mail cambiata con successo");
                            else
                                Log.i(TAG,"problemi con il cambio mail");
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {
                               close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void changePassword(final String new_password, final String ember_token)
            throws NullPointerException, EmberTokenInvalid, IOException {

        if (new_password == null) { throw new NullPointerException("missing password."); }
        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("changePassword");
                    out.writeObject(new_password);
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG,"password cambiata con successo");
                            else
                                Log.i(TAG,"problemi con il cambio password");
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void addSocial(final int social_account, final String token_social_account, final String ember_token)
            throws NullPointerException, SocialAccountInvalid, SocialAccountTokenInvalid, EmberTokenInvalid, IOException {

        if (token_social_account == null) { throw new NullPointerException("missing social token."); }
        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("addSocial");
                    out.writeObject(social_account);
                    out.writeObject(token_social_account);
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof SocialAccountInvalid) { throw (SocialAccountInvalid) result; }
                        if (result instanceof SocialAccountTokenInvalid) { throw (SocialAccountTokenInvalid) result; }
                        else if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG,"social aggiunto con successo");
                            else
                                Log.i(TAG,"problemi con la aggiunta social");
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void deleteSocial(final int social_account, final String ember_token)
            throws NullPointerException, EmberTokenInvalid, SocialAccountInvalid, IOException {

        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("deleteSocial");
                    out.writeObject(social_account);
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof SocialAccountInvalid) { throw (SocialAccountInvalid) result; }
                        else if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG,"social cancellato con successo");
                            else
                                Log.i(TAG,"problemi con la cancellazione social");
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void synchronizeCampaigns(final String ember_token) throws NullPointerException, EmberTokenInvalid, IOException {

        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("synchronizeCampaigns");
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            campaign_json = (String) result;
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void participateCampaign(final int campaign, final int social_account, final String photo_url, final String ember_token)
            throws NullPointerException, CampaignInvalid, SocialAccountInvalid, PhotoInvalid, EmberTokenInvalid, IOException {

        if (photo_url == null) { throw new NullPointerException("missing photo url."); }
        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("partecipateCampaign");
                    out.writeObject(campaign);
                    out.writeObject(social_account);
                    out.writeObject(photo_url);
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof CampaignInvalid) { throw (CampaignInvalid) result; }
                        else if (result instanceof SocialAccountInvalid) { throw (SocialAccountInvalid) result; }
                        else if (result instanceof PhotoInvalid) { throw (PhotoInvalid) result; }
                        else if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG,"partecipazione aggiunta con successo");
                            else
                                Log.i(TAG,"problemi con la aggiunta partecipazione");
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void synchronizeCoupons(final String ember_token) throws NullPointerException, EmberTokenInvalid, IOException {

        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("coupons");
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            coupon_json = (String)result;
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void requestCoupon(final int coupon, final String ember_token)
            throws NullPointerException, EmberTokenInvalid, CouponInvalid, IOException {

        if (ember_token == null) { throw new NullPointerException("missing ember token."); }

        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    initialize();
                    out.writeObject("requestCoupon");
                    out.writeObject(coupon);
                    out.writeObject(ember_token);
                    out.flush();

                    try {

                        Object result = in.readObject();
                        if (result instanceof CouponInvalid) { throw (CouponInvalid) result; }
                        else if (result instanceof EmberTokenInvalid) { throw (EmberTokenInvalid) result; }
                        else if (result instanceof String) {
                            if (result.equals("OK"))
                                Log.i(TAG,"coupon richiesto con successo");
                            else
                                Log.i(TAG,"problemi con la richiesta coupon");
                        } else { throw new IOException("input/output error"); }

                    } catch (ClassNotFoundException e) {
                        throw new IOException("input/output error");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    try {

                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }
}