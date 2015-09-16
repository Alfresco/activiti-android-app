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

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manage application preferences associated to accounts objects.
 * 
 * @author Jean Marie Pascal
 */
public final class AccountsPreferences
{
    public static final String ACCOUNT_PREFS = "org.activiti.bpmn.android.account.preferences";

    public static final String ACCOUNT_DEFAULT = "org.activiti.bpmn.android.account.preferences.default";

    private AccountsPreferences()
    {
    }

    public static long getCurrentAccountId(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(ACCOUNT_PREFS, 0);
        return settings.getLong(ACCOUNT_DEFAULT, -1);
    }

    public static ActivitiAccount getDefaultAccount(Context context)
    {
        // Default account to load
        SharedPreferences settings = context.getSharedPreferences(ACCOUNT_PREFS, 0);
        long id = settings.getLong(ACCOUNT_DEFAULT, -1);
        if (id == -1)
        {
            return ActivitiAccountManager.getInstance(context).retrieveFirstAccount();
        }
        else
        {
            ActivitiAccount acc = ActivitiAccountManager.getInstance(context).getByAccountId(id);
            if (acc == null)
            {
                acc = ActivitiAccountManager.getInstance(context).retrieveFirstAccount();
                if (acc != null)
                {
                    setDefaultAccount(context, acc.getId());
                }
            }
            return acc;
        }
    }

    public static Account getDefaultAndroidAccount(Context context)
    {
        // Default account to load
        SharedPreferences settings = context.getSharedPreferences(ACCOUNT_PREFS, 0);
        long id = settings.getLong(ACCOUNT_DEFAULT, -1);
        if (id == -1)
        {
            return ActivitiAccountManager.getInstance(context).getFirstAndroidAccount();
        }
        else
        {
            return ActivitiAccountManager.getInstance(context).getAndroidAccount(id);
        }
    }

    public static void setDefaultAccount(Context context, long id)
    {
        SharedPreferences settings = context.getSharedPreferences(ACCOUNT_PREFS, 0);
        if (settings != null)
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(AccountsPreferences.ACCOUNT_DEFAULT, id);
            editor.commit();
        }
    }

    public static void clear(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(ACCOUNT_PREFS, 0);
        if (settings != null)
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.clear().commit();
        }
    }

}
