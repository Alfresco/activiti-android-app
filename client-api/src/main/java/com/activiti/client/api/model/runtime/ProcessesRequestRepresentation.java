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

package com.activiti.client.api.model.runtime;

import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.common.PagingRequestRepresentation;

/**
 * Created by jpascal on 12/12/2014.
 */
public class ProcessesRequestRepresentation extends PagingRequestRepresentation
{
    final String processDefinitionId;

    final Long appDefinitionId;

    /**
     * By Default State == Running
     */
    public ProcessesRequestRepresentation()
    {
        super(RequestConstant.STATE_RUNNING, null, null, null);
        this.processDefinitionId = null;
        this.appDefinitionId = null;
    }

    public ProcessesRequestRepresentation(Long appDefinitionId)
    {
        super(RequestConstant.STATE_RUNNING, null, null, null);
        this.appDefinitionId = appDefinitionId;
        this.processDefinitionId = null;
    }

    public ProcessesRequestRepresentation(String processDefinitionId, String state, String sort, Long page, Long size)
    {
        super(state, sort, page, size);
        this.processDefinitionId = processDefinitionId;
        this.appDefinitionId = null;
    }

    public ProcessesRequestRepresentation(Long appDefinitionId, String processDefinitionId, String state, String sort,
            Long page, Long size)
    {
        super(state, sort, page, size);
        this.processDefinitionId = processDefinitionId;
        this.appDefinitionId = appDefinitionId;
    }
}
