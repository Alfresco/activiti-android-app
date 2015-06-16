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

package com.activiti.android.platform.provider.app;

/**
 * Created by jpascal on 12/03/2015.
 */
public class RuntimeAppInstance
{
    protected Long id, appId, accountId;

    protected String name;

    protected String description;

    protected Long modelId;

    protected String theme;

    protected int icon;

    protected String deploymentId;

    protected long number1 = -1, number2 = -1, number3 = -1;

    public RuntimeAppInstance(Long id, Long accountId, Long appId, String name, String description, Long modelId,
            String theme, int icon, String deploymentId, Long number1, Long number2, Long number3)
    {
        this.id = id;
        this.accountId = accountId;
        this.appId = appId;
        this.name = name;
        this.description = description;
        this.modelId = modelId;
        this.theme = theme;
        this.icon = icon;
        this.deploymentId = deploymentId;
        this.number1 = number1;
        this.number2 = number2;
        this.number3 = number3;
    }

    public Long getProviderId()
    {
        return id;
    }

    public Long getId()
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

    public Long getModelId()
    {
        return modelId;
    }

    public String getTheme()
    {
        return theme;
    }

    public int getIcon()
    {
        return icon;
    }

    public String getDeploymentId()
    {
        return deploymentId;
    }

    public Long getNumber1()
    {
        return number1;
    }

    public Long getNumber2()
    {
        return number2;
    }

    public Long getNumber3()
    {
        return number3;
    }

    public Long getAccountId()
    {
        return accountId;
    }
}
