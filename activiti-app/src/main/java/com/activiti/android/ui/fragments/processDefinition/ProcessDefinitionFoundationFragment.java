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

package com.activiti.android.ui.fragments.processDefinition;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.ProcessDefinitionRepresentation;

public class ProcessDefinitionFoundationFragment extends BasePagingGridFragment
{
    public static final String TAG = ProcessDefinitionFoundationFragment.class.getName();

    public static final String ARGUMENT_APPDEFINITION_ID = "processDefinitionId";

    protected List<ProcessDefinitionRepresentation> selectedItems = new ArrayList<ProcessDefinitionRepresentation>(1);

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    protected Callback<ResultList<ProcessDefinitionRepresentation>> callBack = new Callback<ResultList<ProcessDefinitionRepresentation>>()
    {
        @Override
        public void onResponse(Call<ResultList<ProcessDefinitionRepresentation>> call,
                Response<ResultList<ProcessDefinitionRepresentation>> response)
        {
            displayData(response.body());
        }

        @Override
        public void onFailure(Call<ResultList<ProcessDefinitionRepresentation>> call, Throwable error)
        {
            displayError(error);
        }
    };

    private Long appDefinitionId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessDefinitionFoundationFragment()
    {
        emptyListMessageId = R.string.process_message_no_instances;
        retrieveDataOnCreation = true;
    }

    protected void onRetrieveParameters(Bundle bundle)
    {
        if (getArguments().containsKey(ARGUMENT_APPDEFINITION_ID))
        {
            appDefinitionId = (Long) getArguments().get(ARGUMENT_APPDEFINITION_ID);
        }
    }

    @Override
    protected void performRequest()
    {
        if (appDefinitionId != null)
        {
            getAPI().getProcessDefinitionService().getProcessDefinitions(appDefinitionId, callBack);
        }
        else
        {
            getAPI().getProcessDefinitionService().getProcessDefinitions(callBack);
        }
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new ProcessDefinitionAdapter(getActivity(), R.layout.row_two_lines,
                new ArrayList<ProcessDefinitionRepresentation>(0));
    }
}
