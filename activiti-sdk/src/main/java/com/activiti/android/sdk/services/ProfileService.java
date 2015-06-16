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
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import com.activiti.android.sdk.RestManager;
import com.activiti.client.api.ApiIdmProfileResource;
import com.activiti.client.api.model.idm.UserRepresentation;
import com.activiti.client.api.model.idm.request.ChangePasswordRepresentation;
import com.activiti.client.api.model.idm.request.UpdateProfileRepresentation;
import com.activiti.client.api.model.runtime.integration.dto.AlfrescoEndpointsRepresentation;

/**
 * Created by jpascal on 17/03/2015.
 */
public class ProfileService extends ActivitiService
{
    protected ApiIdmProfileResource api;

    ProfileService(RestManager manager)
    {
        super(manager);
        api = manager.adapter.create(ApiIdmProfileResource.class);
    }

    public String getProfilePictureURL()
    {
        return restManager.endpoint.concat("/api/enterprise/profile-picture");
    }

    public void getProfile(Callback<UserRepresentation> callback)
    {
        api.getProfile(callback);
    }

    public UserRepresentation getProfile()
    {
        return api.getProfile();
    }

    public AlfrescoEndpointsRepresentation getAlfrescoRepositories()
    {
        return api.getRepositories();
    }

    public void getAlfrescoRepositories(Callback<AlfrescoEndpointsRepresentation> callback)
    {
        api.getRepositories(callback);
    }

    public void getProfilePicture(Callback<Void> callback)
    {
        api.getProfilePicture(callback);
    }

    public void updateProfile(UpdateProfileRepresentation request, Callback<UserRepresentation> callback)
    {
        api.updateUser(request, callback);
    }

    public void updatePassword(ChangePasswordRepresentation request, Callback<UserRepresentation> callback)
    {
        api.changePassword(request, callback);
    }

    public Response updateProfilePicture(TypedFile resource)
    {
        return api.uploadProfilePicture(resource);
    }
}
