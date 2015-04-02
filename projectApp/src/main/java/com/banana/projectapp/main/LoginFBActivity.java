package com.banana.projectapp.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.exception.UserInvalid;
import com.banana.projectapp.profile.MyProfile;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginFBActivity extends ActionBarActivity{

    LoginButton authButton = null;
    private UiLifecycleHelper uiHelper;
    Session session;
    ClientStub client;
    LoginTask loginTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!DataHolder.testing_with_server){

            loginTask = new LoginTask();
            loginTask.execute();
        } else {
            this.setContentView(R.layout.login_fb);
            uiHelper = new UiLifecycleHelper(this, callback);

            client = new ClientStub();

            setupLogin();
            getApplicationHASH();
            uiHelper.onCreate(savedInstanceState);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (DataHolder.testing_with_server) {
            session = Session.getActiveSession();
            if (session != null &&
                    (session.isOpened() || session.isClosed())) {
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DataHolder.testing_with_server) {
            uiHelper.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (loginTask!=null)
            loginTask.cancel(true);
        super.onDestroy();
        if (DataHolder.testing_with_server) {
            uiHelper.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (DataHolder.testing_with_server) {
            uiHelper.onSaveInstanceState(outState);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (DataHolder.testing_with_server) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onSessionStateChange(Session session, SessionState state) {
        this.session = session;
        if (state.isOpened()) {
            Log.i("","session opened");
            authButton.setVisibility(View.INVISIBLE);
            if (loginTask != null){
                return;
            }

            loginTask = new LoginTask();
            loginTask.execute((Void) null);
        } else if (state.isClosed()){
            Log.i("","session closed");
        } else {
            Log.i("",""+state );
        }
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String userInfo;
            try {
                if (DataHolder.testing_with_server){
                    String token = client.login(session.getAccessToken());
                    DataHolder.setAuthToken(token);
                    float credits = client.getCreditAmount(token);
                    DataHolder.setCredits(credits);
                    userInfo = client.getUserInfo(token);
                } else {
                    InputStream inputStream = getResources().openRawResource(R.raw.user_info);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    try {
                        while ((line = r.readLine()) != null) {
                            total.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    userInfo = total.toString();
                }

                try {
                    JSONObject o = new JSONObject(userInfo);
                    JSONObject data = o.getJSONObject("data");
                    String firstName = data.getString("firstname");
                    String lastName = data.getString("lastname");
                    String photoUrl = data.getString("photo");
                    HttpURLConnection connection;

                    connection = (HttpURLConnection) new URL(photoUrl).openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input;
                    input = connection.getInputStream();
                    Bitmap photo = BitmapFactory.decodeStream(input);
                    DataHolder.setMyProfile(new MyProfile(firstName, lastName, photo));

                } catch (IOException e) {
                    e.printStackTrace();
                }


                return true;
            } catch (IOException | AuthTokenInvalid | SocialAccountTokenInvalid | UserInvalid | JSONException e) {
                e.printStackTrace();

            } catch (NoConnectionException e) {
                LoginFBActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginFBActivity.this,"No connection",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                DataHolder.setSession(session);
                Intent intent = new Intent (LoginFBActivity.this, MainFragmentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else {
                Toast.makeText(LoginFBActivity.this, "c'è stato qualche imprevisto", Toast.LENGTH_SHORT).show();
            }
            loginTask = null;
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
            super.onCancelled();
        }
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
}
