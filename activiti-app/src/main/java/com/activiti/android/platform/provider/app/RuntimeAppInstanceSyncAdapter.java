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

package com.activiti.android.platform.provider.app;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.model.TaskState;
import com.activiti.android.sdk.services.ServiceRegistry;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.AppDefinitionRepresentation;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.activiti.client.api.model.runtime.request.QueryTasksRepresentation;

public class RuntimeAppInstanceSyncAdapter extends AbstractThreadedSyncAdapter
{
    public static final String ARGUMENT_APP_ID = "appId";

    private final AccountManager mAccountManager;

    private final RuntimeAppInstanceManager runtimeAppInstanceManager;

    private ServiceRegistry api;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public RuntimeAppInstanceSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        runtimeAppInstanceManager = RuntimeAppInstanceManager.getInstance(context);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult)
    {
        Log.d("Activiti", "onPerformSync for account[" + account.name + "]");
        try
        {
            // Retrieve ActivitiAccount
            long accountId = Long.parseLong(AccountManager.get(getContext()).getUserData(account,
                    ActivitiAccount.ACCOUNT_ID));

            if (ActivitiSession.getInstance() != null)
            {
                api = ActivitiSession.getInstance().getServiceRegistry();
            }
            else
            {
                return;
            }

            if (extras.containsKey(ARGUMENT_APP_ID))
            {
                appSync(accountId, extras, authority, provider, syncResult);
            }
            else
            {
                fullSync(accountId, extras, authority, provider, syncResult);
            }
        }
        catch (Exception e)
        {
            Log.e("Activiti", Log.getStackTraceString(e));
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // FULL SYNC
    // ///////////////////////////////////////////////////////////////////////////
    protected void appSync(long accountId, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult)
    {
        // UPDATE MY TASKS ID
        RuntimeAppInstance appInstance = RuntimeAppInstanceManager.getInstance(getContext()).getById(-1L, accountId);
        if (appInstance != null)
        {
            QueryTasksRepresentation request = new QueryTasksRepresentation(null, null, null, null, null,
                    TaskState.OPEN.value(), null, null, null, 0L);
            ResultList<TaskRepresentation> response = api.getTaskService().list(request);
            runtimeAppInstanceManager.update(appInstance.getProviderId(), response.getTotal(), -1, -1);
            syncResult.stats.numUpdates++;
        }

        // Check if simple task or task from appId ?
        long appId = extras.getLong(ARGUMENT_APP_ID, -10);
        if (appId == -1L || appId == -10) { return; }

        // UPDATE APP ID
        appInstance = RuntimeAppInstanceManager.getInstance(getContext()).getById(appId, accountId);
        if (appInstance != null)
        {
            QueryTasksRepresentation request = new QueryTasksRepresentation(appId, null, null, null, null,
                    TaskState.OPEN.value(), null, null, null, 0L);
            ResultList<TaskRepresentation> response = api.getTaskService().list(request);
            runtimeAppInstanceManager.update(appInstance.getProviderId(), response.getTotal(), -1, -1);
            syncResult.stats.numUpdates++;
        }
    }

    protected void fullSync(long accountId, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult)
    {
        ResultList<AppDefinitionRepresentation> appsFromServer = api.getApplicationService().getRuntimeAppDefinitions();

        List<Long> localAppIds = runtimeAppInstanceManager.getIds(accountId);
        if (localAppIds == null)
        {
            localAppIds = new ArrayList<>(0);
        }

        for (AppDefinitionRepresentation app : appsFromServer.getList())
        {
            // Ignore Default APP
            if (!TextUtils.isEmpty(app.getDefaultAppId()))
            {
                continue;
            }
            QueryTasksRepresentation request = new QueryTasksRepresentation(app.getId(), null, null, null, null,
                    TaskState.OPEN.value(), null, null, null, 0L);
            ResultList<TaskRepresentation> response = api.getTaskService().list(request);
            RuntimeAppInstance localApp = runtimeAppInstanceManager.getById(app.getId(), accountId);

            if (localApp == null)
            {
                // CREATE
                runtimeAppInstanceManager.createAppInstance(accountId, app.getId(), app.getName(),
                        Long.toString(app.getModelId()), app.getTheme(), app.getDescription(), app.getIcon(),
                        app.getDeploymentId(), response.getTotal(), -1, -1);
                syncResult.stats.numInserts++;
            }
            else
            {
                // UPDATE
                localAppIds.remove(app.getId());
                runtimeAppInstanceManager.update(localApp.getProviderId(), accountId, app.getId(), app.getName(),
                        Long.toString(app.getModelId()), app.getTheme(), app.getDescription(), app.getIcon(),
                        app.getDeploymentId(), response.getTotal(), -1, -1);
                syncResult.stats.numUpdates++;
            }
            syncResult.stats.numEntries++;
        }

        // Retrieve info for MyTasks
        QueryTasksRepresentation request = new QueryTasksRepresentation(null, null, null, null, null,
                TaskState.OPEN.value(), "no value", null, null, 0L);
        ResultList<TaskRepresentation> response = api.getTaskService().list(request);
        RuntimeAppInstance localApp = runtimeAppInstanceManager.getById(-1L, accountId);
        ContentValues updateValues = new ContentValues();
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_NUMBER_1, response.getTotal());
        getContext().getContentResolver().update(RuntimeAppInstanceManager.getUri(localApp.getProviderId()),
                updateValues, null, null);

        // DELETE
        for (long appId : localAppIds)
        {
            if (appId < 0)
            {
                continue;
            }
            runtimeAppInstanceManager.deleteByAppId(appId);
            syncResult.stats.numDeletes++;
        }
    }
}
