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

import java.util.List;

import retrofit.Callback;
import retrofit.mime.TypedFile;

import com.activiti.android.sdk.RestManager;
import com.activiti.client.api.ApiCommentResource;
import com.activiti.client.api.ApiRelatedContentResource;
import com.activiti.client.api.ApiTaskResource;
import com.activiti.client.api.model.editor.form.FormDefinitionRepresentation;
import com.activiti.client.api.model.editor.form.OptionRepresentation;
import com.activiti.client.api.model.editor.form.request.CompleteFormRepresentation;
import com.activiti.client.api.model.runtime.CommentRepresentation;
import com.activiti.client.api.model.runtime.CommentsRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentsRepresentation;
import com.activiti.client.api.model.runtime.SaveFormRepresentation;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.activiti.client.api.model.runtime.TasksRepresentation;
import com.activiti.client.api.model.runtime.request.AddContentRelatedRepresentation;
import com.activiti.client.api.model.runtime.request.AssignTaskRepresentation;
import com.activiti.client.api.model.runtime.request.CreateTaskRepresentation;
import com.activiti.client.api.model.runtime.request.InvolveTaskRepresentation;
import com.activiti.client.api.model.runtime.request.QueryTasksRepresentation;
import com.activiti.client.api.model.runtime.request.UpdateTaskRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public class TaskService extends ActivitiService
{
    protected ApiTaskResource api;

    protected ApiCommentResource commentApi;

    protected ApiRelatedContentResource contentApi;

    TaskService(RestManager manager)
    {
        super(manager);
        api = manager.adapter.create(ApiTaskResource.class);
        commentApi = manager.adapter.create(ApiCommentResource.class);
        contentApi = manager.adapter.create(ApiRelatedContentResource.class);
    }

    public String getShareUrl(String taskId)
    {
        return String.format(restManager.endpoint.concat("/workflow/#/task/%s"), taskId);
    }

    // ///////////////////////////////////////////////////////////////////
    // LISTING
    // ///////////////////////////////////////////////////////////////////
    public void list(QueryTasksRepresentation filter, Callback<TasksRepresentation> callback)
    {
        api.listTasks(filter, callback);
    }

    public TasksRepresentation list(QueryTasksRepresentation filter)
    {
        return api.listTasks(filter);
    }

    // ///////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////
    public void getById(String taskId, Callback<TaskRepresentation> callback)
    {
        api.getTask(taskId, callback);
    }

    public void create(CreateTaskRepresentation task, Callback<TaskRepresentation> callback)
    {
        api.createNewTask(task, callback);
    }

    public void edit(String taskId, UpdateTaskRepresentation request, Callback<TaskRepresentation> callback)
    {
        api.updateTask(taskId, request, callback);
    }

    public void complete(String taskId, Callback<Void> callback)
    {
        api.completeTask(taskId, new Object(), callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // CONTENT
    // ///////////////////////////////////////////////////////////////////
    public void getAttachments(String taskId, Callback<RelatedContentsRepresentation> callback)
    {
        contentApi.getRelatedContentForTask(taskId, callback);
    }

    public void addAttachment(String taskId, TypedFile resource, Callback<RelatedContentRepresentation> callback)
    {
        contentApi.createRelatedContentOnTask(taskId, resource, callback);
    }

    public void deleteAttachment(Long contentId, Callback<Void> callback)
    {
        contentApi.deleteContent(Long.toString(contentId), callback);
    }

    public void linkAttachment(String taskId, AddContentRelatedRepresentation representation,
            Callback<RelatedContentRepresentation> callback)
    {
        contentApi.linkRelatedContentOnTask(taskId, representation, callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // COMMENT
    // ///////////////////////////////////////////////////////////////////
    public void getComments(String taskId, Callback<CommentsRepresentation> callback)
    {
        commentApi.getTaskComments(taskId, callback);
    }

    public void addComment(String taskId, CommentRepresentation request, Callback<CommentRepresentation> callback)
    {
        commentApi.addTaskComment(taskId, request, callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////
    public void assign(String taskId, AssignTaskRepresentation request, Callback<TaskRepresentation> callback)
    {
        api.assignTask(taskId, request, callback);
    }

    public void involve(String taskId, InvolveTaskRepresentation request, Callback<Void> callback)
    {
        api.involveUser(taskId, request, callback);
    }

    public void removeInvolved(String taskId, InvolveTaskRepresentation request, Callback<Void> callback)
    {
        api.removeInvolvedUser(taskId, request, callback);
    }

    public void claimTask(String taskId, Callback<Void> callback)
    {
        api.claimTask(taskId, new Object(), callback);
    }

    // ///////////////////////////////////////////////////////////////////
    // FORMS
    // ///////////////////////////////////////////////////////////////////
    public void getTaskForm(String taskId, Callback<FormDefinitionRepresentation> callback)
    {
        api.getTaskForm(taskId, callback);
    }

    public void getFormFieldValues(String taskId, String fieldId, Callback<List<OptionRepresentation>> callback)
    {
        api.getFormFieldValues(taskId, fieldId, callback);
    }

    public void completeTaskForm(String taskId, CompleteFormRepresentation request, Callback<Void> callback)
    {
        api.completeTaskForm(taskId, request, callback);
    }

    public void saveTaskForm(String taskId, SaveFormRepresentation request, Callback<Void> callback)
    {
        api.saveTaskForm(taskId, request, callback);
    }

}
