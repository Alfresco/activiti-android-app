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

package com.activiti.android.platform.provider.integration;

import java.util.Date;

import com.activiti.client.api.constant.ISO8601Utils;

/**
 * Created by jpascal on 12/03/2015.
 */
public class Integration
{
    public static final int OPEN_UNDEFINED = 0;

    public static final int OPEN_BROWSER = 1;

    public static final int OPEN_NATIVE_APP = 2;

    protected Long providerId, integrationId, activitiAccountId, alfrescoAccountId;

    protected Long tenantId;

    protected String name;

    protected String accountUsername;

    protected String alfrescoTenantId;

    protected String alfrescoName;

    protected String alfrescoUsername;

    protected String shareUrl, repositoryUrl;

    protected Date created, lastUpdated;

    protected int openType;

    public Integration(long id, long integrationId, String name, String username, long tenantId,
            String alfrescoTenantId, String created, String updated, String shareUrl, String repositoryUrl,
            long activitiAccountId, long alfrescoAccountId, String alfrescoName, String alfrescoUsername, int openType)
    {
        this.providerId = id;
        this.integrationId = integrationId;
        this.tenantId = tenantId;
        this.alfrescoTenantId = alfrescoTenantId;
        this.name = name;
        this.accountUsername = username;
        this.shareUrl = shareUrl;
        this.repositoryUrl = repositoryUrl;
        this.created = ISO8601Utils.parse(created);
        this.lastUpdated = ISO8601Utils.parse(updated);
        this.activitiAccountId = activitiAccountId;
        this.alfrescoAccountId = alfrescoAccountId;
        this.alfrescoName = alfrescoName;
        this.alfrescoUsername = alfrescoUsername;
        this.openType = openType;
    }

    public Long getId()
    {
        return integrationId;
    }

    public void setId(Long id)
    {
        this.providerId = integrationId;
    }

    public Long getProviderId()
    {
        return providerId;
    }

    public void setProviderId(Long id)
    {
        this.providerId = id;
    }

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getAlfrescoTenantId()
    {
        return alfrescoTenantId;
    }

    public void setAlfrescoTenantId(String alfrescoTenantId)
    {
        this.alfrescoTenantId = alfrescoTenantId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAccountUsername()
    {
        return accountUsername;
    }

    public void setAccountUsername(String accountUsername)
    {
        this.accountUsername = accountUsername;
    }

    public String getShareUrl()
    {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl)
    {
        this.shareUrl = shareUrl;
    }

    public String getRepositoryUrl()
    {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl)
    {
        this.repositoryUrl = repositoryUrl;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public Long getAlfrescoAccountId()
    {
        return alfrescoAccountId;
    }

    public void setAlfrescoAccountId(Long alfrescoAccountId)
    {
        this.alfrescoAccountId = alfrescoAccountId;
    }

    public String getAlfrescoName()
    {
        return alfrescoName;
    }

    public void setAlfrescoName(String alfrescoName)
    {
        this.alfrescoName = alfrescoName;
    }

    public String getAlfrescoUsername()
    {
        return alfrescoUsername;
    }

    public void setAlfrescoUsername(String alfrescoUsername)
    {
        this.alfrescoUsername = alfrescoUsername;
    }

    public Long getActivitiAccountId()
    {
        return activitiAccountId;
    }

    public void setActivitiAccountId(Long activitiAccountId)
    {
        this.activitiAccountId = activitiAccountId;
    }

    public int getOpenType()
    {
        return openType;
    }

    public void setOpenType(int openType)
    {
        this.openType = openType;
    }
}
