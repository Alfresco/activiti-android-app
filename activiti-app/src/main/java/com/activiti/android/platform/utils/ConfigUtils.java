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

import android.content.Context;

/**
 * Created by jpascal on 12/12/2014.
 */
public final class ConfigUtils
{
    public static final String FAMILY_CONFIG = "config";

    public static final String FAMILY_FORM = "form";

    public static final String FAMILY_VALIDATION = "validation";

    public static final String FAMILY_OPERATION = "operation";

    public static final String FAMILY_MANAGER = "manager";

    public static final String FAMILY_OPERATION_CALLBACK = "callback";

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    private static final String STRING = "string";

    private static final String SEPARATOR = "_";

    public static String getString(Context context, String family, String key)
    {
        int stringId = context.getResources().getIdentifier(
                family.concat(SEPARATOR).concat(key).replace(".", SEPARATOR), STRING,
                context.getApplicationContext().getPackageName());
        return (stringId == 0) ? null : context.getString(stringId);
    }

}
