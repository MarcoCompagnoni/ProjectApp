package com.banana.projectapp.profile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;
import com.banana.projectapp.R.id;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.db.DBManager;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.exception.SocialTypeInvalid;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SocialAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	List<Social> socials;
    ClientStub client;
    Context context;
    DeleteSocialTask deleteSocialTask;

	
	public SocialAdapter(Context context, List<Social> socials) {
		mInflater = LayoutInflater.from(context);
		this.socials = socials;
        try {
            client = new ClientStub();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

	@Override
	public int getCount() {
		return socials.size();
	}

	@Override
	public Object getItem(int position) {
		return socials.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(convertView == null) {
			view = mInflater.inflate(R.layout.social_row, parent, false);
			holder = new ViewHolder();
			holder.avatar = (ImageView)view.findViewById(R.id.avatar);
			holder.name = (TextView)view.findViewById(R.id.name);
            holder.button = (Button)view.findViewById(id.button1);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder)view.getTag();
		}
		
		final Social social = socials.get(position);
		holder.avatar.setImageBitmap(social.getLogo());
		holder.name.setText(social.getName());
		holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"rimuovo "+social.getName(),Toast.LENGTH_SHORT).show();
                if (deleteSocialTask!=null) {
                    return;
                }
                deleteSocialTask = new DeleteSocialTask((int)social.getId());
                deleteSocialTask.execute((Void)null);

            }
        });

		return view;
	}
	
	private class ViewHolder {
		public ImageView avatar;
		public TextView name;
        public Button button;
	}

    private class DeleteSocialTask extends AsyncTask<Void, Void, Boolean> {

        private final int mSocialType;

        DeleteSocialTask(int socialType) {
            mSocialType = socialType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.deleteSocial(mSocialType, ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid | SocialTypeInvalid e) {
                e.printStackTrace();
            } catch (NoConnectionException e) {
                Toast.makeText(context,"No connection",Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                DBManager db = new DBManager(context);
                db.open();
                db.remove(mSocialType, "SOCIALS");
                socials = db.getSocials();
                db.close();
                notifyDataSetChanged();
                deleteSocialTask = null;
            }
        }

        @Override
        protected void onCancelled() {

            deleteSocialTask = null;
            super.onCancelled();
        }
    }

}