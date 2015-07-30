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
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;

import com.activiti.client.api.model.editor.ModelRepresentation;
import com.activiti.client.api.model.editor.ModelsRepresentation;

public interface ApiModelsResource
{
    // ///////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/models")
    ModelsRepresentation getModels(@Query("filter") String filter, @Query("modelType") String modelType,
            @Query("sort") String sort);

    // ///////////////////////////////////////////////////////////////////
    // ASYNC
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/models")
    void getModels(@Query("filter") String filter, @Query("modelType") String modelType, @Query("sort") String sort,
            Callback<ModelsRepresentation> callback);

    // Since 1.3
    @GET("/api/enterprise/models/{modelId}/thumbnail")
    @Streaming
    void getModelThumbnail(@Path("modelId") String modelId, Callback<Response> callback);

    // Since 1.3
    @GET("/api/enterprise/models/{modelId}")
    @Streaming
    void getModel(@Path("modelId") String processInstanceId, @Query("includePermissions") String includePermissions,
            Callback<ModelRepresentation> callback);

}
