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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalyticsManagerImpl extends AnalyticsManager
{
    private FirebaseAnalytics analytics;

    private boolean hasOptOut = false;

    protected SharedPreferences.Editor editor;

    protected Integer status = null;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public static FirebaseAnalyticsManagerImpl getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new FirebaseAnalyticsManagerImpl(context);
            }

            return (FirebaseAnalyticsManagerImpl) mInstance;
        }
    }

    protected FirebaseAnalyticsManagerImpl(Context context)
    {
        super(context);
        analytics = FirebaseAnalytics.getInstance(context);
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
    }

    @Override
    public void reportScreen(Activity activity, String name)
    {
        analytics.setCurrentScreen(activity, name, null);
    }

    @Override
    public void reportEvent(String category, String action, String label, int value)
    {
        Bundle bundle = new Bundle();
        bundle.putString("Category", category);
        bundle.putString("Label", label);
        analytics.logEvent(action, bundle);
    }

    @Override
    public void reportInfo(String label, HashMap<String, String> dimensions, HashMap<String, Long> metrics) {
        for (Map.Entry<String, String> entry : dimensions.entrySet()) {
            analytics.setUserProperty(entry.getKey(), entry.getValue());
        }

        Bundle bundle = new Bundle();
        for (Map.Entry<String, Long> entry : metrics.entrySet()) {
            bundle.putLong(entry.getKey(), entry.getValue());
        }
        analytics.logEvent("Info", bundle);
    }

    @Override
    public void reportError(boolean isFatal, String description)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsFatal", isFatal);
        bundle.putString("Description", description);
        analytics.logEvent("Error", bundle);
    }
}
