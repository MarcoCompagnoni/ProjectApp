package com.banana.projectapp.social;

import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.communication.ClientStub;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowCampaign extends ActionBarActivity {
	
	private static final String TAG = "MainFragment";
	//PhotosGridAdapter adapter;
    //ArrayAdapter placeArrayAdapter;
    //LoginButton authButton = null;
	//private UiLifecycleHelper uiHelper;
	Session session;
    ClientStub client;
    Location myLocation;
    //Request photosRequest = null;
	//ArrayList<Bitmap> images = new ArrayList<>();
	//ArrayList<String> sources = new ArrayList<>();
    //ArrayList<String> places = new ArrayList<>();
    //ParticipateCampaignTask participateCampaignTask;
    //String token = null;
	//String toSend = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.show_campaign);
	    //uiHelper = new UiLifecycleHelper(this, callback);

        try {
            client = new ClientStub(this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ImageView image = (ImageView) findViewById(R.id.logo_campagna);
        image.setImageBitmap(DataHolder.getCampaign().getLogo());
        image.invalidate();
        TextView name = (TextView) findViewById(R.id.nome_campagna);
        name.setText(DataHolder.getCampaign().getName());
        name.invalidate();
        TextView credits = (TextView) findViewById(R.id.crediti_campagna);
        credits.setText(DataHolder.getCampaign().getCredits()+" CR");
        credits.invalidate();

        //setupLogin();
	    //setupGridAdapter();
        //setupListAdapter();
	    //setupPhotoButton();
        //setupPostsButton();
        setupGeoView();
	    getApplicationHASH();

	    //uiHelper.onCreate(savedInstanceState);
	    
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    session = Session.getActiveSession();
//	    if (session != null &&
//	           (session.isOpened() || session.isClosed()) ) {
//	        onSessionStateChange(session, session.getState());
//	    }
//	    uiHelper.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    //uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    //uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    //uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    //uiHelper.onSaveInstanceState(outState);
	}
	
//	private Session.StatusCallback callback = new Session.StatusCallback() {
//	    @Override
//	    public void call(Session session, SessionState state, Exception exception) {
//	        onSessionStateChange(session, state);
//	    }
//	};
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
	
//	private void setupPhotoButton() {
//	    Button button = (Button) findViewById(R.id.photosButton);
//	    button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				retrievePhotos();
//			}
//		});
//	}

//    private void setupPostsButton() {
//        Button button = (Button) findViewById(R.id.postsButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getPosts();
//            }
//        });
//    }

    private void setupGeoView(){
        Button button = (Button) findViewById(R.id.geoButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getGeoLocation();
            }
        });
    }

    public void getGeoLocation(){
        final LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Location recentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(recentLocation != null && recentLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
            myLocation = recentLocation;
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    myLocation = location;
                    locationManager.removeUpdates(this);
                    DataHolder.setLocation(myLocation);
                    Intent intent = new Intent (ShowCampaign.this,ParticipateCampaign.class);
                    startActivity(intent);

//                    myLocation.getLatitude();
//                    myLocation.getLongitude();
//                    String text = myLocation.getLatitude() + ", " + myLocation.getLongitude();
//                    textView.setText(text);
//                    textView.invalidate();
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            });
        }
    }

//    protected void getPosts(){
//        Request posts = Request.newGraphPathRequest(session, "/me/feed", new Request.Callback(){
//            @Override
//            public void onCompleted(Response response){
//                final JSONObject postsConnection = response.getGraphObject().getInnerJSONObject();
//                new Thread(){
//                    public void run(){
//                        JSONArray array;
//                        try {
//                            array = postsConnection.getJSONArray("data");
//                            int number_of_posts = array.length();
//                            for (int i=0; i<number_of_posts;i++){
//                                JSONObject post = array.getJSONObject(i);
//                                JSONObject place = post.getJSONObject("place");
//                                places.add(place.getString("name"));
//                                Log.i(TAG, place.toString());
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        ShowCampaign.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                placeArrayAdapter.notifyDataSetChanged();
//                            }
//                        });
//                    }
//                }.start();
//            }
//        } );
//        Bundle params = new Bundle();
//        params.putString("with", "location");
//        posts.setParameters(params);
//        posts.executeAsync();
//        Log.i(TAG, "execute get posts");
//    }

