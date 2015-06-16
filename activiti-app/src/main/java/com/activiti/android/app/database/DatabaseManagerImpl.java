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

package com.activiti.android.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.activiti.android.platform.database.DatabaseManager;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceSchema;
import com.activiti.android.platform.provider.appIcon.AppIconSchema;
import com.activiti.android.platform.provider.group.GroupInstanceSchema;
import com.activiti.android.platform.provider.integration.IntegrationSchema;
import com.activiti.android.platform.provider.mimetype.MimeTypeSchema;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelSchema;

/**
 * @author Jean Marie Pascal
 */
public class DatabaseManagerImpl extends DatabaseManager
{
    protected DatabaseManagerImpl(Context context)
    {
        super(context);
    }

    public static DatabaseManagerImpl getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new DatabaseManagerImpl(context.getApplicationContext());
            }

            return (DatabaseManagerImpl) mInstance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        ProcessDefinitionModelSchema.onCreate(ctx, db);
        RuntimeAppInstanceSchema.onCreate(ctx, db);
        IntegrationSchema.onCreate(ctx, db);
        GroupInstanceSchema.onCreate(ctx, db);
        MimeTypeSchema.onCreate(ctx, db);
        AppIconSchema.onCreate(ctx, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        ProcessDefinitionModelSchema.onUpgrade(ctx, db, oldVersion, newVersion);
        RuntimeAppInstanceSchema.onUpgrade(ctx, db, oldVersion, newVersion);
        IntegrationSchema.onUpgrade(ctx, db, oldVersion, newVersion);
        GroupInstanceSchema.onUpgrade(ctx, db, oldVersion, newVersion);
        MimeTypeSchema.onUpgrade(ctx, db, oldVersion, newVersion);
        AppIconSchema.onUpgrade(ctx, db, oldVersion, newVersion);
    }

}
