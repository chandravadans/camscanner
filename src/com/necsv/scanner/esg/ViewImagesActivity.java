package com.necsv.scanner.esg;

import com.necsv.scanner.esg.util.FileUtil;
import com.necsv.scanner.esg.util.GlobalVariable;
import com.necsv.scanner.esg.view.ImageZoomView;
import com.necsv.scanner.esg.view.TouchZoomListener;
import com.necsv.scanner.esg.view.ZoomControl;
import com.necsv.scanner.esg.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewImagesActivity extends Activity {
	
	private Bundle extra;	
    private TextView txtTitle;
    private TextView txtTitleImage;
    private TextView txtNumberPage;
    private TextView btnBack;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private ProgressBar mProgressBar;
    private RelativeLayout layoutNavigation;
    private RelativeLayout layoutNavigationTop;
    private RelativeLayout layoutNavigationBottom;
    
    private LinearLayout layoutZoomView;
    private ImageZoomView mZoomView;
    private ZoomControl mZoomControl;
    private TouchZoomListener mZoomListener;
    private Context mContext;
    
    private MainCountDown countDown;
    private ParserTask parserTask;
    private Animation animRightToLeft;
    private Animation animLeftToRight;
    private Animation animRightToLeft1;
    private Animation animLeftToRight1;
    private Animation animShowNavbar;
    
    private String strMaxSize;
    public static String filename;
    
    private int count = 0;    
    private boolean isLeftorRight = true;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image_activity);
        GlobalVariable.config 	= getResources().getConfiguration();
        mContext 				= this;
        layoutZoomView 			= (LinearLayout)findViewById(R.id.zoomview);
        layoutNavigation		= (RelativeLayout)findViewById(R.id.navigation);
        layoutNavigationTop 	= (RelativeLayout)findViewById(R.id.navigation_top);
        layoutNavigationBottom 	= (RelativeLayout)findViewById(R.id.navigation_bottom);
        txtTitle				= (TextView)findViewById(R.id.view_image_txt_title);
        txtTitleImage			= (TextView)findViewById(R.id.view_image_txt_title_image);
        txtNumberPage			= (TextView)findViewById(R.id.view_image_txt_number_page);
        btnBack					= (TextView)findViewById(R.id.view_image_btn_back_top);
        btnLeft					= (ImageButton)findViewById(R.id.view_image_btn_back);
        btnRight				= (ImageButton)findViewById(R.id.view_image_btn_next);
        mProgressBar			= (ProgressBar)findViewById(R.id.view_image_progress_bar);
        animRightToLeft  		= AnimationUtils.loadAnimation(mContext, R.anim.popup_right_to_left);
        animLeftToRight  		= AnimationUtils.loadAnimation(mContext, R.anim.popup_left_to_right);
        animRightToLeft1 		= AnimationUtils.loadAnimation(mContext, R.anim.popup_right_to_left1);
        animLeftToRight1 		= AnimationUtils.loadAnimation(mContext, R.anim.popup_left_to_right1);
        animShowNavbar	 		= AnimationUtils.loadAnimation(mContext, R.anim.popup_show_navbar);
        
        strMaxSize = " / " + String.valueOf(GlobalVariable.mAlbumsGrid.size());
        extra = getIntent().getExtras();
        count = Integer.valueOf(extra.getString("COUNT"));        
        setValue(count);
        txtTitle.setText(GlobalVariable.ALBUMSTITLE);

        btnBack.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
        
        
        btnLeft.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				goLeft();
			}
		});
        
        btnRight.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				goRight();
			}
		});
        
        layoutNavigationTop.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v){}
		});
        
        layoutNavigationBottom.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v){}
		});
    }
        
    @Override
    protected void onStart()
    {
        super.onStart();
        parserTask = new ParserTask(count);
        parserTask.execute();
    }
    
    private class ParserTask extends AsyncTask<String, Integer, Long>
    {
    	private int mCount;
    	
    	public ParserTask(int count)
    	{
    		mCount = count;
    	}
    	
    	@Override
    	protected void onPreExecute()
    	{
    		layoutZoomView.removeAllViewsInLayout();
    	}
    	
		@Override
		protected Long doInBackground(String... params)
		{
			GlobalVariable.clearBitmap();
			filename = GlobalVariable.TARGET_PATH + "images/" + GlobalVariable.ALBUMSID + "/" + GlobalVariable.mAlbumsGrid.get(mCount).getImage();
			GlobalVariable.byteBuffer 	= FileUtil.readFileFromSDCard(mContext, filename);
			GlobalVariable.mBitmap 		= BitmapFactory.decodeByteArray(GlobalVariable.byteBuffer, 0, GlobalVariable.byteBuffer.length);
			GlobalVariable.byteBuffer 	= null;
			System.gc();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Long arg0)
		{
			setValue(count);
			setImageView();
	        layoutZoomView.addView(mZoomView);
	        mProgressBar.setVisibility(View.GONE);
	        if(isLeftorRight)
	        {
	        	layoutZoomView.startAnimation(animRightToLeft);
	        }
	        else
	        {
	        	layoutZoomView.startAnimation(animLeftToRight);
	        }
		}
    }    
    
    public void goLeft()
    {
    	setNullAsyncTask();  	
    	count--;
		if(count < 0)
		{
			count = GlobalVariable.mAlbumsGrid.size()-1;
		}
		isLeftorRight = false;
		mProgressBar.setVisibility(View.VISIBLE);
		layoutZoomView.startAnimation(animLeftToRight1);
		countDown = new MainCountDown(180, 180);
    	countDown.start();
    }
    
    public void goRight()
    {
    	setNullAsyncTask();    	
    	count++;
		if(count == GlobalVariable.mAlbumsGrid.size())
		{
			count = 0;
		}
		isLeftorRight = true; 
		mProgressBar.setVisibility(View.VISIBLE);
		layoutZoomView.startAnimation(animRightToLeft1);
    	countDown = new MainCountDown(180, 180);
    	countDown.start();    	
    }
    
    public class MainCountDown extends CountDownTimer
    {

		public MainCountDown(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
			GlobalVariable.clearBitmap();
		    parserTask = new ParserTask(count);
		    parserTask.execute();
		}

		@Override
		public void onTick(long millisUntilFinished) {}
    }
    
    private void setImageView()
    {
    	mZoomControl 	= new ZoomControl();
        mZoomListener 	= new TouchZoomListener(mContext);
        mZoomView 		= new ImageZoomView(mContext, null);
        mZoomControl.setContext(mContext);
        mZoomListener.setZoomControl(mZoomControl);
        mZoomView.setZoomState(mZoomControl.getZoomState());
        mZoomView.setImage();
        mZoomView.setOnTouchListener(mZoomListener);
        mZoomControl.setAspectQuotient(mZoomView.getAspectQuotient());
        resetZoomState();
    }
    
    private void setValue(int count)
    {
    	txtTitleImage.setText(GlobalVariable.mAlbumsGrid.get(count).getTitle());
        txtNumberPage.setText(String.valueOf(count + 1) + strMaxSize);
    }
    
    private void setNullAsyncTask()
    {
    	if(parserTask != null)
		{
			parserTask.cancel(true);
			parserTask = null;
		}
    }
    
    public void visibilityLayoutNav(boolean check)
    {
    	if(check)
    	{
    		layoutNavigation.setVisibility(View.VISIBLE);
    		layoutNavigation.startAnimation(animShowNavbar);
    	}
    	else
    	{
    		layoutNavigation.setVisibility(View.GONE);
    	}
    }
    
    public void resetLayout()
    {
    	layoutZoomView.removeAllViews();
    	setImageView();
    	layoutZoomView.addView(mZoomView);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        setNullAsyncTask();
        GlobalVariable.clearBitmap();
        GlobalVariable.byteBuffer = null;
        System.gc();
    }

    private void resetZoomState()
    {
    	mZoomControl.getZoomState().setPanX(0.5f);
        if(GlobalVariable.config.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mZoomControl.getZoomState().setPanY(0.5f);
            mZoomControl.getZoomState().setZoom(1f);
        } 
        if(GlobalVariable.config.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
        	mZoomControl.getZoomState().setPanY(0.22f);
        	mZoomControl.getZoomState().setZoom(2.6f); 
        }
        mZoomControl.getZoomState().notifyObservers();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null)
		{
			setNullAsyncTask();
			mProgressBar.setVisibility(View.VISIBLE);
			count = Integer.valueOf(data.getStringExtra("COUNT"));
		}
	}
}