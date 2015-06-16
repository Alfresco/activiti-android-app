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

package com.activiti.android.ui.fragments.base;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.android.ui.utils.Formatter;

public abstract class BaseListAdapter<T, VH> extends ArrayAdapter<T>
{

    // ///////////////////////////////////////////////
    // ADAPTER
    // ///////////////////////////////////////////////
    public static final String DISPLAY_ICON = "org.activiti.bpmn.android.restManager.display.icon";

    public static final int DISPLAY_ICON_NONE = 0;

    public static final int DISPLAY_ICON_DEFAULT = 1;

    protected int iconItemType = DISPLAY_ICON_DEFAULT;

    public static final int DISPLAY_ICON_CREATOR = 2;

    public static final String DISPLAY_DATE = "org.activiti.bpmn.android.restManager.display.date";

    public static final int DISPLAY_DATE_NONE = 0;

    public static final int DISPLAY_DATE_RELATIVE = 1;

    protected int dateFormatType = DISPLAY_DATE_RELATIVE;

    public static final int DISPLAY_DATE_DATETIME = 2;

    public static final int DISPLAY_DATE_DATE = 3;

    public static final int DISPLAY_DATE_TIME = 4;

    private static final String TAG = "BaseListAdapter";

    // ///////////////////////////////////////////////
    // MEMBERS
    // ///////////////////////////////////////////////
    protected int textViewResourceId;

    protected String vhClassName;

    public BaseListAdapter(Context context, int textViewResourceId, List<T> objects)
    {
        super(context, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
        this.vhClassName = TwoLinesViewHolder.class.getCanonicalName();
    }

    public BaseListAdapter(Context context, int textViewResourceId, int textViewId, List<T> objects)
    {
        super(context, textViewResourceId, textViewId, objects);
        this.textViewResourceId = textViewResourceId;
        this.vhClassName = TwoLinesViewHolder.class.getCanonicalName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = recycleOrCreateView(getContext(), convertView, textViewResourceId);
        VH vh = (VH) v.getTag();
        T item = getItem(position);
        updateControls(vh, item);
        return v;
    }

    protected void updateControls(VH vh, T item)
    {
        if (item != null && vh != null)
        {
            updateTopText(vh, item);
            updateBottomText(vh, item);
            updateIcon(vh, item);
        }
    }

    protected abstract void updateTopText(VH vh, T item2);

    protected abstract void updateBottomText(VH vh, T item2);

    protected abstract void updateIcon(VH vh, T item2);

    protected View recycleOrCreateView(Context c, View v, int layoutId)
    {
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(layoutId, null);
            VH vh = create(vhClassName, v);
            v.setTag(vh);
        }
        return v;
    }

    protected View createView(Context c, View v, int layoutId)
    {
        LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(layoutId, null);
        VH vh = create(vhClassName, v);
        v.setTag(vh);
        return v;
    }

    @SuppressWarnings("unchecked")
    protected VH create(String className, View v)
    {
        VH s = null;
        try
        {
            Class<?> c = Class.forName(className);
            Constructor<?> t = c.getDeclaredConstructor(View.class);
            s = (VH) t.newInstance(v);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return s;
    }

    public void setFragmentSettings(Bundle fragmentSettings)
    {
        if (fragmentSettings != null)
        {
            if (fragmentSettings.containsKey(DISPLAY_DATE))
            {
                dateFormatType = (Integer) fragmentSettings.get(DISPLAY_DATE);
            }
            if (fragmentSettings.containsKey(DISPLAY_ICON))
            {
                iconItemType = (Integer) fragmentSettings.get(DISPLAY_ICON);
            }
        }
    }

    public String formatDate(Context c, Date date)
    {
        switch (dateFormatType)
        {
            case DISPLAY_DATE_RELATIVE:
                return Formatter.formatToRelativeDate(getContext(), date);
            case DISPLAY_DATE_NONE:
                return "";
            case DISPLAY_DATE_DATE:
                return DateFormat.getLongDateFormat(c).format(date);
            case DISPLAY_DATE_DATETIME:
                return date.toLocaleString();
            case DISPLAY_DATE_TIME:
                return DateFormat.getTimeFormat(c).format(date);
            default:
                break;
        }
        return "";
    }
}
