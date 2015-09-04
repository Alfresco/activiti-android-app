/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.client.api.model.runtime;

/**
 * @author Bassam Al-Sarori Filter representation for saved or dynamic process
 *         instance filters' parameters.
 */
public class ProcessInstanceFilterRepresentation extends FilterRepresentation
{

    protected String name;

    protected String state;

    protected String processDefinitionId;

    protected String processDefinitionKey;

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
