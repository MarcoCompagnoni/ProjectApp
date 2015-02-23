package com.banana.projectapp.shop;

import java.util.List;

import com.banana.projectapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShoppingAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	List<ShoppingItem> items;
	
	public ShoppingAdapter(Context context, List<ShoppingItem> items) {
		mInflater = LayoutInflater.from(context);
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
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
			view = mInflater.inflate(R.layout.row_item_shopping, parent, false);
			holder = new ViewHolder();
			holder.avatar = (ImageView)view.findViewById(R.id.avatar);
			holder.name = (TextView)view.findViewById(R.id.name);
			holder.credits = (TextView)view.findViewById(R.id.credits);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder)view.getTag();
		}
		
		ShoppingItem item = items.get(position);
		holder.avatar.setImageBitmap(item.getLogo());
		holder.name.setText(item.getName());
		holder.credits.setText(item.getCredits()+" CR");
		
		return view;
	}
	
	private class ViewHolder {
		public ImageView avatar;
		public TextView name, credits;
	}
}