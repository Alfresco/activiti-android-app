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

import java.util.ArrayList;
import java.util.List;

import com.activiti.client.api.model.common.AbstractRepresentation;
import com.google.gson.internal.LinkedTreeMap;

/**
 * @author Joram Barrez
 */
public class LightGroupRepresentation extends AbstractRepresentation
{

    protected Long id;

    protected String name;

    protected String externalId;

    protected String status;

    protected List<LightGroupRepresentation> groups;

    public static LightGroupRepresentation parse(Object jsonObject)
    {
        if (jsonObject instanceof LinkedTreeMap)
        {
            LightGroupRepresentation user = new LightGroupRepresentation();
            user.name = (String) ((LinkedTreeMap) jsonObject).get("name");
            user.externalId = (String) ((LinkedTreeMap) jsonObject).get("externalId");
            user.status = (String) ((LinkedTreeMap) jsonObject).get("status");
            user.id = ((Double) ((LinkedTreeMap) jsonObject).get("id")).longValue();
            return user;
        }
        return null;
    }

    public LightGroupRepresentation()
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

    public String getExternalId()
    {
        return externalId;
    }

    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void addGroup(LightGroupRepresentation group)
    {
        if (groups == null)
        {
            groups = new ArrayList<LightGroupRepresentation>();
        }
        groups.add(group);
    }

    public List<LightGroupRepresentation> getGroups()
    {
        return groups;
    }

    public void setGroups(List<LightGroupRepresentation> groups)
    {
        this.groups = groups;
    }

}
