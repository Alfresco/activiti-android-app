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

package com.activiti.android.platform.provider.group;

/**
 * Created by jpascal on 12/03/2015.
 */
public class GroupInstance
{
    public static final int TYPE_SYSTEM_GROUP = 0;

    public static final int TYPE_FUNCTIONAL_GROUP = 1;

    protected Long id, groupId, accountId;

    protected int type;

    protected Long parentGroupId;

    protected String status;

    protected String externalId;

    protected String name;

    public GroupInstance(Long id, Long groupId, Long accountId, String name, int type, Long parentGroupId,
            String status, String externalId)
    {
        this.id = id;
        this.groupId = groupId;
        this.accountId = accountId;
        this.name = name;
        this.type = type;
        this.parentGroupId = parentGroupId;
        this.status = status;
        this.externalId = externalId;
    }

    public Long getProviderId()
    {
        return id;
    }

    public Long getId()
    {
        return groupId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public int getType()
    {
        return type;
    }

    public Long getParentGroupId()
    {
        return parentGroupId;
    }

    public String getStatus()
    {
        return status;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public String getName()
    {
        return name;
    }

}
