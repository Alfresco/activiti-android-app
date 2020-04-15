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

import android.content.Intent;

import androidx.annotation.StringRes;

import com.activiti.android.app.R;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.account.AccountsPreferences;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.provider.group.GroupInstanceManager;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.integration.IntegrationSyncEvent;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelManager;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.model.runtime.AppVersion;
import com.activiti.client.api.model.idm.UserRepresentation;
import com.activiti.client.api.model.runtime.AppVersionRepresentation;
import com.alfresco.auth.AuthConfig;
import com.alfresco.auth.AuthInterceptor;
import com.alfresco.auth.Credentials;
import com.alfresco.auth.activity.LoginActivity;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeSsoActivity extends LoginActivity
{

    private ActivitiAccount acc;
    private ActivitiSession activitiSession;
    private AuthInterceptor authInterceptor;
    private String endpoint;
    private UserRepresentation user;
    private AppVersion version;

    private String username;
    private String authType;
    private AuthConfig authConfig;
    private String authState;

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
    protected void onDestroy() {
        super.onDestroy();

        cleanupSession();
    }

    private void cleanupSession() {
        if (authInterceptor != null) {
            authInterceptor.finish();
            authInterceptor = null;
        }
        activitiSession = null;
    }

    @Override
    public void onCredentials(
            @NotNull Credentials credentials,
            @NotNull String endpoint,
            @NotNull AuthConfig authConfig)
    {
        // ActivitiSession requires the endpoint to end in /
        if (!endpoint.endsWith("/"))
        {
            endpoint = endpoint.concat("/");
        }

        this.endpoint = endpoint;
        this.authConfig = authConfig;
        this.authType = credentials.getAuthType();
        this.username = credentials.getUsername();
        this.authState = credentials.getAuthState();

        connect();
    }

    private void connect() {
        try {
            authInterceptor = new AuthInterceptor(getApplicationContext(), "", authType , authState, authConfig.jsonSerialize());
            activitiSession = new ActivitiSession.Builder()
                    .connect(endpoint)
                    .authInterceptor(authInterceptor)
                    .build();

            activitiSession.getServiceRegistry().getProfileService().getProfile(new Callback<UserRepresentation>() {
                @Override
                public void onResponse(Call<UserRepresentation> call, Response<UserRepresentation> response)
                {
                    if (response.isSuccessful())
                    {
                        user = response.body();
                        retrieveServerInfoIfNecessary();
                    }
                    else if (response.code() == 401)
                    {
                        onConnectError(R.string.auth_error_wrong_credentials);
                    }
                    else
                    {
                        onConnectError(R.string.auth_error_app_incorrect);
                    }
                }

                @Override
                public void onFailure(Call<UserRepresentation> call, Throwable t)
                {
                    onConnectError(R.string.auth_error_app_unreachable);
                }
            });
        } catch(IllegalArgumentException illegalArgumentException) {
            onConnectError(R.string.auth_error_wrong_credentials);
        }
    }

    private void onConnectError(@StringRes  int messageResId) {
        // On connect failure cleanup the session
        cleanupSession();

        onError(messageResId);
    }

    private void retrieveServerInfoIfNecessary()
    {
        if (!getViewModel().isReLogin()) {
            retrieveServerInfo();
        } else {
            updateCurrentAccount();
        }
    }

    private void retrieveServerInfo()
    {
        activitiSession.getServiceRegistry().getInfoService().getInfo(new Callback<AppVersionRepresentation>()
        {
            @Override
            public void onResponse(Call<AppVersionRepresentation> call, Response<AppVersionRepresentation> response)
            {
                if (response.isSuccessful())
                {
                    // BPM Suite 1.2
                    version = new AppVersion(response.body());
                    createAccount();
                }
                else
                {
                    // BPM Suite 1.1
                    version = null;
                    createAccount();
                }
            }

            @Override
            public void onFailure(Call<AppVersionRepresentation> call, Throwable error)
            {
                // BPM Suite 1.1
                version = null;
                createAccount();
            }
        });
    }

    private void createAccount()
    {
        acc = null;

        if (user == null)
        {
            return;
        }

        String userId = user.getId().toString();
        String fullName = user.getFullname();

        String tenantId = (user.getTenantId() != null) ? user.getTenantId().toString() : null;
        String authConfig = this.authConfig.jsonSerialize();


        // If no version info it means Activiti pre 1.2
        if (version == null)
        {
            acc = ActivitiAccountManager.getInstance(this).create(username, authState, authType, authConfig, endpoint,
                    "Activiti Server", "bpmSuite", "Alfresco Activiti Enterprise BPM Suite", "1.1.0",
                    Long.toString(user.getId()), user.getFullname(),
                    (user.getTenantId() != null) ? Long.toString(user.getTenantId()) : null);
        }
        else
        {
            acc = ActivitiAccountManager.getInstance(this).create(username, authState, authType, authConfig, endpoint,
                    "Activiti Server", version.type, version.edition, version.getFullVersion(),
                    Long.toString(user.getId()), user.getFullname(),
                    (user.getTenantId() != null) ? Long.toString(user.getTenantId()) : null);
        }

        // Create My Tasks Applications
        RuntimeAppInstanceManager.getInstance(this).createAppInstance(acc.getId(), -1L, "My Tasks", "", "",
                "Access your full task getProcessInstances and work on any tasks assigned to you from any process app",
                "", "", 0, 0, 0);

        // Set as Default
        AccountsPreferences.setDefaultAccount(this, acc.getId());

        // Start a sync for integration
        IntegrationManager.sync(this);

        // Analytics
        AnalyticsHelper.reportOperationEvent(this, AnalyticsManager.CATEGORY_ACCOUNT,
                AnalyticsManager.ACTION_CREATE, acc.getServerType(), 1, false);
    }

    private void updateCurrentAccount()
    {
        acc = ActivitiAccountManager.getInstance(this).getCurrentAccount();
        if (acc != null)
        {
            ActivitiAccountManager.getInstance(this).update(this, acc.getId(), username, authState);
            IntegrationManager.sync(this);
        }
        else
        {
            onError("Illegal argument");
        }
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
        if (acc == null) { return; }
        sync();

        getViewModel().isLoading().setValue(false);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        EventBusManager.getInstance().unregister(this);
        finish();
    }
}
