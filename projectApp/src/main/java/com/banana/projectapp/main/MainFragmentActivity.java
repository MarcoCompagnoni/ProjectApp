package com.banana.projectapp.main;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.campagne.CampaignFragment;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.exception.UserInvalid;
import com.banana.projectapp.profile.ProfileFragment;
import com.banana.projectapp.shop.ShoppingFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import java.io.IOException;
import java.net.UnknownHostException;

public class MainFragmentActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
    private UserLogoutTask userLogoutTask = null;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
    private ClientStub client;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

        try {
            client = new ClientStub(this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        if (userLogoutTask!= null)
            userLogoutTask.cancel(true);
        super.onDestroy();
    }

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
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
		} else if (position==3){

            if (userLogoutTask!=null){
                return;
            }

            userLogoutTask = new UserLogoutTask(DataHolder.getEmail());
            userLogoutTask.execute((Void) null);

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
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		//int id = item.getItemId();
        //return id == R.id.action_settings || super.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container,
                    false);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainFragmentActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;

        UserLogoutTask(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    client.logout(mEmail);
                }
                return true;
            } catch (UserInvalid | IOException userInvalid) {
                userInvalid.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            DataHolder.setToken(null);
            userLogoutTask = null;
            if (success){
                Intent intent = new Intent(MainFragmentActivity.this,LoginActivity.class);
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
