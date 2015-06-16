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

/**
 * @author Yvo Swillens
 */
public class EndpointConfigurationRepresentation
{

    private Long id;

    private Long tenantId;

    private String name;

    private String protocol;

    private String host;

    private String port;

    private String path;

    private Long basicAuthId;

    private String basicAuthName;

    public EndpointConfigurationRepresentation()
    {

    }

    public EndpointConfigurationRepresentation(Long tenantId, String name, String protocol, String host, String port,
            String path, Long basicAuth, String basicAuthName)
    {
        this.tenantId = tenantId;
        this.name = name;
        this.protocol = protocol;
        this.host = host;
        this.path = path;
        this.basicAuthId = basicAuth;
        this.basicAuthName = basicAuthName;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Long getBasicAuthId()
    {
        return basicAuthId;
    }

    public void setBasicAuthId(Long basicAuthId)
    {
        this.basicAuthId = basicAuthId;
    }

    public String getBasicAuthName()
    {
        return basicAuthName;
    }

    public void setBasicAuthName(String basicAuthName)
    {
        this.basicAuthName = basicAuthName;
    }
}
