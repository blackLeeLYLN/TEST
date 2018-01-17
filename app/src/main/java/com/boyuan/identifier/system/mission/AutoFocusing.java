package com.boyuan.identifier.system.mission;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.util.Log;

import com.boyuan.identifier.system.common.Device;
import com.boyuan.identifier.system.io.Schema;
import com.boyuan.identifier.system.page.Callbackable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import af.boyuan.com.rrraf.Camera;

/**
 * Created by houcheng on 2016/9/1.
 */
public class AutoFocusing extends Mission
{
    public static enum Ratio
    {
        x15, x40
    };

    private static final int STEPS_5X = 11;
    private static final int STEPS_15X = 35;
    private static final int STEPS_40X = 97;
    private static final int MAX_STEPS = STEPS_40X + 20;
    // private final int MAX_STEPS = 100;

    private static int TIMES = 0;

    // static int[] colors = new int[(int)Math.ceil(0xffffff / 32.0f + 1)];

    private final String CMD_STEP_ZOOM_IN = "steppshort";
    private final String CMD_STEP_ZOOM_OUT = "stepnshort";

    private final String CMD_ZOOM_IN = "stepplong";
    private final String CMD_ZOOM_OUT = "stepnlong";
    private final String CMD_STOP = "stepstop";

    private static int steps = -1;

    private Ratio ratio;
    private MotorAction action;

    public AutoFocusing(Callbackable receiver, String mission, Ratio ratio)
    {
        super(mission, receiver);
        this.ratio = ratio;

        Log.e("liyanan", "12121212121");
    }

    public AutoFocusing(Callbackable receiver, String mission, MotorAction action)
    {
        super(mission, receiver);
        this.action = action;
    }

    public Object handle() throws Exception
    {
        byte exposure = 0;
        if (this.action == MotorAction.reset)
        {
            reset();
            return "reset";
        }
        if (this.action == MotorAction.step_zoom_in)
        {
            // TODO：限制最大步数
            // TODO: 限制步数范围
             zoomIn();
            return steps;
        }
        if (this.action == MotorAction.step_zoom_out)
        {
            zoomOut();
            return steps;
        }
        if (this.action == MotorAction.ratio_5)
        {
            aftonx(STEPS_5X);
            return "5x";
        }
        if (this.action == MotorAction.ratio_15)
        {
//            aftonx(STEPS_15X);
//            return afTo_nx(STEPS_15X);
            TIMES = TIMES + 1;
            return afTo_nx_xxoo(STEPS_15X);
        }
        if (this.action == MotorAction.ratio_40)
        {
//            aftonx(STEPS_40X);
            TIMES = TIMES + 1;
            return afTo_nx_xxoo(STEPS_40X);
        }
        return null;
    }

    private void aftonx(int targetStep)
    {
        if (steps == targetStep) return;
        int count = steps - targetStep;
        for (int i = 0, l = Math.abs(count); i < l; i++)
        {
            if (count > 0) zoomOut();
            else zoomIn();
            pause();
            if (!isCallbackable()) return;
        }
    }

    public long afTo_nx(int steps_nx) throws Exception
    {
        Camera camera = (Camera)this.getContext();

        try {
            final int frameWidth = camera.getFrameWidth();
            final int frameHeight = camera.getFrameHeight();
            //15x对焦起点
            final int FOCUSING_ORIGIN = steps_nx - 5;
            //15x对焦终点
            final int FOCUSING_END = FOCUSING_ORIGIN + 10;
            //颜色差值最大值
            double maxValue = 0;
            //颜色差值最大的步数
            int step = FOCUSING_ORIGIN;
            boolean flag = false;
            // 移动到开始计算颜色差值的位置
            to_calcFocus_origin(FOCUSING_ORIGIN, FOCUSING_END);
            if (steps == FOCUSING_ORIGIN){
                flag = true;
            }
            //15x自动对焦
            long spend = System.currentTimeMillis();
            long time = 0;

            for(int i = FOCUSING_ORIGIN; i <= FOCUSING_END; i++)
            {
                to_goal_move(flag);
                Thread.sleep(250);
                int differenceValue = 0;
                byte[] cameraData = null;
                for (int x = 0; x < 1; x++)
                {
                    cameraData = camera.obtainCameraData();
                    while (cameraData == null)
                    {
                        Thread.sleep(10);
                        if (camera.isFinishing()) return 0;
                        cameraData = camera.obtainCameraData();
                    }
                    differenceValue = Math.max(contrast(0, cameraData, frameWidth, frameHeight), differenceValue);
                }

                final int diff = differenceValue;

                if(maxValue < differenceValue){
                    maxValue = differenceValue;
                    step = steps;
                }
            }
            //移动到颜色值最大的步数
            to_focus(FOCUSING_ORIGIN, FOCUSING_END, step);
            // return System.currentTimeMillis() - spend;
            return steps;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Log.e("liyanan", "Error:" + ex.toString());
        }
        return 0;
    }

