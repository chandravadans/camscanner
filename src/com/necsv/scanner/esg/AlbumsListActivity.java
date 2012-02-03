package com.necsv.scanner.esg;

import java.io.File;

import com.necsv.scanner.esg.adapter.AlbumsListAdapter;
import com.necsv.scanner.esg.util.FileUtil;
import com.necsv.scanner.esg.util.GlobalVariable;
import com.necsv.scanner.esg.util.XMLUtil;
import com.necsv.scanner.esg.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class AlbumsListActivity extends Activity {
	private Context mContext;
	private Bitmap mBitmap = null;
	private BitmapDrawable mBitmapRepeat = null;
	
	private LinearLayout layoutRepeatLine;
	private ListView mListView;

	private AlbumsListAdapter adapter = null;
	private static final int CAPTURE = 0;
	private static final int NAME = 1;
	private static final int CONFIRM_DELETE_ALL = 2;
	public static final int CONFIRM_DELETE = 3;
	private static final int CAMERA_REQUEST = 1888;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums_list_activity); 
        layoutRepeatLine = (LinearLayout)findViewById(R.id.albums_activity_layout_line_repeat);
        mListView = (ListView)findViewById(R.id.albums_activity_list_view);
        mContext = this;
        
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.line_none);
        mBitmapRepeat = new BitmapDrawable(mBitmap);
        mBitmapRepeat.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
       	layoutRepeatLine.setBackgroundDrawable(mBitmapRepeat);
       	
       	adapter = new AlbumsListAdapter(this);
       	mListView.setAdapter(adapter);
       	
       	if (!getIntent().getBooleanExtra("data", false)) {
       		
       		Toast.makeText(mContext, "Data is not found, please capture more picture!", Toast.LENGTH_SHORT).show();
       		showDialog(CAPTURE);
       	}
    }
    
    protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final Handler handler = new Handler();
		switch (id) {
		case CONFIRM_DELETE_ALL:
			builder.setMessage("Are you sure you want to delete all existed data?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
			            	handler.post(new Runnable() {
			        			public void run() {
			        				Boolean success = FileUtil.removeDirectory(new File(GlobalVariable.TARGET_PATH));
									if (!success) {
										Toast.makeText(mContext, "Error in delete data", Toast.LENGTH_SHORT).show();
									} else {
						        		FileUtil.copyFilesToSdCard(mContext);
						        		adapter = new AlbumsListAdapter(mContext);
			    						mListView.setAdapter(adapter);
										Toast.makeText(mContext, " All is deleted successfully", Toast.LENGTH_SHORT).show();
									}
			        			}
			        		});				

						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			break;
		case CONFIRM_DELETE:
			builder.setMessage("Are you sure you want to delete this album?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
			            	handler.post(new Runnable() {
			        			public void run() {
			        				Boolean success = XMLUtil.DeleteAlbum(mContext, GlobalVariable.positionList);
			        				if (!success) {
										Toast.makeText(mContext, "Error in delete this album", Toast.LENGTH_SHORT).show();
									} else { 
			    						adapter = new AlbumsListAdapter(mContext);
			    						mListView.setAdapter(adapter);
										Toast.makeText(mContext, "The album is deleted successfully", Toast.LENGTH_SHORT).show();
									}
			        			}
			        		});
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			break;
		case CAPTURE:
			
			View layoutSetting = inflater.inflate(R.layout.setting_dialog,
					(ViewGroup) findViewById(R.id.layout_setting));

			builder.setTitle("Select source");
			builder.setView(layoutSetting);
			
			final Button mCamera = (Button)layoutSetting.findViewById(R.id.camera);
			mCamera.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
	                startActivityForResult(cameraIntent, CAMERA_REQUEST); 
				}
			});
			
			final Button mScanner = (Button)layoutSetting.findViewById(R.id.scanner);
			mScanner.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

				}
			});

			break;
		case NAME:
			
			View layoutName = inflater.inflate(R.layout.new_album_dialog,
					(ViewGroup) findViewById(R.id.layout_name2));

			builder.setTitle("Create new album");
			builder.setView(layoutName);
			
			final EditText mName = (EditText)layoutName.findViewById(R.id.name2);
			
			final Button mOK = (Button)layoutName.findViewById(R.id.ok2);
			mOK.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					removeDialog(NAME);

	            	handler.post(new Runnable() {
	        			public void run() {
	        				Boolean mSuccess = XMLUtil.CreateNewAlbum(mContext, mName.getText().toString());
	    					if (mSuccess) {
	    						adapter = new AlbumsListAdapter(mContext);
	    						mListView.setAdapter(adapter);
	    						Toast.makeText(mContext, "The album is created successfully", Toast.LENGTH_SHORT).show();
	    					} else {
	    						Toast.makeText(mContext, "The album is created unsuccessfully", Toast.LENGTH_SHORT).show();
	    					}
	        			}
	        		});					
				}
			});

			break;

		default:
			break;
		}
		return builder.create();
	}
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == CAMERA_REQUEST) {  
        	GlobalVariable.mBitmap = (Bitmap)data.getExtras().get("data"); 
        	
        	Intent intent = new Intent(mContext, ProcessingImageActivity.class);
        	intent.putExtra("exist", "none");
        	mContext.startActivity(intent);
        }  
    } 
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_main, menu);
	    return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_id_new_album:
            {
            	showDialog(NAME);
                break;
            }
            case R.id.menu_id_capture_picture:
            {
            	showDialog(CAPTURE);
                break;
            }
            case R.id.menu_id_clear_all_data:
            {
            	showDialog(CONFIRM_DELETE_ALL);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        recycleData();
        System.gc();
    }
    
    private void recycleData()
    {
    	if(mBitmapRepeat != null)
    	{
    		mBitmap.recycle();
    		mBitmap = null;
    		mBitmapRepeat = null;
    	}
    	if(adapter != null)
    	{
    		adapter  = null;
    		mListView = null;
    	}
    }

	@Override
	protected void onResume() {
		super.onResume();
		adapter = new AlbumsListAdapter(mContext);
		mListView.setAdapter(adapter);
	}


}