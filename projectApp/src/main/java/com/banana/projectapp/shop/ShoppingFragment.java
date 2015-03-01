package com.banana.projectapp.shop;

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
import com.banana.projectapp.R;
import com.banana.projectapp.exception.CouponInvalid;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ShoppingFragment extends Fragment{

	ShoppingAdapter adapter;
	ListView list;
    ClientStub client;
    SynchronizeCouponsTask synchronizeCouponsTask;
    RequestCouponTask requestCouponTask;
    TextView creditsText;
    ShoppingItem requestedCoupon;
    String coupon_json;
    String requested_coupon_json;
    private List<ShoppingItem> coupons;
	
	public static ShoppingFragment newInstance() {
		return new ShoppingFragment();
	}

    public void updateView(){

        DBManager db = new DBManager(getActivity()).open();
        coupons = db.getShoppingItems();
        db.close();

        adapter = new ShoppingAdapter(getActivity(), coupons);
        list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
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
        if (requestCouponTask != null)
            requestCouponTask.cancel(true);
        if (synchronizeCouponsTask != null)
            synchronizeCouponsTask.cancel(true);
        super.onDestroy();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.shopping, container,
				false);
		TextView nome = (TextView) rootView.findViewById(R.id.nome_utente);
		nome.setText(DataHolder.getEmail());
		nome.invalidate();
        creditsText = (TextView) rootView.findViewById(R.id.numero_crediti);
        creditsText.setText(DataHolder.getCredits()+" CR");
        creditsText.invalidate();

        final Button synchronizeCoupons = (Button) rootView.findViewById(R.id.synchronizeCoupons);
        synchronizeCoupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (synchronizeCouponsTask != null){
                    return;
                }

                synchronizeCouponsTask = new SynchronizeCouponsTask();
                synchronizeCouponsTask.execute((Void) null);
            }
        });

		list = (ListView) rootView.findViewById(R.id.list_view);

        DBManager db = new DBManager(getActivity()).open();
        coupons = db.getShoppingItems();
        db.close();

        adapter = new ShoppingAdapter(getActivity(), coupons);
        list.setAdapter(adapter);

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (requestCouponTask != null){
                            return;
                        }

                        requestCouponTask = new RequestCouponTask((int)requestedCoupon.getId());
                        requestCouponTask.execute((Void) null);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                requestedCoupon = coupons.get(position);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        int fragment_id = 2;
        ((MainFragmentActivity) activity).onSectionAttached(fragment_id);
	}

    private class SynchronizeCouponsTask extends AsyncTask<Void, Void, Boolean> {

        ArrayList<ShoppingItem> couponList = new ArrayList<>();

        SynchronizeCouponsTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    coupon_json = client.synchronizeCoupons(ember_token);
                } else {
                    InputStream inputStream = getResources().openRawResource(R.raw.coupons);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    coupon_json = total.toString();
                    JSONObject o = new JSONObject(coupon_json);
                    JSONArray aa = o.getJSONArray("data");
                    int number_of_coupons = aa.length();
                    for (int i=0; i<number_of_coupons;i++){
                        JSONObject obj = aa.getJSONObject(i);
                        ShoppingItem s = new ShoppingItem(
                                obj.getLong("id"),
                                obj.getString("url"),
                                obj.getString("coupon"),
                                obj.getInt("credits"));
                        couponList.add(s);
                    }
                }
            } catch (IOException | EmberTokenInvalid | JSONException e) {
                e.printStackTrace();
            }
            for (ShoppingItem s : couponList) {
                HttpURLConnection connection;
                try {
                    connection = (HttpURLConnection) new URL(s.getUrl()).openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input;
                    input = connection.getInputStream();
                    s.setLogo(BitmapFactory.decodeStream(input));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("aaa", "caricata immagine dal link " + s.getUrl());
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            DBManager db = new DBManager(getActivity()).open();
            db.deleteShoppingItems();
            for (ShoppingItem s: couponList){
                db.insert(s);
            }
            db.close();

            updateView();
            synchronizeCouponsTask = null;
        }

        @Override
        protected void onCancelled() {
            synchronizeCouponsTask = null;
            super.onCancelled();
        }
    }

    private class RequestCouponTask extends AsyncTask<Void, Void, Boolean> {

        int mCoupon;
        long id;
        String code;
        String coupon;
        int credits;
        RequestCouponTask(int coupon) {
            mCoupon = coupon;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    requested_coupon_json = client.requestCoupon(mCoupon, ember_token);
                } else {
                    InputStream inputStream = getResources().openRawResource(R.raw.requested_coupon);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    requested_coupon_json = total.toString();
                    JSONObject o = new JSONObject(requested_coupon_json);
                    JSONObject data = o.getJSONObject("data");
                    id = data.getLong("id");
                    coupon = data.getString("coupon");
                    credits = data.getInt("credits");
                    code = data.getString("code");
                }
            } catch (JSONException | IOException | EmberTokenInvalid | CouponInvalid e1) {
                e1.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            requestCouponTask = null;
            DataHolder.setCredits(DataHolder.getCredits()-credits);
            creditsText.setText(DataHolder.getCredits()+" CR");
            creditsText.invalidate();
            Toast.makeText(getActivity(),
                    "id = "+id+" coupon = "+coupon+" credits = "+credits+" code = "+code
                    ,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            requestCouponTask = null;
            super.onCancelled();
        }
    }
}
