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

package com.activiti.android.ui.fragments.accounts;

import java.util.ArrayList;
import java.util.List;

import android.widget.BaseAdapter;

import com.activiti.android.app.R;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.client.api.constant.RequestConstant;

public class AccountsFoundationFragment extends BasePagingGridFragment implements RequestConstant
{
    public static final String TAG = AccountsFoundationFragment.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AccountsFoundationFragment()
    {
        emptyListMessageId = R.string.empty_app;
        retrieveDataOnCreation = false;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void performRequest()
    {
        // Do nothing
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        setListShown(true);
        List<ActivitiAccount> accounts = ActivitiAccountManager.getInstance(getActivity()).retrieveAccounts(
                getActivity());
        accounts.add(new ActivitiAccount(AccountAdapter.ADD_ACCOUNT_ID, null, null, null, null, null, null, null, null,
                null, null));
        refreshHelper.setRefreshComplete();
        isFullLoad = true;
        return new AccountAdapter(getActivity(), R.layout.row_two_lines, new ArrayList<>(accounts));
    }
}
