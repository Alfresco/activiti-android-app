package com.activiti.android.app.fragments.account;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.activity.WelcomeSsoActivity;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.alfresco.auth.activity.LoginViewModel;
import com.alfresco.auth.fragments.SignedOutFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SignedOutAdapter implements com.alfresco.auth.fragments.SignedOutAdapter
{
    private List<ActivitiAccount> accountList;
    private ActivitiAccount currentAccount;

    public SignedOutAdapter(Context context)
    {
        accountList = ActivitiAccountManager.retrieveAccounts(context);
        currentAccount = ActivitiAccountManager.getInstance(context).getCurrentAccount();
        accountList.remove(currentAccount);
    }

    @Override
    public void onSignInButtonClicked(@NotNull DialogFragment fr, @NotNull View v)
    {
        Intent i = new Intent(fr.getActivity(), WelcomeSsoActivity.class);
        i.putExtra(LoginViewModel.EXTRA_ENDPOINT, currentAccount.getServerUrl());
        i.putExtra(LoginViewModel.EXTRA_AUTH_TYPE, currentAccount.getAuthType());
        i.putExtra(LoginViewModel.EXTRA_AUTH_CONFIG, currentAccount.getAuthConfig());
        i.putExtra(LoginViewModel.EXTRA_AUTH_STATE, currentAccount.getAuthState());
        fr.startActivity(i);
    }

    @Override
    public void onAddAccountButtonClicked(@NotNull DialogFragment fr, @NotNull View v)
    {
        Intent i = new Intent(fr.getActivity(), WelcomeSsoActivity.class);
        fr.startActivity(i);
    }

    @Override
    public int numberOfAccounts()
    {
        return accountList.size();
    }

    @NotNull
    @Override
    public View viewForAccount(@NotNull DialogFragment fr, @NotNull ViewGroup container, int index)
    {
        ActivitiAccount account = accountList.get(index);
        View view = SignedOutFragment.accountViewWith(fr, container, account.getUsername(), account.getLabel());

        view.setOnClickListener( v -> {
            ((MainActivity) fr.getActivity()).switchAccount(account);
            fr.dismiss();
        });

        return view;
    }
}
