package com.necsv.scanner.esg.adapter;

import java.util.ArrayList;
import java.util.List;

import com.necsv.scanner.esg.AlbumsGridActivity;
import com.necsv.scanner.esg.AlbumsListActivity;
import com.necsv.scanner.esg.util.Albums;
import com.necsv.scanner.esg.util.GlobalVariable;
import com.necsv.scanner.esg.util.XMLUtil;
import com.necsv.scanner.esg.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumsListAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater mInflater;
	public static List<Albums> mAlbums = null;
	
	public AlbumsListAdapter(Context context)
	{
		mContext  = context;
		mAlbums   = new ArrayList<Albums>();
		mAlbums	  = XMLUtil.parserAlbumsListData(context);
		mInflater = LayoutInflater.from(context);
	}
	
	public class ViewHolder
	{
		TextView textView;
		ImageView imageView;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent)
	{
		final ViewHolder holder;
		if (view == null)
		{
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.albums_list_item, null);
			holder.textView = (TextView)view.findViewById(R.id.albums_list_item_text_view);
			holder.imageView = (ImageView)view.findViewById(R.id.albums_list_item_image_view);
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}
		
		holder.imageView.setTag(position);
		holder.textView.setTag(position);
		
//		filename 	= GlobalVariable.TARGET_PATH + "images/albumslist/" + mAlbums.get(position).getImage();
//		GlobalVariable.byteBuffer = FileUtil.readFileFromSDCard(mContext, filename);
//		mBitmap		= BitmapFactory.decodeByteArray(GlobalVariable.byteBuffer, 0, GlobalVariable.byteBuffer.length);
//		holder.imageView.setImageBitmap(mBitmap);
		holder.imageView.setBackgroundResource(R.drawable.album);
		holder.textView.setText(mAlbums.get(position).getTitle());
		holder.textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				GlobalVariable.ALBUMSID 	= mAlbums.get(position).getID();
				GlobalVariable.ALBUMSTITLE 	= mAlbums.get(position).getTitle();
				Intent intent = new Intent(mContext, AlbumsGridActivity.class);
				intent.putExtra("ID", GlobalVariable.ALBUMSID);
				intent.putExtra("TITLE", GlobalVariable.ALBUMSTITLE);
				mContext.startActivity(intent);
				GlobalVariable.positionList = position;
			}
		});
		
		holder.textView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				GlobalVariable.positionList = position;
				((Activity) mContext).showDialog(AlbumsListActivity.CONFIRM_DELETE);
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