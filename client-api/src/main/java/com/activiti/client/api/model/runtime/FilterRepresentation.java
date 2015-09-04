/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.client.api.model.runtime;

/**
 * @author Bassam Al-Sarori Base class representation for saved or dynamic
 *         filters' parameters. Used by {@link UserFilterRepresentation} for
 *         saving/retrieving user filters. Used by
 *         {@link FilterRequestRepresentation} for executing dynamic filters.
 */
public class FilterRepresentation
{

    protected String sort;

    protected Boolean asc;

    public String getSort()
    {
        return sort;
    }

    public void setSort(String sort)
    {
        this.sort = sort;
    }

    public Boolean getAsc()
    {
        return asc;
    }

    public void setAsc(Boolean asc)
    {
        this.asc = asc;
    }

}
