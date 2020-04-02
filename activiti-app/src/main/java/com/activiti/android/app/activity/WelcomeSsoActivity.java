package com.activiti.android.app.activity;

import android.content.Intent;
import android.widget.Toast;

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
import com.alfresco.client.AbstractClient.AuthType;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeSsoActivity extends LoginActivity {

    private ActivitiAccount acc;
    private ActivitiSession activitiSession;
    private String endpoint;
    private UserRepresentation user;
    private AppVersion version;

    private String username;
    private String password;
    private AuthType authType;
    private AuthConfig authConfig;
    private String authState;

    @Override
    protected void onStart() {
        super.onStart();
        EventBusManager.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusManager.getInstance().unregister(this);
    }

    @Override
    public void onCredentials(
            @NotNull Credentials credentials,
            @NotNull String endpoint,
            @NotNull AuthConfig authConfig)
    {
        this.endpoint = endpoint;
        this.authConfig = authConfig;

        if (credentials instanceof Credentials.Basic) {
            Credentials.Basic it = (Credentials.Basic) credentials;
            username = it.getUsername();
            password = it.getPassword();
            authType = AuthType.BASIC;
        } else if (credentials instanceof Credentials.Sso) {
            Credentials.Sso it = (Credentials.Sso) credentials;
            username = it.getUsername();
            authState = it.getAuthState();
            authType = AuthType.TOKEN;
        }

        connect();
    }

    private void connect() {
        onLoading(true);

        try {
            AbstractClient.Builder<ActivitiSession> sessionBuilder =
                    new ActivitiSession.Builder().connect(endpoint, username, password, authType);
            if (authType == AuthType.TOKEN) {
                AuthInterceptor interceptor = new AuthInterceptor(getApplicationContext(), authState, authConfig);
                sessionBuilder = sessionBuilder.interceptor(interceptor);
            }
            activitiSession = sessionBuilder.build();
            activitiSession.getServiceRegistry().getProfileService().getProfile(new Callback<UserRepresentation>() {
                @Override
                public void onResponse(Call<UserRepresentation> call, Response<UserRepresentation> response) {
                    if (response.isSuccessful()) {
                        user = response.body();
                        retrieveServerInfo();
                    } else if (response.code() == 401) {
                        Toast.makeText(WelcomeSsoActivity.this, "Get Profile failed! 401", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserRepresentation> call, Throwable t) {
                    Toast.makeText(WelcomeSsoActivity.this, "Get Profile failed!", Toast.LENGTH_LONG).show();
                }
            });
        } catch(IllegalArgumentException illegalArgumentException) {
            Toast.makeText(WelcomeSsoActivity.this, "Get Profile failed!", Toast.LENGTH_LONG).show();
        }
    }

    private void retrieveServerInfo() {
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

    private void createAccount() {
        acc = null;

        if (user == null) {
            return;
        }

        String userId = user.getId().toString();
        String fullName = user.getFullname();

        String tenantId = (user.getTenantId() != null) ? user.getTenantId().toString() : null;
        String authConfig = new Gson().toJson(this.authConfig);


        // If no version info it means Activiti pre 1.2
        if (version == null)
        {
            acc = ActivitiAccountManager.getInstance(this).create(username, password, authState, authType.getValue(), authConfig, endpoint,
                    "Activiti Server", "bpmSuite", "Alfresco Activiti Enterprise BPM Suite", "1.1.0",
                    Long.toString(user.getId()), user.getFullname(),
                    (user.getTenantId() != null) ? Long.toString(user.getTenantId()) : null);
        }
        else
        {
            acc = ActivitiAccountManager.getInstance(this).create(username, password, authState, authType.getValue(), authConfig, endpoint,
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

        onLoading(false);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        EventBusManager.getInstance().unregister(this);
        finish();
//        OptionalFragment.with(this).acocuntId(acc.getId()).back(false).display();
    }
}
