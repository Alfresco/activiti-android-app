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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Jean Marie Pascal
 */
public final class ProcessDefinitionModelSchema
{

    public static final String TABLENAME = "processDefinitionModel";

    // ////////////////////////////////////////////////////
    // DEBUG
    // ////////////////////////////////////////////////////
    private static final String QUERY_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLENAME;

    public static final String COLUMN_ID = "_id";

    public static final int COLUMN_ID_ID = 0;

    public static final String COLUMN_PROCESS_DEFINITION_ID = "processDefinitionId";

    public static final int COLUMN_PROCESS_DEFINITION_ID_ID = COLUMN_ID_ID + 1;

    public static final String COLUMN_ACCOUNT_ID = "accountId";

    public static final int COLUMN_ACCOUNT_ID_ID = COLUMN_PROCESS_DEFINITION_ID_ID + 1;

    public static final String COLUMN_APP_ID = "appId";

    public static final int COLUMN_APP_ID_ID = COLUMN_ACCOUNT_ID_ID + 1;

    public static final String COLUMN_NAME = "name";

    public static final int COLUMN_NAME_ID = COLUMN_APP_ID_ID + 1;

    public static final String COLUMN_DESCRIPTION = "description";

    public static final int COLUMN_DESCRIPTION_ID = COLUMN_NAME_ID + 1;

    public static final String COLUMN_VERSION = "version";

    public static final int COLUMN_VERSION_ID = COLUMN_DESCRIPTION_ID + 1;

    public static final String COLUMN_HAS_START_FORM = "hasStartForm";

    public static final int COLUMN_HAS_START_FORM_ID = COLUMN_VERSION_ID + 1;

    public static final String[] COLUMN_ALL = { COLUMN_ID, COLUMN_PROCESS_DEFINITION_ID, COLUMN_ACCOUNT_ID,
            COLUMN_APP_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_VERSION, COLUMN_HAS_START_FORM };

    private static final String QUERY_TABLE_CREATE = "CREATE TABLE " + TABLENAME + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PROCESS_DEFINITION_ID + " TEXT," + COLUMN_ACCOUNT_ID
            + " INTEGER," + COLUMN_APP_ID + " INTEGER," + COLUMN_NAME + " TEXT," + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_VERSION + " INTEGER," + COLUMN_HAS_START_FORM + " INTEGER" + ");";

    private ProcessDefinitionModelSchema()
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

    public static long insert(SQLiteDatabase db, String modelId, Long accountId, Long appId, String name,
            String description, Integer version, Boolean hasStartForm)
    {
        ContentValues insertValues = ProcessDefinitionModelManager.createContentValues(modelId, accountId, appId, name,
                description, version, hasStartForm);
        return db.insert(ProcessDefinitionModelSchema.TABLENAME, null, insertValues);
    }

    /** Use with Caution ! */
    public static void reset(SQLiteDatabase db)
    {
        db.execSQL(QUERY_TABLE_DROP);
        create(db);
    }
}
