package com.boyuan.identifier.system.mission;

import com.boyuan.identifier.system.page.Callbackable;

/**
 * Created by houcheng on 2016/8/17.
 */
public class Timeout extends Mission
{
    int timeout = 0;
    public Timeout(Callbackable receiver, String mission, int timeout)
    {
        super(mission, receiver);
        this.timeout = timeout;
    }

    @Override
    public Object handle() throws Exception
    {
        Thread.sleep(timeout);
        return null;
    }
}
