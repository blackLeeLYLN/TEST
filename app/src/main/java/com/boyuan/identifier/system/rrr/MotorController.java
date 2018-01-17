package com.boyuan.identifier.system.rrr;

import java.io.FileWriter;

// import com.boyuan.identifier.common.Callbackable;
// import com.boyuan.identifier.common.Configuration;

import android.app.Activity;
public class MotorController
{
	private Activity context = null;
	private int steps = -1;

	private final int STEPS_1X = 64;
	private final int STEPS_2X = 178;
	private final int MAX_STEPS = STEPS_2X + 48;

	private final String STEP_ZOOM_IN = "8";
	private final String STEP_ZOOM_OUT = "7";

	private int getSteps()
	{
		// this.steps = Configuration.getInstance(this.context).getInt("steps", 0);
		return this.steps;
	}

	public MotorController(Activity context)
	{
		this.context = context;
		// this.steps = Configuration.getInstance(context).getInt("steps", 0);
	}

	public void zoomIn()
	{
		this.steps += 1;
		if (this.steps > MAX_STEPS) this.steps = MAX_STEPS;
		move(STEP_ZOOM_IN);
	}

	public void zoomOut()
	{
		this.steps -= 1;
		if (this.steps < 0) this.steps = 0;
		move(STEP_ZOOM_OUT);
	}

	private void move(String dir)
	{
		FileWriter writer = null;

		try
		{
			writer = new FileWriter("/proc/remote_stm32/motor");
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

	// 移焦到第1个倍率
	public void to1X()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				if (steps == STEPS_1X) return;
				int count = steps - STEPS_1X;
				for (int i = 0, l = Math.abs(count); i < l; i++)
				{
					try
					{
						if (count > 0) zoomOut();
						else zoomIn();
						Thread.sleep(20);
					}
					catch(Exception e) { }
				}

				// ((Callbackable)context).echo("zoom", "1x");
			}
		}).start();
	}

	// 移焦到第2个倍率
	public void to2X()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				if (steps == STEPS_2X) return;
				int count = steps - STEPS_2X;
				for (int i = 0, l = Math.abs(count); i < l; i++)
				{
					try
					{
						if (count > 0) zoomOut();
						else zoomIn();
						Thread.sleep(20);
					}
					catch(Exception e) { }
				}

				// ((Callbackable)context).echo("zoom", "2x");
			}
		}).start();
	}

	// 自动校准
	public void reset()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					// 电机移动200步，回到最大焦距
					for (int i = 0; i < 200; i++)
					{
						zoomOut();
						Thread.sleep(20);
					}

					// ((Callbackable)context).echo("zoom", "zero");
				}
				catch(Exception ex)
				{

				}
			}
		}).start();
	}
}
