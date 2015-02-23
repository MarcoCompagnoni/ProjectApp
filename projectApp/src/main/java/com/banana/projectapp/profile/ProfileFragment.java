package com.banana.projectapp.profile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.db.DBManager;
import com.banana.projectapp.R;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.UserInvalid;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment{

    static List<Bitmap> result = new ArrayList<>();
	SocialAdapter adapter;
	ListView list;
	String email;
    private ClientStub client;

    DownloadSocialsImagesTask socialsImagesTask;
    ChangeMailTask changeMailTask;
    ChangePasswordTask changePasswordTask;

    private List<Social> socials;
	
	public static ProfileFragment newInstance(String email) {
		ProfileFragment fragment = new ProfileFragment();
		fragment.email = email;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        try {
            client = new ClientStub(getActivity());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        if (socialsImagesTask!= null)
            socialsImagesTask.cancel(true);
        if (changeMailTask!= null)
            changeMailTask.cancel(true);
        if (changePasswordTask!= null)
            changePasswordTask.cancel(true);
        super.onDestroy();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.profilo, container,
				false);
		TextView nome = (TextView) rootView.findViewById(R.id.nome_utente);
		nome.setText(email);
		nome.invalidate();

        final Button change_mail = (Button) rootView.findViewById(R.id.change_email);
        change_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeMailTask != null){
                    return;
                }
                changeMailTask = new ChangeMailTask(email);
                changeMailTask.execute((Void) null);
            }
        });
		list = (ListView) rootView.findViewById(R.id.list_view);
		URL[] urls = new URL[2];
 		try {
 			urls[0]=new URL("http://3.bp.blogspot.com/-z4r06pZa54g/VBzoCWVdoPI/AAAAAAAAAPg/aAQ0MoaBKnc/s1600/facebook_logo.png");
 			urls[1]=new URL("https://cdn.serinus42.com/2a90baa3fb5ed8c/uploads/c/300/7f58d/twitter-logo_17.png");
 		} catch (MalformedURLException e) {
 			e.printStackTrace();
 		}

        DBManager db = new DBManager(getActivity()).open();
        socials = db.getSocials();
        db.close();

        //TODO verificare che il db sia aggiornato
        if (socials.size() < 2){
            Toast.makeText(getActivity(),"nessun social in db, le scarico e le inserisco",Toast.LENGTH_SHORT).show();

            if (socialsImagesTask != null)
                return rootView;
            socialsImagesTask = new DownloadSocialsImagesTask();
            socialsImagesTask.execute(urls);
        } else {
            adapter = new com.banana.projectapp.profile.SocialAdapter(getActivity(), socials);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(getActivity(), socials.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        int fragment_id = 3;
        ((MainFragmentActivity) activity).onSectionAttached(fragment_id);
	}

    public class ChangeMailTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;

        ChangeMailTask(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.production) {
                    String ember_token = DataHolder.getToken();
                    client.changeEmail(mEmail, ember_token);
                }
                return true;
            } catch (UserInvalid | EmberTokenInvalid | IOException userInvalid) {
                userInvalid.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            changeMailTask = null;
        }

        @Override
        protected void onCancelled() {
            changeMailTask = null;
            super.onCancelled();
        }
    }

    public class ChangePasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPassword;

        ChangePasswordTask(String password) {
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.production) {
                    String ember_token = DataHolder.getToken();
                    client.changePassword(mPassword, ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            changePasswordTask = null;
        }

        @Override
        protected void onCancelled() {
            changePasswordTask = null;
            super.onCancelled();
        }
    }

    private class DownloadSocialsImagesTask extends AsyncTask<URL, Integer, Long> {
        public Long doInBackground(URL... urls) {
            long totalSize = 0;
            for (URL url : urls) {
                HttpURLConnection connection;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input;
                    input = connection.getInputStream();
                    result.add(BitmapFactory.decodeStream(input));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("aaa", "caricata immagine dal link " + url);
            }
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {
        }
        @Override
        protected void onCancelled() {
            Log.i("aa","task cancellato");
            super.onCancelled();
        }
        protected void onPostExecute(Long a) {

            DBManager db = new DBManager(getActivity()).open();
            db.insert(new Social(result.get(0),
                    "facebook"));
            db.insert(new Social(result.get(1),
                    "twitter"));
            socials = db.getSocials();

             /*
	    	final List<Social> socials = new ArrayList<Social>();
	 		socials.add(new Social(result.get(0),
	 				"facebook"));
	 		socials.add(new Social(result.get(1),
	 				"twitter"));
	 		*/
            adapter = new com.banana.projectapp.profile.SocialAdapter(getActivity(), socials);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(getActivity(), socials.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            });
            db.close();
        }
    }
}
