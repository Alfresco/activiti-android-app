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
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.activiti.client.api.model.idm.LightGroupsRepresentation;
import com.activiti.client.api.model.idm.LightUsersRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public interface ApiUserGroupResource
{

    // ///////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////////////
    // ASYNC
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/users")
    void getUsers(@Query("filter") String filter, @Query("email") String email, @Query("externalId") String externalId,
            @Query("excludeTaskId") String excludeTaskId, @Query("excludeProcessId") String excludeProcessId,
            @Query("groupId") String groupId, @Query("tenantId") String tenantId,
            Callback<LightUsersRepresentation> callback);

    @GET("/api/enterprise/groups")
    void getGroups(@Query("filter") String filter, @Query("groupId") String groupId,
            Callback<LightGroupsRepresentation> callback);

    @GET("/api/enterprise/groups/{groupId}/users")
    void getUsersForGroup(@Path("groupId") String groupId, Callback<LightUsersRepresentation> callback);

}
