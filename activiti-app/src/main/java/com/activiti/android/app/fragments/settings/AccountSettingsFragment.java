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

package com.activiti.android.app.fragments.settings;

import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.activity.WelcomeActivity;
import com.activiti.android.app.fragments.account.AccountEditFragment;
import com.activiti.android.app.fragments.integration.alfresco.AlfrescoIntegrationFragment;
import com.activiti.android.platform.account.AccountsPreferences;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.preferences.InternalAppPreferences;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.provider.group.GroupInstanceManager;
import com.activiti.android.platform.provider.integration.Integration;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.integration.IntegrationSchema;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.LeafFragmentBuilder;
import com.activiti.android.ui.fragments.form.EditTextDialogFragment;
import com.activiti.android.ui.holder.ThreeLinesViewHolder;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Manage global application preferences.
 * 
 * @author Jean Marie Pascal
 */
public class AccountSettingsFragment extends AlfrescoFragment implements EditTextDialogFragment.onEditTextFragment
{
    public static final String TAG = AccountSettingsFragment.class.getName();

    private static final String ARGUMENT_ACCOUNT_ID = "accountId";

    private static final int ACCOUNT_ID = 1;

    private Long accountId = null;

    private boolean isLatest = false;

    private MaterialDialog progressdialog;

    private ActivitiAccount account;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public AccountSettingsFragment()
    {
        setHasOptionsMenu(true);
    }

