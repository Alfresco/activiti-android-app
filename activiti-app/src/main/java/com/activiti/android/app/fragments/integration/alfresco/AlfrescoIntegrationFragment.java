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

package com.activiti.android.app.fragments.integration.alfresco;

import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.BuildConfig;
import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.activity.WelcomeActivity;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.integration.alfresco.AlfrescoIntegrator;
import com.activiti.android.platform.integration.alfresco.AlfrescoIntentAPI;
import com.activiti.android.platform.intent.IntentUtils;
import com.activiti.android.platform.provider.integration.Integration;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.utils.UIUtils;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created by jpascal on 26/03/2015.
 */
public class AlfrescoIntegrationFragment extends AlfrescoFragment
{
    public static final String TAG = AlfrescoIntegrationFragment.class.getName();

    private static final String ARGUMENT_ACCOUNT_ID = "accountId";

    private static final String ARGUMENT_INTEGRATION_PROVIDER_ID = "providerId";

    private TextView titleTv;

    private TextView summaryTv;

    private Button actionButton;

    private Account selectedAccount = null;

    private Long accountId = null;

    private ActivitiAccount activitiAccount = null;

    private String alfrescoUsername;

    private String alfrescoAccountName;

    private String alfrescoId;

    private Map<Long, Integration> integrations;

    private Integration selectedIntegration;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AlfrescoIntegrationFragment()
    {
        super();
    }

