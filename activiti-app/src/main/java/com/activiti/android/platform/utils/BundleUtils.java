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

package com.activiti.android.platform.utils;

import java.io.Serializable;

import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by jpascal on 12/12/2014.
 */
public class BundleUtils
{

    public static void addIfNotNull(Bundle b, String key, Serializable value)
    {
        if (value != null)
        {
            b.putSerializable(key, value);
        }
    }

    public static void addIfNotEmpty(Bundle b, Bundle extra)
    {
        if (extra != null)
        {
            b.putAll(extra);
        }
    }

    public static void addIfNotEmpty(Bundle b, String key, String value)
    {
        if (!TextUtils.isEmpty(value))
        {
            b.putString(key, value);
        }
    }

    public static void addIfNotNull(Bundle b, String key, Integer value)
    {
        if (value != null)
        {
            b.putInt(key, value);
        }
    }

    public static String getString(Bundle arguments, String key)
    {
        if (arguments.containsKey(key))
        {
            return arguments.getString(key);
        }
        else
        {
            return null;
        }
    }

    public static Boolean getBoolean(Bundle arguments, String key)
    {
        if (arguments != null && arguments.containsKey(key))
        {
            return arguments.getBoolean(key);
        }
        else
        {
            return null;
        }
    }

    public static Long getLong(Bundle arguments, String key)
    {
        if (arguments.containsKey(key))
        {
            return arguments.getLong(key);
        }
        else
        {
            return null;
        }
    }

    public static Integer getInt(Bundle arguments, String key)
    {
        if (arguments.containsKey(key))
        {
            return arguments.getInt(key);
        }
        else
        {
            return null;
        }
    }

}