    //todo 2016.10.9
    public long afTo_nx_xxoo(int steps_nx) throws Exception
    {
        Camera camera = (Camera)this.getContext();

        try {

            int index = 0;

            final int frameWidth = camera.getFrameWidth();
            final int frameHeight = camera.getFrameHeight();
            // 移动到开始计算颜色差值的位置
            to_calcFocus_origin_xxoo(steps_nx);
            //15x自动对焦
            long spend = System.currentTimeMillis();
            long time = 0;
            //确定电机移动方向，默认向上移动
            byte direction = -1;
            //获取34,35,36颜色值，确定电机移动方向
            int prevValue = contrast(index++, to_goal_move_xxoo(false), frameWidth, frameHeight);
            int currentValue = contrast(index++, to_goal_move_xxoo(true), frameWidth, frameHeight);
            int nextValue = contrast(index++, to_goal_move_xxoo(true), frameWidth, frameHeight);
            int maxValue = Math.max(Math.max(prevValue, currentValue), nextValue);
            int count = 0;
            if(maxValue == nextValue){
                direction = 1;
            }else if(maxValue == currentValue){
                direction = 0;
            }
            if(direction < 0){
                count = 0;
                to_goal_move(false);
                to_goal_move(false);
                maxValue = contrast(index++, to_goal_move_xxoo(false), frameWidth, frameHeight);
                while(((maxValue - prevValue) > 0) && (count <10)){
                    count++;
                    prevValue = maxValue;
                    maxValue = contrast(index++, to_goal_move_xxoo(false), frameWidth, frameHeight);
                }
                to_goal_move(true);
            }else if(direction > 0){
                count = 0;
                maxValue = contrast(index++, to_goal_move_xxoo(true), frameWidth, frameHeight);
                while(((maxValue - nextValue) > 0) && (count < 10)){
                    count ++;
                    nextValue = maxValue;
                    maxValue = contrast(index++, to_goal_move_xxoo(true), frameWidth, frameHeight);
                }
                to_goal_move(false);
            }else{
                to_goal_move(false);
            }
            // return System.currentTimeMillis() - spend;
            // return steps;
            return TIMES;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Log.e("liyanan", "Error:" + ex.toString());
        }
        return TIMES;
    }

