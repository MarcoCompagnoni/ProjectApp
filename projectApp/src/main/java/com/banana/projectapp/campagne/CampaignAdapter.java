package com.banana.projectapp.campagne;

import java.util.List;

import com.banana.projectapp.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CampaignAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	List<Campaign> campaigns;
    private Context context;
	
	public CampaignAdapter(Context context, List<Campaign> campaigns) {
		mInflater = LayoutInflater.from(context);
		this.campaigns = campaigns;
        this.context = context;
	}

	@Override
	public int getCount() {
		return campaigns.size();
	}

	@Override
	public Object getItem(int position) {
		return campaigns.get(position);
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

			view = mInflater.inflate(R.layout.row_item_campagna, parent, false);
			holder = new ViewHolder();
			holder.avatar = (ImageView)view.findViewById(R.id.avatar);
			holder.name = (TextView)view.findViewById(R.id.name);
			holder.credits = (TextView)view.findViewById(R.id.credits);
            holder.type = (ImageView)view.findViewById(R.id.type);
			view.setTag(holder);

		} else {

			view = convertView;
			holder = (ViewHolder)view.getTag();

		}
		
		Campaign campaign = campaigns.get(position);
		holder.avatar.setImageBitmap(campaign.getLogo());
		holder.name.setText(campaign.getName());
        holder.credits.setText(campaign.getUserGain()+ " €");

        switch (campaign.getType()){
            case GEO:
                holder.type.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.mappin));
                break;
            case PHOTO:
                holder.type.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.cameraicon));
                break;
            case GEOPHOTO:
                holder.type.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.geotagging));
                break;

        }

		return view;
	}
	
	private class ViewHolder {
		public ImageView avatar,type;
		public TextView name, credits;
	}
}