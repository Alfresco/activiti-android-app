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

package com.activiti.android.ui.fragments.task.filter;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.activiti.android.app.fragments.task.TasksFragment;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModel;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.form.EditTextDialogFragment;
import com.activiti.client.api.constant.RequestConstant;

/**
 * Created by jpascal on 07/03/2015.
 */
public abstract class CommonTaskFilterFragment extends AlfrescoFragment
        implements RequestConstant, EditTextDialogFragment.onEditTextFragment
{
    public static final String TAG = CommonTaskFilterFragment.class.getName();

    private static final int EDIT_NAME_ID = 0;

    protected TasksFragment frag;

    protected Bundle bundle;

    protected String sort, keywords, state, processDefId, assignment;

    protected TextView sortText, keywordsText, stateText, processText, assignmentText;

    protected Long appId;

    private ProcessDefinitionModel processDef;

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public void onSortNegative()
    {
        bundle.putString(ARGUMENT_SORT, SORT_CREATED_DESC);
        sendInfoToTasks();
        refreshInfo();
    }

    public void onSortSelection(String sort)
    {
        bundle.putString(ARGUMENT_SORT, sort);
        sendInfoToTasks();
        refreshInfo();
    }

    public void onStateSelection(String state)
    {
        bundle.putString(ARGUMENT_STATE, state);
        sendInfoToTasks();
        refreshInfo();
    }

    public void onAssignmentSelection(String assignment)
    {
        bundle.putString(ARGUMENT_ASSIGNMENT, assignment);
        sendInfoToTasks();
        refreshInfo();
    }

    public void onProcessDefinitionSelection(ProcessDefinitionModel processDef)
    {
        this.processDef = processDef;
        bundle.putString(ARGUMENT_PROCESSDEFINITION_ID, processDef.getId());
        sendInfoToTasks();
        refreshInfo();
    }

    public void onTaskNameSelection(String keywords)
    {
        bundle.putString(ARGUMENT_TEXT, keywords);
        sendInfoToTasks();
        refreshInfo();
    }

    private void sendInfoToTasks()
    {
        frag.setFilterBundle(bundle, true);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    private void refreshInfo()
    {
        if (bundle != null)
        {
            keywords = (getArguments().containsKey(RequestConstant.ARGUMENT_TEXT))
                    ? getArguments().getString(RequestConstant.ARGUMENT_TEXT) : "";
            sort = (getArguments().containsKey(RequestConstant.ARGUMENT_SORT))
                    ? getArguments().getString(RequestConstant.ARGUMENT_SORT) : "";
            state = (getArguments().containsKey(RequestConstant.ARGUMENT_STATE))
                    ? getArguments().getString(RequestConstant.ARGUMENT_STATE) : "";
            processDefId = (getArguments().containsKey(RequestConstant.ARGUMENT_PROCESSDEFINITION_ID))
                    ? getArguments().getString(RequestConstant.ARGUMENT_PROCESSDEFINITION_ID) : "";
            assignment = (getArguments().containsKey(RequestConstant.ARGUMENT_ASSIGNMENT))
                    ? getArguments().getString(RequestConstant.ARGUMENT_ASSIGNMENT) : "";
        }

        if (appId == null)
        {
            appId = getLastAppId();
        }

        keywordsText.setText(TextUtils.isEmpty(keywords) ? "" : keywords);
        sortText.setText(TaskSortingDialogFragment.getSortTitle(getActivity(), sort));
        stateText.setText(TaskStateDialogFragment.getStateTitle(getActivity(), state));
        processText.setText(TaskProcessDefinitionFragment.getProcessTitle(getActivity(),
                (processDef != null) ? processDef.getName() : processDefId));
        assignmentText.setText(
                TaskAssignmentDialogFragment.getAssignmentTitle(getActivity(), assignment, getAccount().getId()));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onTextEdited(int id, String newValue)
    {
        switch (id)
        {
            case EDIT_NAME_ID:
                onTaskNameSelection(newValue);
                break;
        }
    }

    @Override
    public void onTextClear(int id)
    {
        switch (id)
        {
            case EDIT_NAME_ID:
                onTaskNameSelection(null);
                break;
        }
    }
}
