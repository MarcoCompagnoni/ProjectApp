package com.banana.projectapp.profile;

import java.io.IOException;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.R;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.main.LoginFBActivity;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment{

    private ClientStub client;

    private DeleteAccountTask deleteAccountTask;

    public void onActivityResult(int a, int b, Intent i){
        super.onActivityResult(a,b,i);
    }

	public static ProfileFragment newInstance() {
		return new ProfileFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        client = new ClientStub();
    }

    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        if (deleteAccountTask!=null)
            deleteAccountTask.cancel(true);

        super.onDestroy();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.profilo, container,
				false);
		TextView nome = (TextView) rootView.findViewById(R.id.nome_utente);
        nome.setText(DataHolder.getUserName());
        nome.invalidate();
        TextView credits = (TextView) rootView.findViewById(R.id.numero_crediti);
        credits.setText(DataHolder.getCredits()+" €");
        credits.invalidate();
        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageView.setImageBitmap(DataHolder.getMyProfile().getPhoto());
        imageView.invalidate();

        return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        int fragment_id = 3;
        ((MainFragmentActivity) activity).onSectionAttached(fragment_id);
	}

    private class DeleteAccountTask extends AsyncTask<Void, Void, Boolean> {

        DeleteAccountTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing_with_server) {
                    String ember_token = DataHolder.getAuthToken();
                    client.deleteYourAccount(ember_token);
                }
                return true;
            } catch (IOException | AuthTokenInvalid e) {
                e.printStackTrace();
                return false;
            } catch (NoConnectionException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                getString(R.string.no_connection)
                                ,Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            deleteAccountTask = null;
            if (success) {
                DataHolder.setAuthToken(null);
                DataHolder.getSession().closeAndClearTokenInformation();
                DataHolder.setSession(null);
                Intent intent = new Intent(getActivity(), LoginFBActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            deleteAccountTask = null;
            super.onCancelled();
        }
    }
}
