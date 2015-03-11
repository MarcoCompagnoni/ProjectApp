package com.banana.projectapp.campagne;

import java.util.List;

import com.banana.projectapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CampaignAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	List<CompanyCampaign> companies;
	
	public CampaignAdapter(Context context, List<CompanyCampaign> companies) {
		mInflater = LayoutInflater.from(context);
		this.companies = companies;
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
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder)view.getTag();
		}
		
		CompanyCampaign campagna = companies.get(position);
		holder.avatar.setImageBitmap(campagna.getLogo());
		holder.name.setText(campagna.getName());
        holder.credits.setText(campagna.getUserGain()+ " CR");
		
		return view;
	}
	
	private class ViewHolder {
		public ImageView avatar;
		public TextView name, credits;
	}
}