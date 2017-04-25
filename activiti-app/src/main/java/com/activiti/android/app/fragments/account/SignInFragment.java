/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
 *
 *  This file is part of Alfresco Activiti Mobile for Android.
 *
 *  Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package com.activiti.android.app.fragments.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.activiti.android.app.R;
import com.activiti.android.platform.account.AccountsPreferences;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.exception.ExceptionMessageUtils;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.provider.group.GroupInstanceManager;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.integration.IntegrationSyncEvent;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelManager;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.model.runtime.AppVersion;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.constant.ActivitiAPI;
import com.activiti.client.api.model.idm.UserRepresentation;
import com.activiti.client.api.model.runtime.AppVersionRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInFragment extends AlfrescoFragment
{
    public static final String TAG = SignInFragment.class.getName();

    private static final String ARGUMENT_HOSTNAME = "hostnameView";

    private static final String ARGUMENT_HTTPS = "https";

    // UI references.
    private MaterialAutoCompleteTextView mEmailView;

    private EditText mPasswordView;

    private View mProgressView, mFormView;

    private MaterialEditText hostnameView;

    private CheckBox httpsView;

    private String username, password, hostname;

    private boolean https;

    private Uri endpoint;

    private UserRepresentation user;

    private AppVersion version;

    private ActivitiSession session;

    private ActivitiAccount acc;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public SignInFragment()
    {
        super();
        eventBusRequired = true;
    }

    public static SignInFragment newInstanceByTemplate(Bundle b)
    {
        SignInFragment cbf = new SignInFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_account_signin, container, false));
        return getRootView();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null)
        {
            hostname = getArguments().getString(ARGUMENT_HOSTNAME);
            https = getArguments().getBoolean(ARGUMENT_HTTPS);
            String url = ServerFragment.createHostnameURL(hostname, https);
            endpoint = TextUtils.isEmpty(url) ? null : Uri.parse(url);
        }

        // We retrieve emails from accounts.
        mEmailView = (MaterialAutoCompleteTextView) viewById(R.id.username);
        Account[] accounts = AccountManager.get(getActivity()).getAccounts();
        List<String> names = new ArrayList<>(accounts.length);
        String accountName;
        for (int i = 0; i < accounts.length; i++)
        {
            accountName = accounts[i].name;
            if (!TextUtils.isEmpty(accountName) && !names.contains(accountName))
            {
                names.add(accounts[i].name);
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, names);
        mEmailView.setAdapter(adapter);

        mPasswordView = (EditText) viewById(R.id.password);

        Button mEmailSignInButton = (Button) viewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });

        mProgressView = viewById(R.id.login_progress);
        mFormView = viewById(R.id.login_form);

        // Server part
        httpsView = (CheckBox) viewById(R.id.signing_https);
        httpsView.setChecked(https);
        hostnameView = (MaterialEditText) viewById(R.id.signing_hostname);
        hostnameView.setText(hostname);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin()
    {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        hostnameView.setError(null);

        // Store values at the time of the login attempt.
        username = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        hostname = hostnameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the task entered one.
        if (!TextUtils.isEmpty(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid hostname.
        if (TextUtils.isEmpty(hostname))
        {
            hostnameView.setError(getString(R.string.error_field_required));
            focusView = hostnameView;
            cancel = true;
        }

        // Check URL
        String url = ServerFragment.createHostnameURL(hostname, httpsView.isChecked());
        if (TextUtils.isEmpty(url))
        {
            hostnameView.setError(getString(R.string.error_invalid_url));
            focusView = hostnameView;
            cancel = true;
        }
        else
        {
            endpoint = Uri.parse(url);
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            showProgress(true);
            connect();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void connect()
    {
        UIUtils.hideKeyboard(getActivity(), mEmailView);

        session = new ActivitiSession.Builder().connect(endpoint.toString(), username, password).build();
        session.getServiceRegistry().getProfileService().getProfile(new Callback<UserRepresentation>()
        {
            @Override
            public void onResponse(Call<UserRepresentation> call, Response<UserRepresentation> response)
            {
                if (response.isSuccessful())
                {
                    user = response.body();
                    retrieveServerInfo();
                }
                else if (response.code() == 401)
                {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    View focusView = mPasswordView;
                    UIUtils.showKeyboard(getActivity(), focusView);
                }

            }

            @Override
            public void onFailure(Call<UserRepresentation> call, Throwable error)
            {
                View focusView = null;

                showProgress(false);
                if (!ActivitiAPI.SERVER_URL_ENDPOINT.equals(endpoint.toString()))
                {
                    show(R.id.server_form);
                }

                if (focusView == null)
                {
                    int messageId = ExceptionMessageUtils.getSignInMessageId(getActivity(), error.getCause());
                    if (messageId == R.string.error_session_creation)
                    {
                        Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(),
                                Snackbar.LENGTH_SHORT).show();
                    }
                    else
                    {
                        // Revert to Alfresco WebApp
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                                .title(R.string.error_session_creation_title)
                                .cancelListener(new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                dismiss();
                            }
                        }).content(Html.fromHtml(getString(messageId))).positiveText(R.string.ok);
                        builder.show();
                    }
                }
            }
        });
    }

    private void createAccount()
    {
        acc = null;
        // If no version info it means Activiti pre 1.2
        if (version == null)
        {
            acc = ActivitiAccountManager.getInstance(getActivity()).create(username, password, endpoint.toString(),
                    "Activiti Server", "bpmSuite", "Alfresco Activiti Enterprise BPM Suite", "1.1.0",
                    Long.toString(user.getId()), user.getFullname(),
                    (user.getTenantId() != null) ? Long.toString(user.getTenantId()) : null);
        }
        else
        {
            acc = ActivitiAccountManager.getInstance(getActivity()).create(username, password, endpoint.toString(),
                    "Activiti Server", version.type, version.edition, version.getFullVersion(),
                    Long.toString(user.getId()), user.getFullname(),
                    (user.getTenantId() != null) ? Long.toString(user.getTenantId()) : null);
        }

        // Create My Tasks Applications
        RuntimeAppInstanceManager.getInstance(getActivity()).createAppInstance(acc.getId(), -1L, "My Tasks", "", "",
                "Access your full task getProcessInstances and work on any tasks assigned to you from any process app",
                "", "", 0, 0, 0);

        // Set as Default
        AccountsPreferences.setDefaultAccount(getActivity(), acc.getId());

        // Start a sync for integration
        IntegrationManager.sync(getActivity());

        // Analytics
        AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_ACCOUNT,
                AnalyticsManager.ACTION_CREATE, acc.getServerType(), 1, false);
    }

    private void sync()
    {
        // Sync all required Informations
        RuntimeAppInstanceManager.sync(getActivity());
        ProcessDefinitionModelManager.sync(getActivity());
        GroupInstanceManager.sync(getActivity());
    }

    private void retrieveServerInfo()
    {
        session.getServiceRegistry().getInfoService().getInfo(new Callback<AppVersionRepresentation>()
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

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onIntegrationSyncEvent(IntegrationSyncEvent event)
    {
        if (acc == null) { return; }
        sync();

        OptionalFragment.with(getActivity()).acocuntId(acc.getId()).back(false).display();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends AlfrescoFragmentBuilder
    {
        // ///////////////////////////////////////////////////////////////////////////
        // CONSTRUCTORS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder(FragmentActivity activity)
        {
            super(activity);
            this.extraConfiguration = new Bundle();
        }

        public Builder(FragmentActivity appActivity, Map<String, Object> configuration)
        {
            super(appActivity, configuration);
        }

        public Builder hostname(String processId)
        {
            extraConfiguration.putString(ARGUMENT_HOSTNAME, processId);
            return this;
        }

        public Builder https(boolean https)
        {
            extraConfiguration.putBoolean(ARGUMENT_HTTPS, https);
            return this;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        };
    }
}
