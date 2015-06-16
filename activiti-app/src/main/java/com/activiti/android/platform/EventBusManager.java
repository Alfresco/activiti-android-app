/*
 *  Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco Activiti Mobile for Android.
 *
 * Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.activiti.android.platform;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class EventBusManager extends Bus
{
    protected static final Object LOCK = new Object();

    protected static EventBusManager mInstance;

    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public EventBusManager()
    {

    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public static EventBusManager getInstance()
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new EventBusManager();
            }

            return mInstance;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void unregister(Object object)
    {
        try
        {
            super.unregister(object);
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public void post(final Object event)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            super.post(event);
        }
        else
        {
            mainThread.post(new Runnable()
            {
                @Override
                public void run()
                {
                    post(event);
                }
            });
        }
    }
}
