/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.client.api.model.runtime;

/**
 * @author Bassam Al-Sarori Representation for task saved filter
 */
public class UserTaskFilterRepresentation extends UserFilterRepresentation<TaskFilterRepresentation>
{

    public UserTaskFilterRepresentation()
    {
        super();
    }

    public String getType()
    {
        return TASK_FILTER;
    }

}
