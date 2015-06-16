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

package com.activiti.client.api.model.idm;

import java.util.Date;
import java.util.List;

/**
 * @author Joram Barrez
 */
public class GroupRepresentation
{

    protected Long id;

    protected String name;

    protected Long tenantId;

    protected int type;

    protected Long parentGroupId;

    protected String status;

    protected String externalId;

    protected Date lastSyncTimeStamp;

    protected Long userCount;

    protected List<UserRepresentation> users;

    protected List<GroupCapabilityRepresentation> capabilities;

    protected List<GroupRepresentation> groups;

    public GroupRepresentation()
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

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public Long getParentGroupId()
    {
        return parentGroupId;
    }

    public void setParentGroupId(Long parentGroupId)
    {
        this.parentGroupId = parentGroupId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    public Date getLastSyncTimeStamp()
    {
        return lastSyncTimeStamp;
    }

    public void setLastSyncTimeStamp(Date lastSyncTimeStamp)
    {
        this.lastSyncTimeStamp = lastSyncTimeStamp;
    }

    public Long getUserCount()
    {
        return userCount;
    }

    public void setUserCount(Long userCount)
    {
        this.userCount = userCount;
    }

    public List<UserRepresentation> getUsers()
    {
        return users;
    }

    public void setUsers(List<UserRepresentation> users)
    {
        this.users = users;
    }

    public List<GroupCapabilityRepresentation> getCapabilities()
    {
        return capabilities;
    }

    public void setCapabilities(List<GroupCapabilityRepresentation> capabilities)
    {
        this.capabilities = capabilities;
    }

    public List<GroupRepresentation> getGroups()
    {
        return groups;
    }

    public void setGroups(List<GroupRepresentation> groups)
    {
        this.groups = groups;
    }

}
