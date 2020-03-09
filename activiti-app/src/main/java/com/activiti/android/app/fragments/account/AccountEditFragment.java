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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
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
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.LeafFragmentBuilder;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.constant.ActivitiAPI;
import com.activiti.client.api.model.idm.UserRepresentation;
import com.activiti.client.api.model.runtime.AppVersionRepresentation;
import com.alfresco.client.AbstractClient.AuthType;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

public class AccountEditFragment extends AlfrescoFragment
{
    public static final String TAG = AccountEditFragment.class.getName();

    public static final String ARGUMENT_ACCOUNT_ID = "accountId";

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

    private Long accountId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AccountEditFragment()
    {
        super();
        eventBusRequired = true;
        setHasOptionsMenu(true);
    }

    public static AccountEditFragment newInstanceByTemplate(Bundle b)
    {
        AccountEditFragment cbf = new AccountEditFragment();
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
        if (getActivity() instanceof MainActivity)
        {
            UIUtils.displayActionBarBack((MainActivity) getActivity());
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (getActivity() instanceof MainActivity)
        {
            UIUtils.setActionBarDefault((MainActivity) getActivity());
        }

        if (DisplayUtils.hasCentralPane(getActivity()))
        {
            getToolbar().setTitle(null);
            getToolbar().setSubtitle(null);
            getToolbar().getMenu().clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            accountId = getArguments().getLong(ARGUMENT_ACCOUNT_ID);
        }
        acc = ActivitiAccountManager.getInstance(getActivity()).getByAccountId(accountId);
        Uri serverUri = Uri.parse(acc.getServerUrl());

        if (serverUri.getPath().equals("/activiti-app"))
        {
            // It's default url
            https = ("https".equals(serverUri.getScheme().toLowerCase()));
            hostname = serverUri.getAuthority();
        }
        else
        {
            // It's not default we display full url in hostname
            hostname = acc.getServerUrl();
            hide(R.id.signing_https);
        }

        // TITLE
        TextView tv = (TextView) viewById(R.id.signin_title);
        tv.setText(R.string.settings_userinfo_account_summary);

        // USERNAME
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
        mEmailView.setText(acc.getUsername());

        // PASSWORD
        mPasswordView = (EditText) viewById(R.id.password);
        mPasswordView.setText(acc.getPassword());

        Button mEmailSignInButton = (Button) viewById(R.id.email_sign_in_button);
        mEmailSignInButton.setText(R.string.general_action_confirm);
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

        show(R.id.server_form);
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
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password))
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

    private boolean isPasswordValid(String password)
    {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
.setListener(new AnimatorListenerAdapter()
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

        session = new ActivitiSession.Builder().connect(endpoint.toString(), username, password, AuthType.BASIC).build();
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
                else
                {
                    View focusView = null;

                    showProgress(false);
                    if (!ActivitiAPI.SERVER_URL_ENDPOINT.equals(endpoint.toString()))
                    {
                        show(R.id.server_form);
                    }

                    if (response.code() == 401)
                    {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        focusView = mPasswordView;
                    }
                    UIUtils.showKeyboard(getActivity(), focusView);

                    if (focusView == null)
                    {
                        // TODO
                        /*
                         * int messageId =
                         * ExceptionMessageUtils.getSignInMessageId(getActivity(
                         * ), response.message()); if (messageId ==
                         * R.string.error_session_creation) {
                         * Snackbar.make(getActivity().findViewById(R.id.
                         * left_panel), response.message(),
                         * Snackbar.LENGTH_LONG); } else { // Revert to Alfresco
                         * WebApp MaterialDialog.Builder builder = new
                         * MaterialDialog.Builder(getActivity())
                         * .cancelListener(new
                         * DialogInterface.OnCancelListener() {
                         * @Override public void onCancel(DialogInterface
                         * dialog) { dismiss(); }
                         * }).title(R.string.error_session_creation_title)
                         * .content(Html.fromHtml(getString(messageId))).
                         * positiveText(R.string.ok); builder.show(); }
                         */
                    }
                }

            }

            public void onFailure(Call<UserRepresentation> call, Throwable error)
            {

            }

        });
    }

    private void updateAccount()
    {
        ActivitiAccountManager.getInstance(getActivity()).update(acc.getId(), username, endpoint.toString(),
                acc.getLabel(), version.type, version.edition, version.getFullVersion(), Long.toString(user.getId()),
                user.getFullname(), (user.getTenantId() != null) ? Long.toString(user.getTenantId()) : null);

        // Set as Default
        AccountsPreferences.setDefaultAccount(getActivity(), acc.getId());

        // Start a sync for integration
        IntegrationManager.sync(getActivity());

        // Analytics
        AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_ACCOUNT,
                AnalyticsManager.ACTION_UPDATE, acc.getServerType(), 1, false);
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
                    updateAccount();
                }
                else
                {
                    // BPM Suite 1.1
                    version = null;
                    updateAccount();
                }
            }

            @Override
            public void onFailure(Call<AppVersionRepresentation> call, Throwable error)
            {
                // BPM Suite 1.1
                version = null;
                updateAccount();
            }
        });
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onIntegrationSyncEvent(IntegrationSyncEvent event)
    {
        sync();
        getActivity().onBackPressed();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends LeafFragmentBuilder
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

        public Builder accountId(Long accountId)
        {
            extraConfiguration.putLong(ARGUMENT_ACCOUNT_ID, accountId);
            return this;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        }
    }
}
