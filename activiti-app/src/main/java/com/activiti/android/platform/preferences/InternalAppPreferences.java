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

package com.activiti.android.platform.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jpascal on 16/03/2015.
 */
public class InternalAppPreferences
{
    private static final String INTERNAL_PREFERENCES = "org.activiti.bpmn.android.preferences.internal";

    public static final String PREF_LAST_APP_USED = "org.activiti.bpmn.android.preferences.internal.apps.latest";

    public static final String PREF_LAST_FILTER_USED = "org.activiti.bpmn.android.preferences.internal.filter.latest";

    public static final String PREF_LAST_APP_NAME = "org.activiti.bpmn.android.preferences.internal.apps.latest.name";

    public static final String PREF_INTEGRATION_SYNCED = "org.activiti.bpmn.android.preferences.internal.apps.integration.synced";

    public static SharedPreferences getPref(Context context, long accountId)
    {
        return context
                .getSharedPreferences(INTERNAL_PREFERENCES.concat(Long.toString(accountId)), Context.MODE_PRIVATE);
    }

    public static void savePref(Context context, long accountId, String id, String value)
    {
        SharedPreferences settings = context.getSharedPreferences(
                INTERNAL_PREFERENCES.concat(Long.toString(accountId)), Context.MODE_PRIVATE);
        if (settings != null)
        {
            settings.edit().putString(id, value).apply();
        }
    }

    public static void savePref(Context context, long accountId, String id, Long value)
    {
        SharedPreferences settings = context.getSharedPreferences(
                INTERNAL_PREFERENCES.concat(Long.toString(accountId)), Context.MODE_PRIVATE);
        if (settings != null)
        {
            settings.edit().putLong(id, value).apply();
        }
    }

    public static Long getLongPref(Context context, long accountId, String id)
    {
        SharedPreferences settings = context.getSharedPreferences(
                INTERNAL_PREFERENCES.concat(Long.toString(accountId)), Context.MODE_PRIVATE);
        if (settings != null) { return settings.getLong(id, -1L); }
        return -1L;
    }

    public static String getStringPref(Context context, long accountId, String id)
    {
        SharedPreferences settings = context.getSharedPreferences(
                INTERNAL_PREFERENCES.concat(Long.toString(accountId)), Context.MODE_PRIVATE);
        if (settings != null) { return settings.getString(id, null); }
        return null;
    }

    public static void clean(Context context, long accountId)
    {
        SharedPreferences settings = context.getSharedPreferences(
                INTERNAL_PREFERENCES.concat(Long.toString(accountId)), Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }

}
