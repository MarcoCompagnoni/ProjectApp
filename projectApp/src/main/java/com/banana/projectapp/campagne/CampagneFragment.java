package com.banana.projectapp.campagne;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.banana.projectapp.db.DBManager;
import com.banana.projectapp.FacebookPhotos;
import com.banana.projectapp.R;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CampagneFragment extends Fragment {

    static List<Bitmap> result = new ArrayList<>();
	com.banana.projectapp.campagne.CampaignAdapter adapter;
	ListView list;
	String email;
    DownloadCompaniesImagesTask companiesImagesTask;
    private List<CompanyCampaign> campaigns;

    public static CampagneFragment newInstance(String email) {
		CampagneFragment fragment = new CampagneFragment();
		fragment.email = email;
		return fragment;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy(){
        if (companiesImagesTask!= null)
            companiesImagesTask.cancel(true);
        super.onDestroy();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.campagne, container,
                false);
        TextView nome = (TextView) rootView.findViewById(R.id.nome_utente);
        nome.setText(email);
        Log.e("ciao", "email = " + email);
        nome.invalidate();
        list = (ListView) rootView.findViewById(R.id.list_view);

        URL[] urls = new URL[5];
        try {
            urls[0] = new URL("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTLSZwiSx1wV28ZyPF3JN_oGOkZBfdyIs-ixXeYXHQouglWDRXKDw");
            urls[1] = new URL("https://pullquotesandexcerpts.files.wordpress.com/2013/11/silver-apple-logo.png?w=360");
            urls[2] = new URL("http://blog.logomyway.com/wp-content/uploads/2013/05/bmw-logo.jpg");
            urls[3] = new URL("http://upload.wikimedia.org/wikipedia/en/archive/a/a9/20140331161125!Logitech_logo.png");
            urls[4] = new URL("http://www.elmec.com/ElmecCom2014v3/images/tplpage/logo_elmec_IT.png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        DBManager db = new DBManager(getActivity()).open();
        campaigns = db.getCampaigns();
        db.close();
        if (campaigns.size() < 5){
            Toast.makeText(getActivity(),"nessuna campagna in db, le scarico e le inserisco",Toast.LENGTH_SHORT).show();
            if (companiesImagesTask != null)
                return rootView;
            companiesImagesTask = new DownloadCompaniesImagesTask();
            companiesImagesTask.execute(urls);
        } else {
            adapter = new com.banana.projectapp.campagne.CampaignAdapter(getActivity(), campaigns);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(getActivity(), campaigns.get(position).getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),FacebookPhotos.class);
                    startActivity(intent);
                }
            });
        }
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        int fragment_id = 1;
        ((MainFragmentActivity) activity).onSectionAttached(fragment_id);
	}

    private class DownloadCompaniesImagesTask extends AsyncTask<URL, Integer, Long> {
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
            db.insert(new CompanyCampaign(result.get(0),
                    "coca cola", 70));
            db.insert(new CompanyCampaign(result.get(1),
                    "apple", 30));
            db.insert(new CompanyCampaign(result.get(2),
                    "BMW", 50));
            db.insert(new CompanyCampaign(result.get(3),
                    "logitech", 120));
            db.insert(new CompanyCampaign(result.get(4),
                    "elmec", 34));
            final List<CompanyCampaign> companies = db.getCampaigns();

	    	/*final List<CompanyCampaign> companies = new ArrayList<CompanyCampaign>();
	 		companies.add(new CompanyCampaign(result.get(0),
	 				"coca cola", 70));
	 		companies.add(new CompanyCampaign(result.get(1),
	 				"apple", 30));
	 		companies.add(new CompanyCampaign(result.get(2),
	 				"BMW", 50));
	 		companies.add(new CompanyCampaign(result.get(3),
	 				"logitech", 120));
	 		companies.add(new CompanyCampaign(result.get(4),
	 				"elmec", 34));*/

            adapter = new com.banana.projectapp.campagne.CampaignAdapter(getActivity(), companies);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(getActivity(), companies.get(position).getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),FacebookPhotos.class);
                    startActivity(intent);
                }
            });


            db.close();
        }
    }
}
