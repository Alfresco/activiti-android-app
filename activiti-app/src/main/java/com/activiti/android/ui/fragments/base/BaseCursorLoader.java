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

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.activiti.android.ui.holder.TwoLinesViewHolder;

public abstract class BaseCursorLoader<VH> extends CursorAdapter
{
    private static final String TAG = BaseCursorLoader.class.getName();

    protected int layoutResourceId;

    protected String vhClassName;

    protected Context context;

    public BaseCursorLoader(Context context, Cursor c, int layoutId)
    {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.layoutResourceId = layoutId;
        this.vhClassName = TwoLinesViewHolder.class.getCanonicalName();
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View view = LayoutInflater.from(context).inflate(layoutResourceId, null);
        VH vh = create(vhClassName, view);
        updateControls(vh, cursor);
        view.setTag(vh);
        return view;
    }

    protected View createView(Context c, Cursor cursor, int layoutId)
    {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        VH vh = create(vhClassName, view);
        updateControls(vh, cursor);
        view.setTag(vh);
        return view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        VH vh = (VH) view.getTag();
        updateControls(vh, cursor);
    }

    protected void updateControls(VH vh, Cursor cursor)
    {
        if (vh != null)
        {
            updateTopText(vh, cursor);
            updateBottomText(vh, cursor);
            updateIcon(vh, cursor);
        }
    }

    protected abstract void updateTopText(VH vh, Cursor cursor);

    protected abstract void updateBottomText(VH vh, Cursor cursor);

    protected abstract void updateIcon(VH vh, Cursor cursor);

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

}
