/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
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
package com.activiti.android.platform.integration.analytics;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.SparseArray;

import com.activiti.android.app.R;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class GAnalyticsManagerImpl extends AnalyticsManager
{
    private Tracker mTracker;

    private GoogleAnalytics analytics;

    private boolean hasOptOut = false;

    private boolean dispatchManually = false;

    protected SharedPreferences.Editor editor;

    protected Integer status = null;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public static GAnalyticsManagerImpl getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new GAnalyticsManagerImpl(context);
            }

            return (GAnalyticsManagerImpl) mInstance;
        }
    }

    protected GAnalyticsManagerImpl(Context context)
    {
        super(context);
        analytics = GoogleAnalytics.getInstance(context);
        // Set it via resource to support override mechanism
        mTracker = analytics.newTracker(context.getString(R.string.ga_trackingId));
        mTracker.setSampleRate(Double.parseDouble(context.getResources().getString(R.string.ga_sampleFrequency)));
        mTracker.enableAutoActivityTracking(context.getResources().getBoolean(R.bool.ga_autoActivityTracking));
        mTracker.enableExceptionReporting(context.getResources().getBoolean(R.bool.ga_reportUncaughtExceptions));
        mTracker.setSessionTimeout(context.getResources().getInteger(R.integer.ga_sessionTimeout));

        dispatchManually = context.getResources().getBoolean(R.bool.ga_manualDispatch);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // O
    // ///////////////////////////////////////////////////////////////////////////
    public void optIn(Activity activity)
    {
        setStatus(STATUS_ENABLE);
        status = getStatus();
    }

    public void optOut(Activity activity)
    {
        setStatus(STATUS_DISABLE);
        status = getStatus();
    }

    public void cleanOptInfo(Context context, ActivitiAccount account)
    {
        if (editor == null)
        {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        }
        if (account != null)
        {
            editor.remove(ANALYTICS_PREFIX + account.getId());
            editor.apply();
        }
        status = getStatus();
    }

    private void opt(Context context, int value, ActivitiAccount account)
    {
        if (editor == null)
        {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        }
        if (account != null)
        {
            editor.putInt(ANALYTICS_PREFIX + account.getId(), value);
            editor.apply();
        }
    }

    public boolean isEnable()
    {
        if (status == null)
        {
            getStatus();
        }
        return status == STATUS_ENABLE;
    }

    public boolean isEnable(ActivitiAccount account)
    {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getInt(ANALYTICS_PREFIX + account.getId(),
                STATUS_ENABLE) == STATUS_ENABLE;
    }

    public boolean isBlocked(ActivitiAccount account)
    {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getInt(ANALYTICS_PREFIX + account.getId(),
                STATUS_ENABLE) == STATUS_BLOCKED;
    }

    public boolean isBlocked()
    {
        if (status == null)
        {
            getStatus();
        }
        return status == STATUS_BLOCKED;
    }

    private int getStatus()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext);
        List<ActivitiAccount> accounts = ActivitiAccountManager.retrieveAccounts(appContext);
        int tempStatus = STATUS_ENABLE;
        Integer resultStatus = null;
        for (ActivitiAccount account : accounts)
        {
            tempStatus = sharedPref.getInt(ANALYTICS_PREFIX + account.getId(), STATUS_ENABLE);
            switch (tempStatus)
            {
                case STATUS_BLOCKED:
                    status = STATUS_BLOCKED;
                    return STATUS_BLOCKED;
                case STATUS_DISABLE:
                    resultStatus = STATUS_DISABLE;
                    break;
                default:
                    continue;
            }
        }
        status = resultStatus != null ? resultStatus : STATUS_ENABLE;
        return status;
    }

    private void setStatus(int status)
    {
        List<ActivitiAccount> accounts = ActivitiAccountManager.retrieveAccounts(appContext);
        for (ActivitiAccount account : accounts)
        {
            opt(appContext, status, account);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // REPORT
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void startReport(Activity activity)
    {
        analytics.enableAutoActivityReports(activity.getApplication());
        mTracker.send(new HitBuilders.ScreenViewBuilder().setNewSession().build());
    }

    @Override
    public void reportScreen(String name)
    {
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (dispatchManually)
        {
            analytics.dispatchLocalHits();
        }
    }

    @Override
    public void reportEvent(String category, String action, String label, int value)
    {
        mTracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label)
                .setValue(value).build());
        if (dispatchManually)
        {
            analytics.dispatchLocalHits();
        }
    }

    @Override
    public void reportEvent(String category, String action, String label, int value, int customMetricId,
            Long customMetricValue)
    {
        mTracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label)
                .setValue(value).setCustomMetric(customMetricId, customMetricValue).build());
        if (dispatchManually)
        {
            analytics.dispatchLocalHits();
        }
    }

    @Override
    public void reportInfo(String label, SparseArray<String> dimensions, SparseArray<Long> metrics)
    {
        reportEvent(CATEGORY_SESSION, ACTION_INFO, label, 1, dimensions, metrics);
    }

    @Override
    public void reportEvent(String category, String action, String label, int eventValue,
            SparseArray<String> dimensions, SparseArray<Long> metrics)
    {
        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder().setCategory(category).setAction(action)
                .setLabel(label).setValue(eventValue);

        if (dimensions != null)
        {
            for (int i = 0; i < dimensions.size(); i++)
            {
                int dimensionId = dimensions.keyAt(i);
                String value = dimensions.get(dimensionId);
                builder.setCustomDimension(dimensionId, value);
            }
        }

        if (metrics != null)
        {
            for (int i = 0; i < metrics.size(); i++)
            {
                int dimensionId = metrics.keyAt(i);
                Long value = metrics.get(dimensionId);
                builder.setCustomMetric(dimensionId, value);
            }
        }

        mTracker.send(builder.build());
        if (dispatchManually)
        {
            analytics.dispatchLocalHits();
        }
    }

    @Override
    public void reportError(boolean isFatal, String description)
    {
        mTracker.send(new HitBuilders.ExceptionBuilder().setFatal(isFatal).setDescription(description).build());
        if (dispatchManually)
        {
            analytics.dispatchLocalHits();
        }
    }

    public void enableManualDispatch(boolean enable)
    {
        dispatchManually = enable;
    }
}
