package com.boyuan.identifier.system.io;

import java.io.*;
import java.net.*;

public final class Network
{
    public static byte[] get(String url, Header[] headers, int timeout) throws Exception
    {
        int ch = -1;
        String shtml = "";
        URL oUrl = null;
        HttpURLConnection urlConn = null;
        InputStream bis = null;
        byte[] buf = new byte[256];
        ByteArrayOutputStream baos = null;
        headers = null == headers ? new Header[0] : headers;
        
        long stime = System.currentTimeMillis();

        try
        {
            baos = new ByteArrayOutputStream(256);
            oUrl = new URL(url);
            urlConn = (HttpURLConnection)oUrl.openConnection();
            urlConn.setConnectTimeout(timeout);
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.00; Windows 98)");
            urlConn.setRequestProperty("Pragma", "no-cache");
            urlConn.setRequestProperty("Cache-Control", "no-cache");
            for (int i = 0, l = headers.length; i < l; i++)
            {
                urlConn.setRequestProperty(headers[i].name, headers[i].value);
            }
            
            stime = System.currentTimeMillis() - stime;
            urlConn.setReadTimeout((int) (timeout - stime));
            
            bis = urlConn.getInputStream();
            while ((ch = bis.read(buf)) != -1)
            {
                baos.write(buf, 0, ch);
            }
            return baos.toByteArray();
        }
        finally
        {
            try { bis.close(); } catch (Exception x) { }
            try { urlConn.disconnect(); } catch (Exception x) { }
            try { baos.close(); } catch (Exception x) { }

            urlConn = null;
            oUrl = null;
            bis = null;
            baos = null;
        }
    }
    
    public static InputStream read(String url, Header[] headers, int timeout) throws Exception
    {
        URL oUrl = null;
        HttpURLConnection urlConn = null;
        headers = null == headers ? new Header[0] : headers;
        
        try
        {
            oUrl = new URL(url);
            urlConn = (HttpURLConnection)oUrl.openConnection();
            urlConn.setConnectTimeout(timeout);
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Referer", url);
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.00; Windows 98)");
            urlConn.setRequestProperty("Pragma", "no-cache");
            urlConn.setRequestProperty("Cache-Control", "no-cache");
            for (int i = 0, l = headers.length; i < l; i++)
            {
                urlConn.setRequestProperty(headers[i].name, headers[i].value);
            }
            return urlConn.getInputStream();
        }
        finally
        {
            urlConn = null;
            oUrl = null;
        }
    }

    public static byte[] post(String url, byte[] data, Header[] headers, int timeout) throws Exception
    {
        int ch = -1;
        String shtml = "";
        URL oUrl = null;
        HttpURLConnection urlConn = null;
        InputStream bis = null;
        OutputStream writer = null;
        byte[] buf = new byte[256];
        ByteArrayOutputStream baos = null;
        headers = null == headers ? new Header[0] : headers;
        try
        {
            baos = new ByteArrayOutputStream(256);
            oUrl = new URL(url);
            urlConn = (HttpURLConnection)oUrl.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setConnectTimeout(timeout);
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Referer", url);
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.00; Windows 98)");
            urlConn.setRequestProperty("Pragma", "no-cache");
            urlConn.setRequestProperty("Cache-Control", "no-cache");
            for (int i = 0, l = headers.length; i < l; i++)
            {
                urlConn.setRequestProperty(headers[i].name, headers[i].value);
            }
            urlConn.setRequestProperty("Content-Length", String.valueOf(data.length));
            urlConn.setRequestMethod("POST");
            writer = urlConn.getOutputStream();
            writer.write(data);
            writer.flush();
            writer.close();

            if (urlConn.getResponseCode() != 200) bis = urlConn.getErrorStream();
            else bis = urlConn.getInputStream();

            while ((ch = bis.read(buf)) != -1)
            {
                baos.write(buf, 0, ch);
            }
            return baos.toByteArray();
        }
        finally
        {
            try { bis.close(); } catch (Exception x) { }
            try { urlConn.disconnect(); } catch (Exception x) { }
            try { baos.close(); } catch (Exception x) { }
            try { writer.close(); } catch(Exception x) { }

            urlConn = null;
            oUrl = null;
            bis = null;
            baos = null;
            writer = null;
        }
    }

    public static class Header
    {
        public String name;
        public String value;
        public Header(String name, String value)
        {
            this.name = name;
            this.value = value;
        }
    }
}
