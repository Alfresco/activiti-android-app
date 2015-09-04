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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.app.fragments.task.TasksFragment;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModel;
import com.activiti.android.ui.fragments.form.EditTextDialogFragment;
import com.activiti.client.api.constant.RequestConstant;

/**
 * Created by jpascal on 07/03/2015.
 */
public class TaskFiltersFragment extends CommonTaskFilterFragment
{
    public static final String TAG = TaskFiltersFragment.class.getName();

    private static final int EDIT_NAME_ID = 0;

    protected TasksFragment frag;

    protected Bundle bundle;

    protected String sort, keywords, state, processDefId, assignment;

    protected TextView sortText, keywordsText, stateText, processText, assignmentText;

    protected Long appId;

    private ProcessDefinitionModel processDef;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TaskFiltersFragment()
    {
        super();
        setRetainInstance(true);
    }

    public static TaskFiltersFragment newInstanceByTemplate(Bundle b)
    {
        TaskFiltersFragment cbf = new TaskFiltersFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRetainInstance(true);

        // Horrible hack to support screen rotation and keep filter info
        // available.
        if (getArguments() != null)
        {
            bundle = getArguments();
            if (getArguments().containsKey(RequestConstant.ARGUMENT_APPDEFINITION_ID))
            {
                appId = getArguments().getLong(RequestConstant.ARGUMENT_APPDEFINITION_ID);
            }
        }
        else
        {
            bundle = new Bundle();
        }

        if (appId == null)
        {
            appId = getLastAppId();
        }

        setRootView(inflater.inflate(R.layout.fr_task_filters, container, false));

        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        frag = (TasksFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TasksFragment.TAG);

        keywordsText = (TextView) viewById(R.id.task_filter_keywords_value);
        sortText = (TextView) viewById(R.id.task_filter_sort_value);
        stateText = (TextView) viewById(R.id.task_filter_state_value);
        processText = (TextView) viewById(R.id.task_filter_process_definition_value);
        assignmentText = (TextView) viewById(R.id.task_filter_assignment_value);

        refreshInfo();

        viewById(R.id.task_filter_keywords).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditTextDialogFragment.with(getActivity()).fieldId(EDIT_NAME_ID).tag(getTag()).singleLine(true)
                        .notNull(false).value(keywordsText.getText().toString())
                        .hintId(R.string.task_filter_text_placeholder_long).displayAsDialog();
            }
        });

        LinearLayout sortClick = (LinearLayout) viewById(R.id.task_filter_sort);
        sortClick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TaskSortingDialogFragment.with(getActivity()).sort(sort).displayAsDialog();
            }
        });

        LinearLayout stateButton = (LinearLayout) viewById(R.id.task_filter_state);
        stateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TaskStateDialogFragment.with(getActivity()).state(state).displayAsDialog();
            }
        });

        LinearLayout assignmentButton = (LinearLayout) viewById(R.id.task_filter_assignment);
        assignmentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TaskAssignmentDialogFragment.with(getActivity()).assignment(assignment).displayAsDialog();
            }
        });

        LinearLayout processDefIdButton = (LinearLayout) viewById(R.id.task_filter_process_definition);
        processDefIdButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TaskProcessDefinitionFragment.with(getActivity()).appId(appId).processDefId(processDefId)
                        .displayAsDialog();
            }
        });

        viewById(R.id.action_reset).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Reset all parameters
                bundle.putString(ARGUMENT_PROCESSDEFINITION_ID, null);
                bundle.putString(ARGUMENT_TEXT, null);
                bundle.putString(ARGUMENT_ASSIGNMENT, null);
                bundle.putString(ARGUMENT_STATE, null);
                bundle.putString(ARGUMENT_SORT, null);
                getArguments().putString(ARGUMENT_PROCESSDEFINITION_ID, null);
                processDef = null;
                getArguments().putString(ARGUMENT_TEXT, null);
                getArguments().putString(ARGUMENT_ASSIGNMENT, null);
                getArguments().putString(ARGUMENT_STATE, null);
                getArguments().putString(ARGUMENT_SORT, null);
                sendInfoToTasks();
                refreshInfo();
            }
        });
    }

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
        frag.setFilterBundle(bundle);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    private void refreshInfo()
    {
        if (bundle != null)
        {
            keywords = (getArguments().containsKey(RequestConstant.ARGUMENT_TEXT)) ? getArguments().getString(
                    RequestConstant.ARGUMENT_TEXT) : "";
            sort = (getArguments().containsKey(RequestConstant.ARGUMENT_SORT)) ? getArguments().getString(
                    RequestConstant.ARGUMENT_SORT) : "";
            state = (getArguments().containsKey(RequestConstant.ARGUMENT_STATE)) ? getArguments().getString(
                    RequestConstant.ARGUMENT_STATE) : "";
            processDefId = (getArguments().containsKey(RequestConstant.ARGUMENT_PROCESSDEFINITION_ID)) ? getArguments()
                    .getString(RequestConstant.ARGUMENT_PROCESSDEFINITION_ID) : "";
            assignment = (getArguments().containsKey(RequestConstant.ARGUMENT_ASSIGNMENT)) ? getArguments().getString(
                    RequestConstant.ARGUMENT_ASSIGNMENT) : "";
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
        assignmentText.setText(TaskAssignmentDialogFragment.getAssignmentTitle(getActivity(), assignment, getAccount()
                .getId()));
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
