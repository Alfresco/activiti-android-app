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

package com.activiti.android.ui.form.fields;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.SingleLineViewHolder;

/**
 * @author Jean Marie Pascal
 */
public class TypeAheadAdapter extends BaseListAdapter<String, SingleLineViewHolder>
{
    protected Context context;

    public TypeAheadAdapter(Activity context, int textViewResourceId, List<String> listItems)
    {
        super(context, textViewResourceId, listItems);
        this.context = context;
        this.vhClassName = SingleLineViewHolder.class.getName();
    }

    @Override
    protected void updateTopText(SingleLineViewHolder vh, String item)
    {
        vh.topText.setText(item);
        vh.topText.setSingleLine(false);
        vh.topText.setMaxLines(10);
    }

    @Override
    protected void updateBottomText(SingleLineViewHolder vh, String item)
    {
    }

    @Override
    protected void updateIcon(SingleLineViewHolder vh, String item)
    {
        vh.icon.setVisibility(View.GONE);
    }
}
