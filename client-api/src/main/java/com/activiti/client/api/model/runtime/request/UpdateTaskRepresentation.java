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

package com.activiti.client.api.model.runtime.request;

import java.util.Date;

import com.activiti.client.api.constant.ISO8601Utils;
import com.activiti.client.api.model.common.AbstractRepresentation;

public class UpdateTaskRepresentation extends AbstractRepresentation
{
    protected String name;

    protected String description;

    protected String dueDate;

    public UpdateTaskRepresentation(String name, String description, Date dueDate)
    {
        setName(name);
        setDescription(description);
        if (name == null)
        {
            this.name = "";
        }
        if (description == null)
        {
            this.description = "";
        }
        if (dueDate != null)
        {
            this.dueDate = ISO8601Utils.format(dueDate);
        }
        else
        {
            this.dueDate = "";
        }
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

    public String getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(String dueDate)
    {
        this.dueDate = dueDate;
    }
}
