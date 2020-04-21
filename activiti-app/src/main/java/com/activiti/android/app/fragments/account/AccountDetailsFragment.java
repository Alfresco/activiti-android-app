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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.LeafFragmentBuilder;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.alfresco.auth.utils.AccountDetailsHelper;

import java.util.List;
import java.util.Map;

public class AccountDetailsFragment extends AlfrescoFragment {

    public static final String TAG = AccountDetailsFragment.class.getName();

    public static final String ARGUMENT_ACCOUNT_ID = "accountId";

    private ActivitiAccount acc;

    private Long accountId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AccountDetailsFragment()
    {
        super();
    }

    public static AccountDetailsFragment newInstanceByTemplate(Bundle b)
    {
        AccountDetailsFragment adf = new AccountDetailsFragment();
        adf.setArguments(b);
        return adf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_account_details, container, false));

        if (getArguments() != null)
        {
            accountId = getArguments().getLong(ARGUMENT_ACCOUNT_ID);
        }
        acc = ActivitiAccountManager.getInstance(getActivity()).getByAccountId(accountId);

        // TITLE
        getToolbar().setTitle(getString(R.string.settings_account_details));
        getToolbar().setSubtitle((acc != null) ? acc.getUsername() : null);

        LinearLayout detailsContainer = getRootView().findViewById(R.id.details_container);
        List<AccountDetailsHelper.Item> details =
                AccountDetailsHelper.itemsWith(
                    getContext(),
                    acc.getUsername(),
                    acc.getAuthType(),
                    acc.getAuthState(),
                    acc.getAuthConfig(),
                    acc.getServerUrl());

        View itemView;
        for (AccountDetailsHelper.Item item : details)
        {
            itemView = getLayoutInflater().inflate(R.layout.row_two_lines, detailsContainer, false);
            HolderUtils.configure(itemView, item.getTitle(), item.getValue(), -1);
            detailsContainer.addView(itemView);
        }

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

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static AccountDetailsFragment.Builder with(FragmentActivity activity)
    {
        return new AccountDetailsFragment.Builder(activity);
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

        public AccountDetailsFragment.Builder accountId(Long accountId)
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
