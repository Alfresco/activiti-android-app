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

package com.activiti.android.platform.provider.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.activiti.android.app.BuildConfig;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.integration.alfresco.AlfrescoIntegrator;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.services.ServiceRegistry;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.integration.dto.AlfrescoEndpointRepresentation;
import com.alfresco.client.utils.ISO8601Utils;

public class IntegrationSyncAdapter extends AbstractThreadedSyncAdapter
{

    private final AccountManager mAccountManager;

    private final IntegrationManager integrationManager;

    private ServiceRegistry api;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public IntegrationSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        integrationManager = IntegrationManager.getInstance(context);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult)
    {
        Log.d("Activiti", "onPerformSync Integration for account[" + account.name + "]");
        try
        {
            // Retrieve ActivitiAccount
            long accountId = Long
                    .parseLong(AccountManager.get(getContext()).getUserData(account, ActivitiAccount.ACCOUNT_ID));

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
            ResultList<AlfrescoEndpointRepresentation> repositories = api.getProfileService().getAlfrescoRepositories();
            if (repositories == null)
            {
                EventBusManager.getInstance().post(new IntegrationSyncEvent("10"));
                return;
            }
            List<AlfrescoEndpointRepresentation> alfrescoEndpoints = repositories.getList();

            // Retrieve Local Data
            Map<Long, Integration> localModelIds = integrationManager.getByAccountId(accountId);
            if (localModelIds == null)
            {
                localModelIds = new HashMap<>(0);
            }

            for (AlfrescoEndpointRepresentation alfrescoEndpoint : alfrescoEndpoints)
            {
                Integration localIntegration = integrationManager.getById(alfrescoEndpoint.getId(), accountId);
                if (localIntegration == null)
                {
                    // Let's find if Alfresco account
                    Account selectedAccount = null;
                    Account[] accounts = AccountManager.get(getContext())
                            .getAccountsByType(AlfrescoIntegrator.ALFRESCO_ACCOUNT_TYPE);
                    String alfrescoUsername = null, alfrescoAccountName = null;
                    int integration = Integration.OPEN_UNDEFINED;
                    Long alfrescoId = -1L;
                    if (accounts.length > 0)
                    {
                        selectedAccount = retrieveAlfrescoAccount(getContext(), alfrescoEndpoint.getAccountUsername(),
                                alfrescoEndpoint.getRepositoryUrl());
                        if (selectedAccount != null)
                        {
                            alfrescoId = Long.parseLong(AccountManager.get(getContext()).getUserData(selectedAccount,
                                    BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".id")));
                            alfrescoUsername = AccountManager.get(getContext()).getUserData(selectedAccount,
                                    BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".username"));
                            alfrescoAccountName = AccountManager.get(getContext()).getUserData(selectedAccount,
                                    BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".name"));
                            integration = Integration.OPEN_NATIVE_APP;
                        }
                    }

                    // CREATE
                    integrationManager.createIntegration(alfrescoEndpoint.getId(), alfrescoEndpoint.getName(),
                            alfrescoEndpoint.getAccountUsername(), alfrescoEndpoint.getTenantId(),
                            alfrescoEndpoint.getAlfrescoTenantId(), ISO8601Utils.format(alfrescoEndpoint.getCreated()),
                            ISO8601Utils.format(alfrescoEndpoint.getLastUpdated()), alfrescoEndpoint.getShareUrl(),
                            alfrescoEndpoint.getRepositoryUrl(), accountId, alfrescoId, alfrescoAccountName,
                            alfrescoUsername, integration);
                    syncResult.stats.numInserts++;
                }
                else
                {
                    // UPDATE
                    localModelIds.remove(localIntegration.getProviderId());
                    integrationManager.update(localIntegration.getProviderId(), localIntegration.getId(),
                            alfrescoEndpoint.getName(), alfrescoEndpoint.getAccountUsername(),
                            alfrescoEndpoint.getTenantId(), alfrescoEndpoint.getAlfrescoTenantId(),
                            ISO8601Utils.format(alfrescoEndpoint.getCreated()),
                            ISO8601Utils.format(alfrescoEndpoint.getLastUpdated()), alfrescoEndpoint.getShareUrl(),
                            alfrescoEndpoint.getRepositoryUrl(), accountId, localIntegration.getAlfrescoAccountId(),
                            localIntegration.getAlfrescoName(), localIntegration.getAlfrescoUsername(),
                            localIntegration.openType);
                    syncResult.stats.numUpdates++;
                }
                syncResult.stats.numEntries++;
            }

            // DELETE
            for (long modelId : localModelIds.keySet())
            {
                if (modelId < 0)
                {
                    continue;
                }
                integrationManager.deleteById(accountId, modelId);
                syncResult.stats.numDeletes++;
            }
        }
        catch (Exception e)
        {
            Log.e("Activiti", Log.getStackTraceString(e));
        }
        EventBusManager.getInstance().post(new IntegrationSyncEvent("10"));

    }

    public static Account retrieveAlfrescoAccount(Context context, String accountUsername, String repoUrl)
    {
        if (accountUsername == null)
        {
            return null;
        }

        // Let's find if Alfresco account
        Account selectedAccount = null;
        Account[] accounts = AccountManager.get(context).getAccountsByType(AlfrescoIntegrator.ALFRESCO_ACCOUNT_TYPE);
        if (accounts.length > 0)
        {
            for (int i = 0; i < accounts.length; i++)
            {
                String username = AccountManager.get(context).getUserData(accounts[i],
                        BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".username"));

                if (accountUsername.equals(username))
                {
                    // Lets compare hostname
                    String alfUrl = AccountManager.get(context).getUserData(accounts[i],
                            BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".url"));
                    Uri alfUri = Uri.parse(alfUrl);
                    Uri activitiUri = Uri.parse(repoUrl);

                    if (alfUri != null && activitiUri != null && alfUri.getHost().equals(activitiUri.getHost()))
                    {
                        // We found one !
                        selectedAccount = accounts[i];
                        break;
                    }
                }
            }
        }
        return selectedAccount;
    }
}
