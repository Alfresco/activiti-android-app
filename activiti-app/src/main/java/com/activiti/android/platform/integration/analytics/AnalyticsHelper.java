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

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.provider.CursorUtils;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceSchema;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.integration.IntegrationSchema;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelManager;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelSchema;

/**
 * Created by jpascal on 29/01/2016.
 */
public class AnalyticsHelper
{
    public static void reportScreen(Activity activity, String screenName)
    {
        if (AnalyticsManager.getInstance(activity) == null
                || !AnalyticsManager.getInstance(activity).isEnable()) { return; }
        AnalyticsManager.getInstance(activity).reportScreen(activity, screenName);
    }

    public static void reportOperationEvent(Context context, String category, String action, String label, int value,
            boolean hasException)
    {
        if (AnalyticsManager.getInstance(context) == null
                || !AnalyticsManager.getInstance(context).isEnable()) { return; }
        AnalyticsManager.getInstance(context).reportEvent(category, action,
                (hasException) ? AnalyticsManager.LABEL_FAILED : label, value);
    }

    public static void optIn(Activity activity, ActivitiAccount account)
    {
        AnalyticsManager.getInstance(activity).optIn(activity);
        AnalyticsManager.getInstance(activity).startReport(activity);
        try
        {
            // Analytics
            AnalyticsHelper.analyzeAccount(activity, account);
        }
        catch (Exception e)
        {

        }
        AnalyticsManager.getInstance(activity).reportEvent(AnalyticsManager.CATEGORY_SETTINGS,
                AnalyticsManager.ACTION_ANALYTICS, AnalyticsManager.LABEL_ENABLE, 1);
    }

    public static void optOut(Activity activity, ActivitiAccount account)
    {
        AnalyticsManager.getInstance(activity).reportEvent(AnalyticsManager.CATEGORY_SETTINGS,
                AnalyticsManager.ACTION_ANALYTICS, AnalyticsManager.LABEL_DISABLE, 1);
        AnalyticsManager.getInstance(activity).optOut(activity);
    }

    public static void cleanOpt(Activity activity, ActivitiAccount account)
    {
        if (AnalyticsManager.getInstance(activity) == null) { return; }
        AnalyticsManager.getInstance(activity).cleanOptInfo(activity, account);
    }

    protected static HashMap<String, String> retrieveAccountInfo(ActivitiAccount account)
    {
        HashMap<String, String> customDimensions = new HashMap<>();

        if (account != null)
        {
            if (account.getServerUrl() != null && account.getServerUrl().startsWith("https://activiti.alfresco.com/"))
            {
                customDimensions.put(AnalyticsManager.SERVER_TYPE, "Cloud");
            }
            else
            {
                customDimensions.put(AnalyticsManager.SERVER_TYPE, account.getServerType());
            }
            customDimensions.put(AnalyticsManager.SERVER_VERSION, account.getServerVersion());
            customDimensions.put(AnalyticsManager.SERVER_EDITION, account.getServerEdition().substring(0, 36));
        }
        return customDimensions;
    }

    public static void analyzeAccount(Context context, ActivitiAccount account)
    {
        if (account == null) { return; }
        if (AnalyticsManager.getInstance(context) == null
                || !AnalyticsManager.getInstance(context).isEnable()) { return; }
        try
        {
            HashMap<String, String> customDimensions = new HashMap<>();
            HashMap<String, Long> customMetrics = new HashMap<>();

            // Accounts Info
            List<ActivitiAccount> accounts = ActivitiAccountManager.retrieveAccounts(context);
            customMetrics.put(AnalyticsManager.ACCOUNT_NUMBER, (long) accounts.size());
            customMetrics.put(AnalyticsManager.APPS_NUMBER, (long) getNumberOfApps(context, account));
            customMetrics.put(AnalyticsManager.PROCESS_DEFINITION_NUMBER, (long) getNumberOfProcessDefinition(context, account));
            customMetrics.put(AnalyticsManager.ALFRESCO_ACCOUNTS_NUMBER, (long) getNumberOfAlfrescoAccount(context, account));

            // Server Info
            customDimensions = retrieveAccountInfo(account);
            AnalyticsManager.getInstance(context).reportInfo(AnalyticsManager.ACTION_INFO, customDimensions,
                    customMetrics);
        }
        catch (Exception e)
        {
            // Do Nothing
        }
    }

    protected static Integer getNumberOfApps(Context context, ActivitiAccount account)
    {
        if (account == null) { return 0; }
        Integer syncedFolders = 0;
        try
        {
            Cursor syncedCursor = context.getContentResolver().query(RuntimeAppInstanceManager.CONTENT_URI,
                    RuntimeAppInstanceSchema.COLUMN_ALL,
                    RuntimeAppInstanceSchema.COLUMN_ACCOUNT_ID + " = " + account.getId() + "", null, null);
            syncedFolders = syncedCursor != null ? syncedCursor.getCount() : 0;
            CursorUtils.closeCursor(syncedCursor);
        }
        catch (Exception e)
        {
            Log.d("Analytics Folders", Log.getStackTraceString(e));
        }
        return syncedFolders;
    }

    protected static Integer getNumberOfProcessDefinition(Context context, ActivitiAccount account)
    {
        if (account == null) { return 0; }
        Integer syncedFolders = 0;
        try
        {
            Cursor syncedCursor = context.getContentResolver().query(ProcessDefinitionModelManager.CONTENT_URI,
                    ProcessDefinitionModelManager.COLUMN_ALL,
                    ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID + " = " + account.getId() + "", null, null);
            syncedFolders = syncedCursor != null ? syncedCursor.getCount() : 0;
            CursorUtils.closeCursor(syncedCursor);
        }
        catch (Exception e)
        {
            Log.d("Analytics Folders", Log.getStackTraceString(e));
        }
        return syncedFolders;
    }

    protected static Integer getNumberOfAlfrescoAccount(Context context, ActivitiAccount account)
    {
        if (account == null) { return 0; }
        Integer syncedFolders = 0;
        try
        {
            Cursor syncedCursor = context.getContentResolver().query(IntegrationManager.CONTENT_URI,
                    IntegrationManager.COLUMN_ALL,
                    IntegrationSchema.COLUMN_ACTIVITI_ACCOUNT_ID + " = " + account.getId() + "", null, null);
            syncedFolders = syncedCursor != null ? syncedCursor.getCount() : 0;
            CursorUtils.closeCursor(syncedCursor);
        }
        catch (Exception e)
        {
            Log.d("Analytics Folders", Log.getStackTraceString(e));
        }
        return syncedFolders;
    }

}
