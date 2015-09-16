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
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.FourLinesViewHolder;
import com.activiti.android.ui.utils.Formatter;
import com.activiti.client.api.model.runtime.TaskRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class TaskAdapter extends BaseListAdapter<TaskRepresentation, FourLinesViewHolder>
{
    protected Context context;

    protected List<TaskRepresentation> selectedTask;

    protected Typeface tf;

    public TaskAdapter(Activity context, int textViewResourceId, List<TaskRepresentation> listItems,
            List<TaskRepresentation> selectedTask)
    {
        super(context, textViewResourceId, listItems);
        this.context = context;
        this.vhClassName = FourLinesViewHolder.class.getName();
        this.selectedTask = selectedTask;

        try
        {
            String fontPath = "fonts/glyphicons-halflings-regular.ttf";
            tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        }
        catch (Exception e)
        {
            // No icons available
        }
    }

    @Override
    protected void updateTopText(FourLinesViewHolder vh, TaskRepresentation item)
    {
        vh.topText.setText(item.getName());
        vh.middleText.setText(createRelativeDateInfo(getContext(), item));

        if (selectedTask != null && selectedTask.contains(item))
        {
            ((View) vh.choose.getParent())
                    .setBackgroundColor(getContext().getResources().getColor(R.color.secondary_background));
        }
        else
        {
            ((View) vh.choose.getParent())
                    .setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    protected void updateBottomText(FourLinesViewHolder vh, TaskRepresentation item)
    {

        if (TextUtils.isEmpty(item.getDescription()))
        {
            vh.topTextRight.setVisibility(View.GONE);
        }
        else
        {
            vh.topTextRight.setVisibility(View.VISIBLE);
            vh.topTextRight.setText(item.getDescription());
        }

        if (item.getAssignee() != null)
        {
            vh.bottomText.setText(createAssigneeInfo(getContext(), item));
        }
    }

    @Override
    protected void updateIcon(FourLinesViewHolder vh, TaskRepresentation item)
    {
        if (tf != null)
        {
            vh.middleTextIcon.setText(Html.fromHtml("&#xe109"));
            vh.middleTextIcon.setTypeface(tf);
            vh.bottomTextIcon.setText(Html.fromHtml("&#xe008"));
            vh.bottomTextIcon.setTypeface(tf);
        }
        else
        {
            vh.middleTextIcon.setVisibility(View.GONE);
            vh.bottomTextIcon.setVisibility(View.GONE);
        }
    }

    public static String createRelativeDateInfo(Context context, TaskRepresentation item)
    {
        if (item.getDueDate() != null)
        {
            return String.format(context.getString(R.string.task_message_due_on),
                    Formatter.formatToRelativeDate(context, item.getDueDate()));
        }
        else if (item.getCreated() != null) { return String.format(context.getString(R.string.task_message_created_on),
                Formatter.formatToRelativeDate(context, item.getCreated())); }
        return "";
    }

    public static String createAssigneeInfo(Context context, TaskRepresentation item)
    {
        if (item.getAssignee() != null) { return item.getAssignee().getFullname(); }
        return "";
    }
}
