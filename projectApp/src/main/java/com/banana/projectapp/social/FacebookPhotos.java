package com.banana.projectapp.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.PhotoInvalid;
import com.banana.projectapp.exception.SocialAccountInvalid;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FacebookPhotos extends ActionBarActivity {
	
	private static final String TAG = "MainFragment";
	PhotosGridAdapter adapter;
    ArrayAdapter placeArrayAdapter;
    LoginButton authButton = null;
	private UiLifecycleHelper uiHelper;
	Session session;
    ClientStub client;
    Request photosRequest = null;
	ArrayList<Bitmap> images = new ArrayList<>();
	ArrayList<String> sources = new ArrayList<>();
    ArrayList<String> places = new ArrayList<>();
    ParticipateCampaignTask participateCampaignTask;
    String token = null;
	String toSend = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.facebook_photos);
	    uiHelper = new UiLifecycleHelper(this, callback);

        try {
            client = new ClientStub(this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //setupLogin();
	    setupGridAdapter();
        setupListAdapter();
	    setupPhotoButton();
        setupPostsButton();
        setupGeoView();
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
	    } catch (NameNotFoundException e1) {
	        Log.e("name not found", e1.toString());
	    } catch (NoSuchAlgorithmException e) {
	        Log.e("no such an algorithm", e.toString());
	    } catch (Exception e) {
	        Log.e("exception", e.toString());
	    }
		return null;
	}
	
	private void setupPhotoButton() {
	    Button button = (Button) findViewById(R.id.photosButton);
	    button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				retrievePhotos();
			}
		});
	}

    private void setupPostsButton() {
        Button button = (Button) findViewById(R.id.postsButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosts();
            }
        });
    }

    private void setupGeoView(){
        Button button = (Button) findViewById(R.id.geoButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getGeoLocation();
            }
        });
    }

    private void getGeoLocation(){
        final TextView textView = (TextView) findViewById(R.id.geoView);
        final LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(loc != null && loc.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
            Log.i(TAG,"location recente");
            loc.getLatitude();
            loc.getLongitude();
            String text = loc.getLatitude() + ", " + loc.getLongitude();
            textView.setText(text);
            textView.invalidate();
            Log.i(TAG,"fatto");

        } else {
            Log.i(TAG,"no recente, mettiamo il location listener.");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    Log.i(TAG, "on location changed");
                    loc.getLatitude();
                    loc.getLongitude();
                    String text = loc.getLatitude() + ", " + loc.getLongitude();
                    textView.setText(text);
                    textView.invalidate();
                    Log.i(TAG, "fatto");
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    Log.i(TAG, "on location changed");
                    loc.getLatitude();
                    loc.getLongitude();
                    String text = loc.getLatitude() + ", " + loc.getLongitude();
                    textView.setText(text);
                    textView.invalidate();
                    Log.i(TAG, "fatto");
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
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

                        FacebookPhotos.this.runOnUiThread(new Runnable() {
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
        Log.i(TAG,"execute get posts" );
    }

	protected void retrievePhotos() {
        if (photosRequest!=null){
            Toast.makeText(getApplicationContext(),"Stai calmo nigga",Toast.LENGTH_SHORT).show();
            return;
        }
        if (session==null || session.isClosed()) {
            return;
        }
		photosRequest = Request.newGraphPathRequest(session, "/me/photos/uploaded/", new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				final JSONObject photosConnection = response.getGraphObject().getInnerJSONObject();

				new Thread(){
					public void run(){
						JSONArray array;
						ArrayList<String> thumbnails = new ArrayList<>();
				        HttpClient httpclient = new DefaultHttpClient();
				
						try {
							array = photosConnection.getJSONArray("data");
							for (int i=0;i<array.length();i++){
								thumbnails.add(i,array.getJSONObject(i).getString("picture"));
						        HttpRequest httpRequest = new HttpGet(new URI(thumbnails.get(i)));
						        HttpResponse http_response = httpclient
						                .execute((HttpUriRequest) httpRequest);
						        HttpEntity entity = http_response.getEntity();
						        BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
						        InputStream input = b_entity.getContent();
						        Bitmap bitmap = BitmapFactory.decodeStream(input);
						        sources.add(i, array.getJSONObject(i).getString("source"));
						        images.add(i, bitmap);
						       
						        FacebookPhotos.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										adapter.notifyDataSetChanged();
									}
								});
							}
						} catch (JSONException | URISyntaxException | IOException e) {
							e.printStackTrace();
						}
                    }
				}.start();
			}
		});
		Bundle params = new Bundle();
		params.putString("limit", "100");
		photosRequest.setParameters(params);
        photosRequest.executeAsync();
	}

	private void setupGridAdapter() {
		adapter = new PhotosGridAdapter(this, sources, images);
	    GridView grid = (GridView) findViewById(R.id.grid);
	    grid.setAdapter(adapter);
	    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            switch (which){
	            case DialogInterface.BUTTON_POSITIVE:
                    if (participateCampaignTask != null){
                        return;
                    }

                    //TODO
                    int campaign = 0;
                    int socialType = 0;
                    String url = "a";

                    participateCampaignTask = new ParticipateCampaignTask(campaign, socialType, url);
                    participateCampaignTask.execute((Void) null);
	                break;
	            case DialogInterface.BUTTON_NEGATIVE:
	                break;
	            }
	        }
	    };
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            	toSend = sources.get(position);
            	builder.setMessage("Are you sure?").setPositiveButton("Hell yeah!!", dialogClickListener)
    	        .setNegativeButton("Nah, fuck it", dialogClickListener).show();
            	
            	//Toast.makeText(MainActivity.this, "You Clicked at " +sources.get(position), Toast.LENGTH_SHORT).show();
            }
        });
	}

    private void setupListAdapter() {
        placeArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);
        ListView listView = (ListView) findViewById(R.id.postList);
        listView.setAdapter(placeArrayAdapter);
    }

	private void setupLogin() {
        session = Session.getActiveSession();
        if (session!= null && session.isOpened())
            return;

	    authButton = (LoginButton) findViewById(R.id.login_button);
	    authButton.setReadPermissions(Arrays.asList("user_likes","user_status","user_status","user_photos","user_location","user_birthday","read_stream"));
	    authButton.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
	}
	
	private void getToken(){
		token = session.getAccessToken();
        Date exp = session.getExpirationDate();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.ITALY);
        String reportDate = df.format(exp);
        Log.i(TAG, "ottenuto token!!! = "+token);
        Log.i(TAG, "scadra il "+reportDate);
	}
	
	private void onSessionStateChange(Session session, SessionState state) {
		this.session = session;
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        
	        getToken();
	        //c.sendObject(token);
		        
	    } else if (state.isClosed()) {
	        	Log.i(TAG, "Logged out...");
	    }
	}

    private class ParticipateCampaignTask extends AsyncTask<Void, Void, Boolean> {


        int mCampaign;
        int mSocialType;
        String photoUrl;

        ParticipateCampaignTask(int campaign, int socialType, String url) {
            mCampaign = campaign;
            mSocialType = socialType;
            photoUrl = url;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.participateCampaign(mCampaign,mSocialType,photoUrl,ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid | PhotoInvalid | CampaignInvalid | SocialAccountInvalid e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            participateCampaignTask = null;
        }

        @Override
        protected void onCancelled() {
            participateCampaignTask = null;
            super.onCancelled();
        }
    }
}

