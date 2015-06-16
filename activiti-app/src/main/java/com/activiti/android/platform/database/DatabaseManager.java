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

package com.activiti.android.platform.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Jean Marie Pascal
 */
public abstract class DatabaseManager extends SQLiteOpenHelper implements DatabaseVersionNumber
{
    public static final int DATABASE_VERSION = LATEST_VERSION;

    protected static final String DATABASE_NAME = "ActivitiDB";

    protected static final Object LOCK = new Object();

    protected static DatabaseManager mInstance;

    protected Context ctx;

    protected SQLiteDatabase sqliteDb;

    protected DatabaseManager(Context context)
    {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    public SQLiteDatabase getWriteDb()
    {
        if (sqliteDb == null || !sqliteDb.isOpen())
        {
            sqliteDb = getWritableDatabase();
        }
        while (sqliteDb.isDbLockedByCurrentThread() || sqliteDb.isDbLockedByOtherThreads())
        {
            // db is locked, keep looping
        }
        return sqliteDb;
    }

    public void close()
    {
        if (sqliteDb != null)
        {
            sqliteDb.close();
        }
    }
}
