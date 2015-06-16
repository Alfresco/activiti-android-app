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
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;
import com.activiti.client.api.model.runtime.ProcessInstancesRepresentation;
import com.activiti.client.api.model.runtime.ProcessesRequestRepresentation;
import com.activiti.client.api.model.runtime.request.CreateProcessInstanceRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public interface ApiProcessInstanceResource
{
    // ///////////////////////////////////////////////////////////////////
    // ASYNC
    // ///////////////////////////////////////////////////////////////////
    @Headers({ "Content-type: application/json" })
    @POST("/api/enterprise/process-instances/query")
    void getProcessInstances(@Body ProcessesRequestRepresentation body,
            Callback<ProcessInstancesRepresentation> callback);

    @GET("/api/enterprise/process-instances/{processInstanceId}")
    void getProcessInstance(@Path("processInstanceId") String taskId, Callback<ProcessInstanceRepresentation> callback);

    @Headers({ "Content-type: application/json" })
    @POST("/api/enterprise/process-instances")
    void startNewProcessInstance(@Body CreateProcessInstanceRepresentation request,
            Callback<ProcessInstanceRepresentation> callback);

    @DELETE("/api/enterprise/process-instances/{processInstanceId}")
    void deleteProcessInstance(@Path("processInstanceId") String processInstanceId, Callback<Void> callback);
}
