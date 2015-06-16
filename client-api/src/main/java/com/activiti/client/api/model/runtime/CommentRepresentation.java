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

import java.util.Date;

import com.activiti.client.api.model.common.AbstractRepresentation;
import com.activiti.client.api.model.idm.LightUserRepresentation;

/**
 * Created by jpascal on 12/03/2015.
 */
public class CommentRepresentation extends AbstractRepresentation
{
    private Long id;

    private String message;

    private Date created;

    private LightUserRepresentation createdBy;

    /*
     * public CommentRepresentation(Comment comment) { this.id =
     * comment.getId(); this.message = comment.getMessage(); this.created =
     * comment.getCreated(); if (comment.getCreatedBy() != null) {
     * this.createdBy = new LightUserRepresentation(comment.getCreatedBy()); } }
     */

    public CommentRepresentation()
    {
    }

    /**
     * Used to create a comment
     * 
     * @param message
     */
    public CommentRepresentation(String message)
    {
        this.message = message;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public LightUserRepresentation getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(LightUserRepresentation createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
