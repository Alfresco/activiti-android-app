/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.client.api.model.runtime;

/**
 * @author Bassam Al-Sarori Base class representation for executing filters. For
 *         executing user saved filter, the filter id should be set in
 *         {@link FilterRequestRepresentation#filterId} property. For executing
 *         dynamic filter, the filter representation should be set in
 *         {@link FilterRequestRepresentation#filter} property. Only
 *         {@link FilterRequestRepresentation#filter} or
 *         {@link FilterRequestRepresentation#filterId} should be set. The class
 *         includes parameters not included in filter, such as
 *         {@link FilterRequestRepresentation#page},
 *         {@link FilterRequestRepresentation#size}, and,
 *         {@link FilterRequestRepresentation#}. Subclasses can include
 *         additional parameters.
 */
public class FilterRequestRepresentation<T extends FilterRepresentation>
{

    public static final int DEFAULT_SIZE = 50;

    protected int page = 0;

    protected int size = DEFAULT_SIZE;

    protected Long appDefinitionId;

    protected Long filterId;

    protected T filter;

    public int getPage()
    {
        return page;
    }

    public void setPage(int page)
    {
        this.page = page;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public Long getAppDefinitionId()
    {
        return appDefinitionId;
    }

    public void setAppDefinitionId(Long appDefinitionId)
    {
        this.appDefinitionId = appDefinitionId;
    }

    public Long getFilterId()
    {
        return filterId;
    }

    public void setFilterId(Long filterId)
    {
        this.filterId = filterId;
    }

    public T getFilter()
    {
        return filter;
    }

    public void setFilter(T filter)
    {
        this.filter = filter;
    }

}
