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

import com.activiti.android.sdk.RestManager;

/**
 * Created by jpascal on 17/03/2015.
 */
public class ServiceRegistry
{
    protected RestManager adapter;

    protected ProfileService profileService;

    protected ApplicationService applicationservice;

    protected TaskService taskService;

    protected ProcessService processService;

    protected UserGroupService userGroupService;

    protected ProcessDefinitionService processDefinitionService;

    protected ContentService contentService;

    protected InfoService infoService;

    protected ModelService modelService;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public ServiceRegistry(RestManager adapter)
    {
        this.adapter = adapter;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SERVICES
    // ///////////////////////////////////////////////////////////////////////////
    public ProfileService getProfileService()
    {
        if (profileService == null)
        {
            profileService = new ProfileService(adapter);
        }
        return profileService;
    }

    public ApplicationService getApplicationService()
    {
        if (applicationservice == null)
        {
            applicationservice = new ApplicationService(adapter);
        }
        return applicationservice;
    }

    public TaskService getTaskService()
    {
        if (taskService == null)
        {
            taskService = new TaskService(adapter);
        }
        return taskService;
    }

    public ProcessService getProcessService()
    {
        if (processService == null)
        {
            processService = new ProcessService(adapter);
        }
        return processService;
    }

    public ApplicationService getApplicationservice()
    {
        if (applicationservice == null)
        {
            applicationservice = new ApplicationService(adapter);
        }
        return applicationservice;
    }

    public UserGroupService getUserGroupService()
    {
        if (userGroupService == null)
        {
            userGroupService = new UserGroupService(adapter);
        }
        return userGroupService;
    }

    public ProcessDefinitionService getProcessDefinitionService()
    {
        if (processDefinitionService == null)
        {
            processDefinitionService = new ProcessDefinitionService(adapter);
        }
        return processDefinitionService;
    }

    public ContentService getContentService()
    {
        if (contentService == null)
        {
            contentService = new ContentService(adapter);
        }
        return contentService;
    }

    public InfoService getInfoService()
    {
        if (infoService == null)
        {
            infoService = new InfoService(adapter);
        }
        return infoService;
    }

    public ModelService getModelService()
    {
        if (modelService == null)
        {
            modelService = new ModelService(adapter);
        }
        return modelService;
    }

}
