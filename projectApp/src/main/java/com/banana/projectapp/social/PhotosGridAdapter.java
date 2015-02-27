package com.banana.projectapp.social;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.banana.projectapp.R;

public class PhotosGridAdapter extends BaseAdapter {
	private Context mContext;
    private final ArrayList<String> strings;
    private final ArrayList<Bitmap> Images;
    public PhotosGridAdapter(Context c, ArrayList<String> strings, ArrayList<Bitmap> Images ) {
        mContext = c;
        this.Images = Images;
        this.strings = strings;
    }
      
	@Override
	public int getCount() {
		return strings.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View grid;
		LayoutInflater inflater = 
				(LayoutInflater) mContext.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null){
			grid = inflater.inflate(R.layout.grid_single, null );
		} else {
			grid = convertView;
		}
		//TextView textView = (TextView) grid.findViewById(R.id.grid_text);
		ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
		//textView.setText(strings.get(position));
		imageView.setImageBitmap(Images.get(position));
		return grid;
	}

}
