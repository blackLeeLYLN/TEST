package com.boyuan.identifier.system.mission;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.text.*;
import java.util.*;

import com.boyuan.identifier.system.common.Configuration;
import com.boyuan.identifier.system.common.Device;
import com.boyuan.identifier.system.common.Encrypt;
import com.boyuan.identifier.system.common.Issue;
import com.boyuan.identifier.system.page.*;
import com.boyuan.identifier.system.io.*;
import android.content.Context;

public class JsonRequestor extends Mission
{
	private String url;
	private int timeout = 30000;
	private int cacheTime = 0;
	
	public JsonRequestor(Callbackable receiver, String mission, String url)
	{
		super(mission, receiver);
		this.url = url;
	}
	
	public JsonRequestor(Callbackable receiver, String mission, String url, int cacheTime)
	{
		super(mission, receiver);
		this.url = url;
		this.cacheTime = cacheTime;
	}
	
	public JsonRequestor(Callbackable receiver, String mission, String url, int timeout, int cacheTime)
	{
		super(mission, receiver);
		this.url = url;
		this.timeout = timeout;
		this.cacheTime = cacheTime;
	}

	public Object handle() throws Exception
	{
		// cache://json-EEF977E7ABA236ED26D71B295988DD2C
		String jsonText = null;
		Schema.Uri resource = Schema.parse("cache://json-" + Encrypt.MD5(this.url));
		if (this.cacheTime > 0)
		{
			// 缓存文件是否存在
			File cacheFile = resource.getFile(this.getContext());
			if (cacheFile.exists() && cacheFile.lastModified() + this.cacheTime > System.currentTimeMillis())
				jsonText = new String(FileSystem.read(new FileInputStream(cacheFile)));
		}
		if (Device.getNetworkType(this.getContext()) == Device.NETWORK_TYPE_NONE)
		{
			throw new Exception("无法连接到网络，请稍后再试。");
		}
		if (jsonText == null)
		{
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

			byte[] dBytes = Network.get(this.url, headers, this.timeout);
			jsonText = new String(dBytes, Configuration.ENCODING);
			if (this.cacheTime > 0) FileSystem.write(resource.getWriter(this.getContext()), dBytes);
		}
		JSONObject json = new JSONObject(jsonText);
		if (json.getBoolean("error"))
			throw new Issue(json.getString("message"));
		return json;
	}
}
