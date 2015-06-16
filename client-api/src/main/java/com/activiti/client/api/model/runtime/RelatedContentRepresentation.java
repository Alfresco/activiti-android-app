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
import com.google.gson.internal.LinkedTreeMap;

/**
 * @author Frederik Heremans
 */
public class RelatedContentRepresentation extends AbstractRepresentation
{

    protected Long id;

    protected String name;

    protected Date created;

    protected LightUserRepresentation createdBy;

    protected boolean contentAvailable;

    protected boolean link;

    protected String source;

    protected String sourceId;

    protected String mimeType;

    protected String simpleType;

    protected String linkUrl;

    public String previewStatus = "queued";

    public String thumbnailStatus = "queued";

    public RelatedContentRepresentation()
    {

    }

    public static RelatedContentRepresentation parse(String uri, String name, String mimetype)
    {
        RelatedContentRepresentation content = new RelatedContentRepresentation();
        content.name = name;
        content.mimeType = mimetype;
        content.id = -1L;
        content.linkUrl = uri;
        return content;
    }

    public static RelatedContentRepresentation parse(Object jsonObject)
    {
        if (jsonObject instanceof LinkedTreeMap)
        {
            RelatedContentRepresentation content = new RelatedContentRepresentation();
            content.id = ((Double) ((LinkedTreeMap) jsonObject).get("id")).longValue();
            content.createdBy = LightUserRepresentation.parse(((LinkedTreeMap) jsonObject).get("createdBy"));
            content.name = (String) ((LinkedTreeMap) jsonObject).get("name");
            content.source = (String) ((LinkedTreeMap) jsonObject).get("source");
            content.sourceId = (String) ((LinkedTreeMap) jsonObject).get("sourceId");
            content.mimeType = (String) ((LinkedTreeMap) jsonObject).get("mimeType");
            content.simpleType = (String) ((LinkedTreeMap) jsonObject).get("simpleType");
            content.link = (Boolean) ((LinkedTreeMap) jsonObject).get("link");
            content.linkUrl = (String) ((LinkedTreeMap) jsonObject).get("linkUrl");
            return content;
        }
        return null;
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

    public boolean isContentAvailable()
    {
        return contentAvailable;
    }

    public void setContentAvailable(boolean contentAvailable)
    {
        this.contentAvailable = contentAvailable;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getSimpleType()
    {
        return simpleType;
    }

    public void setSimpleType(String simpleType)
    {
        this.simpleType = simpleType;
    }

    public void setLink(boolean link)
    {
        this.link = link;
    }

    public boolean isLink()
    {
        return link;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public String getThumbnailStatus()
    {
        return thumbnailStatus;
    }

    public void setThumbnailStatus(String thumbnailStatus)
    {
        this.thumbnailStatus = thumbnailStatus;
    }

    public String getPreviewStatus()
    {
        return previewStatus;
    }

    public void setPreviewStatus(String previewStatus)
    {
        this.previewStatus = previewStatus;
    }
}
