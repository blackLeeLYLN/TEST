package com.boyuan.identifier.system.common;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.boyuan.identifier.system.io.Schema;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by houcheng on 2016/7/21.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler
{
    private Thread.UncaughtExceptionHandler defaultHandler;
    private static CrashHandler instance;
    private Context context;
    private Map<String, String> info = new HashMap<String, String>();
    private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private CrashHandler()
    {
        // ...
    }

    public static synchronized CrashHandler getInstance()
    {
        if (instance == null) instance = new CrashHandler();
        return instance;
    }

    public void init(Context context)
    {
        this.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        if (!handleException(ex) && defaultHandler != null)
        {
            defaultHandler.uncaughtException(thread, ex);
        }
        else
        {
            try
            {
                Thread.sleep(3000);
            }
            catch(Exception x)
            {
                // ...
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(final Throwable ex)
    {
        if (ex == null) return false;

        new Thread()
        {
            public void run()
            {
                Looper.prepare();
                Toast.makeText(context, "程序出现异常: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        ex.printStackTrace();
        saveToFile(ex);

        return true;
    }

    private void saveToFile(Throwable ex)
    {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        Throwable cause = ex.getCause();
        while (cause != null)
        {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String errorStack = writer.toString();

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(Schema.uri("file://crash.log").getFile(this.context), true);
            fos.write(errorStack.getBytes());
        }
        catch(Exception e)
        {
            // ..
            e.printStackTrace();
        }
        finally
        {
            try { fos.close(); } catch(Exception x) { }
        }
    }
}