//	protected void retrievePhotos() {
//        if (photosRequest!=null){
//            Toast.makeText(getApplicationContext(),"Stai calmo nigga",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (session==null || session.isClosed()) {
//            return;
//        }
//		photosRequest = Request.newGraphPathRequest(session, "/me/photos/uploaded/", new Request.Callback() {
//			@Override
//			public void onCompleted(Response response) {
//				final JSONObject photosConnection = response.getGraphObject().getInnerJSONObject();
//
//				new Thread(){
//					public void run(){
//						JSONArray array;
//						ArrayList<String> thumbnails = new ArrayList<>();
//				        HttpClient httpclient = new DefaultHttpClient();
//
//						try {
//							array = photosConnection.getJSONArray("data");
//							for (int i=0;i<array.length();i++){
//								thumbnails.add(i,array.getJSONObject(i).getString("picture"));
//						        HttpRequest httpRequest = new HttpGet(new URI(thumbnails.get(i)));
//						        HttpResponse http_response = httpclient
//						                .execute((HttpUriRequest) httpRequest);
//						        HttpEntity entity = http_response.getEntity();
//						        BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
//						        InputStream input = b_entity.getContent();
//						        Bitmap bitmap = BitmapFactory.decodeStream(input);
//						        sources.add(i, array.getJSONObject(i).getString("source"));
//						        images.add(i, bitmap);
//
//						        ShowCampaign.this.runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										adapter.notifyDataSetChanged();
//									}
//								});
//							}
//						} catch (JSONException | URISyntaxException | IOException e) {
//							e.printStackTrace();
//						}
//                    }
//				}.start();
//			}
//		});
//		Bundle params = new Bundle();
//		params.putString("limit", "100");
//		photosRequest.setParameters(params);
//        photosRequest.executeAsync();
//	}

//	private void setupGridAdapter() {
//		adapter = new PhotosGridAdapter(this, sources, images);
//	    GridView grid = (GridView) findViewById(R.id.grid);
//	    grid.setAdapter(adapter);
//	    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//	        @Override
//	        public void onClick(DialogInterface dialog, int which) {
//	            switch (which){
//	            case DialogInterface.BUTTON_POSITIVE:
//                    if (participateCampaignTask != null){
//                        return;
//                    }
//
//                    int campaign = (int) DataHolder.getCampaign().getId();
//
//                    final LocationManager locationManager = (LocationManager)
//                            getSystemService(Context.LOCATION_SERVICE);
//                    participateCampaignTask = new ParticipateCampaignTask(campaign,
//                            myLocation);
//                    participateCampaignTask.execute((Void) null);
//	                break;
//	            case DialogInterface.BUTTON_NEGATIVE:
//	                break;
//	            }
//	        }
//	    };
//	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//	    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//            	toSend = sources.get(position);
//            	builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
//    	        .setNegativeButton("No", dialogClickListener).show();
//
//            }
//        });
//	}

//    private void setupListAdapter() {
//        placeArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);
//        ListView listView = (ListView) findViewById(R.id.postList);
//        listView.setAdapter(placeArrayAdapter);
//    }

//	private void setupLogin() {
//        session = Session.getActiveSession();
//        if (session!= null && session.isOpened())
//            return;
//
//	    authButton = (LoginButton) findViewById(R.id.login_button);
//	    authButton.setReadPermissions(Arrays.asList("user_likes","user_status","user_status","user_photos","user_location","user_birthday","read_stream"));
//	    authButton.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
//	}
	
//	private void getToken(){
//		token = session.getAccessToken();
//        Date exp = session.getExpirationDate();
//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.ITALY);
//        String reportDate = df.format(exp);
//        Log.i(TAG, "ottenuto token!!! = "+token);
//        Log.i(TAG, "scadra il "+reportDate);
//	}
	
//	private void onSessionStateChange(Session session, SessionState state) {
//		this.session = session;
//	    if (state.isOpened()) {
//	        Log.i(TAG, "Logged in...");
//
//	        getToken();
//	        //c.sendObject(token);
//
//	    } else if (state.isClosed()) {
//	        	Log.i(TAG, "Logged out...");
//	    }
//	}

//    private class ParticipateCampaignTask extends AsyncTask<Void, Void, Boolean> {
//
//        int mCampaign;
//        Location mLocation;
//
//        ParticipateCampaignTask(int campaign, Location location) {
//            mCampaign = campaign;
//            mLocation = location;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            try {
//                if (DataHolder.testing) {
//                    String ember_token = DataHolder.getToken();
//                    client.participateCampaign(mCampaign,mLocation,ember_token);
//                }
//            } catch (IOException | EmberTokenInvalid | CampaignInvalid | LocationInvalid e) {
//                e.printStackTrace();
//            }
//
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            participateCampaignTask = null;
//            if (success){
//                DataHolder.setCredits(DataHolder.getCredits() + DataHolder.getCampaign().getCredits());
//                Log.e("","aggiornati crediti= "+DataHolder.getCredits());
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            participateCampaignTask = null;
//            super.onCancelled();
//        }
//    }
}

