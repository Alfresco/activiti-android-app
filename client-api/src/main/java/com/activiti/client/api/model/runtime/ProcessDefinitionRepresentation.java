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

package com.activiti.client.api.model.runtime;

import com.activiti.client.api.model.common.AbstractRepresentation;

/**
 * Created by jpascal on 12/12/2014.
 */
public class ProcessDefinitionRepresentation extends AbstractRepresentation
{
    protected String id;

    protected String url;

    protected int version;

    protected String key;

    protected String category;

    protected boolean suspended;

    protected String name;

    protected String description;

    protected String deploymentId;

    protected String deploymentUrl;

    protected String graphicalNotationDefined;

    protected String resource;

    protected String diagramResource;

    protected boolean startFormDefined;

    public String getId()
    {
        return id;
    }

    public String getCategory()
    {
        return category;
    }

    public String getName()
    {
        return name;
    }

    public String getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }

    public int getVersion()
    {
        return version;
    }

    public String getResourceName()
    {
        return resource;
    }

    public String getDeploymentId()
    {
        return deploymentId;
    }

    public String getDiagramResourceName()
    {
        return diagramResource;
    }

    public boolean hasStartFormKey()
    {
        return startFormDefined;
    }

    public boolean hasGraphicalNotation()
    {
        return graphicalNotationDefined != null;
    }

    public boolean isSuspended()
    {
        return suspended;
    }

    public String getTenantId()
    {
        return null;
    }
}
