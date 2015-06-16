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

package com.activiti.android.sdk.model;

import com.activiti.client.api.constant.RequestConstant;

/**
 * Created by jpascal on 13/03/2015.
 */
public enum ProcessSorting
{
    CREATED_ASC(RequestConstant.SORT_CREATED_ASC), CREATED_DESC(RequestConstant.SORT_CREATED_DESC);

    private final String value;

    ProcessSorting(String v)
    {
        value = v;
    }

    public static ProcessSorting fromValue(String v)
    {
        for (ProcessSorting c : ProcessSorting.values())
        {
            if (c.value.equals(v)) { return c; }
        }
        throw new IllegalArgumentException(v);
    }

    public String value()
    {
        return value;
    }
}
