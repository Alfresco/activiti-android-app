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
import com.activiti.client.api.ApiModelsResource;
import com.activiti.client.api.model.editor.AppDefinitionsRepresentation;

/**
 * Created by jpascal on 12/12/2014.
 */
public class ModelService extends ActivitiService
{
    protected ApiModelsResource api;

    ModelService(RestManager manager)
    {
        super(manager);
        api = manager.adapter.create(ApiModelsResource.class);
    }

    public void getAppDefinitionModels(Callback<AppDefinitionsRepresentation> callback)
    {
        api.getModels("myApps", "3", "modifiedDesc", callback);
    }

    public AppDefinitionsRepresentation getAppDefinitionModels()
    {
        return api.getModels("myApps", "3", "modifiedDesc");
    }
}
