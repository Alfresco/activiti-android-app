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

package com.activiti.android.platform.provider.models;

/**
 * Created by jpascal on 12/03/2015.
 */
public class FormDefinitionModel
{
    protected Long id, appId, accountId;

    protected String processDefinitionId;

    protected String name;

    protected String description;

    protected Integer version;

    protected Boolean hasStartForm;

    public FormDefinitionModel(Long id, String processDefinitionId, Long accountId, Long appId, String name,
            String description, Integer version, Integer hasStartForm)
    {
        this.id = id;
        this.processDefinitionId = processDefinitionId;
        this.accountId = accountId;
        this.appId = appId;
        this.name = name;
        this.description = description;
        this.version = version;
        this.hasStartForm = (hasStartForm == 1) ? Boolean.TRUE : Boolean.FALSE;
    }

    public Long getProviderId()
    {
        return id;
    }

    public String getId()
    {
        return processDefinitionId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public Long getAppId()
    {
        return appId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Integer getVersion()
    {
        return version;
    }

    public Boolean hasStartForm()
    {
        return hasStartForm;
    }

}
