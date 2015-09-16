/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.client.api.model.runtime;

import java.util.Date;

/**
 * @author Bassam Al-Sarori Filter representation for saved or dynamic task
 *         filters' parameters.
 */
public class TaskFilterRepresentation extends FilterRepresentation
{

    protected String name;

    protected String state;

    protected String assignment;

    protected String processDefinitionId;

    protected String processDefinitionKey;

    protected Date dueBefore;

    protected Date dueAfter;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getAssignment()
    {
        return assignment;
    }

    public void setAssignment(String assignment)
    {
        this.assignment = assignment;
    }

    public Date getDueBefore()
    {
        return dueBefore;
    }

    public void setDueBefore(Date dueBefore)
    {
        this.dueBefore = dueBefore;
    }

    public Date getDueAfter()
    {
        return dueAfter;
    }

    public void setDueAfter(Date dueAfter)
    {
        this.dueAfter = dueAfter;
    }

    public String getProcessDefinitionId()
    {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId)
    {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionKey()
    {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey)
    {
        this.processDefinitionKey = processDefinitionKey;
    }

}
