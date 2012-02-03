package com.necsv.scanner.esg.view;

import com.necsv.scanner.esg.ProcessingImageActivity;
import com.necsv.scanner.esg.ViewImagesActivity;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Listener for controlling zoom state through touch events
 */
public class TouchZoomListener implements View.OnTouchListener
{
    /**
     * Enum defining listener modes. Before the view is touched the listener is
     * in the UNDEFINED mode. Once touch starts it can enter either one of the
     * other two modes: If the user scrolls over the view the listener will
     * enter PAN mode, if the user lets his finger rest and makes a longpress
     * the listener will enter ZOOM mode.
     */

    /** Zoom control to manipulate */
    private ZoomControl mZoomControl;

    /** X-coordinate of latest down event */
    private float mDownX;

    /** Y-coordinate of latest down event */
    private float mDownY;

    /** Velocity tracker for touch events */
    private VelocityTracker mVelocityTracker;
  
    private int width,height,value;
	private boolean isNavigation = true;
    private Context mContext;
	
    /**
     * Creates a new instance
     * 
     * @param context Application context
     */
    public TouchZoomListener(Context context)
    {
    	mContext = context;
    }

    /**
     * Sets the zoom control to manipulate
     * 
     * @param control Zoom control
     */
    public void setZoomControl(ZoomControl control)
    {
        mZoomControl = control;
    }

    // implements View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event)
    {
        final int action = event.getAction();

        if (mVelocityTracker == null)
        {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            {
            	Intent intent = new Intent(this.mContext, ProcessingImageActivity.class);
            	intent.putExtra("filename", ViewImagesActivity.filename);
            	this.mContext.startActivity(intent);
            }
            default:
            {
                break;
            }
        }

        return true;
    }

    public void setZoom(float value)
    {
    	mZoomControl.zoom((float)Math.pow(20, value), mDownX / width, mDownY / height);
    }
	
	public class Hidden extends CountDownTimer
	{
		public Hidden(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
			if(value == 2)
        	{
				return;
        	}
			if(isNavigation)
			{
				((ViewImagesActivity)mContext).visibilityLayoutNav(true);
			}
			else
			{
				((ViewImagesActivity)mContext).visibilityLayoutNav(false);
			}
		}

		@Override
		public void onTick(long millisUntilFinished)
		{
			if(value == 2)
        	{
				((ViewImagesActivity)mContext).visibilityLayoutNav(false);
				return;
        	}
		}
	}
	   
    public class CountDown extends CountDownTimer
	{
		public CountDown(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
		}

		@Override
		public void onTick(long millisUntilFinished) {}
	}
}