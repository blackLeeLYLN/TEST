package com.boyuan.identifier;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.boyuan.identifier.system.common.Resolution;
import com.boyuan.identifier.system.common.SystemBarTintManager;
import com.boyuan.identifier.system.page.Callbackable;
import com.tandong.sa.activity.SmartFragmentActivity;

import java.util.HashMap;

import af.boyuan.com.rrraf.R;

/**
 * Activity 基类  ldb 2016/3/24
 */
public abstract class BaseActivity extends SmartFragmentActivity implements Callbackable
{
    protected Context mContext;
    private HashMap<String, Object> missions = null;
    private Handler handler = null;
    Callbackable instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (!fullscreen()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
                SystemBarTintManager tintManager = new SystemBarTintManager(this);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintResource(R.color.colorTitle);

                // android:fitsSystemWindows="true"
            }
        }
        Resolution.init(this);
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);
        }
        mContext = this.getApplicationContext();
        App.setListActivity(this);
        preliminary();
    }

    protected boolean fullscreen()
    {
        return false;
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on)
    {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on)
        {
            winParams.flags |= bits;
        }
        else
        {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 向用户展示信息前的准备工作在这个方法里处理
     */
    protected void preliminary()
    {
        initMission();
        // 初始化组件
        initListener();
        // 初始化数据
        initialized();
    }

    public void initMission()
    {
        instance = this;
        this.missions = new HashMap<String, Object>();
        this.handler = new Handler(Looper.getMainLooper())
        {
            public void handleMessage(Message msg)
            {
                Bundle bundle = msg.getData();
                String mission = bundle.getString("mission");
                try
                {
                    Object data = instance.getMissionData(mission);
                    if (data instanceof Exception) instance.error(mission, (Exception)data);
                    else instance.complete(mission, data);
                }
                catch(Exception ex)
                {
                    // tooltip(mission + ":" + ex.toString());
                    ex.printStackTrace();
                    Log.d("houcheng", ex.toString() + " init error");
                }
            }
        };
    }

    /**
     * 获取全局的Context
     *
     * @return {@link #mContext = this.getApplicationContext();}
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 布局文件ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initialized();


    /**
     * 默认退出
     */
    public void defaultFinish() {
        super.finish();
    }


    public void gotoActivity(Class<? extends Activity> clazz, boolean finish) {
        Intent intent = new Intent(this, clazz);
        this.startActivity(intent);
        if (finish) {
            this.finish();
            // this.overridePendingTransition(R.anim.activity_move_in_start, R.anim.activity_move_out_start);
        }

    }

    @Override
    public void echo(Object receiver, String mission, Object data)
    {
        synchronized(this.missions)
        {
            this.missions.put(mission, data);

            Bundle bundle = new Bundle();
            bundle.putString("mission", mission);

            if (receiver instanceof BaseFragment) bundle.putInt("fragment", ((BaseFragment)receiver).getId());
            else bundle.putInt("fragment", -1);

            Message msg = Message.obtain();
            msg.setData(bundle);
            this.handler.sendMessage(msg);
        }
    }

    @Override
    public void complete(String mission, Object data) throws Exception
    {
        // ...
    }

    @Override
    public void error(String mission, Exception issue) throws Exception
    {
        // ...
    }

    @Override
    public Object getMissionData(String mission)
    {
        Object data = null;
        synchronized(this.missions)
        {
            data = this.missions.remove(mission);
        }
        return data;
    }

    public void tooltip(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
