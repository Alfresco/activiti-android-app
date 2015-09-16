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

import com.activiti.android.sdk.RestManager;
import com.activiti.client.api.ApiUserFiltersResource;
import com.activiti.client.api.model.runtime.UserFilterOrderRepresentation;
import com.activiti.client.api.model.runtime.UserProcessInstanceFilterRepresentation;
import com.activiti.client.api.model.runtime.UserProcessInstanceFiltersRepresentation;
import com.activiti.client.api.model.runtime.UserTaskFilterRepresentation;
import com.activiti.client.api.model.runtime.UserTaskFiltersRepresentation;

public class UserFiltersService extends ActivitiService
{
    protected ApiUserFiltersResource api;

    UserFiltersService(RestManager manager)
    {
        super(manager);
        api = manager.adapter.create(ApiUserFiltersResource.class);

    }

    // ///////////////////////////////////////////////////////////////////
    // TASKS
    // ///////////////////////////////////////////////////////////////////
    public void getUserTaskFilters(Long appId, Callback<UserTaskFiltersRepresentation> callback)
    {
        api.getUserTaskFilters(appId, callback);
    }

    public void getUserTaskFilter(Long userFilterId, Callback<UserTaskFilterRepresentation> callback)
    {
        api.getUserTaskFilter(userFilterId, callback);
    }

    public void createUserTaskFilter(UserTaskFilterRepresentation userTaskFilterRepresentation,
            Callback<UserTaskFilterRepresentation> callback)
    {
        api.createUserTaskFilter(userTaskFilterRepresentation, callback);
    }

    public void updateUserTaskFilter(Long userFilterId, UserTaskFilterRepresentation userTaskFilterRepresentation,
            Callback<UserTaskFilterRepresentation> callback)
    {
        api.updateUserTaskFilter(userFilterId, userTaskFilterRepresentation, callback);
    }

    public void orderUserTaskFilters(UserFilterOrderRepresentation filterOrderRepresentation, Callback<Void> callback)
    {
        api.orderUserTaskFilters(filterOrderRepresentation, callback);
    }

    public void deleteUserTaskFilter(Long userFilterId, Callback<Void> callback)
    {
        api.deleteUserTaskFilter(userFilterId, callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // PROCESSES
    // ///////////////////////////////////////////////////////////////////
    public void getUserProcessInstanceFilters(Long appId, Callback<UserProcessInstanceFiltersRepresentation> callback)
    {
        api.getUserProcessInstanceFilters(appId, callback);
    }

    public void getUserProcessInstanceFilter(Long userFilterId,
            Callback<UserProcessInstanceFilterRepresentation> callback)
    {
        api.getUserProcessInstanceFilter(userFilterId, callback);
    }

    public void createUserProcessInstanceFilter(UserProcessInstanceFilterRepresentation userTaskFilterRepresentation,
            Callback<UserProcessInstanceFilterRepresentation> callback)
    {
        api.createUserProcessInstanceFilter(userTaskFilterRepresentation, callback);
    }

    public void updateUserProcessInstanceFilter(Long userFilterId,
            UserProcessInstanceFilterRepresentation userTaskFilterRepresentation,
            Callback<UserProcessInstanceFilterRepresentation> callback)
    {
        api.updateUserProcessInstanceFilter(userFilterId, userTaskFilterRepresentation, callback);
    }

    public void orderUserProcessInstanceFilters(UserFilterOrderRepresentation filterOrderRepresentation,
            Callback<Void> callback)
    {
        api.orderUserProcessInstanceFilters(filterOrderRepresentation, callback);
    }

    public void deleteUserProcessInstanceFilter(Long userFilterId, Callback<Void> callback)
    {
        api.deleteUserProcessInstanceFilter(userFilterId, callback);
    }
}
