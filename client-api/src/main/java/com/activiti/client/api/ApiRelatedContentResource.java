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

package com.activiti.client.api;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

import com.activiti.client.api.model.runtime.ProcessContentsRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentsRepresentation;
import com.activiti.client.api.model.runtime.request.AddContentRelatedRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public interface ApiRelatedContentResource
{
    // /////////////////////////////////////////////////////////////////////////////////////
    // SYNC
    // /////////////////////////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/content/{contentId}/raw")
    @Streaming
    Response getRawContent(@Path("contentId") String contentId);

    // ?isRelatedContent has been added automatically to support 1.3 (mandatory)
    @Multipart
    @POST("/api/enterprise/tasks/{taskId}/raw-content?isRelatedContent=true")
    RelatedContentRepresentation createRelatedContentOnTask(@Path("taskId") String taskId,
            @Part("file") TypedFile resource);

    @Multipart
    @POST("/api/enterprise/process-instances/{processInstanceId}/raw-content")
    RelatedContentRepresentation createRelatedContentOnProcessInstance(
            @Path("processInstanceId") String processInstanceId, @Part("file") TypedFile resource);

    @POST("/api/enterprise/content")
    RelatedContentRepresentation createTemporaryRelatedContent(@Body AddContentRelatedRepresentation representation);

    @Multipart
    @POST("/api/enterprise/content/raw")
    RelatedContentRepresentation createTemporaryRawRelatedContent(@Part("file") TypedFile resource);

    // /////////////////////////////////////////////////////////////////////////////////////
    // ASYNC
    // /////////////////////////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/process-instances/{processInstanceId}/content")
    void getRelatedContentForProcessInstance(@Path("processInstanceId") String processInstanceId,
            Callback<RelatedContentsRepresentation> callback);

    @GET("/api/enterprise/process-instances/{processInstanceId}/field-content")
    void getProcessInstanceContent(@Path("processInstanceId") String processInstanceId,
            Callback<ProcessContentsRepresentation> callback);

    @Multipart
    @POST("/api/enterprise/process-instances/{processInstanceId}/raw-content")
    void createRelatedContentOnProcessInstance(@Path("processInstanceId") String processInstanceId,
            @Part("file") TypedFile resource, Callback<RelatedContentsRepresentation> callback);

    @POST("/api/enterprise/process-instances/{processInstanceId}/content")
    void linkRelatedContentOnProcessInstance(@Path("processInstanceId") String processInstanceId,
            @Body AddContentRelatedRepresentation representation, Callback<RelatedContentRepresentation> callback);

    @GET("/api/enterprise/tasks/{taskId}/content")
    void getRelatedContentForTask(@Path("taskId") String taskId, Callback<RelatedContentsRepresentation> callback);

    @Multipart
    @POST("/api/enterprise/tasks/{taskId}/raw-content")
    void createRelatedContentOnTask(@Path("taskId") String taskId, @Part("file") TypedFile resource,
            Callback<RelatedContentRepresentation> callback);

    @POST("/api/enterprise/tasks/{taskId}/content")
    void linkRelatedContentOnTask(@Path("taskId") String taskId, @Body AddContentRelatedRepresentation representation,
            Callback<RelatedContentRepresentation> callback);

    @POST("/api/enterprise/content")
    void createTemporaryRelatedContent(@Body AddContentRelatedRepresentation representation,
            Callback<RelatedContentRepresentation> callback);

    @GET("/api/enterprise/content/{contentId}")
    void getContent(@Path("contentId") String contentId, Callback<RelatedContentRepresentation> callback);

    @DELETE("/api/enterprise/content/{contentId}")
    void deleteContent(@Path("contentId") String contentId, Callback<Void> callback);

    @GET("/api/enterprise/content/{contentId}/raw")
    @Streaming
    void getRawContent(@Path("contentId") String contentId, Callback<Response> callback);
}
