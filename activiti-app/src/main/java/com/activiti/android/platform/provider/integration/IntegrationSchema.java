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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Jean Marie Pascal
 */
public final class IntegrationSchema
{
    public static final String TABLENAME = "integration";

    // ////////////////////////////////////////////////////
    // DEBUG
    // ////////////////////////////////////////////////////
    private static final String QUERY_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLENAME;

    public static final String COLUMN_ID = "_id";

    public static final int COLUMN_ID_ID = 0;

    public static final String COLUMN_INTEGRATION_ID = "integrationId";

    public static final int COLUMN_INTEGRATION_ID_ID = COLUMN_ID_ID + 1;

    public static final String COLUMN_NAME = "name";

    public static final int COLUMN_NAME_ID = COLUMN_INTEGRATION_ID_ID + 1;

    public static final String COLUMN_USERNAME = "username";

    public static final int COLUMN_USERNAME_ID = COLUMN_NAME_ID + 1;

    public static final String COLUMN_TENANT_ID = "tenantId";

    public static final int COLUMN_TENANT_ID_ID = COLUMN_USERNAME_ID + 1;

    public static final String COLUMN_ALFRESCO_TENANT_ID = "alfrescoTenantId";

    public static final int COLUMN_ALFRESCO_TENANT_ID_ID = COLUMN_TENANT_ID_ID + 1;

    public static final String COLUMN_CREATED = "created";

    public static final int COLUMN_CREATED_ID = COLUMN_ALFRESCO_TENANT_ID_ID + 1;

    public static final String COLUMN_UPDATED = "lastUpdated";

    public static final int COLUMN_UPDATED_ID = COLUMN_CREATED_ID + 1;

    public static final String COLUMN_SHARE_URL = "shareUrl";

    public static final int COLUMN_SHARE_URL_ID = COLUMN_UPDATED_ID + 1;

    public static final String COLUMN_REPOSITORY_URL = "repositoryUrl";

    public static final int COLUMN_REPOSITORY_URL_ID = COLUMN_SHARE_URL_ID + 1;

    public static final String COLUMN_ACTIVITI_ACCOUNT_ID = "activitiAccountId";

    public static final int COLUMN_ACTIVITI_ACCOUNT_ID_ID = COLUMN_REPOSITORY_URL_ID + 1;

    public static final String COLUMN_ALFRESCO_ACCOUNT_ID = "alfrescoAccountId";

    public static final int COLUMN_ALFRESCO_ACCOUNT_ID_ID = COLUMN_ACTIVITI_ACCOUNT_ID_ID + 1;

    public static final String COLUMN_ALFRESCO_ACCOUNT_USERNAME = "alfrescoUsername";

    public static final int COLUMN_ALFRESCO_ACCOUNT_USERNAME_ID = COLUMN_ALFRESCO_ACCOUNT_ID_ID + 1;

    public static final String COLUMN_ALFRESCO_ACCOUNT_NAME = "alfrescoAccountName";

    public static final int COLUMN_ALFRESCO_ACCOUNT_NAME_ID = COLUMN_ALFRESCO_ACCOUNT_USERNAME_ID + 1;

    public static final String COLUMN_OPEN_TYPE = "openType";

    public static final int COLUMN_OPEN_TYPE_ID = COLUMN_ALFRESCO_ACCOUNT_NAME_ID + 1;

    public static final String[] COLUMN_ALL = { COLUMN_ID, COLUMN_INTEGRATION_ID, COLUMN_NAME, COLUMN_USERNAME,
            COLUMN_TENANT_ID, COLUMN_ALFRESCO_TENANT_ID, COLUMN_CREATED, COLUMN_UPDATED, COLUMN_SHARE_URL,
            COLUMN_REPOSITORY_URL, COLUMN_ACTIVITI_ACCOUNT_ID, COLUMN_ALFRESCO_ACCOUNT_ID,
            COLUMN_ALFRESCO_ACCOUNT_USERNAME, COLUMN_ALFRESCO_ACCOUNT_NAME, COLUMN_OPEN_TYPE };

    private static final String QUERY_TABLE_CREATE = "CREATE TABLE " + TABLENAME + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_INTEGRATION_ID + " INTEGER," + COLUMN_NAME + " TEXT,"
            + COLUMN_USERNAME + " TEXT," + COLUMN_TENANT_ID + " INTEGER," + COLUMN_ALFRESCO_TENANT_ID + " TEXT,"
            + COLUMN_CREATED + " TEXT," + COLUMN_UPDATED + " TEXT," + COLUMN_SHARE_URL + " TEXT,"
            + COLUMN_REPOSITORY_URL + " INTEGER," + COLUMN_ACTIVITI_ACCOUNT_ID + " INTEGER,"
            + COLUMN_ALFRESCO_ACCOUNT_ID + " INTEGER," + COLUMN_ALFRESCO_ACCOUNT_USERNAME + " TEXT,"
            + COLUMN_ALFRESCO_ACCOUNT_NAME + " TEXT," + COLUMN_OPEN_TYPE + " INTEGER" + ");";

    private IntegrationSchema()
    {
    }

    public static void onCreate(Context context, SQLiteDatabase db)
    {
        create(db);
    }

    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    private static void create(SQLiteDatabase db)
    {
        db.execSQL(QUERY_TABLE_CREATE);
    }

    public static long insert(SQLiteDatabase db, long integrationId, String name, String username, long tenantId,
            String alfrescoTenantId, String created, String updated, String shareUrl, String repositoryUrl,
            long activitiId, long alfrescoId, String alfrescoName, String alfrescoUsername, int openType)
    {
        ContentValues insertValues = IntegrationManager.createContentValues(integrationId, name, username, tenantId,
                alfrescoTenantId, created, updated, shareUrl, repositoryUrl, activitiId, alfrescoId, alfrescoName,
                alfrescoUsername, openType);
        return db.insert(IntegrationSchema.TABLENAME, null, insertValues);
    }

    /** Use with Caution ! */
    public static void reset(SQLiteDatabase db)
    {
        db.execSQL(QUERY_TABLE_DROP);
        create(db);
    }

}
