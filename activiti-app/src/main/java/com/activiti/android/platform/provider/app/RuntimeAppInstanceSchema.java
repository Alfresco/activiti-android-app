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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Jean Marie Pascal
 */
public final class RuntimeAppInstanceSchema
{

    public static final String TABLENAME = "applications";

    // ////////////////////////////////////////////////////
    // DEBUG
    // ////////////////////////////////////////////////////
    private static final String QUERY_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLENAME;

    public static final String COLUMN_ID = "_id";

    public static final int COLUMN_ID_ID = 0;

    public static final int COLUMN_ACCOUNT_ID_ID = COLUMN_ID_ID + 1;

    public static final int COLUMN_APP_ID_ID = COLUMN_ACCOUNT_ID_ID + 1;

    public static final int COLUMN_NAME_ID = COLUMN_APP_ID_ID + 1;

    public static final int COLUMN_MODEL_ID = COLUMN_NAME_ID + 1;

    public static final int COLUMN_THEME_ID = COLUMN_MODEL_ID + 1;

    public static final int COLUMN_DESCRIPTION_ID = COLUMN_THEME_ID + 1;

    public static final int COLUMN_ICON_ID = COLUMN_DESCRIPTION_ID + 1;

    public static final int COLUMN_DEPLOYMENT_ID = COLUMN_ICON_ID + 1;

    public static final int COLUMN_NUMBER_1_ID = COLUMN_DEPLOYMENT_ID + 1;

    public static final int COLUMN_NUMBER_2_ID = COLUMN_NUMBER_1_ID + 1;

    public static final int COLUMN_NUMBER_3_ID = COLUMN_NUMBER_2_ID + 1;

    public static final String COLUMN_ACCOUNT_ID = "accountId";

    public static final String COLUMN_APP_ID = "appId";

    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_DESCRIPTION = "description";

    public static final String COLUMN_MODEL = "modelId";

    public static final String COLUMN_THEME = "theme";

    public static final String COLUMN_ICON = "icon";

    public static final String COLUMN_DEPLOYMENT = "deployment";

    public static final String COLUMN_NUMBER_1 = "number1";

    public static final String COLUMN_NUMBER_2 = "number2";

    public static final String COLUMN_NUMBER_3 = "number3";

    public static final String[] COLUMN_ALL = { COLUMN_ID, COLUMN_ACCOUNT_ID, COLUMN_APP_ID, COLUMN_NAME, COLUMN_MODEL,
            COLUMN_THEME, COLUMN_DESCRIPTION, COLUMN_ICON, COLUMN_DEPLOYMENT, COLUMN_NUMBER_1, COLUMN_NUMBER_2,
            COLUMN_NUMBER_3 };

    private static final String QUERY_TABLE_CREATE = "CREATE TABLE " + TABLENAME + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ACCOUNT_ID + " INTEGER," + COLUMN_APP_ID + " INTEGER,"
            + COLUMN_NAME + " TEXT ," + COLUMN_MODEL + " INTEGER," + COLUMN_THEME + " TEXT NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT," + COLUMN_ICON + " TEXT," + COLUMN_DEPLOYMENT + " TEXT," + COLUMN_NUMBER_1
            + " INTEGER," + COLUMN_NUMBER_2 + " INTEGER," + COLUMN_NUMBER_3 + " INTEGER" + ");";

    private RuntimeAppInstanceSchema()
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

    public static long insert(SQLiteDatabase db, long accountId, long appId, String name, String model, String theme,
            String description, String icon, String deployment, int number1, int number2, int number3)
    {
        ContentValues insertValues = RuntimeAppInstanceManager.createContentValues(accountId, appId, name, model,
                theme, description, icon, deployment, number1, number2, number3);
        return db.insert(RuntimeAppInstanceSchema.TABLENAME, null, insertValues);
    }

    /** Use with Caution ! */
    public static void reset(SQLiteDatabase db)
    {
        db.execSQL(QUERY_TABLE_DROP);
        create(db);
    }

}
