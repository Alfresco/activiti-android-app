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

package com.activiti.android.ui.fragments.task;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.BaseAdapter;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.activiti.client.api.model.runtime.request.QueryTasksRepresentation;

public class TasksFoundationFragment extends BasePagingGridFragment implements RequestConstant
{
    public static final String TAG = TasksFoundationFragment.class.getName();

    protected String appName;

    protected Long appId;

    protected String processDefinitionId;

    protected String processId;

    protected Long assignee;

    protected String state;

    protected String assignment;

    protected String sort;

    protected String keywords;

    protected Long filterId;

    protected Long page;

    protected Long size;

    protected List<TaskRepresentation> selectedTask = new ArrayList<>();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TasksFoundationFragment()
    {
        emptyListMessageId = R.string.task_message_no_task_help;
        retrieveDataOnCreation = true;
        layoutId = R.layout.grid;
        setRetainInstance(true);
    }

    protected void updateParameters(Bundle bundle)
    {
        appId = (getActivity() instanceof MainActivity) ? ((MainActivity) getActivity()).getAppId() : null;
        if (appId == null)
        {
            appId = getLastAppId();
        }
        appName = (appId != null)
                ? RuntimeAppInstanceManager.getInstance(getActivity()).getById(appId, getAccount().getId()) != null
                        ? RuntimeAppInstanceManager.getInstance(getActivity()).getById(appId, getAccount().getId())
                                .getName()
                        : null
                : null;

        // if (getVersionNumber() <= ActivitiVersionNumber.VERSION_1_3_0)
        // {
        processDefinitionId = BundleUtils.getString(bundle, ARGUMENT_PROCESSDEFINITION_ID);
        keywords = BundleUtils.getString(bundle, ARGUMENT_TEXT);
        processId = BundleUtils.getString(bundle, ARGUMENT_PROCESS_ID);
        assignee = BundleUtils.getLong(bundle, ARGUMENT_ASSIGNEE);
        state = BundleUtils.getString(bundle, ARGUMENT_STATE);
        if (TextUtils.isEmpty(state))
        {
            state = STATE_OPEN;
        }
        assignment = BundleUtils.getString(bundle, ARGUMENT_ASSIGNMENT);
        if (TextUtils.isEmpty(assignment))
        {
            assignment = "no value";
        }
        sort = BundleUtils.getString(bundle, ARGUMENT_SORT);
        if (TextUtils.isEmpty(sort))
        {
            sort = SORT_CREATED_DESC;
        }
        // }
        page = BundleUtils.getLong(bundle, ARGUMENT_PAGE);
        size = BundleUtils.getLong(bundle, ARGUMENT_SIZE);
    }

    protected void onRetrieveParameters(Bundle bundle)
    {
        updateParameters(bundle);
    }

    @Override
    protected void performRequest(long skipCount)
    {
        if (size == null)
        {
            size = 25L;
        }
        page = (skipCount / size);
        performRequest();
    }

    @Override
    protected void performRequest()
    {
        if (appId == null)
        {
            appId = getLastAppId();
        }
        if (appId == -1)
        {
            getAPI().getTaskService().list(new QueryTasksRepresentation(null, processDefinitionId, keywords, processId,
                    assignee, state, assignment, sort, page, size), callBack);
        }
        else
        {
            getAPI().getTaskService().list(new QueryTasksRepresentation(appId, processDefinitionId, keywords, processId,
                    assignee, state, assignment, sort, page, size), callBack);
        }
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new TaskAdapter(getActivity(), R.layout.row_four_lines, new ArrayList<TaskRepresentation>(0),
                selectedTask);
    }

    protected Callback<ResultList<TaskRepresentation>> callBack = new Callback<ResultList<TaskRepresentation>>()
    {
        @Override
        public void onResponse(Call<ResultList<TaskRepresentation>> call,
                Response<ResultList<TaskRepresentation>> response)
        {
            displayData(response.body());
        }

        @Override
        public void onFailure(Call<ResultList<TaskRepresentation>> call, Throwable error)
        {
            displayError(error);
        }
    };

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.tasks, menu);
    }

}
