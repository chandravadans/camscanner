package com.necsv.scanner.esg;

import java.io.File;

import com.necsv.scanner.esg.util.FileUtil;
import com.necsv.scanner.esg.util.GlobalVariable;
import com.necsv.scanner.esg.util.Rotate3DAnimation;
import com.necsv.scanner.esg.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
	private LinearLayout layout;
	private ImageView mImageView;
	private Context mContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mContext = this;
        layout = (LinearLayout)findViewById(R.id.main_activity_layout);
        mImageView = (ImageView)findViewById(R.id.main_activity_image);
		
        MainCountDown countdown = new MainCountDown(GlobalVariable.COUNTDOWN, GlobalVariable.INTERVAL);
        countdown.start();
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
			applyRotation(0, 90);
		}
		@Override
		public void onTick(long millisUntilFinished) {}
    }
    
    private void applyRotation(float start, float end)
    {
        final float centerX = layout.getWidth() / 2.0f;
        final float centerY = layout.getHeight() / 2.0f;        
        final Rotate3DAnimation rotation = new Rotate3DAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(700);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView());
        layout.startAnimation(rotation);
    }

    private final class DisplayNextView implements Animation.AnimationListener
    {
        public void onAnimationStart(Animation animation) {}

        public void onAnimationEnd(Animation animation)
        {
        	layout.post(new SwapViews());
        }

        public void onAnimationRepeat(Animation animation) {}
    }

    private final class SwapViews implements Runnable
    {
        public void run()
        {
            final float centerX = layout.getWidth() / 2.0f;
            final float centerY = layout.getHeight() / 2.0f;
            Rotate3DAnimation rotation;            
            mImageView.setVisibility(View.GONE);
            rotation = new Rotate3DAnimation(90, 180, centerX, centerY, 310.0f, false);
            rotation.setDuration(700);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            layout.startAnimation(rotation);

            File file = new File(GlobalVariable.TARGET_PATH);

            Intent intent = new Intent(mContext, AlbumsListActivity.class);
            Handler handler = new Handler();
            if(!file.exists()) {
            	handler.post(new Runnable() {
        			public void run() {
        				FileUtil.copyFilesToSdCard(mContext);
        			}
        		}); 
                intent.putExtra("data", false);
            } else {
            	intent.putExtra("data", true);
            }
            mContext.startActivity(intent);
            finish();
        }
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }
    

    
}