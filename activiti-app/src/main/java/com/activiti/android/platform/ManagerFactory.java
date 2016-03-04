/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
 *
 *  This file is part of Alfresco Activiti Mobile for Android.
 *
 *  Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package com.activiti.android.platform;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

public final class ManagerFactory
{
    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    private static final String STRING = "string";

    public static final String FAMILY_MANAGER = "manager";

    private static final String BOOLEAN = "bool";

    private static final String SEPARATOR = "_";

    private static final String ISENABLE = FAMILY_MANAGER.concat("_isEnable");

    private ManagerFactory()
    {
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    protected static int getBooleanId(Context context, String key)
    {
        return context.getResources().getIdentifier(ISENABLE.concat(SEPARATOR).concat(key).replace(".", SEPARATOR),
                BOOLEAN, context.getApplicationContext().getPackageName());
    }

    protected static boolean isEnable(Context context, String key)
    {
        try
        {
            return context.getResources().getBoolean(getBooleanId(context, key));
        }
        catch (NotFoundException e)
        {
            return false;
        }
    }

    public static String getString(Context context, String family, String key)
    {
        int stringId = context.getResources().getIdentifier(
                family.concat(SEPARATOR).concat(key).replace(".", SEPARATOR).replace("-", SEPARATOR), STRING,
                context.getApplicationContext().getPackageName());
        return (stringId == 0) ? null : context.getString(stringId);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // FACTORY
    // ///////////////////////////////////////////////////////////////////////////
    public static Manager getManager(Context context, String managerName)
    {
        if (!isEnable(context, managerName)) { return null; }
        String s = getString(context, FAMILY_MANAGER, managerName);
        if (s == null)
        {
            Log.e("ApplicationManager", "Unable to retrieve Manager definition for : " + managerName);
            return null;
        }
        return createManager(s, context);
    }

    private static Manager createManager(String className, Context context)
    {
        Manager s = null;
        try
        {
            Class<?> c = Class.forName(className);
            Method method = c.getMethod("getInstance", Context.class);
            s = (Manager) method.invoke(null, context);
        }
        catch (Exception e)
        {
            // DO Nothing
            Log.e("ApplicationManager", "Error during Operation creation : " + className);
        }
        return s;
    }
}
