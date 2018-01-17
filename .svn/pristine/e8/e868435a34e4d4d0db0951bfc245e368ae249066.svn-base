package com.boyuan.identifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.boyuan.identifier.system.page.Callbackable;

import java.util.HashMap;

/**
 * 功能描述：对Fragment类进行扩展
 *
 * @author Ldb
 */
public abstract class BaseFragment extends Fragment implements Callbackable
{
    /**
     */
    protected Context mContext;
    private View view;
    private HashMap<String, Object> missions = null;
    private Handler handler = null;
    Callbackable instance = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        view = inflater.inflate(layoutId, null);
        mContext = this.getActivity();
        preliminary();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 向用户展示信息前的准备工作在这个方法里处理
     */
    protected void preliminary() {
        // 初始化组件
        initListener();
        // 初始化数据
        initialized();

        initMission();
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
                    else instance.complete(mission, instance.getMissionData(mission));
                }
                catch(Exception ex)
                {
                    // tooltip(mission + ":" + ex.toString());
                    Log.d("init", ex.toString());
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void gotoActivity(Class<? extends Activity> clazz, boolean finish) {
        Intent intent = new Intent(getActivity(), clazz);
        this.startActivity(intent);
        if (finish) {
            getActivity().finish();
        }
        // getActivity().overridePendingTransition(R.anim.activity_move_in_start, R.anim.activity_move_out_start);

    }

    public void gotoActivity(Class<? extends Activity> clazz, boolean finish, Bundle pBundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }

        this.startActivity(intent);
        if (finish) {
            getActivity().finish();
        }
        // getActivity().overridePendingTransition(R.anim.activity_move_in_start, R.anim.activity_move_out_start);
    }

    @Override
    public void echo(Object receiver, String mission, Object data)
    {
        synchronized(this.missions)
        {
            this.missions.put(mission, data);

            Bundle bundle = new Bundle();
            bundle.putString("mission", mission);
            Message msg = Message.obtain();
            msg.setData(bundle);
            this.handler.sendMessage(msg);
        }
    }

    @Override
    public void complete(String mission, Object data) throws Exception
    {
        // ..
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
        Toast.makeText(this.mContext, message, Toast.LENGTH_SHORT).show();
    }
}
