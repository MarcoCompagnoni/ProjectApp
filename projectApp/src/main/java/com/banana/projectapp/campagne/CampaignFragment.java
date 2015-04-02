package com.banana.projectapp.campagne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.db.DBManager;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.main.ShowCampaign;
import com.banana.projectapp.R;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CampaignFragment extends Fragment {

    private ClientStub client;

    private List<Campaign> campaigns;
    private ListView list;
	private CampaignAdapter adapter;

    private TextView credits;

    private SynchronizeCampaignTask synchronizeCampaignTask;
    private LoadCampaignTask loadCampaignTask;

    public static CampaignFragment newInstance() {
		return new CampaignFragment();
	}

    public void updateView(){

        DBManager db = new DBManager(getActivity()).open();
        campaigns.clear();
        campaigns.addAll(db.getCampaigns());
        db.close();
        adapter.notifyDataSetChanged();
        credits.setText(DataHolder.getCredits()+" €");
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
        client = new ClientStub();

    }

    @Override
    public void onDestroy(){
        if (synchronizeCampaignTask != null)
            synchronizeCampaignTask.cancel(true);
        if (loadCampaignTask != null)
            loadCampaignTask.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (loadCampaignTask == null) {
            loadCampaignTask = new LoadCampaignTask();
            loadCampaignTask.execute();
        }
        super.onResume();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.campagne, container,
                false);

        TextView nome = (TextView) rootView.findViewById(R.id.nome_utente);
        nome.setText(DataHolder.getUserName());
        nome.invalidate();
        credits = (TextView) rootView.findViewById(R.id.numero_crediti);
        credits.setText(DataHolder.getCredits()+" €");
        credits.invalidate();

        list = (ListView) rootView.findViewById(R.id.list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),campaigns.get(position).getType().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (synchronizeCampaignTask != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                synchronizeCampaignTask = new SynchronizeCampaignTask();
                synchronizeCampaignTask.execute();
                swipeRefreshLayout.setRefreshing(false);
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

        ArrayList<Campaign> campaignList = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                String campaign_json;
                if (DataHolder.testing_with_server) {

                    String ember_token = DataHolder.getAuthToken();
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
                }


                JSONObject o = new JSONObject(campaign_json);
                JSONArray aa = o.getJSONArray("data");
                int number_of_campaigns = aa.length();
                for (int i=0; i<number_of_campaigns;i++){

                    JSONObject obj = aa.getJSONObject(i);
                    Campaign c = new Campaign(
                            obj.getLong("id"),
                            obj.getString("url"),
                            obj.getString("customer"),
                            (float)obj.getDouble("userGain"),
                            Campaign.CampaignType.valueOf(obj.getString("type")),
                            obj.getDouble("latitude"),
                            obj.getDouble("longitude"));

                    campaignList.add(c);
                }

            } catch (IOException | AuthTokenInvalid | JSONException e) {
                e.printStackTrace();
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

            DBManager db = new DBManager(getActivity()).open();
            db.deleteCampaigns();

            // download logo per ogni campagna
            for (Campaign c : campaignList) {
                HttpURLConnection connection;
                try {
                    connection = (HttpURLConnection) new URL(c.getUrl()).openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input;
                    input = connection.getInputStream();
                    c.setLogo(BitmapFactory.decodeStream(input));
                    db.insert(c);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateView();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            db.close();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            synchronizeCampaignTask = null;
        }

        @Override
        protected void onCancelled() {
            synchronizeCampaignTask = null;
            super.onCancelled();
        }
    }

    private class LoadCampaignTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            DBManager db = new DBManager(getActivity()).open();
            campaigns = db.getCampaigns();
            db.close();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                adapter = new CampaignAdapter(getActivity(), campaigns);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        DataHolder.setCampaign(campaigns.get(position));
                        Intent intent = new Intent(getActivity(), ShowCampaign.class);
                        startActivityForResult(intent, 1);
                    }
                });
            }

            loadCampaignTask = null;
            super.onPostExecute(success);
        }
        @Override
        protected void onCancelled() {
            loadCampaignTask = null;
            super.onCancelled();
        }
    }
}
