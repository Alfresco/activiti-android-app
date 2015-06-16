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

package com.activiti.android.platform.provider.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.activiti.android.app.R;
import com.activiti.android.platform.Manager;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.provider.CursorUtils;

/**
 * @author Jean Marie Pascal
 */
public class RuntimeAppInstanceManager extends Manager
{
    public static final Uri CONTENT_URI = RuntimeAppInstanceProvider.CONTENT_URI;

    public static final String[] COLUMN_ALL = RuntimeAppInstanceSchema.COLUMN_ALL;

    protected static final Object LOCK = new Object();

    protected static Manager mInstance;

    private Integer appsSize;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    protected RuntimeAppInstanceManager(Context applicationContext)
    {
        super(applicationContext);
        getCount();
    }

    public static RuntimeAppInstanceManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new RuntimeAppInstanceManager(context);
            }
            return (RuntimeAppInstanceManager) mInstance;
        }
    }

    public static Uri getUri(long id)
    {
        return Uri.parse(RuntimeAppInstanceProvider.CONTENT_URI + "/" + id);
    }

    public static ContentValues createContentValues(int number1, int number2, int number3)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_NUMBER_1, number1);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_NUMBER_2, number2);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_NUMBER_3, number3);
        return updateValues;
    }

    public static ContentValues createContentValues(long accountId, long appId, String name, String model,
            String theme, String description, String icon, String deployment, int number1, int number2, int number3)
    {
        ContentValues updateValues = new ContentValues();

        updateValues.put(RuntimeAppInstanceSchema.COLUMN_ACCOUNT_ID, accountId);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_APP_ID, appId);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_NAME, name);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_DESCRIPTION, description);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_MODEL, model);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_THEME, theme);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_ICON, icon);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_DEPLOYMENT, deployment);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_NUMBER_1, number1);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_NUMBER_2, number2);
        updateValues.put(RuntimeAppInstanceSchema.COLUMN_NUMBER_3, number3);
        return updateValues;
    }

    public static void sync(Context context)
    {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(ActivitiAccountManager.getInstance(context).getCurrentAndroidAccount(),
                RuntimeAppInstanceProvider.AUTHORITY, settingsBundle);
    }

    public static void sync(Context context, Long appId)
    {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putLong(RuntimeAppInstanceSyncAdapter.ARGUMENT_APP_ID, appId);
        ContentResolver.requestSync(ActivitiAccountManager.getInstance(context).getCurrentAndroidAccount(),
                RuntimeAppInstanceProvider.AUTHORITY, settingsBundle);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATIC
    // ///////////////////////////////////////////////////////////////////////////
    public int getIcon(String icon)
    {
        int iconId = R.drawable.ic_action_inbox;
        return iconId;
    }

    public RuntimeAppInstance getByProviderId(long id)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(getUri(id), COLUMN_ALL, null, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createAppInstance(cursor);
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

    public RuntimeAppInstance getById(Long appId, Long accountId)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(
                    RuntimeAppInstanceProvider.CONTENT_URI,
                    COLUMN_ALL,
                    RuntimeAppInstanceSchema.COLUMN_APP_ID + " = " + appId + " AND "
                            + RuntimeAppInstanceSchema.COLUMN_ACCOUNT_ID + " = " + accountId, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createAppInstance(cursor);
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

    public RuntimeAppInstance findByName(String name)
    {
        if (TextUtils.isEmpty(name)) { return null; }
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(RuntimeAppInstanceProvider.CONTENT_URI, COLUMN_ALL,
                    RuntimeAppInstanceSchema.COLUMN_NAME + " = \"" + name + "\"", null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createAppInstance(cursor);
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

    private RuntimeAppInstance createAppInstance(Cursor c)
    {
        return new RuntimeAppInstance(c.getLong(RuntimeAppInstanceSchema.COLUMN_ID_ID),
                c.getLong(RuntimeAppInstanceSchema.COLUMN_ACCOUNT_ID_ID),
                c.getLong(RuntimeAppInstanceSchema.COLUMN_APP_ID_ID),
                c.getString(RuntimeAppInstanceSchema.COLUMN_NAME_ID),
                c.getString(RuntimeAppInstanceSchema.COLUMN_DESCRIPTION_ID),
                c.getLong(RuntimeAppInstanceSchema.COLUMN_MODEL_ID),
                c.getString(RuntimeAppInstanceSchema.COLUMN_THEME_ID),
                getIcon(c.getString(RuntimeAppInstanceSchema.COLUMN_ICON_ID)),
                c.getString(RuntimeAppInstanceSchema.COLUMN_DEPLOYMENT_ID),
                c.getLong(RuntimeAppInstanceSchema.COLUMN_NUMBER_1_ID),
                c.getLong(RuntimeAppInstanceSchema.COLUMN_NUMBER_2_ID),
                c.getLong(RuntimeAppInstanceSchema.COLUMN_NUMBER_3_ID));
    }

    public RuntimeAppInstance createAppInstance(Long accountId, Long appId, String name, String model, String theme,
            String description, String icon, String deployment, int number1, int number2, int number3)
    {
        Uri accountUri = appContext.getContentResolver().insert(
                RuntimeAppInstanceProvider.CONTENT_URI,
                createContentValues(accountId, appId, name, model, theme, description, icon, deployment, number1,
                        number2, number3));

        if (accountUri == null) { return null; }

        return getByProviderId(Long.parseLong(accountUri.getLastPathSegment()));
    }

    public RuntimeAppInstance update(long id, long accountId, long appId, String name, String model, String theme,
            String description, String icon, String deployment, int number1, int number2, int number3)
    {
        appContext.getContentResolver().update(
                getUri(id),
                createContentValues(accountId, appId, name, model, theme, description, icon, deployment, number1,
                        number2, number3), null, null);

        return getByProviderId(id);
    }

    public RuntimeAppInstance update(long id, int number1, int number2, int number3)
    {
        appContext.getContentResolver().update(getUri(id), createContentValues(number1, number2, number3), null, null);

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
            cursor = appContext.getContentResolver().query(RuntimeAppInstanceProvider.CONTENT_URI, COLUMN_ALL, null,
                    null, null);
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

    public ArrayList<Long> getIds(Long accountId)
    {
        Cursor cursor = null;
        ArrayList<Long> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(RuntimeAppInstanceProvider.CONTENT_URI,
                    new String[] { RuntimeAppInstanceSchema.COLUMN_APP_ID },
                    RuntimeAppInstanceSchema.COLUMN_ACCOUNT_ID + " = " + accountId + "", null, null);
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

    public Map<Long, RuntimeAppInstance> getAll()
    {
        Cursor cursor = null;
        Map<Long, RuntimeAppInstance> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(RuntimeAppInstanceProvider.CONTENT_URI, COLUMN_ALL, null,
                    null, null);
            instances = new HashMap<>(cursor.getCount());
            RuntimeAppInstance app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createAppInstance(cursor);
                instances.put(app.getProviderId(), app);
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

    public Map<Long, RuntimeAppInstance> getByActivitiId(long activitiId)
    {
        Cursor cursor = null;
        Map<Long, RuntimeAppInstance> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(RuntimeAppInstanceProvider.CONTENT_URI, COLUMN_ALL,
                    RuntimeAppInstanceSchema.COLUMN_ACCOUNT_ID + " = " + activitiId + "", null, null);
            instances = new HashMap<>(cursor.getCount());
            RuntimeAppInstance app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createAppInstance(cursor);
                instances.put(app.getProviderId(), app);
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

    public void deleteByAppId(long appDefinitionId)
    {
        Cursor cursor = null;
        ArrayList<Long> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(RuntimeAppInstanceProvider.CONTENT_URI,
                    new String[] { RuntimeAppInstanceSchema.COLUMN_ID },
                    RuntimeAppInstanceSchema.COLUMN_APP_ID + " = " + appDefinitionId + "", null, null);
            instances = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(RuntimeAppInstanceProvider.CONTENT_URI + "/"
                        + cursor.getInt(RuntimeAppInstanceSchema.COLUMN_ID_ID));
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
        ArrayList<Long> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(RuntimeAppInstanceProvider.CONTENT_URI,
                    new String[] { RuntimeAppInstanceSchema.COLUMN_ID },
                    RuntimeAppInstanceSchema.COLUMN_ACCOUNT_ID + " = " + accountId + "", null, null);
            instances = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(RuntimeAppInstanceProvider.CONTENT_URI + "/"
                        + cursor.getInt(RuntimeAppInstanceSchema.COLUMN_ID_ID));
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
