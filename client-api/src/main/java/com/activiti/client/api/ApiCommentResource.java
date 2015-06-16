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
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

import com.activiti.client.api.model.runtime.CommentRepresentation;
import com.activiti.client.api.model.runtime.CommentsRepresentation;

/**
 * Created by jpascal on 23/03/2015.
 */
public interface ApiCommentResource
{
    // ///////////////////////////////////////////////////////////////////
    // ASYNC
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/tasks/{taskId}/comments")
    void getTaskComments(@Path("taskId") String taskId, Callback<CommentsRepresentation> callback);

    @Headers({ "Content-type: application/json" })
    @POST("/api/enterprise/tasks/{taskId}/comments")
    void addTaskComment(@Path("taskId") String taskId, @Body CommentRepresentation request,
            Callback<CommentRepresentation> callback);

    @GET("/api/enterprise/process-instances/{processInstanceId}/comments")
    void getProcessInstanceComments(@Path("processInstanceId") String processInstanceId,
            Callback<CommentsRepresentation> callback);

    @Headers({ "Content-type: application/json" })
    @POST("/api/enterprise/process-instances/{processInstanceId}/comments")
    void addProcessInstanceComment(@Path("processInstanceId") String processInstanceId,
            @Body CommentRepresentation request, Callback<CommentRepresentation> callback);

}
