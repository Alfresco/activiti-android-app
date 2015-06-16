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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Erik Winlof
 */
public class ContainerRepresentation extends FormFieldRepresentation
{

    protected Map<String, List<FormFieldRepresentation>> fields = new HashMap<String, List<FormFieldRepresentation>>();

    public Map<String, List<FormFieldRepresentation>> getFields()
    {
        return fields;
    }

    public void setFields(Map<String, List<FormFieldRepresentation>> fields)
    {
        this.fields = fields;
    }
}