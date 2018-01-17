package com.boyuan.identifier.system.mission;

import android.os.Bundle;
import android.util.Base64;

import com.boyuan.identifier.system.common.Configuration;
import com.boyuan.identifier.system.common.Device;
import com.boyuan.identifier.system.common.Issue;
import com.boyuan.identifier.system.io.Network;
import com.boyuan.identifier.system.io.Schema;
import com.boyuan.identifier.system.page.Callbackable;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by houcheng on 2016/7/15.
 */
public class FormSubmittor extends Mission
{
    private Bundle fields = null;
    private String url = null;
    private String imageUploadUrl = null;

    public FormSubmittor(Callbackable receiver, String mission, String url, Bundle fields)
    {
        super(mission, receiver);
        this.url = url;
        this.fields = fields;
    }

    public FormSubmittor(Callbackable receiver, String mission, String url, String imageSaveUrl, Bundle fields)
    {
        super(mission, receiver);
        this.url = url;
        this.imageUploadUrl = imageSaveUrl;
        this.fields = fields;
    }

    @Override
    public Object handle() throws Exception
    {
        String jsonText = null;
        if (Device.getNetworkType(this.getContext()) == Device.NETWORK_TYPE_NONE)
        {
            throw new Exception("无法连接到网络，请稍后再试。");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        Configuration conf = Configuration.getInstance(this.getContext());
        Network.Header[] headers = new Network.Header[]
                {
                        new Network.Header("If-Modified-Since", sdf.format(new Date())),
                        new Network.Header("X-Proxy-Token", conf.getString("boyuan_proxy_token", "")),
                        new Network.Header("X-Proxy-User-Id", String.valueOf(conf.getLong("boyuan_user_id", 0))),
                        new Network.Header("X-Proxy-Id", String.valueOf(conf.getLong("boyuan_proxy_id", 0)))
                        // TODO: 加个设备序列号进来
                };

        Iterator itr = fields.keySet().iterator();
        StringBuffer buff = new StringBuffer(5 * 1024 * 1024);
        while (itr.hasNext())
        {
            String key = (String)itr.next();
            if (key.endsWith("_file")) continue;
            String value = (String)fields.get(key);

            buff.append(key);
            buff.append('=');
            buff.append(value);
            buff.append('&');
        }

        // 提交数据
        byte[] dBytes = Network.post(this.url, buff.toString().getBytes(), headers, 30000);
        JSONObject json = new JSONObject(new String(dBytes, "UTF-8"));
        if (json.getBoolean("error"))
            throw new Issue(json.getString("message"));

        // 分片提交图片
        sendFile(Schema.uri(fields.getString("datasrc_5x_file")).getFile(this.getContext()), json.getJSONObject("data").getLong("x5"), headers);
        sendFile(Schema.uri(fields.getString("datasrc_15x_file")).getFile(this.getContext()), json.getJSONObject("data").getLong("x15"), headers);
        sendFile(Schema.uri(fields.getString("datasrc_40x_file")).getFile(this.getContext()), json.getJSONObject("data").getLong("x40"), headers);
        return json;
    }

    private void sendFile(File imgFile, long dotId, Network.Header[] headers) throws Exception
    {
        byte[] buff = new byte[40960];
        FileInputStream fis = null;

        try
        {
            JSONObject json = null;

            int blocks = (int)Math.ceil((float)imgFile.length() / 40960f);
            fis = new FileInputStream(imgFile);
            for (int i = 0; i < blocks; i++)
            {
                int len = fis.read(buff);
                String datasrc = new String(Base64.encode(buff, 0, len, Base64.NO_WRAP));
                String data = "dot_id=" + dotId + "&blocks=" + blocks + "&index=" + (i + 1) + "&datasrc=" + java.net.URLEncoder.encode(datasrc);
                json = new JSONObject(new String(Network.post(this.imageUploadUrl, data.getBytes(), headers, 2000), "UTF-8"));
                if (json.getBoolean("error")) throw new Issue(json.getString("message"));
            }
        }
        finally
        {
            try { fis.close(); } catch(Exception x) { }
        }
    }

    public static String encodeFile(File file)
    {
        if (!file.exists()) return null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            fis = new FileInputStream(file);
            byte[] buff = new byte[512];
            int len = -1;
            while ((len = fis.read(buff)) > 0)
            {
                baos.write(buff, 0, len);
            }
            return new String(Base64.encode(baos.toByteArray(), Base64.NO_WRAP));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
        finally
        {
            try { fis.close(); } catch(Exception x) { }
        }
    }
}
