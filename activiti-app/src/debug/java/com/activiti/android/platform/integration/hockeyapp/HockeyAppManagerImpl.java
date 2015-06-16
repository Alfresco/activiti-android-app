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

package com.activiti.android.platform.integration.hockeyapp;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;

import android.app.Activity;
import android.content.Context;

import com.activiti.android.app.R;

/**
 * Created by jpascal on 01/04/2015.
 */
public class HockeyAppManagerImpl extends HockeyAppManager
{
    protected final String appID;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public static HockeyAppManagerImpl getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new HockeyAppManagerImpl(context);
            }

            return (HockeyAppManagerImpl) mInstance;
        }
    }

    protected HockeyAppManagerImpl(Context applicationContext)
    {
        super(applicationContext);
        appID = applicationContext.getString(R.string.hockeyapp_key);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public void checkForUpdates(Activity activity)
    {
        UpdateManager.register(activity, appID);
    }

    public void checkForCrashes(Activity activity)
    {
        CrashManager.register(activity, appID);
    }

    public void showFeedbackActivity(Activity activity)
    {
        FeedbackManager.register(activity, appID, null);
        FeedbackManager.showFeedbackActivity(activity);
    }
}
