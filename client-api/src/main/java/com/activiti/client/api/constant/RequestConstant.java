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

package com.activiti.client.api.constant;

/**
 * Created by jpascal on 22/12/2014.
 */
public interface RequestConstant
{
    // ///////////////////////////////////////////////
    // USER SERVICE
    // ///////////////////////////////////////////////
    String ARGUMENT_APPDEFINITION = "appDefinition";

    String ARGUMENT_APPDEFINITION_ID = "appDefinitionId";

    String ARGUMENT_PROCESS_ID = "processId";

    String ARGUMENT_PROCESSDEFINITION_ID = "processDefinitionId";

    String ARGUMENT_TEXT = "text";

    String ARGUMENT_ASSIGNEE = "assignee";

    String ARGUMENT_ASSIGNMENT = "assignment";

    String ARGUMENT_STATE = "state";

    String ARGUMENT_SORT = "sort";

    String ARGUMENT_PAGE = "page";

    String ARGUMENT_SIZE = "size";

    // ///////////////////////////////////////////////
    // ASSIGNMENT
    // ///////////////////////////////////////////////
    String ASSIGNMENT_INVOLVED = "involved";

    String ASSIGNMENT_ASSIGNEE = "assignee";

    String ASSIGNMENT_CANDIDATE = "candidate";

    String ARGUMENT_TASK_ID = "taskId";

    // ///////////////////////////////////////////////
    // STATE
    // ///////////////////////////////////////////////
    String STATE_OPEN = "open";

    String STATE_ACTIVE = "active";

    String STATE_RUNNING = "running";

    String STATE_COMPLETED = "completed";

    String STATE_ALL = "all";

    // ///////////////////////////////////////////////
    // SORT
    // ///////////////////////////////////////////////
    String SORT_CREATED_ASC = "created-asc";

    String SORT_CREATED_DESC = "created-desc";

    String SORT_DUE_ASC = "due-asc";

    String SORT_DUE_DESC = "due-desc";
}
