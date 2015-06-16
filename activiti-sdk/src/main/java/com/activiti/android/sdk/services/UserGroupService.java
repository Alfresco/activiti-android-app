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
import com.activiti.client.api.ApiUserGroupResource;
import com.activiti.client.api.model.idm.LightGroupsRepresentation;
import com.activiti.client.api.model.idm.LightUsersRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public class UserGroupService extends ActivitiService
{
    protected ApiUserGroupResource api;

    UserGroupService(RestManager manager)
    {
        super(manager);
        api = manager.adapter.create(ApiUserGroupResource.class);
    }

    // ///////////////////////////////////////////////////////////////////
    // LIST
    // ///////////////////////////////////////////////////////////////////
    public String getPicture(Long userId)
    {
        return String.format(restManager.endpoint.concat("/api/enterprise/users/%s/picture"), userId);
    }

    public void getUsers(String filter, Callback<LightUsersRepresentation> callback)
    {
        api.getUsers(filter, null, null, null, null, null, null, callback);
    }

    public void getUsers(String filter, String excludeTaskId, String excludeProcessId, String groupId, String tenantId,
            Callback<LightUsersRepresentation> callback)
    {
        api.getUsers(filter, null, null, excludeTaskId, excludeProcessId, groupId, tenantId, callback);
    }

    public void getGroups(String filter, String groupId, Callback<LightGroupsRepresentation> callback)
    {
        api.getGroups(filter, groupId, callback);
    }

    public void getUsersForGroup(String groupId, Callback<LightUsersRepresentation> callback)
    {
        api.getUsersForGroup(groupId, callback);
    }

}