    protected static AccountSettingsFragment newInstanceByTemplate(Bundle b)
    {
        AccountSettingsFragment cbf = new AccountSettingsFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_settings_account, container, false));

        account = null;
        if (getArguments() != null)
        {
            accountId = BundleUtils.getLong(getArguments(), ARGUMENT_ACCOUNT_ID);
            account = ActivitiAccountManager.getInstance(getActivity()).getByAccountId(accountId);
        }

        // TITLE
        getToolbar().setTitle(getString(R.string.settings_account));
        getToolbar().setSubtitle((account != null) ? account.getUsername() : null);

        // User Info
        TwoLinesViewHolder vh = new TwoLinesViewHolder(viewById(R.id.settings_account_info));
        vh.topText.setText(R.string.settings_userinfo_account);
        vh.bottomText.setText(R.string.settings_userinfo_account_summary);
        vh.icon.setVisibility(View.GONE);
        viewById(R.id.settings_account_info_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AccountEditFragment.with(getActivity()).accountId(accountId).back(true).display();
            }
        });

        vh = new TwoLinesViewHolder(viewById(R.id.settings_account_name));
        vh.topText.setText(R.string.settings_userinfo_account_name);
        vh.bottomText.setText(R.string.settings_userinfo_account_name_summary);
        vh.icon.setVisibility(View.GONE);
        viewById(R.id.settings_account_name_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditTextDialogFragment.with(getActivity()).fieldId(ACCOUNT_ID).tag(getTag()).value(account.getLabel())
                        .displayAsDialog();
            }
        });

        return getRootView();
    }

    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
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
    public void onResume()
    {
        super.onResume();
        updateIntegrations();
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

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        if (!DisplayUtils.hasCentralPane(getActivity()))
        {
            menu.clear();
            inflater.inflate(R.menu.account, menu);
        }
        else
        {
            getToolbar().getMenu().clear();
            getToolbar().inflateMenu(R.menu.account);
            getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    return onOptionsItemSelected(item);
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;
            case R.id.account_action_remove:
                MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                        .cancelListener(new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                dismiss();
                            }
                        }).content(R.string.account_remove).positiveText(R.string.general_action_confirm)
                        .negativeText(R.string.general_action_cancel).callback(new MaterialDialog.ButtonCallback()
                        {
                            @Override
                            public void onPositive(MaterialDialog dialog)
                            {
                                deleteAccount();
                            }

                            @Override
                            public void onNeutral(MaterialDialog dialog)
                            {
                                // Do nothing
                            }
                        });
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    private void deleteAccount()
    {
        progressdialog = new MaterialDialog.Builder(getActivity()).title(R.string.account_remove_progress)
                .content(R.string.please_wait).progress(true, 0).cancelable(false).show();

        // Detect if latest account
        List<ActivitiAccount> accounts = ActivitiAccountManager.retrieveAccounts(getActivity());
        if (accounts.size() == 1)
        {
            // Latest account
            isLatest = true;
        }
        else
        {
            // Prepare the next account to load
            isLatest = false;
        }

        // Clean Providers
        AccountsPreferences.clear(getActivity());
        InternalAppPreferences.clean(getActivity(), accountId);

        // Clean Providers
        RuntimeAppInstanceManager.getInstance(getActivity()).deleteByAccountId(accountId);
        IntegrationManager.getInstance(getActivity()).deleteByAccountId(accountId);
        GroupInstanceManager.getInstance(getActivity()).deleteByAccountId(accountId);
        ProcessDefinitionModelManager.getInstance(getActivity()).deleteByAccountId(accountId);

        // Delete Account
        ActivitiAccountManager.getInstance(getActivity()).delete(getActivity(), accountId,
                new AccountManagerCallback<Boolean>()
                {
                    @Override
                    public void run(AccountManagerFuture<Boolean> future)
                    {
                        // Clean Providers
                        ActivitiAccount currentAccount = AccountsPreferences.getDefaultAccount(getActivity());

                        if (isLatest)
                        {
                            getActivity().startActivity(new Intent(getActivity(), WelcomeActivity.class));
                            getActivity().finish();
                        }
                        else
                        {
                            if (getActivity() instanceof MainActivity)
                            {
                                ((MainActivity) getActivity()).switchAccount(currentAccount);
                            }
                        }
                        progressdialog.dismiss();
                    }
                });
    }

    private void updateIntegrations()
    {
        Map<Long, Integration> integrationMap = IntegrationManager.getInstance(getActivity()).getByAccountId(accountId);

        // No integration. Display nothing
        if (integrationMap == null || integrationMap.isEmpty())
        {
            hide(R.id.settings_intregration_alfresco_parent_container);
            hide(R.id.settings_intregration_alfresco_parent_title);
            return;
        }

        LinearLayout integrationContainer = (LinearLayout) viewById(R.id.settings_intregration_alfresco_container);
        integrationContainer.removeAllViews();
        ThreeLinesViewHolder vh;
        View integrationView;
        int iconId, descriptionId;
        for (Map.Entry<Long, Integration> entry : integrationMap.entrySet())
        {
            integrationView = LayoutInflater.from(getActivity()).inflate(R.layout.row_three_lines_caption,
                    integrationContainer, false);
            integrationView.setTag(entry.getValue().getProviderId());

            vh = new ThreeLinesViewHolder(integrationView);
            vh.topText.setText(entry.getValue().getName());
            vh.middleText.setText(entry.getValue().getAccountUsername());
            vh.icon.setVisibility(View.VISIBLE);

            switch (entry.getValue().getOpenType())
            {
                case Integration.OPEN_NATIVE_APP:
                    iconId = R.drawable.alfresco_logo;
                    descriptionId = R.string.settings_account_integration_alfresco_mobile;
                    break;
                case Integration.OPEN_BROWSER:
                    iconId = R.drawable.ic_open_in_browser_grey;
                    descriptionId = R.string.settings_account_integration_alfresco_browser;
                    break;
                default:
                    iconId = R.drawable.ic_wrong_grey;
                    descriptionId = R.string.settings_account_integration_alfresco_none;
                    break;
            }
            vh.bottomText.setText(descriptionId);
            vh.icon.setImageResource(iconId);
            vh.choose.setVisibility(View.VISIBLE);
            vh.choose.setBackgroundResource(R.drawable.activititheme_list_selector_holo_light);
            vh.choose.setClickable(true);
            vh.choose.setPadding(16, 16, 16, 16);
            vh.choose.setImageResource(R.drawable.ic_more_grey);
            vh.choose.setTag(entry.getValue());
            vh.choose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    PopupMenu popup = new PopupMenu(getActivity(), v);
                    if (((Integration) v.getTag()).getOpenType() == Integration.OPEN_UNDEFINED)
                    {
                        popup.getMenuInflater().inflate(R.menu.integration_alfresco_add, popup.getMenu());
                    }
                    else
                    {
                        popup.getMenuInflater().inflate(R.menu.integration_alfresco_remove, popup.getMenu());
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            switch (item.getItemId())
                            {
                                case R.id.integration_alfresco_remove:
                                    // Analytics
                                    AnalyticsHelper.reportOperationEvent(getActivity(),
                                            AnalyticsManager.CATEGORY_SETTINGS,
                                            AnalyticsManager.ACTION_ALFRESCO_INTEGRATION, AnalyticsManager.LABEL_REMOVE,
                                            1, false);

                                    IntegrationManager.getInstance(getActivity())
                                            .update(((Integration) v.getTag()).getProviderId(), -1l, null, null);
                                    updateIntegrations();
                                    break;
                                case R.id.integration_alfresco_mobile:
                                    // Analytics
                                    AnalyticsHelper.reportOperationEvent(getActivity(),
                                            AnalyticsManager.CATEGORY_SETTINGS,
                                            AnalyticsManager.ACTION_ALFRESCO_INTEGRATION,
                                            AnalyticsManager.LABEL_LINK_MOBILE, 1, false);

                                    AlfrescoIntegrationFragment.with(getActivity())
                                            .integrationProviverId(((Integration) v.getTag()).getProviderId())
                                            .accountId(accountId).display();
                                    break;
                                case R.id.integration_alfresco_web:
                                    // Analytics
                                    AnalyticsHelper.reportOperationEvent(getActivity(),
                                            AnalyticsManager.CATEGORY_SETTINGS,
                                            AnalyticsManager.ACTION_ALFRESCO_INTEGRATION,
                                            AnalyticsManager.LABEL_LINK_WEB, 1, false);

                                    IntegrationManager.getInstance(getActivity()).update(
                                            ((Integration) v.getTag()).getProviderId(),
                                            IntegrationSchema.COLUMN_OPEN_TYPE, Integration.OPEN_BROWSER);
                                    updateIntegrations();
                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show(); // showing popup menu
                }
            });
            integrationContainer.addView(integrationView);
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // PICKER CALLBACK
    // //////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onTextEdited(int id, String newValue)
    {
        Account androidAccount = ActivitiAccountManager.getInstance(getActivity()).getAndroidAccount(accountId);
        AccountManager manager = AccountManager.get(getActivity());
        manager.setUserData(androidAccount, ActivitiAccount.ACCOUNT_TITLE, newValue);
        account = ActivitiAccountManager.getInstance(getActivity()).getByAccountId(accountId);
    }

    @Override
    public void onTextClear(int valueId)
    {
        Account androidAccount = ActivitiAccountManager.getInstance(getActivity()).getAndroidAccount(accountId);
        AccountManager manager = AccountManager.get(getActivity());
        manager.setUserData(androidAccount, ActivitiAccount.ACCOUNT_TITLE, "Activiti Server");
        account = ActivitiAccountManager.getInstance(getActivity()).getByAccountId(accountId);
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

        public Builder accountId(long accountId)
        {
            extraConfiguration.putLong(ARGUMENT_ACCOUNT_ID, accountId);
            return this;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // SETTERS
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        }
    }

}
