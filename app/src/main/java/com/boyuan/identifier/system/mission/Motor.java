package com.boyuan.identifier.system.mission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.boyuan.identifier.system.common.Configuration;
import com.boyuan.identifier.system.page.Callbackable;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by houcheng on 2016/7/14.
 */
public class Motor extends Mission
{
    private final int STEPS_15X = 74;
    private final int STEPS_40X = 155;
    private final int MAX_STEPS = STEPS_40X + 48;

    private final String STEP_ZOOM_IN = "8";
    private final String STEP_ZOOM_OUT = "7";

    public static int STEPS = -1;
    Configuration conf = null;
    MotorAction action = null;

    public Motor(Callbackable receiver, String mission, MotorAction action)
    {
        super(mission, receiver);
        this.action = action;
        conf = Configuration.getInstance(this.getContext());
        if (conf.getInt("motor-flag", 0) == 1)
        {
            STEPS = conf.getInt("motor-steps", -1);
        }
        else
        {
            STEPS = -1;
        }
        conf.put("motor-flag", 0);
        this.reportProgress(STEPS);
    }

    @Override
    public Object handle() throws Exception
    {
        if (action == MotorAction.ratio_15)
        {
            to15X();
            return "15x";
        }
        else if (action == MotorAction.ratio_40)
        {
            to40X();
            return "40x";
        }
        else if (action == MotorAction.reset)
        {
            reset();
            return "reset";
        }
        else if (action == MotorAction.step_zoom_in)
        {
            zoomIn();
            pause();
            return "step_zoom_in";
        }
        else if (action == MotorAction.step_zoom_out)
        {
            zoomOut();
            pause();
            return "step_zoom_out";
        }
        else return null;
    }

    private void move(String dir)
    {
        FileWriter writer = null;

        try
        {
            File f = new File("/proc/remote_stm32/motor");
            if (!f.exists()) return;
            writer = new FileWriter(f);
            writer.write(dir);
        }
        catch(Exception ex)
        {
            // do nothing here...
            ex.printStackTrace();
        }
        finally
        {
            try { writer.close(); } catch(Exception e) { }
        }
    }

    private void zoomIn()
    {
        move(STEP_ZOOM_IN);
        stepIn();
    }

    private void zoomOut()
    {
        move(STEP_ZOOM_OUT);
        stepOut();
    }

    private void stepIn()
    {
        STEPS += 1;
        if (STEPS > MAX_STEPS) STEPS = MAX_STEPS;
        conf.put("motor-steps", STEPS);
        conf.put("motor-flag", 1);

        this.reportProgress(STEPS);
    }

    private void stepOut()
    {
        STEPS -= 1;
        if (STEPS < 0) STEPS = 0;
        conf.put("motor-steps", STEPS);
        conf.put("motor-flag", 1);
        this.reportProgress(STEPS);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void reset()
    {
        // 电机移动200步，回到最大焦距
        for (int i = 0; i < 240; i++)
        {
            zoomOut();
            pause();
            if (!isCallbackable()) return;
        }
    }

    private void to15X()
    {
        if (STEPS == STEPS_15X) return;
        int count = STEPS - STEPS_15X;
        for (int i = 0, l = Math.abs(count); i < l; i++)
        {
            if (count > 0) zoomOut();
            else zoomIn();
            pause();
            if (!isCallbackable()) return;
        }
    }

    private void to40X()
    {
        if (STEPS == STEPS_40X) return;
        int count = STEPS - STEPS_40X;
        for (int i = 0, l = Math.abs(count); i < l; i++)
        {
            if (count > 0) zoomOut();
            else zoomIn();
            pause();
            if (!isCallbackable()) return;
        }
    }

    private void pause()
    {
        try { Thread.sleep(20); } catch(Exception e) { }
    }
}
