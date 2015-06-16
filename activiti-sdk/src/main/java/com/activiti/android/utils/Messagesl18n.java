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

package com.activiti.android.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides utils to internationalize error exception messages.
 * 
 * @author Jean Marie Pascal
 */
public final class Messagesl18n
{
    private static final String BUNDLE_NAME = "org.activiti.bpmn.android.messages.messages";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("en", "US"));

    private Messagesl18n()
    {
    }

    public static String getString(String key)
    {
        try
        {
            if (key != null && key.length() > 0)
            {
                return RESOURCE_BUNDLE.getString(key);
            }
            else
            {
                return "";
            }
        }
        catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }
}
