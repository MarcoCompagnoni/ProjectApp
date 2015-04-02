package com.banana.projectapp.shop;

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
import com.banana.projectapp.R;
import com.banana.projectapp.exception.CouponTypeInvalid;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.main.MainFragmentActivity;
import com.banana.projectapp.main.ShowCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ShoppingFragment extends Fragment{

    private ClientStub client;

    private ShoppingItem requestedCoupon;

    private List<ShoppingItem> coupons;
    private ListView list;
    private ShoppingAdapter adapter;

    private TextView creditsText;

    private SynchronizeCouponsTask synchronizeCouponsTask;
    private RequestCouponTask requestCouponTask;
    private LoadShoppingTask loadShoppingTask;

	public static ShoppingFragment newInstance() {
		return new ShoppingFragment();
	}

    public void updateView(){

        DBManager db = new DBManager(getActivity()).open();
        coupons.clear();
        coupons.addAll(db.getShoppingItems());
        db.close();

        adapter.notifyDataSetChanged();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        client = new ClientStub();
    }

    @Override
    public void onDestroy(){
        if (requestCouponTask != null)
            requestCouponTask.cancel(true);
        if (synchronizeCouponsTask != null)
            synchronizeCouponsTask.cancel(true);
        if (loadShoppingTask != null)
            loadShoppingTask.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (loadShoppingTask == null) {
            loadShoppingTask = new LoadShoppingTask();
            loadShoppingTask.execute();
        }
        super.onResume();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.shopping, container,
				false);
		TextView nome = (TextView) rootView.findViewById(R.id.nome_utente);
		nome.setText(DataHolder.getUserName());
		nome.invalidate();
        creditsText = (TextView) rootView.findViewById(R.id.numero_crediti);
        creditsText.setText(DataHolder.getCredits()+" €");
        creditsText.invalidate();

		list = (ListView) rootView.findViewById(R.id.list_view);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (synchronizeCouponsTask != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                synchronizeCouponsTask = new SynchronizeCouponsTask();
                synchronizeCouponsTask.execute();
                swipeRefreshLayout.setRefreshing(false);
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

            String coupon_json;
            try {
                if (DataHolder.testing_with_server) {

                    String ember_token = DataHolder.getAuthToken();
                    coupon_json = client.synchronizeCouponTypes(ember_token);

                } else {

                    InputStream inputStream = getResources().openRawResource(R.raw.coupon_types);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    coupon_json = total.toString();
                }
                JSONObject o = new JSONObject(coupon_json);
                JSONArray aa = o.getJSONArray("data");
                int number_of_coupons = aa.length();
                for (int i=0; i<number_of_coupons;i++){
                    JSONObject obj = aa.getJSONObject(i);
                    ShoppingItem s = new ShoppingItem(
                            obj.getLong("id"),
                            obj.getString("url"),
                            obj.getString("shop"),
                            obj.getInt("credits"));
                    couponList.add(s);
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
            db.deleteShoppingItems();

            for (ShoppingItem s : couponList) {
                HttpURLConnection connection;
                try {
                    connection = (HttpURLConnection) new URL(s.getUrl()).openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input;
                    input = connection.getInputStream();
                    s.setLogo(BitmapFactory.decodeStream(input));
                    db.insert(s);
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
        String shop;
        int credits;
        RequestCouponTask(int coupon) {
            mCoupon = coupon;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String requested_coupon_json;
            try {
                if (requestedCoupon.getCredits() > DataHolder.getCredits())
                    return false;

                if (DataHolder.testing_with_server) {
                    String ember_token = DataHolder.getAuthToken();
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
                }

                JSONObject o = new JSONObject(requested_coupon_json);
                JSONObject data = o.getJSONObject("data");
                id = data.getLong("id");
                shop = data.getString("shop");
                credits = data.getInt("credits");
                code = data.getString("code");

            } catch (JSONException | IOException | AuthTokenInvalid | CouponTypeInvalid e1) {
                e1.printStackTrace();
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
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            requestCouponTask = null;
            if (success){
                DataHolder.setCode(code);
                DataHolder.setCredits(DataHolder.getCredits()-credits);
                creditsText.setText(DataHolder.getCredits()+" €");
                creditsText.invalidate();

                Intent intent = new Intent(getActivity(), ShowCode.class);
                intent.putExtra("calling_activity","shopping");
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(),
                        getString(R.string.no_enough_credits)
                        ,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            requestCouponTask = null;
            super.onCancelled();
        }
    }

    private class LoadShoppingTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            DBManager db = new DBManager(getActivity()).open();
            coupons = db.getShoppingItems();
            db.close();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
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
                        builder.setMessage(getString(R.string.are_you_sure))
                                .setPositiveButton(getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
                    }
                });
            }
            loadShoppingTask = null;
            super.onPostExecute(success);
        }
        @Override
        protected void onCancelled() {
            loadShoppingTask = null;
            super.onCancelled();
        }
    }
}
