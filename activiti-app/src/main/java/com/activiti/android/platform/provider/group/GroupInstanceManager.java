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

package com.activiti.android.platform.provider.group;

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

import com.activiti.android.platform.Manager;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.provider.CursorUtils;

/**
 * @author Jean Marie Pascal
 */
public class GroupInstanceManager extends Manager
{
    public static final Uri CONTENT_URI = GroupInstanceProvider.CONTENT_URI;

    public static final String[] COLUMN_ALL = GroupInstanceSchema.COLUMN_ALL;

    protected static final Object LOCK = new Object();

    protected static Manager mInstance;

    private Integer dataSize;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    protected GroupInstanceManager(Context applicationContext)
    {
        super(applicationContext);
        getCount();
    }

    public static GroupInstanceManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new GroupInstanceManager(context);
            }
            return (GroupInstanceManager) mInstance;
        }
    }

    public static Uri getUri(long id)
    {
        return Uri.parse(GroupInstanceProvider.CONTENT_URI + "/" + id);
    }

    public static ContentValues createContentValues(Long groupId, Long accountId, String name, int type,
            Long parentGroupId, String status, String externalId)
    {
        ContentValues updateValues = new ContentValues();

        updateValues.put(GroupInstanceSchema.COLUMN_GROUP_ID, groupId);
        updateValues.put(GroupInstanceSchema.COLUMN_ACCOUNT_ID, accountId);
        updateValues.put(GroupInstanceSchema.COLUMN_NAME, name);
        updateValues.put(GroupInstanceSchema.COLUMN_TYPE, type);
        updateValues.put(GroupInstanceSchema.COLUMN_PARENT_GROUP_ID, parentGroupId);
        updateValues.put(GroupInstanceSchema.COLUMN_STATUS, status);
        updateValues.put(GroupInstanceSchema.COLUMN_EXTERNAL_ID, externalId);
        return updateValues;
    }

    public static void sync(Context context)
    {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(ActivitiAccountManager.getInstance(context).getCurrentAndroidAccount(),
                GroupInstanceProvider.AUTHORITY, settingsBundle);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATIC
    // ///////////////////////////////////////////////////////////////////////////
    public GroupInstance getByProviderId(long id)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(getUri(id), COLUMN_ALL, null, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createGroup(cursor);
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

    public GroupInstance getById(Long groupId, Long accountId)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(
                    GroupInstanceProvider.CONTENT_URI,
                    COLUMN_ALL,
                    GroupInstanceSchema.COLUMN_GROUP_ID + " = \"" + groupId + "\"" + " AND "
                            + GroupInstanceSchema.COLUMN_ACCOUNT_ID + " = " + accountId, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createGroup(cursor);
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

    public GroupInstance findByName(String name)
    {
        if (TextUtils.isEmpty(name)) { return null; }
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(GroupInstanceProvider.CONTENT_URI, COLUMN_ALL,
                    GroupInstanceSchema.COLUMN_NAME + " = \"" + name + "\"", null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createGroup(cursor);
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

    private GroupInstance createGroup(Cursor c)
    {
        return new GroupInstance(c.getLong(GroupInstanceSchema.COLUMN_ID_ID),
                c.getLong(GroupInstanceSchema.COLUMN_GROUP_ID_ID), c.getLong(GroupInstanceSchema.COLUMN_ACCOUNT_ID_ID),
                c.getString(GroupInstanceSchema.COLUMN_NAME_ID), c.getInt(GroupInstanceSchema.COLUMN_TYPE_ID),
                c.getLong(GroupInstanceSchema.COLUMN_PARENT_GROUP_ID_ID),
                c.getString(GroupInstanceSchema.COLUMN_STATUS_ID),
                c.getString(GroupInstanceSchema.COLUMN_EXTERNAL_ID_ID));
    }

    public GroupInstance createGroup(Long groupId, Long accountId, String name, int type, Long parentGroupId,
            String status, String externalId)
    {
        Uri accountUri = appContext.getContentResolver().insert(GroupInstanceProvider.CONTENT_URI,
                createContentValues(groupId, accountId, name, type, parentGroupId, status, externalId));

        if (accountUri == null) { return null; }

        return getByProviderId(Long.parseLong(accountUri.getLastPathSegment()));
    }

    public GroupInstance update(Long id, Long groupId, Long accountId, String name, int type, Long parentGroupId,
            String status, String externalId)
    {
        appContext.getContentResolver().update(getUri(id),
                createContentValues(groupId, accountId, name, type, parentGroupId, status, externalId), null, null);

        return getByProviderId(id);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC
    // ///////////////////////////////////////////////////////////////////////////
    public boolean hasData()
    {
        getCount();
        return (dataSize != null);
    }

    public boolean isEmpty()
    {
        getCount();
        return dataSize == null || (dataSize == 0);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Private
    // ///////////////////////////////////////////////////////////////////////////
    private void getCount()
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(GroupInstanceProvider.CONTENT_URI, COLUMN_ALL, null, null,
                    null);
            if (cursor != null)
            {
                dataSize = cursor.getCount();
            }
            else
            {
                dataSize = 0;
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
            cursor = appContext.getContentResolver().query(GroupInstanceProvider.CONTENT_URI,
                    new String[] { GroupInstanceSchema.COLUMN_GROUP_ID },
                    GroupInstanceSchema.COLUMN_ACCOUNT_ID + " = " + accountId + "", null, null);
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

    public Map<Long, GroupInstance> getAll()
    {
        Cursor cursor = null;
        Map<Long, GroupInstance> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(GroupInstanceProvider.CONTENT_URI, COLUMN_ALL, null, null,
                    null);
            instances = new HashMap<>(cursor.getCount());
            GroupInstance app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createGroup(cursor);
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

    public Map<Long, GroupInstance> getAllByAccountId(long accountId)
    {
        Cursor cursor = null;
        Map<Long, GroupInstance> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(GroupInstanceProvider.CONTENT_URI, COLUMN_ALL,
                    GroupInstanceSchema.COLUMN_ACCOUNT_ID + " = " + accountId + "", null, null);
            instances = new HashMap<>(cursor.getCount());
            GroupInstance app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createGroup(cursor);
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

    public Map<Long, GroupInstance> getFunctionnalByAccountId(long accountId)
    {
        Cursor cursor = null;
        Map<Long, GroupInstance> instances = new HashMap<>(0);
        try
        {
            cursor = appContext.getContentResolver().query(
                    GroupInstanceProvider.CONTENT_URI,
                    COLUMN_ALL,
                    GroupInstanceSchema.COLUMN_ACCOUNT_ID + " = " + accountId + " AND "
                            + GroupInstanceSchema.COLUMN_TYPE + " = " + GroupInstance.TYPE_FUNCTIONAL_GROUP + " AND "
                            + GroupInstanceSchema.COLUMN_STATUS + " = \"active\"", null, null);

            instances = new HashMap<>(cursor.getCount());
            GroupInstance app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createGroup(cursor);
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

    public void deleteByGroupId(String groupId)
    {
        Cursor cursor = null;
        ArrayList<Long> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(GroupInstanceProvider.CONTENT_URI,
                    new String[] { GroupInstanceSchema.COLUMN_ID },
                    GroupInstanceSchema.COLUMN_GROUP_ID + " = \"" + groupId + "\"", null, null);
            instances = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(GroupInstanceProvider.CONTENT_URI + "/"
                        + cursor.getInt(GroupInstanceSchema.COLUMN_ID_ID));
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

    public void deleteByProviderId(long providerId)
    {
        Cursor cursor = null;
        try
        {
            Uri uri = Uri.parse(GroupInstanceProvider.CONTENT_URI + "/" + providerId);
            appContext.getContentResolver().delete(uri, null, null);
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
            cursor = appContext.getContentResolver().query(GroupInstanceProvider.CONTENT_URI,
                    new String[] { GroupInstanceSchema.COLUMN_ID },
                    GroupInstanceSchema.COLUMN_ACCOUNT_ID + " = " + accountId + "", null, null);
            instances = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(GroupInstanceProvider.CONTENT_URI + "/"
                        + cursor.getInt(GroupInstanceSchema.COLUMN_ID_ID));
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
