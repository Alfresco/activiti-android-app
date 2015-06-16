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
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

import com.activiti.client.api.model.idm.UserRepresentation;
import com.activiti.client.api.model.idm.request.ChangePasswordRepresentation;
import com.activiti.client.api.model.idm.request.UpdateProfileRepresentation;
import com.activiti.client.api.model.runtime.integration.dto.AlfrescoEndpointsRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public interface ApiIdmProfileResource
{
    // ///////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/profile")
    UserRepresentation getProfile();

    @Multipart
    @POST("/api/enterprise/profile-picture")
    Response uploadProfilePicture(@Part("file") TypedFile resource);

    @GET("/api/enterprise/profile/accounts/alfresco")
    AlfrescoEndpointsRepresentation getRepositories();

    // ///////////////////////////////////////////////////////////////////
    // ASYNC
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/profile")
    void getProfile(Callback<UserRepresentation> callback);

    @Headers({ "Content-type: application/json" })
    @POST("/api/enterprise/profile")
    void updateUser(@Body UpdateProfileRepresentation request, Callback<UserRepresentation> callback);

    @GET("/api/enterprise/profile-picture")
    void getProfilePicture(Callback<Void> callback);

    @Multipart
    @POST("/api/enterprise/profile-picture")
    void uploadProfilePicture(@Part("file") TypedFile resource, Callback<Response> callback);

    @Headers({ "Content-type: application/json" })
    @POST("/api/enterprise/profile-password")
    void changePassword(@Body ChangePasswordRepresentation request, Callback<UserRepresentation> callback);

    @GET("/api/enterprise/profile/accounts/alfresco")
    void getRepositories(Callback<AlfrescoEndpointsRepresentation> callback);
}
