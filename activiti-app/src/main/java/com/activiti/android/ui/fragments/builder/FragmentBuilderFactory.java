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

package com.activiti.android.ui.fragments.builder;

import java.lang.reflect.Constructor;
import java.util.Map;

import android.app.Activity;
import android.util.Log;

import com.activiti.android.platform.utils.ConfigUtils;

public class FragmentBuilderFactory
{
    private static final String TAG = FragmentBuilderFactory.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // FACTORY
    // ///////////////////////////////////////////////////////////////////////////
    public static AlfrescoFragmentBuilder createViewConfig(Activity activity, String viewTemplate,
            Map<String, Object> configuration)
    {
        String s = ConfigUtils.getString(activity, ConfigUtils.FAMILY_CONFIG, viewTemplate);
        if (s == null) { return null; }
        return createBaseViewConfig(s, activity, configuration);
    }

    // ////////////////////////////////////////////////////
    // CREATION
    // ////////////////////////////////////////////////////
    private static AlfrescoFragmentBuilder createBaseViewConfig(String className, Activity activity,
            Map<String, Object> configuration)
    {
        AlfrescoFragmentBuilder s = null;
        try
        {
            Constructor<?> t = Class.forName(className).getDeclaredConstructor(Activity.class, Map.class);
            s = (AlfrescoFragmentBuilder) t.newInstance(activity, configuration);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            Log.e(TAG, "Error during BaseViewConfig creation : " + className);
        }
        return s;
    }
}
