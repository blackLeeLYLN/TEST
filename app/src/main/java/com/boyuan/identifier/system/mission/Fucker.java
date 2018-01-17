package com.boyuan.identifier.system.mission;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import com.boyuan.identifier.system.io.Schema;
import com.boyuan.identifier.system.page.Callbackable;

import java.io.ByteArrayOutputStream;

/**
 * Created by liyanan on 2016/10/17.
 */
public class Fucker extends Mission
{
    private byte[] data;
    private int times;
    private String fileName;

    private int frameWidth = 0;
    private int frameHeight = 0;

    public Fucker(Callbackable receiver, String mission, byte[] data, int width, int height, int times, String fileName)
    {
        super(mission, receiver);
        this.data = data;
        this.times = times;
        this.fileName = fileName;

        this.frameWidth = width;
        this.frameHeight = height;
    }

    @Override
    public Object handle() throws Exception
    {
        final YuvImage image = new YuvImage(data, ImageFormat.NV21, frameWidth, frameHeight, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        if(!image.compressToJpeg(new Rect(0, 0, frameWidth, frameHeight), 100, os))
        {
            return null;
        }
        byte[] tmp = os.toByteArray();
        Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0,tmp.length);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, Schema.parse("file://" + times + "/" + fileName).getWriter(getContext()));
        return null;
    }
}