    public static AlfrescoIntegrationFragment newInstanceByTemplate(Bundle b)
    {
        AlfrescoIntegrationFragment cbf = new AlfrescoIntegrationFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_alfresco_setting, container, false));
        titleTv = (TextView) viewById(R.id.alfresco_title);
        summaryTv = (TextView) viewById(R.id.alfresco_summary);
        summaryTv.setSingleLine(false);
        summaryTv.setMaxLines(10);
        actionButton = (Button) viewById(R.id.alfresco_action);
        UIUtils.initClear(getRootView(), R.string.general_action_clear, true);

        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null)
        {
            Long providerId = BundleUtils.getLong(getArguments(), ARGUMENT_INTEGRATION_PROVIDER_ID);
            if (providerId != null)
            {
                selectedIntegration = IntegrationManager.getInstance(getActivity()).getByProviderId(providerId);
            }
            accountId = BundleUtils.getLong(getArguments(), ARGUMENT_ACCOUNT_ID);
            integrations = IntegrationManager.getInstance(getActivity()).getByAccountId(accountId);
            activitiAccount = ActivitiAccountManager.getInstance(getActivity()).getByAccountId(accountId);
        }

        checkActivity();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateUI();
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == 10 && resultCode == Activity.RESULT_OK)
        {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Account[] accounts = AccountManager.get(getActivity()).getAccountsByType(
                    AlfrescoIntegrator.ALFRESCO_ACCOUNT_TYPE);
            for (int i = 0; i < accounts.length; i++)
            {
                if (accountName.equals(accounts[i].name))
                {
                    selectedAccount = accounts[i];
                    break;
                }
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    private void updateUI()
    {
        hide(R.id.validation_panel);

        // The idea is to get Integration Information and compare them to
        // alfresco account.
        if (checkActivity()) { return; }

        // Retrieve information from activiti Account
        if (getArguments() != null)
        {
            accountId = BundleUtils.getLong(getArguments(), ARGUMENT_ACCOUNT_ID);
            activitiAccount = ActivitiAccountManager.getInstance(getActivity()).getByAccountId(accountId);
        }

        // We have alfresco account integration
        // Is Alfresco APP Present ?
        // Is Alfresco Version recent ?
        PackageInfo info = getAlfrescoInfo(getActivity());
        boolean outdated = (info != null && info.versionCode < 40);
        if (info == null || outdated)
        {
            // Alfresco APP is not present...
            // We request the installation from the play store
            titleTv.setText(getString(outdated ? R.string.settings_alfresco_update : R.string.settings_alfresco_install));

            summaryTv.setText(Html.fromHtml(getString(outdated ? R.string.settings_alfresco_update_summary
                    : R.string.settings_alfresco_install_summary)));

            actionButton.setText(getString(outdated ? R.string.settings_alfresco_update_action
                    : R.string.settings_alfresco_install_action));
            actionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startPlayStore();
                }
            });

            hide(R.id.validation_panel);

            return;
        }

        // Alfresco APP is present
        actionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedAccount = null;
                launchAccountSelect();
            }
        });

        // Retrieve Alfresco Account
        Account[] accounts = AccountManager.get(getActivity()).getAccountsByType(
                AlfrescoIntegrator.ALFRESCO_ACCOUNT_TYPE);
        if (accounts.length == 0 && selectedAccount == null)
        {
            // Need to create an alfresco account!
            titleTv.setText(getString(R.string.settings_alfresco_not_found));
            summaryTv.setText(Html.fromHtml(getString(R.string.settings_alfresco_not_found_summary)));
            actionButton.setText(getString(R.string.settings_alfresco_not_found_action));
            /*
             * actionButton.setOnClickListener(new View.OnClickListener() {
             * @Override public void onClick(View v) { selectedAccount = null;
             * Intent i = AlfrescoIntegrator.createAccount(getActivity(),
             * selectedIntegration.getRepositoryUrl(),
             * activitiAccount.getUsername()); startActivityForResult(i, 10); }
             * });
             */

            return;
        }

        // An activitiAccount match if username == username
        for (int i = 0; i < accounts.length; i++)
        {
            String username = AccountManager.get(getActivity()).getUserData(accounts[i],
                    BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".username"));

            if (activitiAccount.getUsername().equals(username))
            {
                // Lets compare hostname
                String alfUrl = AccountManager.get(getActivity()).getUserData(accounts[i],
                        BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".url"));
                Uri alfUri = Uri.parse(alfUrl);
                Uri activitiUri = Uri.parse(selectedIntegration.getRepositoryUrl());

                if (alfUri != null && activitiUri != null && alfUri.getHost().equals(activitiUri.getHost()))
                {
                    // We found one !
                    selectedAccount = accounts[i];
                    break;
                }
            }
        }

        // Have found ?
        if (selectedAccount != null)
        {
            alfrescoId = AccountManager.get(getActivity()).getUserData(selectedAccount,
                    BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".id"));
            alfrescoUsername = AccountManager.get(getActivity()).getUserData(selectedAccount,
                    BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".username"));
            alfrescoAccountName = AccountManager.get(getActivity()).getUserData(selectedAccount,
                    BuildConfig.ALFRESCO_ACCOUNT_ID.concat(".name"));

            titleTv.setText(getString(R.string.settings_alfresco_account_found));

            summaryTv.setText(Html.fromHtml(String.format(getString(R.string.settings_alfresco_account_found_summary),
                    alfrescoAccountName, alfrescoUsername)));

            actionButton.setText(getString(R.string.settings_alfresco_account_found_action));

            show(R.id.validation_panel);
            Button validate = UIUtils.initValidation(getRootView(), R.string.general_action_confirm);
            validate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saveIntegration();
                }
            });
            Button cancel = UIUtils.initCancel(getRootView(), R.string.general_action_cancel);
            cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getActivity().onBackPressed();
                }
            });
        }
        else
        {
            titleTv.setText(getString(R.string.settings_alfresco_account_found));
            summaryTv.setText(Html.fromHtml(getString(R.string.settings_alfresco_not_found_summary)));
            actionButton.setText(getString(R.string.settings_alfresco_not_found_action));

            hide(R.id.validation_panel);
        }
    }

    private PackageInfo getAlfrescoInfo(Context context)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            return pm.getPackageInfo(AlfrescoIntegrator.ALFRESCO_APP_PACKAGE, 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return null;
        }
    }

    private void startPlayStore()
    {
        IntentUtils.startPlayStore(getActivity(), AlfrescoIntegrator.ALFRESCO_APP_PACKAGE);
    }

    private void launchAccountSelect()
    {
        Bundle b = new Bundle();
        b.putString(AlfrescoIntentAPI.EXTRA_ALFRESCO_SHARE_URL, selectedIntegration.getShareUrl());
        b.putString(AlfrescoIntentAPI.EXTRA_ALFRESCO_REPOSITORY_URL, selectedIntegration.getRepositoryUrl());
        b.putString(AlfrescoIntentAPI.EXTRA_ALFRESCO_USERNAME, activitiAccount.getUsername());

        Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[] { AlfrescoIntegrator.ALFRESCO_ACCOUNT_TYPE }, true, null, null, null, b);
        startActivityForResult(intent, 10);
    }

    private void saveIntegration()
    {
        if (selectedIntegration != null)
        {
            IntegrationManager.getInstance(getActivity()).update(selectedIntegration.getProviderId(),
                    Long.parseLong(alfrescoId), alfrescoAccountName, alfrescoUsername);

            Snackbar.make(getActivity().findViewById(R.id.left_panel), R.string.account_associated,
                    Snackbar.LENGTH_SHORT).show();

            getActivity().onBackPressed();
        }
    }

    private boolean checkActivity()
    {
        // Switch to main activity only if welcome context
        if (getActivity() instanceof WelcomeActivity)
        {
            if (integrations == null || integrations.isEmpty())
            {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
                return true;
            }
        }
        return false;
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

        public Builder accountId(long accountId)
        {
            extraConfiguration.putLong(ARGUMENT_ACCOUNT_ID, accountId);
            return this;
        }

        public Builder integrationProviverId(long providerId)
        {
            extraConfiguration.putLong(ARGUMENT_INTEGRATION_PROVIDER_ID, providerId);
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
