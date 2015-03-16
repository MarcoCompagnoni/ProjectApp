package com.banana.projectapp.communication;

import android.util.Log;

import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.CouponTypeInvalid;
import com.banana.projectapp.exception.LocationInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.PostInvalid;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.exception.SocialTypeInvalid;
import com.banana.projectapp.exception.UserInvalid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientStub implements CommunicationProfileInterface, CommunicationCommercialInterface{

    private String TAG = "client";

    private Socket sock;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientStub() {}

    private void initialize() throws NoConnectionException {
        try{

            InetAddress ADDRESS = InetAddress.getByName("172.25.16.143");
            int PORT = 9000;
            sock = new Socket(ADDRESS, PORT);
            out = new ObjectOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            Log.i(TAG,"connesso al server");
            out.flush();
            in = new ObjectInputStream(new BufferedInputStream(sock.getInputStream()));
            Log.i(TAG,"creati gli stream");

        } catch(IOException ex){
            throw new NoConnectionException();
        }
    }

    private void close() throws IOException {

        sock.close();
        sock = null;
        Log.i(TAG,"chiudo il socket");
    }

    @Override
    public void registration(final String facebookAccessToken)
            throws SocialAccountTokenInvalid, IOException, NoConnectionException {

        initialize();
        out.writeObject("registration");
        out.writeObject(facebookAccessToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof SocialAccountTokenInvalid){
                throw (SocialAccountTokenInvalid) result;
            } else if (result instanceof String) {
                if (result.equals("OK"))
                    Log.i(TAG, "registrazione effettuata con successo");
                else
                    Log.i(TAG, "problemi con la registrazione");
            } else {
                throw (IOException) result;
            }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");
        } finally {
            try {

                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String login(final String facebookAccessToken)
            throws UserInvalid, SocialAccountTokenInvalid, IOException, NoConnectionException {

        initialize();
        out.writeObject("login");
        out.writeObject(facebookAccessToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof UserInvalid) {
                throw (UserInvalid) result;
            } else if (result instanceof SocialAccountTokenInvalid) {
                throw (SocialAccountTokenInvalid) result;
            } else if (result instanceof String) {
                return (String) result;
            } else {
                throw new IOException("input/output error");
            }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");
        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getUserInfo(final String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException {

        initialize();

        out.writeObject("getUserInfo");
        out.writeObject(authToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof AuthTokenInvalid) { throw (AuthTokenInvalid) result; }
            else if (result instanceof String) {
                return (String) result;
            } else { throw new IOException("input/output error"); }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");

        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public float getCreditAmount(String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException {

        initialize();
        out.writeObject("getCreditAmount");
        out.writeObject(authToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof AuthTokenInvalid) {
                throw (AuthTokenInvalid) result;
            } else if (result instanceof Float) {
                return (Float) result;
            } else {
                throw new IOException("input/output error");
            }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");

        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void logout(final String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException {

        initialize();
        out.writeObject("logout");
        out.writeObject(authToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof AuthTokenInvalid) { throw (AuthTokenInvalid) result; }
            else if (result instanceof String) {
                if (result.equals("OK"))
                    Log.i(TAG,"logout successfull");
                else
                    Log.i(TAG,"problemi con il logout");
            } else { throw new IOException("input/output error"); }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");

        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void deleteYourAccount(final String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException {

        initialize();
        out.writeObject("deleteYourAccount");
        out.writeObject(authToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof AuthTokenInvalid) { throw (AuthTokenInvalid) result; }
            else if (result instanceof String) {
                if (result.equals("OK"))
                    Log.i(TAG,"account cancellato con successo");
                else
                    Log.i(TAG,"problemi con la cancellazione");
            } else { throw new IOException("input/output error"); }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");

        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String synchronizeCampaigns(final String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException {

        initialize();

        out.writeObject("synchronizeCampaigns");
        out.writeObject(authToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof AuthTokenInvalid) { throw (AuthTokenInvalid) result; }
            else if (result instanceof String) {
                return (String) result;
            } else { throw new IOException("input/output error"); }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");

        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void participateCampaign(int campaignID, int socialTypeID, double latitude, double longitude
            , String authToken)
            throws AuthTokenInvalid, SocialAccountTokenInvalid, CampaignInvalid, LocationInvalid,
            PostInvalid, SocialTypeInvalid, IOException, NoConnectionException {

        initialize();

        out.writeObject("participateCampaign");
        out.writeObject(campaignID);
        out.writeObject(socialTypeID);
        out.writeObject(latitude);
        out.writeObject(longitude);
        out.writeObject(authToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof CampaignInvalid) { throw (CampaignInvalid) result; }
            else if (result instanceof AuthTokenInvalid) { throw (AuthTokenInvalid) result; }
            else if (result instanceof SocialAccountTokenInvalid) { throw (SocialAccountTokenInvalid) result; }
            else if (result instanceof LocationInvalid) { throw (LocationInvalid) result; }
            else if (result instanceof PostInvalid) { throw (PostInvalid) result; }
            else if (result instanceof SocialTypeInvalid) { throw (SocialTypeInvalid) result; }
            else if (result instanceof String) {
                if (result.equals("OK"))
                    Log.i(TAG,"partecipazione aggiunta con successo");
                else
                    Log.i(TAG,"problemi con la aggiunta partecipazione");
            }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");

        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String synchronizeCouponTypes(final String authToken)
            throws AuthTokenInvalid, IOException, NoConnectionException {

        initialize();

        out.writeObject("synchronizeCouponTypes");
        out.writeObject(authToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof AuthTokenInvalid) { throw (AuthTokenInvalid) result; }
            else if (result instanceof String) {
                return (String) result;
            } else { throw new IOException("input/output error"); }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");

        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String requestCoupon(final int couponTypeID, final String authToken)
            throws AuthTokenInvalid, CouponTypeInvalid, IOException, NoConnectionException {

        initialize();

        out.writeObject("requestCoupon");
        out.writeObject(couponTypeID);
        out.writeObject(authToken);
        out.flush();

        try {

            Object result = in.readObject();
            if (result instanceof CouponTypeInvalid) { throw (CouponTypeInvalid) result; }
            else if (result instanceof AuthTokenInvalid) { throw (AuthTokenInvalid) result; }
            else if (result instanceof String) {
                return (String) result;
            } else { throw new IOException("input/output error"); }

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found");

        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}