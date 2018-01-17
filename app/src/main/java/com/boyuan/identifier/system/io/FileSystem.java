package com.boyuan.identifier.system.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

public final class FileSystem
{
	/*
    public static Bitmap readImageFile(String fPath) throws Exception
    {
        return BitmapFactory.decodeStream(new FileInputStream(fPath));
    }

    public static Bitmap readImageUri(Uri uri) throws Exception
    {
        // int id = Integer.parseInt(uri.toString().replaceAll("^.*                           /(\\d+)$", "$1"));
        int degree = ImageProc.readPictureDegree(uri.getPath());
        Bitmap bitmap = Images.Thumbnails.getThumbnail(PageManager.getInstance().getCurrent().getContentResolver(), id, 1, new BitmapFactory.Options());
        return ImageProc.rotaingImage(degree, bitmap);
    }

    public static Bitmap readImageRes(String fPath) throws Exception
    {
        return BitmapFactory.decodeStream(FileSystem.class.getResourceAsStream(fPath));
    }
    
    public static Bitmap readImage(String srcName) throws Exception
    {
    	return BitmapFactory.decodeStream(FileSystem.class.getResourceAsStream("/res/drawable-xxhdpi/" + srcName));
    }
    
    public static Drawable readDrawable(String srcName) throws Exception
    {
    	return Drawable.createFromStream(FileSystem.class.getResourceAsStream("/res/drawable-xxhdpi/" + srcName + ".png"), srcName);
    }
	*/

    public static byte[] read(Context context, Schema.Uri uri) throws Exception
    {
        return read(getReader(context, uri));
    }

	/*
    public static Bitmap getThumbnail(String uri) throws Exception
    {
    	long albumId = 0;
    	int type = Images.Thumbnails.MICRO_KIND;
    	albumId = Integer.parseInt(uri.replaceAll("^content://media/external/images/media/(\\d+).*$", "$1"));
    	if (uri.indexOf("mini") > -1) type = Images.Thumbnails.MINI_KIND;
    	return Images.Thumbnails.getThumbnail(PageManager.getInstance().getCurrent().getContentResolver(), albumId, type, null);
    }
	*/
    
    public static InputStream getReader(Context context, Schema.Uri uri) throws Exception
    {
    	/*
		if (uri.getType() == Schema.URI_ASSETS) return PageManager.getInstance().getCurrent().getAssets().open(uri.getPath());
        if (uri.getType() == Schema.URI_RES) return FileSystem.class.getResourceAsStream("/res/drawable-xxhdpi/" + uri.getPath());
        if (uri.getType() == Schema.URI_CONTENT) return PageManager.getInstance().getCurrent().getContentResolver().openInputStream(Uri.parse(uri.toString()));
		*/
        if (uri.getType() == Schema.URI_CACHE)
        {
        	return new FileInputStream(uri.getFile(context));
        }
        if (uri.getType() == Schema.URI_FILE)
        {
        	return new FileInputStream(uri.getFile(context));
        }
    	return null;
    }
    
    public static OutputStream getWriter(Context context, Schema.Uri uri) throws Exception
    {
    	if (uri.getType() == Schema.URI_CACHE)
    	{
    		return new FileOutputStream(uri.getFile(context));
    	}
    	if (uri.getType() == Schema.URI_FILE)
    	{
    		return new FileOutputStream(uri.getFile(context));
    	}
    	return null;
    }
    
    public static boolean isExists(Context context, Schema.Uri uri)
    {
		String cachePath = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			cachePath = context.getExternalCacheDir().getAbsolutePath();
		else
			cachePath = context.getCacheDir().getAbsolutePath();
		
		File f = new File(cachePath + File.separator + uri.getPath());
		return f.exists();
    }
    
    public static void write(OutputStream writer, byte[] data) throws Exception
    {
    	try
    	{
    		for (int i = 0, l = data.length; i < l; i += 512)
    		{
    			writer.write(data, i, (i + 512 > l ? l - i : 512));
    		}
    	}
    	finally
    	{
    		try { writer.close(); } catch (Exception e) { };
    	}
    }
    
    public static void write(OutputStream writer, InputStream reader) throws Exception
    {
    	try
    	{
    		int ch = 0;
    		byte[] buffer = new byte[512];
    		while ((ch = reader.read(buffer)) > 0)
    		{
    			writer.write(buffer, 0, ch);
    		}
    	}
    	finally
    	{
    		try { reader.close(); } catch (Exception e) { }
    		try { writer.close(); } catch (Exception e) { }
    	}
    }
    
    public static byte[] read(InputStream reader) throws Exception
    {
    	ByteArrayOutputStream baos = null;
    	try
    	{
    		baos = new ByteArrayOutputStream(40960);
    		byte[] bytes = new byte[4096];
            int ch = 0;
            while ((ch = reader.read(bytes)) > -1)
            {
            	baos.write(bytes, 0, ch);
            }
    		return baos.toByteArray();
    	}
    	finally
    	{
    		try { reader.close(); } catch(Exception e) { }
    		try { baos.close(); } catch(Exception e) { }
    	}
    }

	/*
    public static void serialize(Schema.Uri uri, java.io.Serializable object) throws Exception
    {
    	ObjectOutputStream oos = null;
    	OutputStream writer = null;
    	try
    	{
    		oos = new ObjectOutputStream(writer = uri.getWriter());
    		oos.writeObject(object);
    	}
    	finally
    	{
    		try { writer.close(); } catch (Exception e) { }
    		try { oos.close(); } catch (Exception e) { }
    	}
    }
    
    public static Object unserialize(Schema.Uri uri) throws Exception
    {
    	ObjectInputStream ois = null;
    	InputStream reader = null;
    	Object data = null;
    	try
    	{
    		ois = new ObjectInputStream(reader = uri.getReader());
    		data = ois.readObject();
    	}
    	finally
    	{
    		try { reader.close(); } catch (Exception e) { }
    		try { ois.close(); } catch (Exception e) { }
    	}
    	return data;
    }
	*/
}
