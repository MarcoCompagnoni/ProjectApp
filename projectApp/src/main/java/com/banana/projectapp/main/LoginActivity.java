package com.banana.projectapp.main;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.campagne.CompanyCampaign;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.exception.ActivationNeeded;
import com.banana.projectapp.exception.AuthenticationFailure;
import com.banana.projectapp.exception.EmailDuplicate;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.MailException;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.UserInvalid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class LoginActivity extends ActionBarActivity {

	private UserLoginTask userLoginTask = null;
    private ClientStub client;

	private EditText mEmailView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;

    String user_info_json;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText("foo@example.com");
		mEmailView.invalidate();
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText("hello");
		mPasswordView.invalidate();
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);

        try{
            client = new ClientStub();
        } catch (UnknownHostException e){
            e.printStackTrace();
        }
	}

    @Override
    public void onDestroy(){
        if (userLoginTask!= null)
            userLoginTask.cancel(true);
        super.onDestroy();
    }

	public void attemptLogin() {
		if (userLoginTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
            userLoginTask = new UserLoginTask(email, password);
            userLoginTask.execute((Void) null);
		}
	}

	public static boolean isEmailValid(String email) {
		return email.contains("@");
	}

	public static boolean isPasswordValid(String password) {
		return password.length() > 3;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;

		UserLoginTask(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
            try {
                if (DataHolder.testing) {

                    Log.e("","chiamo client.login con "+mEmail+","+mPassword);
                    String token = client.login(mEmail, mPassword);
                    Log.e("","mi è arrivato il token "+token);
                    DataHolder.setToken(token);

                    Log.e("","chiamo client.getuserinfo con "+DataHolder.getToken());
                    user_info_json = client.getUserInfo(DataHolder.getToken());


                } else {
                    InputStream inputStream = getResources().openRawResource(R.raw.user_info);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    user_info_json = total.toString();
                }
                JSONObject o = new JSONObject(user_info_json);
                JSONObject data = o.getJSONObject("data");
                int availableCredits = data.getInt("credits");
                int participatingValue = data.getInt("value");
                DataHolder.setCredits(availableCredits);
                DataHolder.setValue(participatingValue);

                DataHolder.setEmail(mEmail);
            } catch (UserInvalid e) {
                try {
                    Log.e("","chiamo client.registration con "+mEmail+","+mPassword);
                    client.registration(mEmail, mPassword);
                    return false;
                } catch (EmailDuplicate | MailException | IOException emailDuplicate) {
                    emailDuplicate.printStackTrace();
                } catch (NoConnectionException e1) {
                    Toast.makeText(LoginActivity.this,"No connection",Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (AuthenticationFailure e){
                return false;
            }catch (IOException | ActivationNeeded | EmberTokenInvalid | JSONException e) {
                e.printStackTrace();
            } catch (NoConnectionException e) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"No connection",Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }

            return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
            userLoginTask = null;
			showProgress(false);

			if (success) {
				
				Intent intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				  
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
            userLoginTask = null;
			showProgress(false);
		}
	}
}
