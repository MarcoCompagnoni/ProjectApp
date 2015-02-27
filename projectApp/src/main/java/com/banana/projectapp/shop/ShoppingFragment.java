package com.banana.projectapp.shop;

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
import com.banana.projectapp.exception.CouponInvalid;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.SocialAccountInvalid;
import com.banana.projectapp.exception.SocialAccountTokenInvalid;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ShoppingFragment extends Fragment{
    static List<Bitmap> result = new ArrayList<>();
	ShoppingAdapter adapter;
	ListView list;
    ClientStub client;
    DownloadShoppingImagesTask shoppingImagesTask;
    SynchronizeCouponsTask synchronizeCouponsTask;
    RequestCouponTask requestCouponTask;

    private List<ShoppingItem> items;
	
	public static ShoppingFragment newInstance() {
		ShoppingFragment fragment = new ShoppingFragment();
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
        if (shoppingImagesTask!= null)
            shoppingImagesTask.cancel(true);
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
		URL[] urls = new URL[4];
 		try {
 			urls[0]=new URL("http://news.dice.com/wp-content/uploads/2013/10/amazon-1.png");
 			urls[1]=new URL("http://www.logodesignlove.com/images/evolution/ebay-logo-02.jpg");
 			urls[2]=new URL("http://fontmeme.com/images/Paypal-Logo.jpg");
 			urls[3]=new URL("http://screenwerk.com/wpn/media/Screen-Shot-2013-02-27-at-1.37.14-PM.png");
 		} catch (MalformedURLException e) {
 			e.printStackTrace();
 		}

        DBManager db = new DBManager(getActivity()).open();
        items = db.getShoppingItems();
        db.close();
        if (items.size() < 4){
            Toast.makeText(getActivity(),"nessun item in db, le scarico e le inserisco",Toast.LENGTH_SHORT).show();
            if (shoppingImagesTask != null)
                return rootView;
            shoppingImagesTask = new DownloadShoppingImagesTask();
            shoppingImagesTask.execute(urls);
        } else {
            adapter = new ShoppingAdapter(getActivity(), items);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(getActivity(), items.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (requestCouponTask != null){
                            return;
                        }

                        //TODO
                        int coupon = 0;

                        requestCouponTask = new RequestCouponTask(coupon);
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
                builder.setMessage("Are you sure?").setPositiveButton("Hell yeah!!", dialogClickListener)
                        .setNegativeButton("Nah, fuck it", dialogClickListener).show();
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

        SynchronizeCouponsTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.synchronizeCoupons(ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid e) {
                e.printStackTrace();
            }

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
        RequestCouponTask(int coupon) {
            mCoupon = coupon;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.requestCoupon(mCoupon, ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid | CouponInvalid e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            requestCouponTask = null;
        }

        @Override
        protected void onCancelled() {
            requestCouponTask = null;
            super.onCancelled();
        }
    }

    private class DownloadShoppingImagesTask extends AsyncTask<URL, Integer, Long> {
        public Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalSize = 0;
            for (int i = 0; i < count && !isCancelled(); i++) {
                URL url = urls[i];
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
                Log.e("aaa", "caricata immagine dal link "+urls[i]);
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
            db.insert(new ShoppingItem(result.get(0),
                    "amazon", 70));
            db.insert(new ShoppingItem(result.get(1),
                    "ebay", 30));
            db.insert(new ShoppingItem(result.get(2),
                    "paypal", 50));
            db.insert(new ShoppingItem(result.get(3),
                    "groupon", 120));
            items = db.getShoppingItems();

	    	/*final List<ShoppingItem> items = new ArrayList<ShoppingItem>();
	 		items.add(new ShoppingItem(result.get(0),
	 				"amazon", 70));
	 		items.add(new ShoppingItem(result.get(1),
	 				"ebay", 30));
	 		items.add(new ShoppingItem(result.get(2),
	 				"paypal", 50));
	 		items.add(new ShoppingItem(result.get(3),
	 				"groupon", 120));
	 		*/

            adapter = new com.banana.projectapp.shop.ShoppingAdapter(getActivity(), items);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(getActivity(), items.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
