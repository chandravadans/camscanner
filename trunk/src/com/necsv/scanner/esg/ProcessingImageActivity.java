package com.necsv.scanner.esg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.necsv.scanner.esg.util.CropOption;
import com.necsv.scanner.esg.util.CropOptionAdapter;
import com.necsv.scanner.esg.util.GlobalVariable;
import com.necsv.scanner.esg.util.XMLUtil;
import com.necsv.scanner.esg.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ProcessingImageActivity extends Activity implements OnTouchListener {
	
   private Context mContext;
   // These matrices will be used to move and zoom image
   private Matrix matrix = new Matrix();
   private Matrix savedMatrix = new Matrix();
   private ImageView imageview;

   // We can be in one of these 3 states
   private static final int NONE = 0;
   private static final int DRAG = 1;
   private static final int ZOOM = 2;
   private int mode = NONE;

   // Remember some things for zooming
   private PointF start = new PointF();
   private PointF mid = new PointF();
   private float oldDist = 1f;
   private Bitmap mBitmap;
   private int bmpWidth = 0;
   private int bmpHeight = 0;
   private static final int NAME_ALBUM = 0;
   private static final int NAME_PIC = 1;
   private static final int SAVE = 2;
   private Uri mImageCaptureUri;
   private static final int CROP_FROM_CAMERA = 0;
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.multitouch);
      mContext = this;
      imageview = (ImageView) findViewById(R.id.touchimage);
      
      BitmapFactory.Options bounds = new BitmapFactory.Options();
      bounds.inJustDecodeBounds = true;

      mBitmap = GlobalVariable.mBitmap;
      imageview.setImageBitmap(mBitmap);  
      imageview.setOnTouchListener(this);

   }

   @Override
   public boolean onTouch(View v, MotionEvent event) {
      ImageView view = (ImageView) v;

      // Handle touch events here...
      switch (event.getAction() & MotionEvent.ACTION_MASK) {
      
      case MotionEvent.ACTION_DOWN:
         savedMatrix.set(matrix);
         start.set(event.getX(), event.getY());
         mode = DRAG;
         break;
      case MotionEvent.ACTION_POINTER_DOWN:
         oldDist = spacing(event);
         if (oldDist > 10f) {
            savedMatrix.set(matrix);
            midPoint(mid, event);
            mode = ZOOM;
         }
         break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
         mode = NONE;
         break;
      case MotionEvent.ACTION_MOVE:
         if (mode == DRAG) {
            // ...
            matrix.set(savedMatrix);
            matrix.postTranslate(event.getX() - start.x,
                  event.getY() - start.y);
         }
         else if (mode == ZOOM) {
            float newDist = spacing(event);
            if (newDist > 10f) {
               matrix.set(savedMatrix);
               float scale = newDist / oldDist;
               matrix.postScale(scale, scale, mid.x, mid.y);
            }
         }
         break;
      }

      view.setImageMatrix(matrix);
      return true; // indicate event was handled
   }

   /** Determine the space between the first two fingers */
   private float spacing(MotionEvent event) {
      float x = event.getX(0) - event.getX(1);
      float y = event.getY(0) - event.getY(1);
      return FloatMath.sqrt(x * x + y * y);
   }

   /** Calculate the mid point of the first two fingers */
   private void midPoint(PointF point, MotionEvent event) {
      float x = event.getX(0) + event.getX(1);
      float y = event.getY(0) + event.getY(1);
      point.set(x / 2, y / 2);
   }
   
   public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_processingimage, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	public Bitmap loadBitmap(String url)
	{
	    Bitmap bm = null;
	    InputStream is = null;
	    BufferedInputStream bis = null;
	    try 
	    {
	        URLConnection conn = new URL(url).openConnection();
	        conn.connect();
	        is = conn.getInputStream();
	        bis = new BufferedInputStream(is, 8192);
	        bm = BitmapFactory.decodeStream(bis);
	    }
	    catch (Exception e) 
	    {
	        e.printStackTrace();
	    }
	    finally {
	        if (bis != null) 
	        {
	            try 
	            {
	                bis.close();
	            }
	            catch (IOException e) 
	            {
	                e.printStackTrace();
	            }
	        }
	        if (is != null) 
	        {
	            try 
	            {
	                is.close();
	            }
	            catch (IOException e) 
	            {
	                e.printStackTrace();
	            }
	        }
	    }
	    return bm;
	}

	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Handler handler = new Handler();
		bmpWidth = mBitmap.getWidth();
		bmpHeight = mBitmap.getHeight();
	    String filename = null;
	    filename = getIntent().getStringExtra("filename");
		switch (item.getItemId()) {
		case R.id.rotate:
			Matrix matrix1 = new Matrix();
			matrix1.postRotate(90,bmpWidth/2, bmpHeight/2);
			mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth, bmpHeight, matrix1, true);		
			imageview.setImageBitmap(mBitmap);
			
			return true;
			
		case R.id.enhanced:
			mBitmap = doBrightness(mBitmap, 30);
			mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
			imageview.setImageBitmap(mBitmap);
					
			return true;	
			
		case R.id.crop:

		    OutputStream outStream = null;
		    if (filename == null) {
		    	filename = GlobalVariable.TARGET_PATH + "images/temp.jpg";
		    }
		    File file = new File(filename);
		    try {
		    	outStream = new FileOutputStream(file);
		    	mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
		    	outStream.flush();
		    	outStream.close();
		    }
		    catch(Exception e) {
		    	Toast.makeText(mContext, "Can not create image", Toast.LENGTH_SHORT).show();
		    }
		    
			mImageCaptureUri = Uri.fromFile(new File(filename));
        	handler.post(new Runnable() {
    			public void run() {
    				doCrop();
    			}
    		});
			mBitmap = loadBitmap(mImageCaptureUri.getPath());
			return true;
			
		case R.id.save:
			final String position = (String)getIntent().getExtras().get("filename"); 
			if (position != null) {
            	handler.post(new Runnable() {
        			public void run() {
						try {
							FileOutputStream out = new FileOutputStream(position);
	 						mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
	 					    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
	 					    Toast.makeText(mContext, "The picture is created successfully", Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Toast.makeText(mContext, "The picture is created unsuccessfully", Toast.LENGTH_SHORT).show();
	 					}
						removeDialog(SAVE);
        			}
        		});
			} else {
				File f = new File(GlobalVariable.TARGET_PATH + "xml/1.xml");
				if (f.exists()) {
					showDialog(SAVE);
				} else {
					showDialog(NAME_PIC);
				}
			}
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        
        int size = list.size();

        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop application", Toast.LENGTH_SHORT).show();
            return;
        } else {
        	intent.setData(mImageCaptureUri);
            
            intent.putExtra("outputX", 210);
            intent.putExtra("outputY", 297);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            
        	if (size == 1) {
        		Intent i = new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop Application");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            public void onClick( DialogInterface dialog, int item ) {
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (mImageCaptureUri != null ) {
		                    getContentResolver().delete(mImageCaptureUri, null, null );
		                    mImageCaptureUri = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        }
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	   
	    switch (requestCode) {  	
	    
		    case CROP_FROM_CAMERA:	    	
		        Bundle extras = data.getExtras();
		        if (extras != null) {	        	
		        	mBitmap = extras.getParcelable("data");
		        imageview.setImageBitmap(mBitmap);
		        }
	    }
	}	
	
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		switch (id) {
		case NAME_ALBUM:

			View layoutNamepic = inflater.inflate(R.layout.name_album_dialog,
					(ViewGroup) findViewById(R.id.layout_name));

			builder.setTitle("Save Picture");
			builder.setView(layoutNamepic);
			
			final EditText mPictureName = (EditText)layoutNamepic.findViewById(R.id.picturename);
			final EditText mAlbumName = (EditText)layoutNamepic.findViewById(R.id.albumname);
			final Button mOKalbum = (Button)layoutNamepic.findViewById(R.id.ok);
			mOKalbum.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
	            	Handler handler = new Handler();
					handler.post(new Runnable() {
	        			public void run() {
	        				String filename = XMLUtil.CreateNewPicture(mContext, mPictureName.getText().toString(), mAlbumName.getText().toString());
		 		        	finish();
		 		        	removeDialog(NAME_ALBUM);
	        				if (filename == null) {
								Toast.makeText(mContext, "Save image error. Please do again", Toast.LENGTH_SHORT);
							} else if (filename == "") {
									Toast.makeText(mContext, "Album don't exist. Please do again", Toast.LENGTH_SHORT);
							} else { 
								removeDialog(SAVE);
		        				try {
			 					       FileOutputStream out = new FileOutputStream(filename);
			 					       mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			 					} catch (Exception e) {
			 					       e.printStackTrace();
			 					}
			 		        	Intent intent = new Intent(mContext, AlbumsListActivity.class);
			 		        	mContext.startActivity(intent);
								Toast.makeText(mContext, "New piture is added", Toast.LENGTH_SHORT);

							}
	        			}
	        		});
				}
			});
			
			break;
		case NAME_PIC:
			
			View layoutNamepic1 = inflater.inflate(R.layout.name_pic_dialog,
					(ViewGroup) findViewById(R.id.layout_name));

			builder.setTitle("Save Picture");
			builder.setView(layoutNamepic1);
			
			final EditText mPictureName1 = (EditText)layoutNamepic1.findViewById(R.id.picturename);
			final EditText mAlbumName1 = (EditText)layoutNamepic1.findViewById(R.id.albumname);
			final Button mOKpic = (Button)layoutNamepic1.findViewById(R.id.ok);
			mOKpic.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
	            	Handler handler = new Handler();
					handler.post(new Runnable() {
	        			public void run() {
	        				String filename = XMLUtil.CreateNewAlbumListData(mContext, mPictureName1.getText().toString(), mAlbumName1.getText().toString());
		 		        	finish();
		 		        	removeDialog(NAME_PIC);
	        				if (filename == null) {
								Toast.makeText(mContext, "Save image error. Please do again", Toast.LENGTH_SHORT);
							} else { 
								removeDialog(SAVE);
		        				try {
			 					       FileOutputStream out = new FileOutputStream(filename);
			 					       mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			 					} catch (Exception e) {
			 					       e.printStackTrace();
			 					}
			 		        	Intent intent = new Intent(mContext, AlbumsListActivity.class);
			 		        	intent.putExtra("data", true);
			 		        	mContext.startActivity(intent);
								Toast.makeText(mContext, "New piture is added", Toast.LENGTH_SHORT);

							}
	        			}
	        		});
				}
			});

			break;
		case SAVE:
			
			View layoutSave = inflater.inflate(R.layout.save_dialog,
					(ViewGroup) findViewById(R.id.layout_save));

			builder.setTitle("Choose type");
			builder.setView(layoutSave);
			
			final Button newPic = (Button)layoutSave.findViewById(R.id.newpic);
			newPic.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showDialog(NAME_PIC);
					removeDialog(SAVE);
				}
			});
			
			final Button saveAlbum = (Button)layoutSave.findViewById(R.id.choose);
			saveAlbum.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showDialog(NAME_ALBUM);
					removeDialog(SAVE);
				}
			});
			removeDialog(SAVE);
			break;
		default:
			break;
		}
		return builder.create();
	}
	
	public static Bitmap doBrightness(Bitmap src, int value) {

		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// colour information
		int A, R, G, B;
	    int pixel;
	 
	    // scan through all pixels
	    for(int x = 0; x < width; ++x) {
	        for(int y = 0; y < height; ++y) {
	            // get pixel colour
	            pixel = src.getPixel(x, y);
	            A = Color.alpha(pixel);
		        R = Color.red(pixel);
		        G = Color.green(pixel);
	            B = Color.blue(pixel);
		 
		        // increase/decrease each channel
	            R += value;
	            if (R > 255) {
	            	R = 255; 
	            } else if (R < 0) { 
	            	R = 0; 
	            }
		 
		        G += value;
	            if (G > 255) { 
	            	G = 255; 
	            } else if (G < 0) { 
	            	G = 0; 
	            }
		 
	            B += value;
	            if (B > 255) { 
	            	B = 255; 
	            } else if (B < 0) { 
	            	B = 0; 
	            }
	 
	            // apply new pixel colour to output bitmap
	            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
		    }
		}
	    // return final image
	    return bmOut;
	}

	@Override
	protected void onResume() {
		mBitmap = GlobalVariable.mBitmap;
		super.onResume();
	}
	
}



