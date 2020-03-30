package com.activiti.android.app.activity;

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

public class ReloginSsoActivity extends ReloginActivity {

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
    public void onCredentials(@NotNull Credentials credentials) {
        String authState = null;
        if (credentials instanceof Credentials.Sso) {
            Credentials.Sso t = (Credentials.Sso) credentials;
            authState = t.getAuthState();
        }

        ActivitiAccount acc = ActivitiAccountManager.getInstance(this).getCurrentAccount();
        if (acc != null && authState != null) {
            ActivitiAccountManager.getInstance(this).update(this, acc.getId(), authState);
            IntegrationManager.sync(this);
        } else {
            onError("Illegal argument");
        }
    }

    @Override
    public void onError(@NotNull String s) {
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
        finish();
    }
}