    private double calculate()
    {
        int differenceValue = 999999999;
        try
        {
            Camera camera = (Camera)this.getContext();

            int frameWidth = camera.getFrameWidth();
            int frameHeight = camera.getFrameHeight();
            byte[] cameraData = camera.obtainCameraData();
            while (cameraData == null)
            {
                Thread.sleep(10);
                if (camera.isFinishing()) return differenceValue;
                Log.e("liyanan", "no data yet");
            }
            //int[] pixels = new int[frameWidth * frameHeight * 3];
            //decodeYUV420SP(pixels, cameraData, frameWidth, frameHeight);
            // pixels[0]
            differenceValue = contrast(0, cameraData, frameWidth, frameHeight);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return differenceValue;
    }

    //判断当前步数距离64、84哪个近，移动到那个位置上去.
    public void to_calcFocus_origin(int focusingOrigin, int focusingEnd){
        int count1 = steps - focusingOrigin;
        int count2 = steps - focusingEnd;
        int moveSteps = Math.min(Math.abs(count1), Math.abs(count2));
        if(count1 < 0 || (count1 > 0 && count2 < 0 && Math.abs(count2) < Math.abs(count1))){
            for(int i = 0; i < moveSteps; i++){
                zoomIn();
            }
        }else if(count2 > 0 || (count1 > 0 && count2 < 0 && Math.abs(count2) > Math.abs(count1))){
            for(int i = 0; i < moveSteps; i++){
                zoomOut();
            }
        }
    }

    //todo 2016.10.9
    public void to_calcFocus_origin_xxoo(int steps_nx){
        int count = steps - steps_nx;
        int moveSteps = Math.abs(count);
        if(count < 0){
            for(int i = 0; i < moveSteps; i++){
                zoomIn();
            }
        }else if(count > 0){
            for(int i = 0; i < moveSteps; i++){
                zoomOut();
            }
        }
    }

    //todo 2016.10.9
    //向目标方向移动1步
    public void to_goal_move(boolean flag){
        if(flag){
            zoomIn();
        }else{
            zoomOut();
        }
    }

    public byte[] to_goal_move_xxoo(boolean flag) throws InterruptedException {
        if(flag){
            zoomIn();
        }else{
            zoomOut();
        }
        Thread.sleep(400);
        Camera camera = (Camera)this.getContext();
        return camera.obtainCameraData();
    }

    //移动到颜色值最大的步数
    public void to_focus(int FOCUSING_ORIGIN, int FOCUSING_END, int step){
        for(int j = FOCUSING_ORIGIN ; j <= FOCUSING_END; j++ ){
            if(steps > step){
                zoomOut();
            }else if(steps < step){
                zoomIn();
            }else{
                break;
            }
        }
    }

    static byte[] pixels = null;
    public int contrast(int index, byte[] yuv420sp, int width, int height)
    {
        final int frameSize = width * height;
        int i = 0, y = 0;
        int uvp = 0, u = 0, v = 0;
        int y1192 = 0, r = 0, g = 0, b = 0;
        if (pixels == null) {
            pixels = new byte[width * height];
        }
        else
        {
            Arrays.fill(pixels, (byte)0);
        }
        for (int j = 0, yp = 0; j < height; j++)
        {
            uvp = frameSize + (j >> 1) * width;
            u = 0;
            v = 0;
            for (i = 0; i < width; i++, yp++)
            {
                y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                y1192 = 1192 * y;
                r = (y1192 + 1634 * v);
                g = (y1192 - 833 * v - 400 * u);
                b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                r = (r >> 10) & 0xff;
                g = (g >> 10) & 0xff;
                b = (b >> 10) & 0xff;
                pixels[yp] = (byte)((r + g + b) / 3);
            }
        }
        int sum = 0;
        for (int s = 0, l = pixels.length; s < l; s++)
        {
            int top = pixels[s - width < 0 ? 0 : s - width] & 0xff;
            int bottom = pixels[s + width >= l ? l - 1 : s + width] & 0xff;
            int left = pixels[s - 1 < 0 ? 0 : s - 1] & 0xff;
            int right = pixels[s + 1 >= l ? l - 1 : s + 1] & 0xff;

            int avg = (top + bottom + left + right) >> 2;

            sum += Math.abs((pixels[s] & 0xff) - avg);
        }

        // new Fucker(getReceiver(), "fucker", yuv420sp, width, height, TIMES, "s-" + index + "-" + steps + "-" + sum + ".jpg").go();

        return sum;
    }

    private void move(String dir)
    {
        FileWriter writer = null;

        try
        {
            File f = new File("/proc/gpio_ctrl/rp_gpio_ctrl");
            if (!f.exists()) return;
            writer = new FileWriter(f);
            writer.write(dir);
        }
        catch(Exception ex)
        {
            // do nothing here...
            // ex.printStackTrace();
        }
        finally
        {
            try { writer.close(); } catch(Exception e) { }
        }
    }

    private void zoomIn()
    {
        move(CMD_STEP_ZOOM_IN);
        stepIn();
        pause();
    }

    private void zoomOut()
    {
        move(CMD_STEP_ZOOM_OUT);
        stepOut();
        pause();
    }

    private void stepIn()
    {
        steps += 1;
        if (steps > MAX_STEPS) steps = MAX_STEPS;
        //this.reportProgress(steps);
    }

    private void stepOut()
    {
        steps -= 1;
        if (steps < 0) steps = 0;
        //this.reportProgress(steps);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void reset()
    {
        // 电机移动200步，回到最大焦距
        for (int i = 0; i < MAX_STEPS; i++)
        {
            zoomOut();
            pause();
            if (!isCallbackable()) return;
        }
    }


//    private void reset()
//    {
//        // 电机移动200步，回到最大焦距
//        long startTime = System.currentTimeMillis();
//        move(CMD_ZOOM_OUT);
//        try
//        {
//            Thread.sleep(2400);
//        }
//        catch(Exception e) { }
//        move(CMD_STOP);
//    }


    private void pause()
    {
        try { Thread.sleep(20); } catch(Exception e) { }
    }
}