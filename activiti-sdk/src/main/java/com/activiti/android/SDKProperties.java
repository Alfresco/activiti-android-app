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

package com.activiti.android;

import java.io.IOException;
import java.util.Properties;

/**
 * Responsible to retrieves informations about SDK.
 *
 * @author Jean Marie Pascal
 */
public final class SDKProperties
{
    private SDKProperties()
    {
    }

    /**
     * Retrieve the value for the specified key.
     * 
     * @param key
     * @return value associated to the key. Null if the key doesn't exist.
     */
    public static String getString(String key)
    {
        try
        {
            Properties properties = new Properties();
            properties.load(SDKProperties.class.getResourceAsStream("/com/activiti/android/api/version.properties"));
            return (String) properties.get(key);
        }
        catch (IOException e)
        {
            return null;
        }
    }

}