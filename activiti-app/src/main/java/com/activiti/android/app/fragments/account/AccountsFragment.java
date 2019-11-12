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

package com.activiti.android.app.fragments.account;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.GridView;

import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.activity.WelcomeActivity;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.ui.fragments.accounts.AccountAdapter;
import com.activiti.android.ui.fragments.accounts.AccountsFoundationFragment;
import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;

public class AccountsFragment extends AccountsFoundationFragment
{
    public static final String TAG = AccountsFragment.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AccountsFragment()
    {
        super();
    }

    public static AccountsFragment newInstanceByTemplate(Bundle b)
    {
        AccountsFragment cbf = new AccountsFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        ActivitiAccount item = (ActivitiAccount) l.getItemAtPosition(position);
        if (item.getId() == AccountAdapter.ADD_ACCOUNT_ID)
        {
            Intent i = new Intent(getActivity(), WelcomeActivity.class);
            i.putExtra(WelcomeActivity.Companion.getEXTRA_ADD_ACCOUNT(), true);
            getActivity().startActivity(i);
        }
        else
        {
            ((MainActivity) getActivity()).switchAccount(item);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends ListingFragmentBuilder
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

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        };
    }
}
