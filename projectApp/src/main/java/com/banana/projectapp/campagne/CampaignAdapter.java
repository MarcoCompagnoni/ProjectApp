package com.banana.projectapp.campagne;

import java.util.List;

import com.banana.projectapp.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.banana.projectapp.campagne.CompanyCampaign.*;
import static com.banana.projectapp.campagne.CompanyCampaign.CampaignType.*;

public class CampaignAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	List<CompanyCampaign> companies;
    private Context context;
	
	public CampaignAdapter(Context context, List<CompanyCampaign> companies) {
		mInflater = LayoutInflater.from(context);
		this.companies = companies;
        this.context = context;
	}

	@Override
	public int getCount() {
		return companies.size();
	}

	@Override
	public Object getItem(int position) {
		return companies.get(position);
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
		
		CompanyCampaign campagna = companies.get(position);
		holder.avatar.setImageBitmap(campagna.getLogo());
		holder.name.setText(campagna.getName());
        holder.credits.setText(campagna.getUserGain()+ " €");

        switch (campagna.getType()){
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