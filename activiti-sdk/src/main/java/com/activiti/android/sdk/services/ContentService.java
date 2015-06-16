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

package com.activiti.android.sdk.services;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import com.activiti.android.sdk.RestManager;
import com.activiti.client.api.ApiRelatedContentResource;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.request.AddContentRelatedRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public class ContentService extends ActivitiService
{
    protected ApiRelatedContentResource api;

    ContentService(RestManager manager)
    {
        super(manager);
        api = manager.adapter.create(ApiRelatedContentResource.class);
    }

    public String getDownloadUrl(Long contentId)
    {
        return String.format(restManager.endpoint.concat("/api/enterprise/content/%s/raw"), Long.toString(contentId));
    }

    public String getThumbnailUrl(Long contentId)
    {
        return String.format(restManager.endpoint.concat("/api/enterprise/content/%s/rendition/thumbnail"),
                Long.toString(contentId));
    }

    public void getByIdentifier(String contentId, Callback<RelatedContentRepresentation> callback)
    {
        api.getContent(contentId, callback);
    }

    public void delete(Long contentId, Callback<Void> callback)
    {
        api.deleteContent(Long.toString(contentId), callback);
    }

    public void download(String contentId, Callback<Response> callback)
    {
        api.getRawContent(contentId, callback);
    }

    public Response download(String contentId)
    {
        return api.getRawContent(contentId);
    }

    public RelatedContentRepresentation createRelatedContentOnTask(String taskId, TypedFile file)
    {
        return api.createRelatedContentOnTask(taskId, file);
    }

    public RelatedContentRepresentation createRelatedContentOnProcessInstance(String processId, TypedFile file)
    {
        return api.createRelatedContentOnProcessInstance(processId, file);
    }

    public RelatedContentRepresentation createTemporaryRawRelatedContent(TypedFile file)
    {
        return api.createTemporaryRawRelatedContent(file);
    }

    public void createTemporaryRelatedContent(AddContentRelatedRepresentation representation,
                                                 Callback<RelatedContentRepresentation> callback)
    {
        api.createTemporaryRelatedContent(representation, callback);
    }

    public void createTemporaryRawRelatedContent(AddContentRelatedRepresentation representation,
            Callback<RelatedContentRepresentation> callback)
    {
        api.createTemporaryRelatedContent(representation, callback);
    }

}
