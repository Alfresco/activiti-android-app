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
import java.util.Date;
import java.util.List;

import com.activiti.client.api.model.common.AbstractRepresentation;
import com.activiti.client.api.model.editor.LightAppRepresentation;

/**
 * Created by jpascal on 15/12/2014.
 */
public class UserRepresentation extends AbstractRepresentation
{

    protected Long id;

    protected String fullname;

    protected String email;

    protected String firstName;

    protected String lastName;

    protected String password;

    protected String company;

    protected String type;

    protected String status;

    protected Date created;

    protected Date lastUpdate;

    protected Long tenantId;

    protected Long pictureId;

    protected String externalId;

    protected Date latestSyncTimeStamp;

    protected List<GroupRepresentation> groups;

    protected List<String> capabilities;

    protected List<LightAppRepresentation> apps = new ArrayList<LightAppRepresentation>();

    /**
     * These properties are not part of the user data, but they are filled by
     * some requests (eg the account request) because they avoid needing to do
     * multiple calls.
     */
    protected Long tenantPictureId;

    protected String tenantName;

    public UserRepresentation()
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

    public String getFullname()
    {
        return fullname;
    }

    public void setFullname(String fullname)
    {
        this.fullname = fullname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
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

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getTenantName()
    {
        return tenantName;
    }

    public void setTenantName(String tenantName)
    {
        this.tenantName = tenantName;
    }

    public Long getPictureId()
    {
        return pictureId;
    }

    public void setPictureId(Long pictureId)
    {
        this.pictureId = pictureId;
    }

    public List<GroupRepresentation> getGroups()
    {
        return groups;
    }

    public void setGroups(List<GroupRepresentation> groups)
    {
        this.groups = groups;
    }

    public List<String> getCapabilities()
    {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities)
    {
        this.capabilities = capabilities;
    }

    public Long getTenantPictureId()
    {
        return tenantPictureId;
    }

    public void setTenantPictureId(Long tenantPictureId)
    {
        this.tenantPictureId = tenantPictureId;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    public Date getLatestSyncTimeStamp()
    {
        return latestSyncTimeStamp;
    }

    public void setLatestSyncTimeStamp(Date latestSyncTimeStamp)
    {
        this.latestSyncTimeStamp = latestSyncTimeStamp;
    }

    public List<LightAppRepresentation> getApps()
    {
        return apps;
    }

    public void setApps(List<LightAppRepresentation> apps)
    {
        this.apps = apps;
    }
}
