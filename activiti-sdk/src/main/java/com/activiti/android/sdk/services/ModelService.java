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

import com.activiti.android.sdk.RestManager;
import com.activiti.client.api.ApiModelsResource;
import com.activiti.client.api.model.editor.ModelsRepresentation;

/**
 * Created by jpascal on 12/12/2014.
 */
public class ModelService extends ActivitiService
{
    protected static final String MODEL_FORMS = "2";
    protected static final String MODEL_APPS = "3";


    protected ApiModelsResource api;

    ModelService(RestManager manager)
    {
        super(manager);
        api = manager.adapter.create(ApiModelsResource.class);
    }

    // ///////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////
    public String getModelThumbnailUrl(String modelId)
    {
        return String.format(restManager.endpoint.concat("/api/enterprise/models/%s/thumbnail"), modelId);
    }

    public ModelsRepresentation getAppDefinitionModels()
    {
        return api.getModels("myApps", MODEL_APPS, "modifiedDesc");
    }

    public ModelsRepresentation getFormModels()
    {
        return api.getModels(null, MODEL_FORMS, "modifiedDesc");
    }

    // ///////////////////////////////////////////////////////////////////
    // ASYNC
    // ///////////////////////////////////////////////////////////////////
    public void getAppDefinitionModels(Callback<ModelsRepresentation> callback)
    {
        api.getModels("myApps", MODEL_APPS, "modifiedDesc", callback);
    }

    public void getFormModels(Callback<ModelsRepresentation> callback)
    {
        api.getModels(null, MODEL_FORMS, "modifiedDesc", callback);
    }

    public void getModelThumbnail(String modelId, Callback<Response> callback)
    {
        api.getModelThumbnail(modelId, callback);
    }



}
