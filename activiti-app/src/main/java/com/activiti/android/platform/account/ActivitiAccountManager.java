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

package com.activiti.android.platform.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.Manager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.client.api.model.idm.LightUserRepresentation;
import com.alfresco.client.AbstractClient;

/**
 * Responsible to manage accounts.
 * 
 * @author Jean Marie Pascal
 */
public class ActivitiAccountManager extends Manager
{
    protected static final Object LOCK = new Object();

    private static final String TAG = ActivitiAccountManager.class.getName();

    protected static Manager mInstance;

    protected Integer accountsSize;

    protected Map<Long, ActivitiAccount> accountIndex = new HashMap<>(0);

    protected ActivitiAccountManager(Context context)
    {
        super(context);
        EventBusManager.getInstance().register(this);
        getCount();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public static ActivitiAccountManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new ActivitiAccountManager(context);
            }

            return (ActivitiAccountManager) mInstance;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LISTING
    // ///////////////////////////////////////////////////////////////////////////
    public static List<ActivitiAccount> retrieveAccounts(Context context)
    {
        List<ActivitiAccount> accounts = null;
        try
        {
            AccountManager mAccountManager = AccountManager.get(context);
            Account[] accountMs = mAccountManager.getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
            accounts = new ArrayList<>(accountMs.length);
            for (Account account : accountMs)
            {
                if (mAccountManager.getUserData(account, ActivitiAccount.ACCOUNT_ID) != null)
                {
                    accounts.add(ActivitiAccount.parse(mAccountManager, account));
                }
            }
            // Log.d(TAG, "accounts " + accounts.size());
        }
        catch (Exception e)
        {
            Log.d(TAG, Log.getStackTraceString(e));
        }

        return accounts;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public void shutdown()
    {
        EventBusManager.getInstance().unregister(this);
        accountsSize = null;
        mInstance = null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC
    // ///////////////////////////////////////////////////////////////////////////
    public boolean hasData()
    {
        getCount();
        return (accountsSize != null);
    }

    public boolean hasAccount()
    {
        if (accountsSize == null) { return false; }
        return (accountsSize > 0);
    }

    public boolean hasMultipleAccount()
    {
        if (accountsSize == null) { return false; }
        return (accountsSize > 1);
    }

    public boolean isEmpty()
    {
        getCount();
        if (accountsSize == null) { return true; }
        return (accountsSize == 0);
    }

    public ActivitiAccount getCurrentAccount()
    {
        long id = AccountsPreferences.getCurrentAccountId(appContext);
        ActivitiAccount acc = accountIndex.containsKey(id) ? accountIndex.get(id) : AccountsPreferences
                .getDefaultAccount(appContext);
        if (acc == null) { return null; }
        accountIndex.put(acc.getId(), acc);
        return acc;
    }

    public LightUserRepresentation getUser()
    {
        ActivitiAccount acc = AccountsPreferences.getDefaultAccount(appContext);
        return new LightUserRepresentation(Long.parseLong(acc.getUserId()), acc.getUserFullname(), null,
                acc.getUsername());
    }

    public Account getCurrentAndroidAccount()
    {
        return AccountsPreferences.getDefaultAndroidAccount(appContext);
    }

    public ActivitiAccount getByAccountId(long id)
    {
        AccountManager mAccountManager = AccountManager.get(appContext);
        Account[] accounts = mAccountManager.getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
        if (accounts.length == 0) { return null; }
        for (Account account : accounts)
        {
            String accountId = mAccountManager.getUserData(account, ActivitiAccount.ACCOUNT_ID);
            if (accountId != null && id == Long.parseLong(accountId)) { return ActivitiAccount.parse(mAccountManager,
                    account); }
        }
        return null;
    }

    public List<Account> getAndroidAccounts()
    {
        List<Account> accounts = null;
        try
        {
            AccountManager mAccountManager = AccountManager.get(appContext);
            Account[] accountMs = mAccountManager.getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
            accounts = new ArrayList<Account>(accountMs.length);
            for (Account account : accountMs)
            {
                accounts.add(account);
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, Log.getStackTraceString(e));
        }

        return accounts;
    }

    public Account getAndroidAccount(long id)
    {
        AccountManager mAccountManager = AccountManager.get(appContext);
        Account[] accounts = mAccountManager.getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
        if (accounts.length == 0) { return null; }
        for (Account account : accounts)
        {
            String accountId = mAccountManager.getUserData(account, ActivitiAccount.ACCOUNT_ID);
            if (accountId != null && id == Long.parseLong(accountId)) { return account; }
        }
        return null;
    }

    public Account getFirstAndroidAccount()
    {
        AccountManager mAccountManager = AccountManager.get(appContext);
        Account[] accounts = mAccountManager.getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
        if (accounts.length == 0) { return null; }
        return accounts[0];
    }

    public ActivitiAccount retrieveFirstAccount()
    {
        AccountManager mAccountManager = AccountManager.get(appContext);
        Account[] accounts = mAccountManager.getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
        if (accounts.length == 0) { return null; }
        return ActivitiAccount.parse(mAccountManager, accounts[0]);
    }

    public void delete(Context context, long accountId, AccountManagerCallback<Boolean> callback)
    {
        // Delete account from cache
        accountIndex.remove(accountId);

        // Update no. of accounts
        accountsSize--;

        // Delete Account from AccountManager
        AccountManager.get(context).removeAccount(getAndroidAccount(accountId), callback, null);
    }

    public String createUniqueAccountName(String defaultName)
    {
        // Check Account Name
        String accountName = defaultName;
        AccountManager mAccountManager = AccountManager.get(appContext);
        Map<String, Account> accountIndex = null;
        Account[] accounts = mAccountManager.getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
        if (accounts.length > 0)
        {
            accountIndex = new HashMap<String, Account>(accounts.length);
            for (Account accountAvailable : accounts)
            {
                accountIndex.put(accountAvailable.name, accountAvailable);
            }

            if (accountIndex != null && accountIndex.containsKey(accountName))
            {
                int index = 0;
                // We need to change the name of the account
                while (accountIndex.containsKey(accountName))
                {
                    accountName = defaultName.concat("-").concat(Integer.toString(index));
                    index++;
                }
            }
        }

        return accountName;
    }

    protected long getAccountId()
    {
        long accountIndex = 0;
        Account[] accounts = AccountManager.get(appContext).getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
        for (Account accountAvailable : accounts)
        {
            String value = AccountManager.get(appContext).getUserData(accountAvailable, ActivitiAccount.ACCOUNT_ID);
            if (value == null)
            {
                continue;
            }
            long currentIndew = Long.parseLong(value);
            if (accountIndex <= currentIndew)
            {
                accountIndex = currentIndew + 1;
            }
        }
        return accountIndex;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    @Deprecated
    public ActivitiAccount create(String username, String password, String serverUrl, String label, String serverType,
            String serverEdition, String serverVersion, String userId, String fullname, String tenantId)
    {
        return create(username, password, null, null, null, serverUrl, label, serverType, serverEdition, serverVersion, userId, fullname, tenantId);
    }

    public ActivitiAccount create(String username, String authState, String authType, String authConfig, String serverUrl, String label, String serverType,
            String serverEdition, String serverVersion, String userId, String fullname, String tenantId)
    {
        return create(username, null, authState, authType, authConfig, serverUrl, label, serverType, serverEdition, serverVersion, userId, fullname, tenantId);
    }

    private ActivitiAccount create(String username, String password, String authState, String authType, String authConfig, String serverUrl, String label, String serverType,
            String serverEdition, String serverVersion, String userId, String fullname, String tenantId)
    {
        // Generate some properties
        String accountName = createUniqueAccountName(username);
        long accountId = getAccountId();

        // Prepare account
        Account newAccount = new Account(accountName, ActivitiAccount.ACCOUNT_TYPE);
        Bundle b = new Bundle();
        b.putString(ActivitiAccount.ACCOUNT_ID, Long.toString(accountId));
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_TITLE, label);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_SERVER_URL, serverUrl);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_USERNAME, username);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_AUTH_TYPE, authType);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_AUTH_STATE, authState);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_AUTH_CONFIG, authConfig);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_SERVER_TYPE, serverType);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_SERVER_EDITION, serverEdition);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_SERVER_VERSION, serverVersion);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_USER_ID, userId);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_USER_FULLNAME, fullname);
        BundleUtils.addIfNotEmpty(b, ActivitiAccount.ACCOUNT_TENANT_ID, tenantId);

        // Time to createNewTask.
        if (AccountManager.get(appContext).addAccountExplicitly(newAccount, password, b))
        {
            // Create the Account data object
            accountsSize++;
            getCount();
            return new ActivitiAccount(accountId, username, authType, authState, authConfig, serverUrl, label,
                    serverType, serverEdition, serverVersion, userId, fullname, tenantId);
        }
        else
        {
            return null;
        }
    }

    public ActivitiAccount update(long accountId, String username, String serverUrl, String label,
            String serverType, String serverEdition, String serverVersion, String userId, String fullname,
            String tenantId)
    {
        return update(appContext, accountId, username, serverUrl, label, serverType, serverEdition,
                serverVersion, userId, fullname, tenantId);
    }

    public ActivitiAccount update(Context context, long accountId, String username, String serverUrl,
            String label, String serverType, String serverEdition, String serverVersion, String userId,
            String fullname, String tenantId)
    {
        Account acc = getAndroidAccount(accountId);
        AccountManager manager = AccountManager.get(context);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_ID, Long.toString(accountId));
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_TITLE, label);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_SERVER_URL, serverUrl);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_USERNAME, username);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_SERVER_TYPE, serverType);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_SERVER_EDITION, serverEdition);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_SERVER_VERSION, serverVersion);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_USER_ID, userId);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_USER_FULLNAME, fullname);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_TENANT_ID, tenantId);

        ActivitiAccount activitiAccount = getByAccountId(accountId);
        accountIndex.put(accountId, activitiAccount);
        return activitiAccount;
    }

    public ActivitiAccount update(Context context, long accountId, String username, String authState) {
        Account acc = getAndroidAccount(accountId);
        AccountManager manager = AccountManager.get(context);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_USERNAME, username);
        manager.setUserData(acc, ActivitiAccount.ACCOUNT_AUTH_STATE, authState);

        ActivitiAccount activitiAccount = getByAccountId(accountId);
        accountIndex.put(accountId, activitiAccount);
        return activitiAccount;
    }

    protected void getCount()
    {
        AccountManager mAccountManager = AccountManager.get(appContext);
        Account[] accountMs = mAccountManager.getAccountsByType(ActivitiAccount.ACCOUNT_TYPE);
        if (accountMs != null)
        {
            accountsSize = accountMs.length;
        }
        else
        {
            accountsSize = 0;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UPDDATE
    // ///////////////////////////////////////////////////////////////////////////
    public void update(long accountId, String key, String value)
    {
        Account acc = getAndroidAccount(accountId);
        AccountManager manager = AccountManager.get(appContext);
        manager.setUserData(acc, key, value);
    }
}
