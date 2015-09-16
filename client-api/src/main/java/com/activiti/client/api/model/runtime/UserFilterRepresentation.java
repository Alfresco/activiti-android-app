/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.client.api.model.runtime;

import com.activiti.client.api.model.common.AbstractRepresentation;

/**
 * @author Bassam Al-Sarori Base class representation for saved filters.
 */
public abstract class UserFilterRepresentation<T extends FilterRepresentation> extends AbstractRepresentation
{

    public static final String TASK_FILTER = "task";

    public static final String PROCESS_FILTER = "process";

    protected Long id;

    protected String name;

    protected Long appId;

    protected Boolean recent;

    protected Integer index;

    protected String icon;

    protected T filter;

    public UserFilterRepresentation()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getAppId()
    {
        return appId;
    }

    public void setAppId(Long appId)
    {
        this.appId = appId;
    }

    public Boolean getRecent()
    {
        return recent;
    }

    public void setRecent(Boolean recent)
    {
        this.recent = recent;
    }

    public Integer getIndex()
    {
        return index;
    }

    public void setIndex(Integer index)
    {
        this.index = index;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public T getFilter()
    {
        return filter;
    }

    public void setFilter(T filter)
    {
        this.filter = filter;
    }

    public abstract String getType();
}
