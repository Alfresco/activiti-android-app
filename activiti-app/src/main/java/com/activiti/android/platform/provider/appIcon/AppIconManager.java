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

package com.activiti.android.platform.provider.appIcon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.activiti.android.platform.Manager;
import com.activiti.android.platform.provider.CursorUtils;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class AppIconManager extends Manager
{
    protected static final Object LOCK = new Object();

    protected static Manager mInstance;

    private Integer AppIconSize;

    public static final Uri CONTENT_URI = AppIconProvider.CONTENT_URI;

    public static final String[] COLUMN_ALL = AppIconSchema.COLUMN_ALL;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    protected AppIconManager(Context applicationContext)
    {
        super(applicationContext);
        getCount();
    }

    public static AppIconManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new AppIconManager(context);
            }
            return (AppIconManager) mInstance;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATIC
    // ///////////////////////////////////////////////////////////////////////////
    public AppIcon findById(long id)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(getUri(id), COLUMN_ALL, null, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createAppIcon(cursor);
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

    public AppIcon findByIconId(String iconId)
    {
        String value = iconId;
        if (value == null || value.isEmpty())
        {
            value = "glyphicon-inbox";
        }
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(AppIconManager.CONTENT_URI, COLUMN_ALL,
                    AppIconSchema.COLUMN_ICON + " = \"" + value + "\"", null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() >= 1)
            {
                cursor.moveToFirst();
                return createAppIcon(cursor);
            }
        }
        catch (Exception e)
        {
            // DO Nothing
            Log.e("AppIcon", Log.getStackTraceString(e));
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
        return null;
    }

    private static AppIcon createAppIcon(Cursor c)
    {
        return new AppIcon(c.getLong(AppIconSchema.COLUMN_ID_ID), c.getString(AppIconSchema.COLUMN_ICON_ID),
                c.getString(AppIconSchema.COLUMN_TEXT_VALUE_ID));
    }

    public AppIcon createAppIcon(String iconId, String textValue)
    {
        Uri accountUri = appContext.getContentResolver().insert(AppIconProvider.CONTENT_URI,
                createContentValues(iconId, textValue));

        if (accountUri == null) { return null; }

        return findById(Long.parseLong(accountUri.getLastPathSegment()));
    }

    public AppIcon update(long id, String iconId, String textValue)
    {
        appContext.getContentResolver().update(getUri(id), createContentValues(iconId, textValue), null, null);

        return findById(id);
    }

    public static Uri getUri(long id)
    {
        return Uri.parse(AppIconProvider.CONTENT_URI + "/" + id);
    }

    public static ContentValues createContentValues(String iconId, String textValue)
    {
        ContentValues updateValues = new ContentValues();

        updateValues.put(AppIconSchema.COLUMN_ICON, iconId);
        updateValues.put(AppIconSchema.COLUMN_TEXT_VALUE, textValue);
        return updateValues;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC
    // ///////////////////////////////////////////////////////////////////////////
    public boolean hasData()
    {
        getCount();
        return (AppIconSize != null);
    }

    public boolean hasAppIcons()
    {
        return AppIconSize != null && (AppIconSize > 0);
    }

    public boolean isEmpty()
    {
        getCount();
        return AppIconSize == null || (AppIconSize == 0);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Private
    // ///////////////////////////////////////////////////////////////////////////
    private void getCount()
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(AppIconProvider.CONTENT_URI, COLUMN_ALL, null, null, null);
            if (cursor != null)
            {
                AppIconSize = cursor.getCount();
            }
            else
            {
                AppIconSize = 0;
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
