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
import android.view.LayoutInflater;
import android.view.View;

import com.activiti.android.ui.holder.TwoLinesViewHolder;

/**
 * List of static utils methods that can be used inside restManager.
 * 
 * @author Jean Marie Pascal
 */
public final class AdapterUtils
{
    private AdapterUtils()
    {
    }

    /**
     * Create or recycle View inside a listview.
     * 
     * @param c : Android context.
     * @param v : getProcessInstances view item.
     * @param layoutId : Unique identifier for the ressource layout.
     * @return a new or recycled item.
     */
    public static View recycleOrCreateView(Context c, View v, int layoutId)
    {
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(layoutId, null);
            TwoLinesViewHolder vh = new TwoLinesViewHolder(v);
            v.setTag(vh);
        }
        return v;
    }
}
