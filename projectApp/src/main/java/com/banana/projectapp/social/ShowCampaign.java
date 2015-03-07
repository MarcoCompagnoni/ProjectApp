package com.banana.projectapp.social;

import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.communication.ClientStub;

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

    ClientStub client;
    Location myLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.show_campaign);

        try {
            client = new ClientStub();
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
        credits.setText(DataHolder.getValue()+" CR");
        credits.invalidate();

        setupGeoView();
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
}

