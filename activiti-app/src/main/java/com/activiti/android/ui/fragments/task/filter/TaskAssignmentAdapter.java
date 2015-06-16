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

package com.activiti.android.ui.fragments.task.filter;

import java.util.List;

import android.app.Activity;
import android.view.View;

import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.SingleLineViewHolder;

/**
 * Created by jpascal on 01/04/2015.
 */
public class TaskAssignmentAdapter extends
        BaseListAdapter<TaskAssignmentDialogFragment.AssignmentItem, SingleLineViewHolder>
{
    public TaskAssignmentAdapter(Activity context, int textViewResourceId,
            List<TaskAssignmentDialogFragment.AssignmentItem> listItems)
    {
        super(context, textViewResourceId, listItems);
        vhClassName = SingleLineViewHolder.class.getName();
    }

    @Override
    protected void updateTopText(SingleLineViewHolder vh, TaskAssignmentDialogFragment.AssignmentItem item)
    {
        vh.topText.setText(item.label);
        vh.topText.setMaxLines(2);
        vh.topText.setSingleLine(false);
    }

    @Override
    protected void updateBottomText(SingleLineViewHolder vh, TaskAssignmentDialogFragment.AssignmentItem item)
    {
    }

    @Override
    protected void updateIcon(SingleLineViewHolder vh, TaskAssignmentDialogFragment.AssignmentItem item)
    {
        vh.icon.setVisibility(View.GONE);
    }
}
