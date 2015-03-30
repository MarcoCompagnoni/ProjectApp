package com.banana.projectapp.social;

import java.io.IOException;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.campagne.CompanyCampaign;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.LocationInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.PostInvalid;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.exception.SocialTypeInvalid;
import com.banana.projectapp.main.MainFragmentActivity;
import com.banana.projectapp.shop.ShowCode;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCampaign extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback{

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private MapView mapView;
    ClientStub client;
    //TextView geoView;
    Button confirm;
    ParticipateCampaignTask participateCampaignTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.show_campaign);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        client = new ClientStub();

        ImageView image = (ImageView) findViewById(R.id.logo_campagna);
        image.setImageBitmap(DataHolder.getCampaign().getLogo());
        image.invalidate();
        TextView name = (TextView) findViewById(R.id.nome_campagna);
        name.setText(DataHolder.getCampaign().getName());
        name.invalidate();
        TextView credits = (TextView) findViewById(R.id.crediti_campagna);
        credits.setText(DataHolder.getCampaign().getUserGain()+" €");
        credits.invalidate();
        //geoView = (TextView) findViewById(R.id.geoView);
        confirm = (Button) findViewById(R.id.geoButton);
        confirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                participateCampaignTask = new ParticipateCampaignTask();
                participateCampaignTask.execute((Void) null);
                Log.e("","lancio participate campaign task");
            }
        });

        if (DataHolder.getCampaign().getType() != CompanyCampaign.CampaignType.PHOTO) {

            buildGoogleApiClient();
            mGoogleApiClient.connect();
            mapView.setVisibility(View.VISIBLE);
        } else {
            confirm.setEnabled(true);
        }



	}
	
	@Override
	public void onResume() {
	    super.onResume();
        mapView.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
        mapView.onPause();
        super.onPause();
	}

	@Override
	public void onDestroy() {
        if (participateCampaignTask != null)
            participateCampaignTask.cancel(true);
	    mapView.onDestroy();
        super.onDestroy();
	}

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    protected synchronized void buildGoogleApiClient() {
        Log.e("aaa","build google api");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.e("aaa","google api client creato");
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
	}

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

    @Override
    public void onConnected(Bundle bundle) {

        Log.e("aaa","on connected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.e("aaa","chiamo get map asinc");
        mapView.getMapAsync(this);


        if (mLastLocation != null){
            //geoView.setText(String.valueOf(mLastLocation.getLatitude()) + String.valueOf(mLastLocation.getLongitude()));
            //geoView.invalidate();
            confirm.setEnabled(true);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.e("aaa","on map ready");
        MapsInitializer.initialize(this);
        googleMap.setMyLocationEnabled(true);
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        LatLng mylocation = new LatLng(latitude, longitude);

        LatLng local = new LatLng(DataHolder.getCampaign().getLatitude(),
                DataHolder.getCampaign().getLongitude());
        googleMap.addMarker(new MarkerOptions().position(
                local).title(DataHolder.getCampaign().getName()));
        float zoom = 15;
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(mylocation,zoom,0,0)));
        googleMap.addCircle(new CircleOptions()
                .center(local)
                .radius(10)
                .visible(true)
                .strokeColor(R.color.material_blue_grey_800));
    }

    private class ParticipateCampaignTask extends AsyncTask<Void, Void, Boolean> {

        String code;
        ParticipateCampaignTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getAuthToken();

                    Log.e("","client.participate campaign");

                    if (DataHolder.getCampaign().getType() != CompanyCampaign.CampaignType.PHOTO) {

                        code = client.participateCampaign(
                                (int) DataHolder.getCampaign().getId(),
                                DataHolder.SocialType.FACEBOOK,
                                DataHolder.getLocation().getLatitude(),
                                DataHolder.getLocation().getLongitude(),
                                ember_token);
                    } else {
                        client.participateCampaign(
                                (int) DataHolder.getCampaign().getId(),
                                DataHolder.SocialType.FACEBOOK,
                                0,
                                0,
                                ember_token);
                    }
                    return true;
                } else {
                    code ="w3Zad12";
                    return true;
                }

            } catch (IOException | AuthTokenInvalid | CampaignInvalid |
                    SocialAccountTokenInvalid | PostInvalid | LocationInvalid | SocialTypeInvalid e) {
                e.printStackTrace();
            } catch (NoConnectionException e) {
                ShowCampaign.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowCampaign.this,
                                getString(R.string.no_connection)
                                ,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            participateCampaignTask = null;
            if (success) {
                if (DataHolder.getCampaign().getType() == CompanyCampaign.CampaignType.PHOTO) {
                    DataHolder.setCredits(DataHolder.getCredits() + DataHolder.getCampaign().getUserGain());
                    Intent intent = new Intent(ShowCampaign.this, MainFragmentActivity.class);
                    startActivity(intent);
                } else {
                    DataHolder.setCode(code);
                    Intent intent = new Intent(ShowCampaign.this, ShowCode.class);
                    intent.putExtra("calling_activity","geo");
                    startActivity(intent);
                }
            }
        }

        @Override
        protected void onCancelled() {
            participateCampaignTask = null;
            super.onCancelled();
        }
    }
}

