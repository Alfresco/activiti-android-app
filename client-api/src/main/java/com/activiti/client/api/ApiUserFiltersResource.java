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
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

import com.activiti.client.api.model.runtime.UserFilterOrderRepresentation;
import com.activiti.client.api.model.runtime.UserProcessInstanceFilterRepresentation;
import com.activiti.client.api.model.runtime.UserProcessInstanceFiltersRepresentation;
import com.activiti.client.api.model.runtime.UserTaskFilterRepresentation;
import com.activiti.client.api.model.runtime.UserTaskFiltersRepresentation;

/**
 *
 */
public interface ApiUserFiltersResource
{
    // ///////////////////////////////////////////////////////////////////
    // ASYNC
    // ///////////////////////////////////////////////////////////////////

    // TASK FILTER
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/filters/tasks")
    void getUserTaskFilters(@Query("appId") Long appId, Callback<UserTaskFiltersRepresentation> callback);

    @GET("/api/enterprise/filters/tasks/{userFilterId}")
    void getUserTaskFilter(@Path("userFilterId") Long userFilterId, Callback<UserTaskFilterRepresentation> callback);

    @POST("/api/enterprise/filters/tasks")
    void createUserTaskFilter(@Body UserTaskFilterRepresentation userTaskFilterRepresentation,
            Callback<UserTaskFilterRepresentation> callback);

    @PUT("/api/enterprise/filters/tasks/{userFilterId}")
    void updateUserTaskFilter(@Path("userFilterId") Long userFilterId,
            @Body UserTaskFilterRepresentation userTaskFilterRepresentation,
            Callback<UserTaskFilterRepresentation> callback);

    @PUT("/api/enterprise/filters/tasks")
    void orderUserTaskFilters(@Body UserFilterOrderRepresentation filterOrderRepresentation, Callback<Void> callback);

    @DELETE("/api/enterprise/filters/tasks/{userFilterId}")
    void deleteUserTaskFilter(@Path("userFilterId") Long userFilterId, Callback<Void> callback);

    // PROCESS FILTER
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/filters/processes")
    void getUserProcessInstanceFilters(@Query("appId") Long appId,
            Callback<UserProcessInstanceFiltersRepresentation> callback);

    @GET("/api/enterprise/filters/processes/{userFilterId}")
    void getUserProcessInstanceFilter(@Path("userFilterId") Long userFilterId,
            Callback<UserProcessInstanceFilterRepresentation> callback);

    @POST("/api/enterprise/filters/processes")
    void createUserProcessInstanceFilter(@Body UserProcessInstanceFilterRepresentation userTaskFilterRepresentation,
            Callback<UserProcessInstanceFilterRepresentation> callback);

    @PUT("/api/enterprise/filters/processes/{userFilterId}")
    void updateUserProcessInstanceFilter(@Path("userFilterId") Long userFilterId,
            @Body UserProcessInstanceFilterRepresentation userTaskFilterRepresentation,
            Callback<UserProcessInstanceFilterRepresentation> callback);

    @PUT("/api/enterprise/filters/processes")
    void orderUserProcessInstanceFilters(@Body UserFilterOrderRepresentation filterOrderRepresentation,
            Callback<Void> callback);

    @DELETE("/api/enterprise/filters/processes/{userFilterId}")
    void deleteUserProcessInstanceFilter(@Path("userFilterId") Long userFilterId, Callback<Void> callback);
}
