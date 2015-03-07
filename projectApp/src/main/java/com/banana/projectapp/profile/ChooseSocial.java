package com.banana.projectapp.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.db.DBManager;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.SocialTypeInvalid;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ChooseSocial extends ActionBarActivity{

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
            client = new ClientStub();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setupLogin();

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
//        new Request(
//                session,
//                "/me/permissions",
//                null,
//                HttpMethod.DELETE,
//                new Request.Callback() {
//                    public void onCompleted(Response response) {
//
//                    }
//                }
//        ).executeAsync();
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

    private void setupLogin() {

        authButton = (LoginButton) findViewById(R.id.login_button);
        authButton.setReadPermissions(Arrays.asList("user_friends","user_photos", "user_birthday", "read_stream"));
        authButton.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
    }

    private void onSessionStateChange(Session session, SessionState state) {
        this.session = session;
        if (state.isOpened()) {

//            Log.i("","START");
//            for (String s: session.getPermissions()) {
//                Log.i("", s);
//            }

            if (addSocialTask != null){
                return;
            }

            int socialType = DataHolder.SocialType.FACEBOOK;
            addSocialTask = new AddSocialTask(socialType);
            addSocialTask.execute((Void) null);
        }
    }

    private class AddSocialTask extends AsyncTask<Void, Void, Boolean> {

        private final int mSocialType;

        AddSocialTask(int socialType){
            mSocialType = socialType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    Log.i("","chiamo client.add social con"+mSocialType+","+session.getAccessToken());
                    client.addSocial(mSocialType, session.getAccessToken(), ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid | SocialTypeInvalid e) {
                e.printStackTrace();
                return false;
            } catch (NoConnectionException e) {
                ChooseSocial.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChooseSocial.this,"No connection",Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.e("","onpostExecute");
            if (success) {
                DBManager db = new DBManager(ChooseSocial.this);
                db.open();
                switch (mSocialType) {
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
            } else {
                Toast.makeText(ChooseSocial.this, "c'è stato qualche imprevisto nel aggiungere il social biricchino", Toast.LENGTH_SHORT).show();
            }
            addSocialTask = null;
        }

        @Override
        protected void onCancelled() {
            addSocialTask = null;
            super.onCancelled();
        }
    }
}
