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

package com.activiti.android.ui.utils;

import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;

import com.activiti.android.platform.utils.AndroidVersion;

/**
 * @author Jean Marie Pascal
 */
public class AccessibilityUtils
{

    public static AccessibilityManager getAccessibilityManager(Context context)
    {
        return (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public static boolean isEnabled(Context context)
    {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        return am.isEnabled();
    }

    public static boolean isTouchExplorationEnabled(Context context)
    {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        return am.isTouchExplorationEnabled();
    }

    public static void sendAccessibilityEvent(Context context)
    {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am.isEnabled())
        {
            am.sendAccessibilityEvent(AccessibilityEvent.obtain(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED));
        }
    }

    public static void addContentDescription(View v, int contentDescriptionId)
    {
        if (isEnabled(v.getContext()))
        {
            addContentDescription(v, v.getContext().getString(contentDescriptionId));
        }
    }

    public static void addHint(View v, int contentDescriptionId)
    {
        if (isEnabled(v.getContext()) && v instanceof EditText)
        {
            ((EditText) v).setHint(v.getContext().getString(contentDescriptionId));
        }
    }

    public static void addContentDescription(View v, String contentDescription)
    {
        if (isEnabled(v.getContext()))
        {
            if (AndroidVersion.isJBOrAbove())
            {
                v.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            }
            v.setContentDescription(contentDescription);
        }
    }

    public static void removeContentDescription(View v)
    {
        if (isEnabled(v.getContext()))
        {
            if (AndroidVersion.isJBOrAbove())
            {
                v.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
            else
            {
                v.setContentDescription("\u00A0");
            }
        }
    }
}
