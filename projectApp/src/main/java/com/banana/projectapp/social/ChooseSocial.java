package com.banana.projectapp.social;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.db.DBManager;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.SocialAccountInvalid;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.profile.Social;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ChooseSocial extends ActionBarActivity{

    private static final String TAG = "MainFragment";
    LoginButton authButton = null;
    private UiLifecycleHelper uiHelper;
    Session session;
    ClientStub client;
    AddSocialTask addSocialTask;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.choose_social);
        uiHelper = new UiLifecycleHelper(this, callback);

        try {
            client = new ClientStub(this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setupLogin();
        getApplicationHASH();

        uiHelper.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState());
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        if (addSocialTask!=null)
            addSocialTask.cancel(true);
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        //return id == R.id.action_settings || super.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    private String getApplicationHASH(){
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.banana.projectapp", PackageManager.GET_SIGNATURES);
            Signature signature = info.signatures[0];
            MessageDigest md;
            md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            String something = new String(Base64.encode(md.digest(), 0));
            Log.e("hash key", something);
            return something;
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return null;
    }

    private void setupLogin() {
        session = Session.getActiveSession();
        if (session!= null && session.isOpened())
            return;

        authButton = (LoginButton) findViewById(R.id.login_button);
        authButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "user_status", "user_photos", "user_location", "user_birthday", "read_stream"));
        authButton.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
    }

    private void saveToken(){
        DataHolder.setToken(session.getAccessToken());
        Log.i(TAG, "salvato token!!! = "+DataHolder.getToken());
    }

    private void onSessionStateChange(Session session, SessionState state) {
        this.session = session;
        if (state.isOpened()) {
            Log.i(TAG, "Logged in..."+DataHolder.getToken());

            saveToken();
            if (addSocialTask != null){
                return;
            }
            int socialType = DataHolder.SocialType.FACEBOOK;
            String socialToken = DataHolder.getToken();
            addSocialTask = new AddSocialTask(socialType, socialToken);
            addSocialTask.execute((Void) null);
            Log.i(TAG,"lanciato add social task");

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private class AddSocialTask extends AsyncTask<Void, Void, Boolean> {

        private final int mSocialType;
        private final String mSocialToken;

        AddSocialTask(int socialType, String socialToken) {
            mSocialType = socialType;
            mSocialToken = socialToken;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.addSocial(mSocialType, mSocialToken, ember_token);
                }
                Log.i(TAG,"chiamo add social passando "+mSocialType+" e come token "+mSocialToken);
                return true;
            } catch (IOException | EmberTokenInvalid | SocialAccountInvalid | SocialAccountTokenInvalid e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            DBManager db = new DBManager(ChooseSocial.this);
            db.open();
            switch (mSocialType){
                case DataHolder.SocialType.FACEBOOK:
                    db.insert(new Social(mSocialType, BitmapFactory.decodeResource(getResources(),
                            R.drawable.facebook_logo), "facebook"));
                    break;
                case DataHolder.SocialType.TWITTER:
                    db.insert(new Social(mSocialType, BitmapFactory.decodeResource(getResources(),
                            R.drawable.twitter_icon), "twitter"));
                    break;
            }
            db.close();
            addSocialTask = null;
        }

        @Override
        protected void onCancelled() {
            addSocialTask = null;
            super.onCancelled();
        }
    }
}
