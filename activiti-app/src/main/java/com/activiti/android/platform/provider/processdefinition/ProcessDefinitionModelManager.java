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

package com.activiti.android.platform.provider.processdefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
public class ProcessDefinitionModelManager extends Manager
{
    public static final Uri CONTENT_URI = ProcessDefinitionModelProvider.CONTENT_URI;

    public static final String[] COLUMN_ALL = ProcessDefinitionModelSchema.COLUMN_ALL;

    protected static final Object LOCK = new Object();

    protected static Manager mInstance;

    private Integer dataSize;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    protected ProcessDefinitionModelManager(Context applicationContext)
    {
        super(applicationContext);
        getCount();
    }

    public static ProcessDefinitionModelManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new ProcessDefinitionModelManager(context);
            }
            return (ProcessDefinitionModelManager) mInstance;
        }
    }

    public static Uri getUri(long id)
    {
        return Uri.parse(ProcessDefinitionModelProvider.CONTENT_URI + "/" + id);
    }

    public static ContentValues createContentValues(String processDefinitionId, Long accountId, Long appId,
            String name, String description, Integer version, Boolean hasStartForm)
    {
        ContentValues updateValues = new ContentValues();

        updateValues.put(ProcessDefinitionModelSchema.COLUMN_PROCESS_DEFINITION_ID, processDefinitionId);
        updateValues.put(ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID, accountId);
        updateValues.put(ProcessDefinitionModelSchema.COLUMN_APP_ID, appId);
        updateValues.put(ProcessDefinitionModelSchema.COLUMN_NAME, name);
        updateValues.put(ProcessDefinitionModelSchema.COLUMN_DESCRIPTION, description);
        updateValues.put(ProcessDefinitionModelSchema.COLUMN_VERSION, version);
        updateValues.put(ProcessDefinitionModelSchema.COLUMN_HAS_START_FORM, hasStartForm ? 1 : 0);
        return updateValues;
    }

    public static void sync(Context context)
    {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(ActivitiAccountManager.getInstance(context).getCurrentAndroidAccount(),
                ProcessDefinitionModelProvider.AUTHORITY, settingsBundle);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATIC
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessDefinitionModel getByProviderId(long id)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(getUri(id), COLUMN_ALL, null, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createProcessDefinitionModel(cursor);
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

    public List<ProcessDefinitionModel> getById(String processDefinitionId, Long accountId)
    {
        Cursor cursor = null;
        ArrayList<ProcessDefinitionModel> processes = new ArrayList<>();
        try
        {
            cursor = appContext.getContentResolver().query(
                    ProcessDefinitionModelProvider.CONTENT_URI,
                    COLUMN_ALL,
                    ProcessDefinitionModelSchema.COLUMN_PROCESS_DEFINITION_ID + " = \"" + processDefinitionId + "\""
                            + " AND " + ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID + " = " + accountId, null, null);
            if (cursor == null) { return processes; }

            ProcessDefinitionModel process;
            processes = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                process = createProcessDefinitionModel(cursor);
                processes.add(process);
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
        return processes;
    }

    public ProcessDefinitionModel getById(String processDefinitionId, Long accountId, Long appId)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(
                    ProcessDefinitionModelProvider.CONTENT_URI,
                    COLUMN_ALL,
                    ProcessDefinitionModelSchema.COLUMN_PROCESS_DEFINITION_ID + " = \"" + processDefinitionId + "\""
                            + " AND " + ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID + " = " + accountId + " AND "
                            + ProcessDefinitionModelSchema.COLUMN_APP_ID + " = " + appId, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createProcessDefinitionModel(cursor);
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

    public ProcessDefinitionModel findByName(String name)
    {
        if (TextUtils.isEmpty(name)) { return null; }
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(ProcessDefinitionModelProvider.CONTENT_URI, COLUMN_ALL,
                    ProcessDefinitionModelSchema.COLUMN_NAME + " = \"" + name + "\"", null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createProcessDefinitionModel(cursor);
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

    private ProcessDefinitionModel createProcessDefinitionModel(Cursor c)
    {
        return new ProcessDefinitionModel(c.getLong(ProcessDefinitionModelSchema.COLUMN_ID_ID),
                c.getString(ProcessDefinitionModelSchema.COLUMN_PROCESS_DEFINITION_ID_ID),
                c.getLong(ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID_ID),
                c.getLong(ProcessDefinitionModelSchema.COLUMN_APP_ID_ID),
                c.getString(ProcessDefinitionModelSchema.COLUMN_NAME_ID),
                c.getString(ProcessDefinitionModelSchema.COLUMN_DESCRIPTION_ID),
                c.getInt(ProcessDefinitionModelSchema.COLUMN_VERSION_ID),
                c.getInt(ProcessDefinitionModelSchema.COLUMN_HAS_START_FORM_ID));
    }

    public ProcessDefinitionModel createProcessDefinitionModel(String processDefinitionId, Long accountId, Long appId,
            String name, String description, Integer version, Boolean hasStartForm)
    {
        Uri accountUri = appContext.getContentResolver().insert(ProcessDefinitionModelProvider.CONTENT_URI,
                createContentValues(processDefinitionId, accountId, appId, name, description, version, hasStartForm));

        if (accountUri == null) { return null; }

        return getByProviderId(Long.parseLong(accountUri.getLastPathSegment()));
    }

    public ProcessDefinitionModel update(Long id, String processDefinitionId, Long accountId, Long appId, String name,
            String description, Integer version, Boolean hasStartForm)
    {
        appContext.getContentResolver().update(getUri(id),
                createContentValues(processDefinitionId, accountId, appId, name, description, version, hasStartForm),
                null, null);

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

    public boolean hasProcessDefinitionModel()
    {
        return dataSize != null && (dataSize > 0);
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
            cursor = appContext.getContentResolver().query(ProcessDefinitionModelProvider.CONTENT_URI, COLUMN_ALL,
                    null, null, null);
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
            cursor = appContext.getContentResolver().query(ProcessDefinitionModelProvider.CONTENT_URI,
                    new String[] { ProcessDefinitionModelSchema.COLUMN_PROCESS_DEFINITION_ID },
                    ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID + " = " + accountId + "", null, null);
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

    public Map<Long, ProcessDefinitionModel> getAll()
    {
        Cursor cursor = null;
        Map<Long, ProcessDefinitionModel> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(ProcessDefinitionModelProvider.CONTENT_URI, COLUMN_ALL,
                    null, null, null);
            instances = new HashMap<>(cursor.getCount());
            ProcessDefinitionModel app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createProcessDefinitionModel(cursor);
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

    public Map<Long, ProcessDefinitionModel> getAllByAccountId(long activitiId)
    {
        Cursor cursor = null;
        Map<Long, ProcessDefinitionModel> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(ProcessDefinitionModelProvider.CONTENT_URI, COLUMN_ALL,
                    ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID + " = " + activitiId + "", null, null);
            instances = new HashMap<>(cursor.getCount());
            ProcessDefinitionModel app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createProcessDefinitionModel(cursor);
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

    /**
     * Ordered by name ASC
     */
    public Map<Long, ProcessDefinitionModel> getAllByAppId(long accountId, long appId)
    {
        Cursor cursor = null;
        Map<Long, ProcessDefinitionModel> instances = null;
        try
        {
            if (appId == -1)
            {
                cursor = appContext.getContentResolver().query(ProcessDefinitionModelProvider.CONTENT_URI, COLUMN_ALL,
                        ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID + " = " + accountId, null,
                        ProcessDefinitionModelSchema.COLUMN_NAME + " ASC");
            }
            else
            {
                cursor = appContext.getContentResolver().query(
                        ProcessDefinitionModelProvider.CONTENT_URI,
                        COLUMN_ALL,
                        ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID + " = " + accountId + " AND "
                                + ProcessDefinitionModelSchema.COLUMN_APP_ID + " = " + appId, null,
                        ProcessDefinitionModelSchema.COLUMN_NAME + " ASC");
            }

            instances = new LinkedHashMap<>(cursor.getCount());
            ProcessDefinitionModel app;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                app = createProcessDefinitionModel(cursor);
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

    public void deleteByProcessDefinitionId(String processDefinitionId)
    {
        Cursor cursor = null;
        ArrayList<Long> instances = null;
        try
        {
            cursor = appContext.getContentResolver().query(ProcessDefinitionModelProvider.CONTENT_URI,
                    new String[] { ProcessDefinitionModelSchema.COLUMN_ID },
                    ProcessDefinitionModelSchema.COLUMN_PROCESS_DEFINITION_ID + " = \"" + processDefinitionId + "\"",
                    null, null);
            instances = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(ProcessDefinitionModelProvider.CONTENT_URI + "/"
                        + cursor.getInt(ProcessDefinitionModelSchema.COLUMN_ID_ID));
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
            Uri uri = Uri.parse(ProcessDefinitionModelProvider.CONTENT_URI + "/" + providerId);
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
            cursor = appContext.getContentResolver().query(ProcessDefinitionModelProvider.CONTENT_URI,
                    new String[] { ProcessDefinitionModelSchema.COLUMN_ID },
                    ProcessDefinitionModelSchema.COLUMN_ACCOUNT_ID + " = " + accountId + "", null, null);
            instances = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                Uri uri = Uri.parse(ProcessDefinitionModelProvider.CONTENT_URI + "/"
                        + cursor.getInt(ProcessDefinitionModelSchema.COLUMN_ID_ID));
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
