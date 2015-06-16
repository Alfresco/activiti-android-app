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

import java.util.List;

import com.activiti.client.api.model.common.AbstractRepresentation;
import com.activiti.client.api.model.editor.NamedObject;

/**
 * Created by jpascal on 09/06/2015.
 */
public class ProcessContentRepresentation extends AbstractRepresentation
{

    private NamedObject field;

    private List<RelatedContentRepresentation> content;

    public ProcessContentRepresentation()
    {
    }

    public NamedObject getField()
    {
        return field;
    }

    public void setField(NamedObject field)
    {
        this.field = field;
    }

    public List<RelatedContentRepresentation> getContent()
    {
        return content;
    }

    public void setContent(List<RelatedContentRepresentation> content)
    {
        this.content = content;
    }

}
