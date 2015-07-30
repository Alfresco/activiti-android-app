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
package com.activiti.client.api.model.editor;

import java.util.Date;

import com.activiti.client.api.model.common.AbstractRepresentation;

/**
 * Representation of process-models, both current and historic models.
 * 
 * @author Tijs Rademakers
 */
public class ModelRepresentation extends AbstractRepresentation
{

    protected Long id;

    protected String name;

    protected String description;

    protected Long createdBy;

    protected String createdByFullName;

    protected Long lastUpdatedBy;

    protected String lastUpdatedByFullName;

    protected Date lastUpdated;

    protected boolean latestVersion;

    protected int version;

    protected String comment;

    protected Long stencilSet;

    protected Long referenceId;

    protected Integer modelType;

    protected Boolean favorite;

    protected String permission;

    public ModelRepresentation()
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public Long getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getCreatedByFullName()
    {
        return createdByFullName;
    }

    public void setCreatedByFullName(String createdByFullName)
    {
        this.createdByFullName = createdByFullName;
    }

    public void setLatestVersion(boolean latestVersion)
    {
        this.latestVersion = latestVersion;
    }

    public boolean isLatestVersion()
    {
        return latestVersion;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public Long getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy)
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getLastUpdatedByFullName()
    {
        return lastUpdatedByFullName;
    }

    public void setLastUpdatedByFullName(String lastUpdatedByFullName)
    {
        this.lastUpdatedByFullName = lastUpdatedByFullName;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getComment()
    {
        return comment;
    }

    public Long getStencilSet()
    {
        return stencilSet;
    }

    public void setStencilSet(Long stencilSet)
    {
        this.stencilSet = stencilSet;
    }

    public Long getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(Long referenceId)
    {
        this.referenceId = referenceId;
    }

    public Integer getModelType()
    {
        return modelType;
    }

    public void setModelType(Integer modelType)
    {
        this.modelType = modelType;
    }

    public void setFavorite(Boolean favorite)
    {
        this.favorite = favorite;
    }

    public Boolean getFavorite()
    {
        return favorite;
    }

    public String getPermission()
    {
        return permission;
    }

    public void setPermission(String permission)
    {
        this.permission = permission;
    }

    public void setSharePermission(SharePermission permission)
    {
        if (permission != null)
        {
            this.permission = permission.toString().toLowerCase();
        }
    }
    /**
     * public Model toModel() { Model model = new Model(); model.setName(name);
     * model.setDescription(description); return model; } Update all editable
     * properties of the given {@link Model} based on the values in this
     * instance. public void updateModel(Model model) {
     * model.setDescription(this.description); model.setName(this.name); }
     */
}
