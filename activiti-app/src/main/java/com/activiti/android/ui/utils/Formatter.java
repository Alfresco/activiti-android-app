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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Resources;

import com.activiti.android.app.R;

public final class Formatter
{

    private static final int MINUTE_IN_SECONDS = 60;

    private static final int HOUR_IN_SECONDS = 3600;

    private static final int HOUR_IN_MINUTES = 60;

    private static final int DAY_IN_SECONDS = 86400;

    private static final int DAY_IN_HOURS = 24;

    private static final int YEAR_IN_SECONDS = 31536000;

    private static final int YEAR_IN_DAYS = 365;

    private Formatter()
    {
    }

    /**
     * Format a date into a relative human readable date.
     * 
     * @param c
     * @param date
     * @return
     */
    public static String formatToRelativeDate(Context c, Date date)
    {
        if (date == null) { return null; }

        Resources res = c.getResources();

        Date todayDate = new Date();
        float ti = (todayDate.getTime() - date.getTime()) / 1000;

        if (ti < 1)
        {
            return res.getString(R.string.relative_date_just_now);
        }
        else if (ti < MINUTE_IN_SECONDS)
        {
            return res.getString(R.string.relative_date_less_than_a_minute_ago);
        }
        else if (ti < HOUR_IN_SECONDS)
        {
            int diff = Math.round(ti / MINUTE_IN_SECONDS);
            return String.format(res.getQuantityString(R.plurals.relative_date_minutes_ago, diff), diff);
        }
        else if (ti < DAY_IN_SECONDS)
        {
            int diff = Math.round(ti / MINUTE_IN_SECONDS / HOUR_IN_MINUTES);
            return String.format(res.getQuantityString(R.plurals.relative_date_hours_ago, diff), diff);
        }
        else if (ti < YEAR_IN_SECONDS)
        {
            int diff = Math.round(ti / MINUTE_IN_SECONDS / HOUR_IN_MINUTES / DAY_IN_HOURS);
            return String.format(res.getQuantityString(R.plurals.relative_date_days_ago, diff), diff);
        }
        else
        {
            int diff = Math.round(ti / MINUTE_IN_SECONDS / HOUR_IN_MINUTES / DAY_IN_HOURS / YEAR_IN_DAYS);
            return String.format(res.getQuantityString(R.plurals.relative_date_years_ago, diff), diff);
        }
    }

    public static String formatDuration(Context c, long millis)
    {
        if (millis < 0) { return null; }

        Resources res = c.getResources();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long hours = TimeUnit.MILLISECONDS.toHours(millis)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        long days = TimeUnit.MILLISECONDS.toDays(millis);

        StringBuilder builder = new StringBuilder();
        if (days > 0 && hours >= 0)
        {
            builder.append(String.format(res.getQuantityString(R.plurals.duration_days, intValue(seconds)), days));
            builder.append(", ");
            builder.append(String.format(res.getQuantityString(R.plurals.duration_hours, intValue(hours)), hours));
        }
        else if (hours > 0 && minutes >= 0)
        {
            builder.append(String.format(res.getQuantityString(R.plurals.duration_hours, intValue(hours)), hours));
            builder.append(", ");
            builder.append(String.format(res.getQuantityString(R.plurals.duration_minutes, intValue(minutes)), minutes));
        }
        else if (minutes > 0 && seconds >= 0)
        {
            builder.append(String.format(res.getQuantityString(R.plurals.duration_minutes, intValue(minutes)), minutes));
            builder.append(", ");
            builder.append(String.format(res.getQuantityString(R.plurals.duration_seconds, intValue(seconds)), seconds));
        }
        else if (seconds > 0)
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }
            builder.append(String.format(res.getQuantityString(R.plurals.duration_seconds, intValue(seconds)), seconds));
        }

        return builder.toString();
    }

    public static int intValue(long value)
    {
        int valueInt = (int) value;
        if (valueInt != value) { throw new IllegalArgumentException("The long value " + value
                + " is not within range of the int type"); }
        return valueInt;
    }

    /**
     * Format a file size in human readable text.
     * 
     * @param context
     * @param sizeInByte
     * @return
     */
    public static String formatFileSize(Context context, long sizeInByte)
    {
        return android.text.format.Formatter.formatShortFileSize(context, sizeInByte);
    }
}
