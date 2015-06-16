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

package com.activiti.android.ui.fragments.task;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.ThreeLinesViewHolder;
import com.activiti.android.ui.utils.Formatter;
import com.activiti.client.api.model.runtime.TaskRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class TaskAdapter extends BaseListAdapter<TaskRepresentation, ThreeLinesViewHolder>
{
    protected Context context;

    public TaskAdapter(Activity context, int textViewResourceId, List<TaskRepresentation> listItems)
    {
        super(context, textViewResourceId, listItems);
        this.context = context;
        this.vhClassName = ThreeLinesViewHolder.class.getName();
    }

    @Override
    protected void updateTopText(ThreeLinesViewHolder vh, TaskRepresentation item)
    {
        vh.topText.setText(item.getName());
        if (item.getDueDate() != null)
        {
            vh.topTextRight.setText(String.format(context.getString(R.string.task_message_due_on),
                    Formatter.formatToRelativeDate(getContext(), item.getDueDate())));
        }
        else if (item.getCreated() != null)
        {
            vh.topTextRight.setText(String.format(context.getString(R.string.task_message_created_on),
                    Formatter.formatToRelativeDate(getContext(), item.getCreated())));
        }
    }

    @Override
    protected void updateBottomText(ThreeLinesViewHolder vh, TaskRepresentation item)
    {
        if (TextUtils.isEmpty(item.getDescription()))
        {
            vh.middleText.setText(R.string.task_message_no_description);
        }
        else
        {
            vh.middleText.setText(item.getDescription());
        }

        if (item.getAssignee() != null)
        {
            vh.bottomText.setText(String.format(context.getString(R.string.task_message_assignee), item.getAssignee()
                    .getFullname()));
        }
    }

    @Override
    protected void updateIcon(ThreeLinesViewHolder vh, TaskRepresentation item)
    {
        // vh.icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_action_gear));
    }
}
