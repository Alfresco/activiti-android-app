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

package com.activiti.android.platform.provider.processdefinition;

import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.services.ServiceRegistry;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.AppDefinitionRepresentation;
import com.activiti.client.api.model.runtime.ProcessDefinitionRepresentation;

public class ProcessDefinitionModelSyncAdapter extends AbstractThreadedSyncAdapter
{

    private final AccountManager mAccountManager;

    private final ProcessDefinitionModelManager processDefinitionModelManager;

    private ServiceRegistry api;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessDefinitionModelSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        processDefinitionModelManager = ProcessDefinitionModelManager.getInstance(context);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult)
    {
        Log.d("Activiti", "onPerformSync ProcessDefinitionModel for account[" + account.name + "]");
        try
        {
            // Retrieve ActivitiAccount
            long accountId = Long.parseLong(AccountManager.get(getContext()).getUserData(account,
                    ActivitiAccount.ACCOUNT_ID));

            // Retrieve Applications from Server
            ActivitiSession session = ActivitiSession.with(String.valueOf(accountId));
            if (session != null)
            {
                api = session.getServiceRegistry();
            }
            else
            {
                return;
            }

            ResultList<AppDefinitionRepresentation> appsFromServer = api.getApplicationService()
                    .getRuntimeAppDefinitions();

            // Retrieve Local Data
            Map<Long, ProcessDefinitionModel> localModelIds = processDefinitionModelManager
                    .getAllByAccountId(accountId);

            for (AppDefinitionRepresentation app : appsFromServer.getList())
            {
                // Ignore Default APP
                if (!TextUtils.isEmpty(app.getDefaultAppId()))
                {
                    continue;
                }

                ResultList<ProcessDefinitionRepresentation> processes = api.getProcessDefinitionService()
                        .getProcessDefinitions(app.getId());
                for (ProcessDefinitionRepresentation model : processes.getList())
                {
                    ProcessDefinitionModel localModel = processDefinitionModelManager.getById(model.getId(), accountId,
                            app.getId());
                    if (localModel == null)
                    {
                        // CREATE
                        processDefinitionModelManager.createProcessDefinitionModel(model.getId(), accountId,
                                app.getId(), model.getName(), model.getDescription(), model.getVersion(),
                                model.hasStartFormKey());
                        syncResult.stats.numInserts++;
                    }
                    else
                    {
                        // UPDATE
                        localModelIds.remove(localModel.getProviderId());
                        processDefinitionModelManager.update(localModel.getProviderId(), model.getId(), accountId,
                                app.getId(), model.getName(), model.getDescription(), model.getVersion(),
                                model.hasStartFormKey());
                        syncResult.stats.numUpdates++;
                    }
                    syncResult.stats.numEntries++;
                }
            }

            // DELETE
            for (long modelId : localModelIds.keySet())
            {
                if (modelId < 0)
                {
                    continue;
                }
                processDefinitionModelManager.deleteByProviderId(modelId);
                syncResult.stats.numDeletes++;
            }
        }
        catch (Exception e)
        {
            Log.e("Activiti", Log.getStackTraceString(e));
        }
    }
}
