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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Jean Marie Pascal
 */
public final class GroupInstanceSchema
{

    public static final String TABLENAME = "usergroup";

    // ////////////////////////////////////////////////////
    // DEBUG
    // ////////////////////////////////////////////////////
    private static final String QUERY_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLENAME;

    public static final String COLUMN_ID = "_id";

    public static final int COLUMN_ID_ID = 0;

    public static final String COLUMN_GROUP_ID = "groupId";

    public static final int COLUMN_GROUP_ID_ID = COLUMN_ID_ID + 1;

    public static final String COLUMN_ACCOUNT_ID = "accountId";

    public static final int COLUMN_ACCOUNT_ID_ID = COLUMN_GROUP_ID_ID + 1;

    public static final String COLUMN_NAME = "name";

    public static final int COLUMN_NAME_ID = COLUMN_ACCOUNT_ID_ID + 1;

    public static final String COLUMN_TYPE = "type";

    public static final int COLUMN_TYPE_ID = COLUMN_NAME_ID + 1;

    public static final String COLUMN_PARENT_GROUP_ID = "parentGroupId";

    public static final int COLUMN_PARENT_GROUP_ID_ID = COLUMN_TYPE_ID + 1;

    public static final String COLUMN_STATUS = "status";

    public static final int COLUMN_STATUS_ID = COLUMN_PARENT_GROUP_ID_ID + 1;

    public static final String COLUMN_EXTERNAL_ID = "externalId";

    public static final int COLUMN_EXTERNAL_ID_ID = COLUMN_STATUS_ID + 1;

    public static final String[] COLUMN_ALL = { COLUMN_ID, COLUMN_GROUP_ID, COLUMN_ACCOUNT_ID, COLUMN_NAME,
            COLUMN_TYPE, COLUMN_PARENT_GROUP_ID, COLUMN_STATUS, COLUMN_EXTERNAL_ID };

    private static final String QUERY_TABLE_CREATE = "CREATE TABLE " + TABLENAME + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_GROUP_ID + " INTEGER," + COLUMN_ACCOUNT_ID + " INTEGER,"
            + COLUMN_NAME + " TEXT," + COLUMN_TYPE + " INTEGER," + COLUMN_PARENT_GROUP_ID + " INTEGER," + COLUMN_STATUS
            + " TEXT," + COLUMN_EXTERNAL_ID + " TEXT" + ");";

    private GroupInstanceSchema()
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

    public static long insert(SQLiteDatabase db, Long groupId, Long accountId, String name, int type,
            Long parentGroupId, String status, String externalId)
    {
        ContentValues insertValues = GroupInstanceManager.createContentValues(groupId, accountId, name, type,
                parentGroupId, status, externalId);
        return db.insert(GroupInstanceSchema.TABLENAME, null, insertValues);
    }

    /** Use with Caution ! */
    public static void reset(SQLiteDatabase db)
    {
        db.execSQL(QUERY_TABLE_DROP);
        create(db);
    }
}
