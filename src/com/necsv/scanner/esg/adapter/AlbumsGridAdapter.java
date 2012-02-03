package com.necsv.scanner.esg.adapter;

import java.util.ArrayList;
import java.util.List;

import com.necsv.scanner.esg.AlbumsGridActivity;
import com.necsv.scanner.esg.ViewImagesActivity;
import com.necsv.scanner.esg.util.Albums;
import com.necsv.scanner.esg.util.FileUtil;
import com.necsv.scanner.esg.util.GlobalVariable;
import com.necsv.scanner.esg.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AlbumsGridAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Albums> mAlbums = null;
	private Bitmap mBitmap = null;
	private BitmapDrawable bitmapDrawable;
	private String filename, folder;
	
	public AlbumsGridAdapter(Context context ,String file)
	{
		mContext  = context;
		folder	  = file;
		mAlbums   = new ArrayList<Albums>();
		mAlbums	  = GlobalVariable.mAlbumsGrid;
		mInflater = LayoutInflater.from(context);
	}
	
	public class ViewHolder
	{
		ImageView imageview;
		LinearLayout layout;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent)
	{
		final ViewHolder holder;
		if (view == null)
		{
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.albums_grid_item, null);
			holder.imageview = (ImageView)view.findViewById(R.id.albums_grid_item_image_view);
			holder.layout = (LinearLayout)view.findViewById(R.id.albums_grid_item_layout_press);
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}
		
		holder.imageview.setTag(position);
		holder.layout.setTag(position);
		
		filename = GlobalVariable.TARGET_PATH + "images/" + folder + "/" + mAlbums.get(position).getImage();
		GlobalVariable.byteBuffer = FileUtil.readFileFromSDCard(mContext, filename);
		mBitmap	= BitmapFactory.decodeByteArray(GlobalVariable.byteBuffer, 0, GlobalVariable.byteBuffer.length);
		bitmapDrawable = new BitmapDrawable(mBitmap);
		holder.imageview.setBackgroundDrawable(bitmapDrawable);
		
		holder.layout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, ViewImagesActivity.class);
				intent.putExtra("COUNT", String.valueOf(position));
				mContext.startActivity(intent);
				GlobalVariable.positionGrid = position;
			}
		});
		
		holder.layout.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				GlobalVariable.positionGrid = position;
				((Activity) mContext).showDialog(AlbumsGridActivity.CONFIRM_DELETE);
				return true;
			}
		});
		
		
		GlobalVariable.byteBuffer = null;
		return view;
	}
	
	@Override
	public int getCount()
	{
		return mAlbums.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
}