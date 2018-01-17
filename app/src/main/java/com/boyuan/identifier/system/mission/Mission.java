package com.boyuan.identifier.system.mission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.boyuan.identifier.BaseActivity;
import com.boyuan.identifier.BaseFragment;
import com.boyuan.identifier.system.page.Callbackable;

public abstract class Mission extends Thread
{
	private String mission = null;
	protected Callbackable receiver = null;

	public Mission(String mission, Callbackable receiver)
	{
		this.mission = mission;
		this.receiver = receiver;
	}
	
	protected void reportProgress(float percent)
	{
		try
		{
			this.getReceiver().echo(this.receiver, "progress:" + this.mission, (percent));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	protected Context getContext()
	{
		if (this.receiver instanceof BaseActivity) return (Context)this.receiver;
		else if (this.receiver instanceof BaseFragment) return ((BaseFragment)this.receiver).getActivity();
		else return null;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	protected boolean isCallbackable()
	{
		Activity page = (Activity)this.getContext();
		return !page.isFinishing();
	}

	protected Callbackable getReceiver()
	{
		Context context = getContext();
		if (null == context) return null;
		else return ((Callbackable)context);
	}

	public Object getData(String field)
	{
		return null;
	}
	
	public final void run()
	{
		boolean callback = true;
		try
		{
			Object data = this.handle();
			this.getReceiver().echo(this.receiver, this.mission, data);
		}
		catch(Exception ex)
		{
			this.getReceiver().echo(this.receiver, this.mission, ex);
		}
	}
	
	public final void go()
	{
		this.start();
	}
	
	public abstract Object handle() throws Exception;
}
