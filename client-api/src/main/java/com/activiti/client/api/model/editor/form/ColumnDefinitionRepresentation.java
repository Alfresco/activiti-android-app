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

package com.activiti.client.api.model.editor.form;

import java.util.List;

/**
 * @author Yvo Swillens
 */
public class ColumnDefinitionRepresentation
{

    protected String id;

    protected String name;

    protected String type;

    protected Object value;

    protected String optionType;

    protected List<OptionRepresentation> options;

    protected String restUrl;

    protected String restIdProperty;

    protected String restLabelProperty;

    protected boolean required;

    protected boolean editable;

    protected boolean sortable;

    protected boolean visible;

    public ColumnDefinitionRepresentation(String id)
    {
        this.id = id;
    }

    public ColumnDefinitionRepresentation()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String getOptionType()
    {
        return optionType;
    }

    public void setOptionType(String optionType)
    {
        this.optionType = optionType;
    }

    public List<OptionRepresentation> getOptions()
    {
        return options;
    }

    public void setOptions(List<OptionRepresentation> options)
    {
        this.options = options;
    }

    public String getRestUrl()
    {
        return restUrl;
    }

    public void setRestUrl(String restUrl)
    {
        this.restUrl = restUrl;
    }

    public String getRestIdProperty()
    {
        return restIdProperty;
    }

    public void setRestIdProperty(String restIdProperty)
    {
        this.restIdProperty = restIdProperty;
    }

    public String getRestLabelProperty()
    {
        return restLabelProperty;
    }

    public void setRestLabelProperty(String restLabelProperty)
    {
        this.restLabelProperty = restLabelProperty;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public boolean isSortable()
    {
        return sortable;
    }

    public void setSortable(boolean sortable)
    {
        this.sortable = sortable;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
}
