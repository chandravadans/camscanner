package com.necsv.scanner.esg;

import java.io.IOException;
import java.net.MalformedURLException;

import com.necsv.scanner.esg.adapter.AlbumsGridAdapter;
import com.necsv.scanner.esg.pdf.ConvertPDF;
import com.necsv.scanner.esg.util.GlobalVariable;
import com.necsv.scanner.esg.util.XMLUtil;
import com.necsv.scanner.esg.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AlbumsGridActivity extends Activity {
		
	private Bundle extra;
	private Context mContext;
	private TextView mBackButton;
	private TextView txtTitle;
	private GridView mGridView;
	private ImageView mImageView;
	private Bitmap mBitmap = null;
	private AlbumsGridAdapter adapter = null;
	public static final int CONFIRM_DELETE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.albums_grid_activity);
		mContext = this;
		mBackButton = (TextView) findViewById(R.id.albums_activity_btn_back);
		txtTitle = (TextView) findViewById(R.id.albums_activity_txt_title);
		mGridView = (GridView) findViewById(R.id.albums_activity_grid_view);
		mImageView = (ImageView) findViewById(R.id.albums_activity_image_view_grid_view);
		setGridViewNumColums();
		extra = getIntent().getExtras();
		txtTitle.setText(extra.getString("TITLE"));
		mBackButton.setText(getResources().getString(R.string.txt_albums));
		mBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if (extra.get("ID") == null ) {
			mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nophotos);					
			mGridView.setVisibility(View.GONE);
			mImageView.setVisibility(View.VISIBLE);
			mImageView.setImageBitmap(mBitmap);
		} else {
			GlobalVariable.mAlbumsGrid = XMLUtil.parserAlbumsGridData(this, extra.getString("ID"));					
			adapter = new AlbumsGridAdapter(this, extra.getString("ID"));
			mGridView.setAdapter(adapter);
		}
	}

	private void setGridViewNumColums() {
		GlobalVariable.config = getResources().getConfiguration();
		try {
			if (GlobalVariable.config.orientation == Configuration.ORIENTATION_PORTRAIT) {

				mGridView.setNumColumns(4);
			} else if (GlobalVariable.config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

				mGridView.setNumColumns(6);
			}
		} catch (Exception e) {
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setGridViewNumColums();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		if (adapter != null) {
			adapter = null;
			mGridView = null;
		}
		System.gc();
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
			case CONFIRM_DELETE:
				builder.setMessage("Are you sure you want to delete this picture?")
						.setCancelable(false)
						.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
					            	Handler handler = new Handler();
									handler.post(new Runnable() {
					        			public void run() {
					        				Boolean success = XMLUtil.DeletePicture(mContext, GlobalVariable.positionList, GlobalVariable.positionGrid);
					        				if (!success) {
												Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
											} else { 
												adapter = new AlbumsGridAdapter(mContext, extra.getString("ID"));
												mGridView.setAdapter(adapter);
												Toast.makeText(mContext, "Delete sussceeded", Toast.LENGTH_SHORT).show();
											}
					        			}
					        		});
									finish();
								}
							})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.dismiss();
									}
								});
				break;
			default:
				break;
		}
		return builder.create();
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_pdf, menu);
	    return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_id_create_pdf:
            {
        		final Handler handler = new Handler();
            	handler.post(new Runnable() {
        			public void run() {
        				try {
							Boolean success = ConvertPDF.createPDFs(GlobalVariable.TARGET_PATH + "images/" + String.valueOf(GlobalVariable.positionList + 1));
	        				if (success) {
	        					Toast.makeText(mContext, "Creating PDF is completed. ", Toast.LENGTH_SHORT).show();
	        				}
        				} catch (MalformedURLException e) {
        					Toast.makeText(mContext, "Creating PDF is not completed. ", Toast.LENGTH_SHORT).show();
        				} catch (IOException e) {
        					Toast.makeText(mContext, "Creating PDF is not completed. ", Toast.LENGTH_SHORT).show();
        				}

        			}
        		});
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}
	
}