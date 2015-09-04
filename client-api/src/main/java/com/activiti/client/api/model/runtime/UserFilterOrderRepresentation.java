/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.client.api.model.runtime;

import java.util.List;

import com.activiti.client.api.model.common.AbstractRepresentation;

/**
 * @author Bassam Al-Sarori
 */
public class UserFilterOrderRepresentation extends AbstractRepresentation
{

    protected List<Long> order;

    protected Long appId;

    public List<Long> getOrder()
    {
        return order;
    }

    public void setOrder(List<Long> order)
    {
        this.order = order;
    }

    public Long getAppId()
    {
        return appId;
    }

    public void setAppId(Long appId)
    {
        this.appId = appId;
    }

}
