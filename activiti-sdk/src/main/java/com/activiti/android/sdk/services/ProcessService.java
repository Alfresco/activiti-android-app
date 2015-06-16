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
import retrofit.mime.TypedFile;

import com.activiti.android.sdk.RestManager;
import com.activiti.client.api.ApiCommentResource;
import com.activiti.client.api.ApiProcessInstanceResource;
import com.activiti.client.api.ApiRelatedContentResource;
import com.activiti.client.api.model.runtime.CommentRepresentation;
import com.activiti.client.api.model.runtime.CommentsRepresentation;
import com.activiti.client.api.model.runtime.ProcessContentsRepresentation;
import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;
import com.activiti.client.api.model.runtime.ProcessInstancesRepresentation;
import com.activiti.client.api.model.runtime.ProcessesRequestRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentsRepresentation;
import com.activiti.client.api.model.runtime.request.AddContentRelatedRepresentation;
import com.activiti.client.api.model.runtime.request.CreateProcessInstanceRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public class ProcessService extends ActivitiService
{
    protected ApiProcessInstanceResource api;

    protected ApiCommentResource commentApi;

    protected ApiRelatedContentResource contentApi;

    ProcessService(RestManager manager)
    {
        super(manager);
        api = manager.adapter.create(ApiProcessInstanceResource.class);
        commentApi = manager.adapter.create(ApiCommentResource.class);
        contentApi = manager.adapter.create(ApiRelatedContentResource.class);
    }

    public String getShareUrl(String processId)
    {
        return String.format(restManager.endpoint.concat("/workflow/#/process/%s"), processId);
    }

    // ///////////////////////////////////////////////////////////////////
    // START
    // ///////////////////////////////////////////////////////////////////
    public void startNewProcessInstance(CreateProcessInstanceRepresentation request,
            Callback<ProcessInstanceRepresentation> callback)
    {
        api.startNewProcessInstance(request, callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // LISTING
    // ///////////////////////////////////////////////////////////////////
    public void list(ProcessesRequestRepresentation body, Callback<ProcessInstancesRepresentation> callback)
    {
        api.getProcessInstances(body, callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////
    public void getById(String processId, Callback<ProcessInstanceRepresentation> callback)
    {
        api.getProcessInstance(processId, callback);
    }

    /*
     * public void start(CreateProcessInstanceRepresentation body,
     * Callback<ProcessInstanceRepresentation> callback) { // api.start(body,
     * callback); }
     */

    public void delete(String processInstanceId, Callback<Void> callback)
    {
        api.deleteProcessInstance(processInstanceId, callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // CONTENT
    // ///////////////////////////////////////////////////////////////////
    public void getAttachments(String processInstanceId, Callback<RelatedContentsRepresentation> callback)
    {
        contentApi.getRelatedContentForProcessInstance(processInstanceId, callback);
    }

    public void getFieldContents(String processInstanceId, Callback<ProcessContentsRepresentation> callback)
    {
        contentApi.getProcessInstanceContent(processInstanceId, callback);
    }

    public void addAttachment(String processInstanceId, TypedFile resource,
            Callback<RelatedContentsRepresentation> callback)
    {
        contentApi.createRelatedContentOnProcessInstance(processInstanceId, resource, callback);
    }

    public void linkAttachment(String processInstanceId, AddContentRelatedRepresentation representation,
            Callback<RelatedContentRepresentation> callback)
    {
        contentApi.linkRelatedContentOnProcessInstance(processInstanceId, representation, callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // COMMENT
    // ///////////////////////////////////////////////////////////////////
    public void getComments(String processInstanceId, Callback<CommentsRepresentation> callback)
    {
        commentApi.getProcessInstanceComments(processInstanceId, callback);
    }

    public void addComment(String processInstanceId, CommentRepresentation request,
            Callback<CommentRepresentation> callback)
    {
        commentApi.addProcessInstanceComment(processInstanceId, request, callback);
    }
}
