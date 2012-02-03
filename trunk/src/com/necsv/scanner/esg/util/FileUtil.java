package com.necsv.scanner.esg.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class FileUtil
{
	public static FileInputStream in = null;
	public static ByteArrayOutputStream out = null;

	//Read file from asset and return byte type
	public static byte[] readFileFromAssets(Context context, String filename)
	{
		in  = null; 					
		out = null; 	
		byte[] readBuffer = new byte[128]; 	
		try
		{
			int size = 0;
			//in  = context.getAssets().open(filename);
			out = new ByteArrayOutputStream();
			while ((size = in.read(readBuffer)) > 0)
			{
				out.write(readBuffer, 0, size);
			}
			out.close();
			in.close();
		}
		catch (Exception e)
		{
			if (out != null)
			{
				try
				{
					out.close();
				} catch (Exception ignore) {
				}
			}
			if (in != null)
			{
				try
				{
					in.close();
				} catch (Exception ignore) {
				}
			}
			return null;
		}
		return out.toByteArray();
	}
	
	//Read file from SDCard and return byte type
	public static byte[] readFileFromSDCard(Context context, String filename)
	{
		in  = null; 					
		out = null; 	
		byte[] readBuffer = new byte[128]; 	
		try
		{
			int size = 0;

			in = new FileInputStream(filename);
			out = new ByteArrayOutputStream();
			while ((size = in.read(readBuffer)) > 0)
			{
				out.write(readBuffer, 0, size);
			}
			out.close();
			in.close();
		}
		catch (Exception e)
		{
			if (out != null)
			{
				try
				{
					out.close();
				} catch (Exception ignore) {
				}
			}
			if (in != null)
			{
				try
				{
					in.close();
				} catch (Exception ignore) {
				}
			}
			return null;
		}
		return out.toByteArray();
	}
	
	public static boolean deleteFile(String filename){
		File file = new File(filename);
		if(file.isFile()){
			return removeFile(file);
		}
		return removeDirectory(file);
	}
	
	private static boolean removeFile(File file){
		return file.delete();
	}

	public static boolean removeDirectory(File directory) {

		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		// Some JVMs return null for File.list() when the directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				if (entry.isDirectory()) {
					if (!removeDirectory(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}

    public static void copyFilesToSdCard(Context context) {
    	// copy all files in assets folder in my project
        copyFileOrDir(context,""); 
    }

    private static void copyFileOrDir(Context context, String path) {
        AssetManager assetManager = context.getAssets();

        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(context, path);
            } else {
                String fullPath =  GlobalVariable.TARGET_PATH + path;
                File dir = new File(fullPath);
                if (!dir.exists())
                    if (!dir.mkdirs());
                        Log.i("tag", "could not create dir " + fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals("")) {
                        p = "";
                    } else { 
                        p = path + "/";
                    }
                    copyFileOrDir(context, p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private static void copyFile(Context context, String filename) {
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            in = assetManager.open(filename);
            newFileName = GlobalVariable.TARGET_PATH + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }
    }
	
}