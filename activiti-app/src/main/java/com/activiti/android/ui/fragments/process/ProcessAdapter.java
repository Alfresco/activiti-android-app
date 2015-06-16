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

package com.activiti.android.ui.fragments.process;

import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.ThreeLinesViewHolder;
import com.activiti.android.ui.utils.Formatter;
import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class ProcessAdapter extends BaseListAdapter<ProcessInstanceRepresentation, ThreeLinesViewHolder>
{
    protected Context context;

    public ProcessAdapter(Activity context, int textViewResourceId, List<ProcessInstanceRepresentation> listItems)
    {
        super(context, textViewResourceId, listItems);
        this.context = context;
        this.vhClassName = ThreeLinesViewHolder.class.getName();
    }

    @Override
    protected void updateTopText(ThreeLinesViewHolder vh, ProcessInstanceRepresentation item)
    {
        vh.topText.setText(item.getName());
        if (item.getEnded() != null)
        {
            vh.topTextRight.setText(String.format(context.getString(R.string.task_message_ended_on),
                    Formatter.formatToRelativeDate(getContext(), item.getEnded())));
        }
        else if (item.getStarted() != null)
        {
            vh.topTextRight.setText(String.format(context.getString(R.string.process_message_started),
                    Formatter.formatToRelativeDate(getContext(), item.getStarted())));
        }
    }

    @Override
    protected void updateBottomText(ThreeLinesViewHolder vh, ProcessInstanceRepresentation item)
    {
        if (item.getStartedBy() != null)
        {
            vh.middleText.setText(String.format(context.getString(R.string.process_message_started_by), item
                    .getStartedBy().getFullname()));
        }

        if (item.getProcessDefinitionName() != null)
        {
            vh.bottomText.setText(item.getProcessDefinitionName());
        }
    }

    @Override
    protected void updateIcon(ThreeLinesViewHolder vh, ProcessInstanceRepresentation item)
    {
        // vh.icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_));
    }
}
