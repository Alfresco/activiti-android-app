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

package com.activiti.android.platform.provider.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.activiti.android.platform.Manager;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.provider.CursorUtils;

/**
 * @author Jean Marie Pascal
 */
public class IntegrationManager extends Manager
{
    public static final Uri CONTENT_URI = IntegrationProvider.CONTENT_URI;

    public static final String[] COLUMN_ALL = IntegrationSchema.COLUMN_ALL;

    protected static final Object LOCK = new Object();

    protected static Manager mInstance;

    private Integer appsSize;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    protected IntegrationManager(Context applicationContext)
    {
        super(applicationContext);
        getCount();
    }

    public static IntegrationManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new IntegrationManager(context);
            }
            return (IntegrationManager) mInstance;
        }
    }

    public static Uri getUri(long id)
    {
        return Uri.parse(IntegrationProvider.CONTENT_URI + "/" + id);
    }

    public static ContentValues createContentValues(long integrationId, String name, String username, long tenantId,
            String alfrescoTenantId, String created, String updated, String shareUrl, String repositoryUrl,
            long activitiId, long alfrescoId, String alfrescoName, String alfrescoUsername, int openType)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put(IntegrationSchema.COLUMN_INTEGRATION_ID, integrationId);
        updateValues.put(IntegrationSchema.COLUMN_NAME, name);
        updateValues.put(IntegrationSchema.COLUMN_USERNAME, username);
        updateValues.put(IntegrationSchema.COLUMN_TENANT_ID, tenantId);
        updateValues.put(IntegrationSchema.COLUMN_ALFRESCO_TENANT_ID, alfrescoTenantId);
        updateValues.put(IntegrationSchema.COLUMN_CREATED, created);
        updateValues.put(IntegrationSchema.COLUMN_UPDATED, updated);
        updateValues.put(IntegrationSchema.COLUMN_SHARE_URL, shareUrl);
        updateValues.put(IntegrationSchema.COLUMN_REPOSITORY_URL, repositoryUrl);
        updateValues.put(IntegrationSchema.COLUMN_ACTIVITI_ACCOUNT_ID, activitiId);
        updateValues.put(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_ID, alfrescoId);
        updateValues.put(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_NAME, alfrescoName);
        updateValues.put(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_USERNAME, alfrescoUsername);
        updateValues.put(IntegrationSchema.COLUMN_OPEN_TYPE, openType);
        return updateValues;
    }

    public static void sync(Context context)
    {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(ActivitiAccountManager.getInstance(context).getCurrentAndroidAccount(),
                IntegrationProvider.AUTHORITY, settingsBundle);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATIC
    // ///////////////////////////////////////////////////////////////////////////
    public Integration getByProviderId(long id)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(getUri(id), COLUMN_ALL, null, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createIntegration(cursor);
            }
            cursor.close();
        }
        catch (Exception e)
        {
            // DO Nothing
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
        return null;
    }

    public Integration getById(Long integrationId, Long accountId)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(
                    IntegrationProvider.CONTENT_URI,
                    COLUMN_ALL,
                    IntegrationSchema.COLUMN_INTEGRATION_ID + " = " + integrationId + "" + " AND "
                            + IntegrationSchema.COLUMN_ACTIVITI_ACCOUNT_ID + " = " + accountId, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createIntegration(cursor);
            }
        }
        catch (Exception e)
        {
            // DO Nothing
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
        return null;
    }

    public Integration getByAlfrescoId(Long alfrescoId, Long accountId)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(
                    IntegrationProvider.CONTENT_URI,
                    COLUMN_ALL,
                    IntegrationSchema.COLUMN_ACTIVITI_ACCOUNT_ID + " = " + accountId + "" + " AND "
                            + IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_ID + " = " + alfrescoId, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                return createIntegration(cursor);
            }
        }
        catch (Exception e)
        {
            // DO Nothing
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
        return null;
    }

    private Integration createIntegration(Cursor c)
    {
        return new Integration(c.getLong(IntegrationSchema.COLUMN_ID_ID),
                c.getLong(IntegrationSchema.COLUMN_INTEGRATION_ID_ID), c.getString(IntegrationSchema.COLUMN_NAME_ID),
                c.getString(IntegrationSchema.COLUMN_USERNAME_ID), c.getLong(IntegrationSchema.COLUMN_TENANT_ID_ID),
                c.getString(IntegrationSchema.COLUMN_ALFRESCO_TENANT_ID_ID),
                c.getString(IntegrationSchema.COLUMN_CREATED_ID), c.getString(IntegrationSchema.COLUMN_UPDATED_ID),
                c.getString(IntegrationSchema.COLUMN_SHARE_URL_ID),
                c.getString(IntegrationSchema.COLUMN_REPOSITORY_URL_ID),
                c.getLong(IntegrationSchema.COLUMN_ACTIVITI_ACCOUNT_ID_ID),
                c.getLong(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_ID_ID),
                c.getString(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_NAME_ID),
                c.getString(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_USERNAME_ID),
                c.getInt(IntegrationSchema.COLUMN_OPEN_TYPE_ID));
    }

    public Integration createIntegration(long integrationId, String name, String username, long tenantId,
            String alfrescoTenantId, String created, String updated, String shareUrl, String repositoryUrl,
            long activitiId, long alfrescoId, String alfrescoName, String alfrescoUsername, int openType)
    {
        Uri accountUri = appContext.getContentResolver().insert(
                IntegrationProvider.CONTENT_URI,
                createContentValues(integrationId, name, username, tenantId, alfrescoTenantId, created, updated,
                        shareUrl, repositoryUrl, activitiId, alfrescoId, alfrescoName, alfrescoUsername, openType));

        if (accountUri == null) { return null; }

        return getByProviderId(Long.parseLong(accountUri.getLastPathSegment()));
    }

    public Integration update(long id, long integrationId, String name, String username, long tenantId,
            String alfrescoTenantId, String created, String updated, String shareUrl, String repositoryUrl,
            long activitiId, long alfrescoId, String alfrescoName, String alfrescoUsername, int openType)
    {
        appContext.getContentResolver().update(
                getUri(id),
                createContentValues(integrationId, name, username, tenantId, alfrescoTenantId, created, updated,
                        shareUrl, repositoryUrl, activitiId, alfrescoId, alfrescoName, alfrescoUsername, openType),
                null, null);

        return getByProviderId(id);
    }

    /**
     * Update only Alfresco part for a defined integration
     * 
     * @return
     */
    public Integration update(long id, long alfrescoId, String alfrescoName, String alfrescoUsername)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_ID, alfrescoId);
        updateValues.put(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_NAME, alfrescoName);
        updateValues.put(IntegrationSchema.COLUMN_ALFRESCO_ACCOUNT_USERNAME, alfrescoUsername);
        updateValues.put(IntegrationSchema.COLUMN_OPEN_TYPE, alfrescoId == -1L ? Integration.OPEN_UNDEFINED
                : Integration.OPEN_NATIVE_APP);

        appContext.getContentResolver().update(getUri(id), updateValues, null, null);

        return getByProviderId(id);
    }

    public Integration update(long id, String key, int value)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put(key, value);
        appContext.getContentResolver().update(getUri(id), updateValues, null, null);
        return getByProviderId(id);
    }

    public Integration update(long id, String key, String value)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put(key, value);
        appContext.getContentResolver().update(getUri(id), updateValues, null, null);
        return getByProviderId(id);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC
    // ///////////////////////////////////////////////////////////////////////////
    public boolean hasData()
    {
        getCount();
        return (appsSize != null);
    }

    public boolean hasApplications()
    {
        return appsSize != null && (appsSize > 0);
    }

    public boolean isEmpty()
    {
        getCount();
        return appsSize == null || (appsSize == 0);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Private
    // ///////////////////////////////////////////////////////////////////////////
    private void getCount()
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(IntegrationProvider.CONTENT_URI, COLUMN_ALL, null, null,
                    null);
            if (cursor != null)
            {
                appsSize = cursor.getCount();
            }
            else
            {
                appsSize = 0;
            }
        }
        catch (Exception e)
        {
            // DO Nothing
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
    }

    public ArrayList<Long> getIds()
    {
        Cursor cursor = null;
        ArrayList<Long> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(IntegrationProvider.CONTENT_URI,
                    new String[] { IntegrationSchema.COLUMN_NAME }, null, null, null);
            instances = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                instances.add(cursor.getLong(0));
            }
        }
        catch (Exception e)
        {
            // DO Nothing
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
        return instances;
    }

    public Map<Long, Integration> getByAccountId(long activitiId)
    {
        Cursor cursor = null;
        Map<Long, Integration> instances = new HashMap<>(0);
        try
        {
            cursor = appContext.getContentResolver().query(IntegrationProvider.CONTENT_URI, COLUMN_ALL,
                    IntegrationSchema.COLUMN_ACTIVITI_ACCOUNT_ID + " = " + activitiId + "", null, null);
            instances = new HashMap<>(cursor.getCount());
            Integration app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createIntegration(cursor);
                instances.put(app.getProviderId(), app);
            }
        }
        catch (Exception e)
        {
            // DO Nothing
            Log.w("Error", Log.getStackTraceString(e));
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
        return instances;
    }

    public void deleteByProviderId(long providerId)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(IntegrationProvider.CONTENT_URI,
                    new String[] { IntegrationSchema.COLUMN_ID },
                    IntegrationSchema.COLUMN_ID + " = " + providerId + "", null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(IntegrationProvider.CONTENT_URI + "/"
                        + cursor.getInt(IntegrationSchema.COLUMN_ID_ID));
                appContext.getContentResolver().delete(uri, null, null);
            }
        }
        catch (Exception e)
        {
            // DO Nothing
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
    }

    public void deleteById(long accountId, long integrationId)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(
                    IntegrationProvider.CONTENT_URI,
                    new String[] { IntegrationSchema.COLUMN_ID },
                    IntegrationSchema.COLUMN_INTEGRATION_ID + " = " + integrationId + "" + " AND "
                            + IntegrationSchema.COLUMN_ACTIVITI_ACCOUNT_ID + " = " + accountId, null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(IntegrationProvider.CONTENT_URI + "/"
                        + cursor.getInt(IntegrationSchema.COLUMN_ID_ID));
                appContext.getContentResolver().delete(uri, null, null);
            }
        }
        catch (Exception e)
        {
            // DO Nothing
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
    }

    public void deleteByAccountId(long accountId)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(IntegrationProvider.CONTENT_URI,
                    new String[] { IntegrationSchema.COLUMN_ID },
                    IntegrationSchema.COLUMN_ACTIVITI_ACCOUNT_ID + " = " + accountId + "", null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(IntegrationProvider.CONTENT_URI + "/"
                        + cursor.getInt(IntegrationSchema.COLUMN_ID_ID));
                appContext.getContentResolver().delete(uri, null, null);
            }
        }
        catch (Exception e)
        {
            // DO Nothing
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
    }
}
