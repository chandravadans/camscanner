package com.necsv.scanner.esg.util;

import java.util.ArrayList;
import java.util.List;


import android.content.res.Configuration;
import android.graphics.Bitmap;

public class GlobalVariable
{
	public static final int COUNTDOWN = 4000;
	public static final int INTERVAL = 4000;
	public static int PANLEFT		= 0;
	public static int PANRIGHT		= 0;
	public static int IMAGESELECT 	= 0;
	
	public static String ALBUMSID		= "";
	public static String ALBUMSTITLE	= "";
	
	public static boolean ISCOVER 			= false;
	public static boolean SELECTPOSITION 	= false;
	public static boolean SETONCLICK 		= false;
	
	public static Configuration config;
	public static List<Albums> mAlbumsGrid = new ArrayList<Albums>();
	public static byte[] byteBuffer = new byte[1024*2];
	public static Bitmap mBitmap	= null;
	public static int positionGrid = 0;
	public static int positionList = 0;
	public static int data_exist = 2;
	public final static String TARGET_PATH = "/mnt/sdcard/CamScanner/";
	
    public static void clearBitmap()
    {
    	if(mBitmap != null)
    	{
    		mBitmap.recycle();
    		mBitmap = null;
    	}
    }
}