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

package com.activiti.android.platform.provider.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.services.ServiceRegistry;
import com.activiti.client.api.model.idm.GroupRepresentation;
import com.activiti.client.api.model.idm.UserRepresentation;

public class GroupInstanceSyncAdapter extends AbstractThreadedSyncAdapter
{

    private final AccountManager mAccountManager;

    private final GroupInstanceManager groupInstanceManager;

    private ServiceRegistry api;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public GroupInstanceSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        groupInstanceManager = GroupInstanceManager.getInstance(context);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult)
    {
        Log.d("Activiti", "onPerformSync GroupInstance for account[" + account.name + "]");
        try
        {
            // Retrieve ActivitiAccount
            long accountId = Long.parseLong(AccountManager.get(getContext()).getUserData(account,
                    ActivitiAccount.ACCOUNT_ID));

            // Retrieve Applications from Server
            if (ActivitiSession.getInstance() != null)
            {
                api = ActivitiSession.getInstance().getServiceRegistry();
            }
            else
            {
                return;
            }
            UserRepresentation user = api.getProfileService().getProfile();
            List<GroupRepresentation> groups = user.getGroups();

            // Retrieve Local Data
            Map<Long, GroupInstance> localModelIds = groupInstanceManager.getAllByAccountId(accountId);
            if (localModelIds == null)
            {
                localModelIds = new HashMap<>(0);
            }

            for (GroupRepresentation group : groups)
            {
                GroupInstance localGroup = groupInstanceManager.getById(group.getId(), accountId);
                if (localGroup == null)
                {
                    // CREATE
                    groupInstanceManager.createGroup(group.getId(), accountId, group.getName(), group.getType(),
                            group.getParentGroupId(), group.getStatus(), group.getExternalId());
                    syncResult.stats.numInserts++;
                }
                else
                {
                    // UPDATE
                    localModelIds.remove(localGroup.getProviderId());
                    groupInstanceManager.update(localGroup.getProviderId(), group.getId(), accountId, group.getName(),
                            group.getType(), group.getParentGroupId(), group.getStatus(), group.getExternalId());
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
                groupInstanceManager.deleteByProviderId(modelId);
                syncResult.stats.numDeletes++;
            }
        }
        catch (Exception e)
        {
            Log.e("Activiti", Log.getStackTraceString(e));
        }
    }
}
