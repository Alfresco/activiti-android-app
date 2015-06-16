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

package com.activiti.client.api.model.editor;

import java.util.Date;

import com.activiti.client.api.model.common.AbstractRepresentation;

/**
 * Pojo representation of an app definition: the metadata (name, description,
 * etc) and the actual model ({@link AppDefinition} instance member).
 */
public class AppDefinitionRepresentation extends AbstractRepresentation
{

    private Long id;

    private String name;

    private String description;

    private Integer version;

    private Date created;

    private AppDefinition appDefinition;

    public AppDefinitionRepresentation()
    {
        // Empty constructor for Jackson
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public AppDefinition getAppDefinition()
    {
        return appDefinition;
    }

    public void setAppDefinition(AppDefinition appDefinition)
    {
        this.appDefinition = appDefinition;
    }
}