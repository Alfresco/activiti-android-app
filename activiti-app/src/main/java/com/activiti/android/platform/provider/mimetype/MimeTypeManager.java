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

package com.activiti.android.platform.provider.mimetype;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.activiti.android.app.R;
import com.activiti.android.platform.Manager;
import com.activiti.android.platform.provider.CursorUtils;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class MimeTypeManager extends Manager
{
    protected static final Object LOCK = new Object();

    protected static Manager mInstance;

    private Integer mimetypeSize;

    public static final Uri CONTENT_URI = MimeTypeProvider.CONTENT_URI;

    public static final String[] COLUMN_ALL = MimeTypeSchema.COLUMN_ALL;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    protected MimeTypeManager(Context applicationContext)
    {
        super(applicationContext);
        getCount();
    }

    public static MimeTypeManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new MimeTypeManager(context);
            }
            return (MimeTypeManager) mInstance;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATIC
    // ///////////////////////////////////////////////////////////////////////////
    public static String getName(String uri)
    {
        if (uri == null) { return null; }

        int dot = uri.lastIndexOf(".".charAt(0));
        if (dot > 0)
        {
            return uri.substring(0, dot).toLowerCase();
        }
        else
        {
            return uri;
        }
    }

    public static String getExtension(String uri)
    {
        if (uri == null) { return null; }

        int dot = uri.lastIndexOf(".".charAt(0));
        if (dot > 0)
        {
            return uri.substring(dot + 1).toLowerCase();
        }
        else
        {
            return "";
        }
    }

    public String getMIMEType(String fileName)
    {
        String extension = "";
        if (fileName != null)
        {
            extension = getExtension(fileName);
        }
        MimeType type = findByExtension(extension);

        if (type != null)
        {
            return type.getMimeType();
        }
        else
        {
            return "application/octet-stream";
        }
    }

    public int getIcon(String fileName)
    {
        String extension = "";
        int iconId = R.drawable.mime_generic;
        if (fileName != null)
        {
            extension = getExtension(fileName);
        }
        MimeType type = findByExtension(extension);

        if (type != null)
        {
            return type.getSmallIconId(appContext);
        }
        else
        {
            return iconId;
        }
    }

    public int getIcon(String fileName, boolean isLarge)
    {
        if (!isLarge) { return getIcon(fileName); }
        String extension = "";
        int iconId = R.drawable.mime_256_generic;
        if (fileName != null)
        {
            extension = getExtension(fileName);
        }
        MimeType type = findByExtension(extension);

        if (type != null)
        {
            return type.getLargeIconId(appContext);
        }
        else
        {
            return iconId;
        }
    }

    public MimeType getMimetype(String fileName)
    {
        String extension = "";
        if (fileName != null)
        {
            extension = getExtension(fileName);
        }
        return findByExtension(extension);
    }

    public MimeType findById(long id)
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(getUri(id), COLUMN_ALL, null, null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createMimeType(cursor);
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

    public MimeType findByExtension(String extension)
    {
        if (extension == null) { return null; }
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(MimeTypeProvider.CONTENT_URI, COLUMN_ALL,
                    MimeTypeSchema.COLUMN_EXTENSION + " = \"" + extension + "\"", null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() == 1)
            {
                cursor.moveToFirst();
                return createMimeType(cursor);
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

    public MimeType findByMimeType(String mimetype)
    {
        if (TextUtils.isEmpty(mimetype) || !mimetype.contains("/")) { return null; }
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(
                    MimeTypeProvider.CONTENT_URI,
                    COLUMN_ALL,
                    MimeTypeSchema.COLUMN_TYPE + " = \"" + mimetype.split("/")[0] + "\"" + " AND "
                            + MimeTypeSchema.COLUMN_SUBTYPE + " = \"" + mimetype.split("/")[1] + "\"", null, null);
            if (cursor == null) { return null; }
            if (cursor.getCount() >= 1)
            {
                cursor.moveToFirst();
                return createMimeType(cursor);
            }
        }
        catch (Exception e)
        {
            // DO Nothing
            Log.e("Mimetype", Log.getStackTraceString(e));
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
        return null;
    }

    private static MimeType createMimeType(Cursor c)
    {
        return new MimeType(c.getLong(MimeTypeSchema.COLUMN_ID_ID), c.getString(MimeTypeSchema.COLUMN_EXTENSION_ID),
                c.getString(MimeTypeSchema.COLUMN_TYPE_ID), c.getString(MimeTypeSchema.COLUMN_SUBTYPE_ID),
                c.getString(MimeTypeSchema.COLUMN_DESCRIPTION_ID), c.getString(MimeTypeSchema.COLUMN_SMALL_ICON_ID),
                c.getString(MimeTypeSchema.COLUMN_LARGE_ICON_ID));
    }

    public MimeType createMimeType(String extension, String type, String subtype, String description, String smallIcon,
            String largeIcon)
    {
        Uri accountUri = appContext.getContentResolver().insert(MimeTypeProvider.CONTENT_URI,
                createContentValues(extension, type, subtype, description, smallIcon, largeIcon));

        if (accountUri == null) { return null; }

        return findById(Long.parseLong(accountUri.getLastPathSegment()));
    }

    public MimeType update(long id, String extension, String type, String subtype, String description,
            String smallIcon, String largeIcon)
    {
        appContext.getContentResolver().update(getUri(id),
                createContentValues(extension, type, subtype, description, smallIcon, largeIcon), null, null);

        return findById(id);
    }

    public static Uri getUri(long id)
    {
        return Uri.parse(MimeTypeProvider.CONTENT_URI + "/" + id);
    }

    public static ContentValues createContentValues(String extension, String type, String subtype, String description,
            String smallIcon, String largeIcon)
    {
        ContentValues updateValues = new ContentValues();

        updateValues.put(MimeTypeSchema.COLUMN_EXTENSION, extension);
        updateValues.put(MimeTypeSchema.COLUMN_TYPE, type);
        updateValues.put(MimeTypeSchema.COLUMN_SUBTYPE, subtype);
        updateValues.put(MimeTypeSchema.COLUMN_DESCRIPTION, description);
        updateValues.put(MimeTypeSchema.COLUMN_SMALL_ICON, smallIcon);
        updateValues.put(MimeTypeSchema.COLUMN_LARGE_ICON, largeIcon);
        return updateValues;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC
    // ///////////////////////////////////////////////////////////////////////////
    public boolean hasData()
    {
        getCount();
        return (mimetypeSize != null);
    }

    public boolean hasMimeTypes()
    {
        return mimetypeSize != null && (mimetypeSize > 0);
    }

    public boolean isEmpty()
    {
        getCount();
        return mimetypeSize == null || (mimetypeSize == 0);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Private
    // ///////////////////////////////////////////////////////////////////////////
    private void getCount()
    {
        Cursor cursor = null;
        try
        {
            cursor = appContext.getContentResolver().query(MimeTypeProvider.CONTENT_URI, COLUMN_ALL, null, null, null);
            if (cursor != null)
            {
                mimetypeSize = cursor.getCount();
            }
            else
            {
                mimetypeSize = 0;
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
