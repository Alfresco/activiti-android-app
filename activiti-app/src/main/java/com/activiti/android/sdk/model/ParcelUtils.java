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

import java.util.Date;

import android.os.Parcel;

/**
 * Created by jpascal on 03/04/2015.
 */
public class ParcelUtils
{
    public static final Long LONG_NULL = -9L;

    public static void writeDate(Parcel dest, Date date)
    {
        if (date != null)
        {
            dest.writeLong(date.getTime());
        }
        else
        {
            dest.writeLong(LONG_NULL);
        }
    }

    public static Date readDate(Parcel dest)
    {
        Long value = dest.readLong();
        return (value == LONG_NULL) ? null : new Date(value);
    }

    public static void writeLong(Parcel dest, Long longValue)
    {
        if (longValue != null)
        {
            dest.writeLong(longValue);
        }
        else
        {
            dest.writeLong(LONG_NULL);
        }
    }

    public static Long readLong(Parcel dest)
    {
        Long value = dest.readLong();
        return (value == LONG_NULL) ? null : value;
    }

}
