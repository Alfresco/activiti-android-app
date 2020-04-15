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

package com.activiti.android.app.fragments.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.activity.WelcomeSsoActivity;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.ui.fragments.accounts.AccountAdapter;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.alfresco.auth.activity.LoginViewModel;

import java.util.List;

public class SignedOutDialogFragment extends DialogFragment
{
    public static final String TAG = SignedOutDialogFragment.class.getName();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fr_signed_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ActivitiAccount currentAccount = ActivitiAccountManager.getInstance(getActivity()).getCurrentAccount();

        // Disable dismissing the dialog
        setCancelable(false);

        // Sign in Button
        Button signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v ->
        {
            Intent i = new Intent(getActivity(), WelcomeSsoActivity.class);
            i.putExtra(LoginViewModel.EXTRA_ENDPOINT, currentAccount.getServerUrl());
            i.putExtra(LoginViewModel.EXTRA_AUTH_TYPE, currentAccount.getAuthType());
            i.putExtra(LoginViewModel.EXTRA_AUTH_CONFIG, currentAccount.getAuthConfig());
            i.putExtra(LoginViewModel.EXTRA_AUTH_STATE, currentAccount.getAuthState());
            startActivity(i);
        });

        // Add Accounts
        List<ActivitiAccount> accounts = ActivitiAccountManager.retrieveAccounts(getActivity());
        accounts.remove(currentAccount);
        accounts.add(AccountAdapter.makeAddAccountItem());
        View accountView;
        LinearLayout accountContainer = view.findViewById(R.id.accounts_container);
        TwoLinesViewHolder vh;
        for (ActivitiAccount account : accounts)
        {
            accountView = getLayoutInflater().inflate(R.layout.row_two_lines_borderless, accountContainer, false);
            accountView.setTag(account.getId());
            if (account.getId() == AccountAdapter.ADD_ACCOUNT_ID)
            {
                HolderUtils.configure(
                        accountView,
                        getResources().getString(R.string.account_add),
                        null,
                        R.drawable.ic_add_circle_outline_grey);
            }
            else
            {
                HolderUtils.configure(
                        accountView,
                        account.getUsername(),
                        account.getLabel(),
                        R.drawable.ic_account_circle_grey);
            }
            accountView.setOnClickListener(v ->
            {
                if ((Long)v.getTag() == AccountAdapter.ADD_ACCOUNT_ID)
                {
                    Intent i = new Intent(getActivity(), WelcomeSsoActivity.class);
                    getActivity().startActivity(i);
                }
                else
                {
                    ((MainActivity) getActivity()).switchAccount(account);
                    dismiss();
                }
            });
            accountContainer.addView(accountView);
        }
    }
}
