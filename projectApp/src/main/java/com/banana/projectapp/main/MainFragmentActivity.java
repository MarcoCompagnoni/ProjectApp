package com.banana.projectapp.main;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.campagne.CampaignFragment;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.profile.ProfileFragment;
import com.banana.projectapp.shop.ShoppingFragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.io.IOException;

public class MainFragmentActivity extends ActionBarActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    private ClientStub client;
    private UserLogoutTask userLogoutTask;
    private String[] mPlanetTitles;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout drawerView;
    private ListView mDrawerListView;
    private int mCurrentSelectedPosition = 0;
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mPlanetTitles = new String[]{"Campagne","Shopping","Profilo","Logout"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer_list);
        drawerView = (RelativeLayout) findViewById(R.id.left_drawer);
        mDrawerListView.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, mPlanetTitles
        ));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        client = new ClientStub();

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState
                    .getInt(STATE_SELECTED_POSITION);
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(DataHolder.getMyProfile().getPhoto());
        imageView.invalidate();
        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container,
                                ProfileFragment.newInstance()).commit();
                mDrawerListView.setItemChecked(2, true);
                setTitle(mPlanetTitles[2]);
                mDrawerLayout.closeDrawer(drawerView);
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //int id = item.getItemId();
        //return id == R.id.action_settings || super.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null
                && mDrawerLayout.isDrawerOpen(drawerView);
    }

    private void selectItem(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position==0){
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            CampaignFragment.newInstance()).commit();
        } else if (position==1){
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            ShoppingFragment.newInstance()).commit();
        } else if (position==2){
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            ProfileFragment.newInstance()).commit();
        }
        else if (position==3){

            if (userLogoutTask!=null){
                return;
            }

            userLogoutTask = new UserLogoutTask();
            userLogoutTask.execute((Void) null);

        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerListView.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(drawerView);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onDestroy(){
        if (userLogoutTask!= null)
            userLogoutTask.cancel(true);
        super.onDestroy();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {

        UserLogoutTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    Log.e("","chiamo logout con token "+DataHolder.getAuthToken());
                    client.logout(DataHolder.getAuthToken());
                }
                return true;
            } catch (AuthTokenInvalid | IOException userInvalid) {
                userInvalid.printStackTrace();
            } catch (NoConnectionException e) {
                MainFragmentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainFragmentActivity.this,"No connection",Toast.LENGTH_SHORT).show();
                    }
                });                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userLogoutTask = null;
            if (success){
                DataHolder.setAuthToken(null);
                DataHolder.getSession().closeAndClearTokenInformation();
                DataHolder.setSession(null);
                Intent intent = new Intent(MainFragmentActivity.this,LoginFBActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            userLogoutTask = null;
            super.onCancelled();
        }
    }
}
