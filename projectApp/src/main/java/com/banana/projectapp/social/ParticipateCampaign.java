package com.banana.projectapp.social;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.LocationInvalid;
import com.banana.projectapp.main.MainFragmentActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ParticipateCampaign extends ActionBarActivity {
    private static final String TAG = "MainFragment";
    ArrayAdapter placeArrayAdapter;
    private UiLifecycleHelper uiHelper;
    Session session;
    ClientStub client;
    ArrayList<String> places = new ArrayList<>();
    ParticipateCampaignTask participateCampaignTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.participate_campaign);

        TextView geoView = (TextView) findViewById(R.id.geoView);
        Location myLocation = DataHolder.getLocation();
        myLocation.getLatitude();
        myLocation.getLongitude();
        String text = myLocation.getLatitude() + ", " + myLocation.getLongitude();
        geoView.setText(text);
        geoView.invalidate();

        Button confirm = (Button) findViewById(R.id.confirm);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (participateCampaignTask != null){
                            return;
                        }

                        participateCampaignTask = new ParticipateCampaignTask();
                        participateCampaignTask.execute((Void) null);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(ParticipateCampaign.this,
                                "non è stato trovato nessun post coerente",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Simulazione").setPositiveButton("Successo", dialogClickListener)
                        .setNegativeButton("Fallimento", dialogClickListener).show();
            }
        });


        uiHelper = new UiLifecycleHelper(this, callback);

        try {
            client = new ClientStub(this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setupListAdapter();
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
        getPosts();
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
        if (participateCampaignTask != null)
            participateCampaignTask.cancel(true);
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

    protected void getPosts(){
        Request posts = Request.newGraphPathRequest(session, "/me/feed", new Request.Callback(){
            @Override
            public void onCompleted(Response response){
                final JSONObject postsConnection = response.getGraphObject().getInnerJSONObject();
                new Thread(){
                    public void run(){
                        JSONArray array;
                        try {
                            array = postsConnection.getJSONArray("data");
                            int number_of_posts = array.length();
                            for (int i=0; i<number_of_posts;i++){
                                JSONObject post = array.getJSONObject(i);
                                JSONObject place = post.getJSONObject("place");
                                places.add(place.getString("name"));
                                Log.i(TAG, place.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ParticipateCampaign.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                placeArrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }.start();
            }
        } );
        Bundle params = new Bundle();
        params.putString("with", "location");
        posts.setParameters(params);
        posts.executeAsync();
        Log.i(TAG, "execute get posts");
    }

    private void setupListAdapter() {
        placeArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);
        ListView listView = (ListView) findViewById(R.id.postList);
        listView.setAdapter(placeArrayAdapter);
    }

    private void onSessionStateChange(Session session, SessionState state) {
        this.session = session;
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private class ParticipateCampaignTask extends AsyncTask<Void, Void, Boolean> {

        ParticipateCampaignTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.participateCampaign((int)DataHolder.getCampaign().getId(),
                            DataHolder.getLocation(),ember_token);
                }

            } catch (IOException | EmberTokenInvalid  | CampaignInvalid  | LocationInvalid e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            participateCampaignTask = null;
            if (success) {
                DataHolder.setCredits(DataHolder.getCredits() + DataHolder.getCampaign().getCredits());
                Intent intent = new Intent(ParticipateCampaign.this, MainFragmentActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            participateCampaignTask = null;
            super.onCancelled();
        }
    }

}
