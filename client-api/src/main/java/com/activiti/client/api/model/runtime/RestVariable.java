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

/**
 * Pojo representing a variable used in REST-service which definies it's name,
 * variable, scope and type.
 * 
 * @author Frederik Heremans
 */
public class RestVariable
{

    private String name;;

    private String type;

    private RestVariableScope variableScope;

    private Object value;

    private String valueUrl;

    public static RestVariableScope getScopeFromString(String scope)
    {
        if (scope != null)
        {
            for (RestVariableScope s : RestVariableScope.values())
            {
                if (s.name().equalsIgnoreCase(scope)) { return s; }
            }
            return null;
        }
        else
        {
            return null;
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public RestVariableScope getVariableScope()
    {
        return variableScope;
    }

    public void setVariableScope(RestVariableScope variableScope)
    {
        this.variableScope = variableScope;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String getScope()
    {
        String scope = null;
        if (variableScope != null)
        {
            scope = variableScope.name().toLowerCase();
        }
        return scope;
    }

    public void setScope(String scope)
    {
        setVariableScope(getScopeFromString(scope));
    }

    public String getValueUrl()
    {
        return valueUrl;
    }

    public void setValueUrl(String valueUrl)
    {
        this.valueUrl = valueUrl;
    }

    public enum RestVariableScope
    {
        LOCAL, GLOBAL
    }
}
