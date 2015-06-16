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

import com.activiti.client.api.model.common.AbstractRepresentation;
import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by jpascal on 12/12/2014.
 */
public class LightUserRepresentation extends AbstractRepresentation
{
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String externalId;

    private Long pictureId;

    public LightUserRepresentation()
    {
    }

    public static LightUserRepresentation parse(Object jsonObject)
    {
        if (jsonObject instanceof LinkedTreeMap)
        {
            LightUserRepresentation user = new LightUserRepresentation();
            user.firstName = (String) ((LinkedTreeMap) jsonObject).get("firstName");
            user.lastName = (String) ((LinkedTreeMap) jsonObject).get("lastName");
            user.email = (String) ((LinkedTreeMap) jsonObject).get("email");
            user.id = ((Double) ((LinkedTreeMap) jsonObject).get("id")).longValue();
            return user;
        }
        return null;
    }

    public LightUserRepresentation(Long id, String firstName, String lastName, String email)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public LightUserRepresentation(String firstName, String lastName, String email)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFullname()
    {
        String fullName = "";
        if (firstName != null)
        {
            fullName = fullName.concat(firstName + " ");
        }

        if (lastName != null)
        {
            fullName = fullName.concat(lastName);
        }

        return (fullName.isEmpty()) ? "" : fullName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    public Long getPictureId()
    {
        return pictureId;
    }

    public void setPictureId(Long pictureId)
    {
        this.pictureId = pictureId;
    }

}
