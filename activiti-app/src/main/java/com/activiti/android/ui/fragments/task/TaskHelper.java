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

import android.text.TextUtils;

import com.activiti.client.api.model.idm.LightUserRepresentation;
import com.activiti.client.api.model.runtime.TaskRepresentation;

/**
 * Created by jpascal on 24/03/2015.
 */
public class TaskHelper
{

    /**
     * True if the task can be reassigned by the user
     */
    public static boolean canReassign(TaskRepresentation task, String userIdValue)
    {
        if (task.getEndDate() != null) { return false; }

        long userId = Long.valueOf(userIdValue);
        long processInstanceUserId = (!TextUtils.isEmpty(task.getProcessInstanceStartUserId())) ? Long.parseLong(task
                .getProcessInstanceStartUserId()) : -1;

        if (task.getAssignee() != null)
        {
            if (task.getAssignee().getId() == userId)
            {
                return true;
            }
            else if (processInstanceUserId == userId) { return true; }
        }
        else if (task.getAssignee() == null && processInstanceUserId == userId) { return true; }
        return false;
    }

    public static boolean canClaim(TaskRepresentation task, LightUserRepresentation assignee)
    {
        if (task.getEndDate() != null) { return false; }
        if (assignee != null) { return false; }
        if (task.getAssignee() != null) { return false; }
        return true;
    }
}
