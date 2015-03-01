package com.banana.projectapp.campagne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.db.DBManager;
import com.banana.projectapp.social.FacebookPhotos;
import com.banana.projectapp.R;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.Activity;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CampaignFragment extends Fragment {

	CampaignAdapter adapter;
	ListView list;
    ClientStub client;
    SynchronizeCampaignTask synchronizeCampaignTask;
    TextView credits;

    String campaign_json;
    private List<CompanyCampaign> campaigns;

    public static CampaignFragment newInstance() {
		return new CampaignFragment();
	}

    public void updateView(){

        DBManager db = new DBManager(getActivity()).open();
        campaigns = db.getCampaigns();
        db.close();

        adapter = new CampaignAdapter(getActivity(), campaigns);
        list.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        credits.setText(DataHolder.getCredits()+" CR");
        credits.invalidate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateView();
        super.onActivityResult(requestCode,resultCode,data);
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
        if (synchronizeCampaignTask != null)
            synchronizeCampaignTask.cancel(true);

        super.onDestroy();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.campagne, container,
                false);
        TextView nome = (TextView) rootView.findViewById(R.id.nome_utente);
        nome.setText(DataHolder.getEmail());
        nome.invalidate();

        credits = (TextView) rootView.findViewById(R.id.numero_crediti);
        credits.setText(DataHolder.getCredits()+" CR");
        credits.invalidate();

        final Button synchronizeCampaign = (Button) rootView.findViewById(R.id.synchronizeCampaign);
        synchronizeCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (synchronizeCampaignTask != null){
                    return;
                }

                synchronizeCampaignTask = new SynchronizeCampaignTask();
                synchronizeCampaignTask.execute((Void) null);
            }
        });

        list = (ListView) rootView.findViewById(R.id.list_view);

        DBManager db = new DBManager(getActivity()).open();
        campaigns = db.getCampaigns();
        db.close();

        adapter = new com.banana.projectapp.campagne.CampaignAdapter(getActivity(), campaigns);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getActivity(), campaigns.get(position).getName(), Toast.LENGTH_SHORT).show();
                DataHolder.setCampaign(campaigns.get(position));
                Intent intent = new Intent(getActivity(),FacebookPhotos.class);
                startActivityForResult(intent,1);
            }
        });

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        int fragment_id = 1;
        ((MainFragmentActivity) activity).onSectionAttached(fragment_id);
	}

    private class SynchronizeCampaignTask extends AsyncTask<Void, Void, Boolean> {

        ArrayList<CompanyCampaign> campaignList = new ArrayList<>();

        SynchronizeCampaignTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    campaign_json = client.synchronizeCampaigns(ember_token);
                } else {
                    InputStream inputStream = getResources().openRawResource(R.raw.campaigns);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    campaign_json = total.toString();
                    JSONObject o = new JSONObject(campaign_json);
                    JSONArray aa = o.getJSONArray("data");
                    int number_of_campaigns = aa.length();
                    for (int i=0; i<number_of_campaigns;i++){
                        JSONObject obj = aa.getJSONObject(i);
                        CompanyCampaign c = new CompanyCampaign(
                                obj.getLong("id"),
                                obj.getString("url"),
                                obj.getString("customer"),
                                obj.getInt("credits"));
                        Log.e("","url="+c.getUrl());
                        campaignList.add(c);
                    }
                }
            } catch (IOException | EmberTokenInvalid | JSONException e) {
                e.printStackTrace();
            }

            for (CompanyCampaign c : campaignList) {
                HttpURLConnection connection;
                try {
                    connection = (HttpURLConnection) new URL(c.getUrl()).openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input;
                    input = connection.getInputStream();
                    c.setLogo(BitmapFactory.decodeStream(input));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("aaa", "caricata immagine dal link " + c.getUrl());
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            DBManager db = new DBManager(getActivity()).open();
            db.deleteCampaigns();
            for (CompanyCampaign c: campaignList){
                db.insert(c);
            }
            db.close();

            updateView();
            synchronizeCampaignTask = null;
        }

        @Override
        protected void onCancelled() {
            synchronizeCampaignTask = null;
            super.onCancelled();
        }
    }
}
