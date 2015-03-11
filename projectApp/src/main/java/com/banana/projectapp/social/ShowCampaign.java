package com.banana.projectapp.social;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.CampaignInvalid;
import com.banana.projectapp.exception.LocationInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.PostInvalid;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.exception.SocialTypeInvalid;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCampaign extends ActionBarActivity {

    ClientStub client;
    Location myLocation;
    TextView geoView;
    Button confirm;
    ParticipateCampaignTask participateCampaignTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.show_campaign);

        client = new ClientStub();

        ImageView image = (ImageView) findViewById(R.id.logo_campagna);
        image.setImageBitmap(DataHolder.getCampaign().getLogo());
        image.invalidate();
        TextView name = (TextView) findViewById(R.id.nome_campagna);
        name.setText(DataHolder.getCampaign().getName());
        name.invalidate();
        TextView credits = (TextView) findViewById(R.id.crediti_campagna);
        credits.setText(DataHolder.getCampaign().getUserGain()+" CR");
        credits.invalidate();
        geoView = (TextView) findViewById(R.id.geoView);
        confirm = (Button) findViewById(R.id.geoButton);
        confirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                participateCampaignTask = new ParticipateCampaignTask();
                participateCampaignTask.execute((Void) null);
                Log.e("","lancio participate campaign task");
            }
        });

        getGeoLocation();
	    getApplicationHASH();
	    
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	}

	@Override
	public void onDestroy() {
        if (participateCampaignTask != null)
            participateCampaignTask.cancel(true);
	    super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
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
                    geoView.setText(myLocation.getLatitude()+","+myLocation.getLongitude());
                    geoView.invalidate();
                    confirm.setEnabled(true);

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
    private class ParticipateCampaignTask extends AsyncTask<Void, Void, Boolean> {

        ParticipateCampaignTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getAuthToken();

                    Log.e("","client.participate campaign");
                    client.participateCampaign(
                            (int)DataHolder.getCampaign().getId(),
                            DataHolder.SocialType.FACEBOOK,
                            DataHolder.getLocation().getLatitude(),
                            DataHolder.getLocation().getLongitude(),
                            ember_token);
                    return true;
                } else {
                    return true;
                }

            } catch (IOException | AuthTokenInvalid | CampaignInvalid |
                    SocialAccountTokenInvalid | PostInvalid | LocationInvalid | SocialTypeInvalid e) {
                e.printStackTrace();
            } catch (NoConnectionException e) {
                ShowCampaign.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowCampaign.this,"No connection",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            participateCampaignTask = null;
            if (success) {
                DataHolder.setCredits(DataHolder.getCredits() + DataHolder.getCampaign().getUserGain());
                Intent intent = new Intent(ShowCampaign.this, MainFragmentActivity.class);
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

