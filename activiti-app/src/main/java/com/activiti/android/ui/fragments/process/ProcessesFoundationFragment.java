/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
 *
 *  This file is part of Alfresco Activiti Mobile for Android.
 *
 *  Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package com.activiti.android.ui.fragments.process;

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
import com.activiti.android.platform.provider.app.RuntimeAppInstance;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.ProcessDefinitionRepresentation;
import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;
import com.activiti.client.api.model.runtime.ProcessesRequestRepresentation;

public class ProcessesFoundationFragment extends BasePagingGridFragment implements RequestConstant
{
    public static final String TAG = ProcessesFoundationFragment.class.getName();

    protected List<ProcessDefinitionRepresentation> selectedItems = new ArrayList<ProcessDefinitionRepresentation>(1);

    protected String appName;

    protected Long appId;

    protected String state;

    protected String sort;

    protected Long page;

    protected Long size;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessesFoundationFragment()
    {
        emptyListMessageId = R.string.process_message_no_instances_help;
        retrieveDataOnCreation = true;
        layoutId = R.layout.grid;
        setRetainInstance(true);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    protected Callback<ResultList<ProcessInstanceRepresentation>> callBack = new Callback<ResultList<ProcessInstanceRepresentation>>()
    {
        @Override
        public void onResponse(Call<ResultList<ProcessInstanceRepresentation>> call,
                Response<ResultList<ProcessInstanceRepresentation>> response)
        {
            if (!response.isSuccessful())
            {
                onFailure(call, new Exception(response.message()));
                return;
            }
            displayData(response.body());
        }

        @Override
        public void onFailure(Call<ResultList<ProcessInstanceRepresentation>> call, Throwable error)
        {
            displayError(error);
        }
    };

    protected void onRetrieveParameters(Bundle bundle)
    {
        appId = (getActivity() instanceof MainActivity) ? ((MainActivity) getActivity()).getAppId() : null;
        if (appId != null) {
            RuntimeAppInstance appInstance = RuntimeAppInstanceManager
                    .getInstance(getActivity())
                    .getById(appId, getAccount().getId());
            appName = (appInstance != null) ? appInstance.getName() : null;
        }
        state = BundleUtils.getString(bundle, ARGUMENT_STATE);
        if (TextUtils.isEmpty(state))
        {
            state = STATE_RUNNING;
        }
        sort = BundleUtils.getString(bundle, ARGUMENT_SORT);
        if (TextUtils.isEmpty(sort))
        {
            sort = SORT_CREATED_DESC;
        }
        page = BundleUtils.getLong(bundle, ARGUMENT_PAGE);
        size = BundleUtils.getLong(bundle, ARGUMENT_SIZE);
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
        ProcessesRequestRepresentation request;
        if (appId == -1)
        {
            request = new ProcessesRequestRepresentation(null, state, sort, page, size);
        }
        else
        {
            request = new ProcessesRequestRepresentation(appId, null, state, sort, page, size);
        }
        getAPI().getProcessService().list(request, callBack);
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new ProcessAdapter(getActivity(), R.layout.row_three_lines_caption,
                new ArrayList<ProcessInstanceRepresentation>(0));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.processes, menu);
    }
}
