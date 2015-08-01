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

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

import com.activiti.client.api.model.editor.form.FormDefinitionRepresentation;
import com.activiti.client.api.model.editor.form.OptionRepresentation;
import com.activiti.client.api.model.editor.form.request.CompleteFormRepresentation;
import com.activiti.client.api.model.runtime.ChecklistOrderRepresentation;
import com.activiti.client.api.model.runtime.SaveFormRepresentation;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.activiti.client.api.model.runtime.TasksRepresentation;
import com.activiti.client.api.model.runtime.request.AssignTaskRepresentation;
import com.activiti.client.api.model.runtime.request.AttachFormTaskRepresentation;
import com.activiti.client.api.model.runtime.request.InvolveTaskRepresentation;
import com.activiti.client.api.model.runtime.request.QueryTasksRepresentation;
import com.activiti.client.api.model.runtime.request.UpdateTaskRepresentation;

/**
 * Created by jpascal on 11/12/2014.
 */
public interface ApiTaskResource
{
    // ///////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////
    @Headers({ "Content-type: application/json" })
    @POST("/api/enterprise/tasks/query")
    TasksRepresentation listTasks(@Body QueryTasksRepresentation filter);

    // ///////////////////////////////////////////////////////////////////
    // ASYNC
    // ///////////////////////////////////////////////////////////////////
    @Headers({ "Content-type: application/json" })
    @POST("/api/enterprise/tasks/query")
    void listTasks(@Body QueryTasksRepresentation filter, Callback<TasksRepresentation> callback);

    @GET("/api/enterprise/tasks/{taskId}")
    void getTask(@Path("taskId") String taskId, Callback<TaskRepresentation> callback);

    @POST("/api/enterprise/tasks")
    void createNewTask(@Body TaskRepresentation task, Callback<TaskRepresentation> callback);

    @Headers({ "Content-type: application/json" })
    @PUT("/api/enterprise/tasks/{taskId}")
    void updateTask(@Path("taskId") String taskId, @Body UpdateTaskRepresentation request,
            Callback<TaskRepresentation> callback);

    @PUT("/api/enterprise/tasks/{taskId}/action/complete")
    void completeTask(@Path("taskId") String taskId, @Body Object request, Callback<Void> callback);

    @Headers({ "Content-type: application/json" })
    @PUT("/api/enterprise/tasks/{taskId}/action/involve")
    void involveUser(@Path("taskId") String taskId, @Body InvolveTaskRepresentation request, Callback<Void> callback);

    @Headers({ "Content-type: application/json" })
    @PUT("/api/enterprise/tasks/{taskId}/action/remove-involved")
    void removeInvolvedUser(@Path("taskId") String taskId, @Body InvolveTaskRepresentation request,
            Callback<Void> callback);

    @Headers({ "Content-type: application/json" })
    @PUT("/api/enterprise/tasks/{taskId}/action/assign")
    void assignTask(@Path("taskId") String taskId, @Body AssignTaskRepresentation request,
            Callback<TaskRepresentation> callback);

    // ACTIONS
    // ///////////////////////////////////////////////////////////////////
    @PUT("/api/enterprise/tasks/{taskId}/action/claim")
    void claimTask(@Path("taskId") String taskId, @Body Object request, Callback<Void> callback);

    @PUT("/api/enterprise/tasks/{taskId}/action/attach-form")
    void attachForm(@Path("taskId") String taskId, @Body AttachFormTaskRepresentation requestNode,
            Callback<Void> callback);

    @DELETE("/api/enterprise/tasks/{taskId}/action/remove-form")
    void removeForm(@Path("taskId") String taskId, Callback<Void> callback);

    @GET("/api/enterprise/task-forms/{taskId}/form-values/{fieldId}")
    void getFormFieldValues(@Path("taskId") String taskId, @Path("fieldId") String fieldId,
            Callback<List<OptionRepresentation>> callback);

    // FORMS
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/task-forms/{taskId}/")
    void getTaskForm(@Path("taskId") String taskId, Callback<FormDefinitionRepresentation> callback);

    @POST("/api/enterprise/task-forms/{taskId}")
    void completeTaskForm(@Path("taskId") String taskId, @Body CompleteFormRepresentation request,
            Callback<Void> callback);

    @POST("/api/enterprise/task-forms/{taskId}/save-form")
    void saveTaskForm(@Path("taskId") String taskId, @Body SaveFormRepresentation request, Callback<Void> callback);

    // CHECKLIST
    // ///////////////////////////////////////////////////////////////////
    @GET("/api/enterprise/tasks/{taskId}/checklist")
    void getChecklist(@Path("taskId") String taskId, Callback<TasksRepresentation> callback);

    @POST("/api/enterprise/tasks/{taskId}/checklist")
    void addSubtask(@Path("taskId") String taskId, @Body TaskRepresentation task, Callback<TaskRepresentation> callback);

    @POST("/api/enterprise/tasks/{taskId}/checklist")
    void orderChecklist(@Path("taskId") String taskId, @Body ChecklistOrderRepresentation task, Callback<Void> callback);

}
