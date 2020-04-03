/*
 *  Copyright (C) 2005-2020 Alfresco Software Limited.
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

package com.activiti.android.app.activity;

import android.app.Activity;
import android.content.Intent;

import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.provider.group.GroupInstanceManager;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.integration.IntegrationSyncEvent;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelManager;
import com.alfresco.auth.Credentials;
import com.alfresco.auth.activity.ReloginActivity;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;

public class ReloginSsoActivity extends ReloginActivity
{

    @Override
    protected void onStart()
    {
        super.onStart();
        EventBusManager.getInstance().register(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        EventBusManager.getInstance().unregister(this);
    }

    @Override
    public void onCredentials(@NotNull Credentials credentials)
    {
        String authState = null;
        String username = null;
        if (credentials instanceof Credentials.Sso)
        {
            Credentials.Sso t = (Credentials.Sso) credentials;
            username = t.getUsername();
            authState = t.getAuthState();
        }

        ActivitiAccount acc = ActivitiAccountManager.getInstance(this).getCurrentAccount();
        if (acc != null && authState != null)
        {
            ActivitiAccountManager.getInstance(this).update(this, acc.getId(), username, authState);
            IntegrationManager.sync(this);
        }
        else
        {
            onError("Illegal argument");
        }
    }

    @Override
    public void onError(@NotNull String s)
    {
        finish();
    }

    private void sync()
    {
        // Sync all required Informations
        RuntimeAppInstanceManager.sync(this);
        ProcessDefinitionModelManager.sync(this);
        GroupInstanceManager.sync(this);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onIntegrationSyncEvent(IntegrationSyncEvent event)
    {
        sync();

        onLoading(false);

        EventBusManager.getInstance().unregister(this);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        finish();
    }
}
