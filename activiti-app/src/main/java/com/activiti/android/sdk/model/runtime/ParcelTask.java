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

package com.activiti.android.sdk.model.runtime;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.activiti.android.sdk.model.ParcelUtils;
import com.activiti.client.api.model.runtime.TaskRepresentation;

/**
 * Created by jpascal on 03/04/2015.
 */
public class ParcelTask implements Parcelable
{
    public final String id;

    public final String name;

    public final String description;

    public final String category;

    // public final LightUserRepresentation assignee;

    public final Date created;

    public final Date dueDate;

    public final Date endDate;

    public final Long duration;

    public final Integer priority;

    public final String processInstanceId;

    public final String processInstanceName;

    public final String processDefinitionId;

    public final String processDefinitionName;

    public final String processDefinitionDescription;

    public final String processDefinitionKey;

    public final String processDefinitionCategory;

    public final int processDefinitionVersion;

    public final String processDefinitionDeploymentId;

    public final String formKey;

    public final String processInstanceStartUserId;

    public final boolean initiatorCanCompleteTask;

    public final boolean isMemberOfCandidateGroup;

    public final boolean isMemberOfCandidateUsers;

    public ParcelTask(TaskRepresentation representation)
    {
        this.id = representation.getId();
        this.name = representation.getName();
        this.description = representation.getDescription();
        this.category = representation.getCategory();
        this.created = representation.getCreated();
        this.dueDate = representation.getDueDate();
        this.endDate = representation.getEndDate();
        this.duration = representation.getDuration();
        this.priority = representation.getPriority();
        this.processInstanceId = representation.getProcessInstanceId();
        this.processInstanceName = representation.getProcessInstanceName();
        this.processDefinitionId = representation.getProcessDefinitionId();
        this.processDefinitionName = representation.getProcessDefinitionName();
        this.processDefinitionDescription = representation.getProcessDefinitionDescription();
        this.processDefinitionKey = representation.getProcessDefinitionKey();
        this.processDefinitionCategory = representation.getProcessDefinitionCategory();
        this.processDefinitionVersion = representation.getProcessDefinitionVersion();
        this.processDefinitionDeploymentId = representation.getProcessDefinitionDeploymentId();
        this.formKey = representation.getFormKey();
        this.processInstanceStartUserId = representation.getProcessInstanceStartUserId();
        this.initiatorCanCompleteTask = representation.isInitiatorCanCompleteTask();
        this.isMemberOfCandidateGroup = representation.isMemberOfCandidateGroup();
        this.isMemberOfCandidateUsers = representation.isMemberOfCandidateUsers();
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1)
    {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        ParcelUtils.writeDate(dest, created);
        ParcelUtils.writeDate(dest, dueDate);
        ParcelUtils.writeDate(dest, endDate);
        ParcelUtils.writeLong(dest, duration);
        dest.writeInt(priority);
        dest.writeString(processInstanceId);
        dest.writeString(processInstanceName);
        dest.writeString(processDefinitionId);
        dest.writeString(processDefinitionName);
        dest.writeString(processDefinitionDescription);
        dest.writeString(processDefinitionKey);
        dest.writeString(processDefinitionCategory);
        dest.writeInt(processDefinitionVersion);
        dest.writeString(processDefinitionDeploymentId);
        dest.writeString(formKey);
        dest.writeString(processInstanceStartUserId);
        dest.writeString(Boolean.toString(initiatorCanCompleteTask));
        dest.writeString(Boolean.toString(isMemberOfCandidateGroup));
        dest.writeString(Boolean.toString(isMemberOfCandidateUsers));
    }

    public static final Parcelable.Creator<ParcelTask> CREATOR = new Parcelable.Creator<ParcelTask>()
    {
        public ParcelTask createFromParcel(Parcel in)
        {
            return new ParcelTask(in);
        }

        public ParcelTask[] newArray(int size)
        {
            return new ParcelTask[size];
        }
    };

    public ParcelTask(Parcel o)
    {
        this.id = o.readString();
        this.name = o.readString();
        this.description = o.readString();
        this.category = o.readString();
        this.created = ParcelUtils.readDate(o);
        this.dueDate = ParcelUtils.readDate(o);
        this.endDate = ParcelUtils.readDate(o);
        this.duration = ParcelUtils.readLong(o);
        this.priority = o.readInt();
        this.processInstanceId = o.readString();
        this.processInstanceName = o.readString();
        this.processDefinitionId = o.readString();
        this.processDefinitionName = o.readString();
        this.processDefinitionDescription = o.readString();
        this.processDefinitionKey = o.readString();
        this.processDefinitionCategory = o.readString();
        this.processDefinitionVersion = o.readInt();
        this.processDefinitionDeploymentId = o.readString();
        this.formKey = o.readString();
        this.processInstanceStartUserId = o.readString();
        this.initiatorCanCompleteTask = Boolean.parseBoolean(o.readString());
        this.isMemberOfCandidateGroup = Boolean.parseBoolean(o.readString());
        this.isMemberOfCandidateUsers = Boolean.parseBoolean(o.readString());
    }
}
