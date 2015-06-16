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

package com.activiti.android.platform.integration.alfresco;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.activiti.android.app.BuildConfig;

/**
 * Created by jpascal on 26/03/2015.
 */
public class AlfrescoIntegrator implements AlfrescoIntentAPI
{
    public static final String ALFRESCO_ACCOUNT_TYPE = BuildConfig.ALFRESCO_ACCOUNT_ID;

    public static final String ALFRESCO_APP_PACKAGE = BuildConfig.ALFRESCO_APPLICATION_ID;

    public static final String STORAGE_ACCESS_PROVIDER = BuildConfig.ALFRESCO_PROVIDER_AUTHORITY.concat(".documents");

    public static Intent viewDocument(Long accountId, String documentId)
    {
        Uri.Builder b = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY_DOCUMENT).appendPath(documentId);
        return new Intent(ACTION_VIEW).setData(b.build()).putExtra(EXTRA_ACCOUNT_ID, accountId);
    }

    public static Intent createAccount(Context context, String repositoryUrl, String shareUrl, String username)
    {
        Intent createIntent = new Intent(ACTION_CREATE_ACCOUNT);
        createIntent.putExtra(EXTRA_ALFRESCO_USERNAME, username);
        createIntent.putExtra(EXTRA_ALFRESCO_REPOSITORY_URL, repositoryUrl);
        createIntent.putExtra(EXTRA_ALFRESCO_SHARE_URL, shareUrl);
        return createIntent;
    }

    public static boolean isAlfrescoInstalled(Context context)
    {
        try
        {
            context.getPackageManager().getApplicationInfo(ALFRESCO_APP_PACKAGE, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }
}
