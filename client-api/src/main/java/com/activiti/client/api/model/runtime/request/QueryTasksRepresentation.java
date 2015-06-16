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

package com.activiti.client.api.model.runtime.request;

import com.activiti.client.api.model.common.PagingRequestRepresentation;

/**
 * Created by jpascal on 12/12/2014.
 */
public class QueryTasksRepresentation extends PagingRequestRepresentation
{
    final Long appDefinitionId;

    final String text;

    final String processDefinitionId;

    final String processInstanceId;

    final Long assignee;

    final String assignment;

    public QueryTasksRepresentation(Long appDefinitionId, String processDefinitionId, String keywords,
            String processId, Long assignee, String state, String assignment, String sort, Long page, Long size)
    {
        super(state, sort, page, size);
        this.text = keywords;
        this.appDefinitionId = appDefinitionId;
        this.processDefinitionId = processDefinitionId;
        this.assignee = assignee;
        this.assignment = assignment;
        this.processInstanceId = processId;
    }

    public QueryTasksRepresentation(Long appDefinitionId, Long assigneeId, String assignment)
    {
        super(null, null, null, null);
        this.appDefinitionId = appDefinitionId;
        this.assignee = assigneeId;
        this.assignment = assignment;
        this.processDefinitionId = null;
        this.processInstanceId = null;
        this.text = null;
    }
}
