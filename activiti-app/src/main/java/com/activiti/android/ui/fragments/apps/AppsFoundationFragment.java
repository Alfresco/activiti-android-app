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

package com.activiti.android.ui.fragments.apps;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.activiti.android.app.R;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.AppDefinitionRepresentation;

public class AppsFoundationFragment extends BasePagingGridFragment
{
    public static final String TAG = AppsFoundationFragment.class.getName();

    protected static final String ARGUMENT_DRAWER_ID = "drawerId";

    protected Integer drawerId;

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    protected Callback<ResultList<AppDefinitionRepresentation>> callBack = new Callback<ResultList<AppDefinitionRepresentation>>()
    {
        @Override
        public void onResponse(Call<ResultList<AppDefinitionRepresentation>> call,
                Response<ResultList<AppDefinitionRepresentation>> response)
        {
            if (!response.isSuccessful())
            {
                onFailure(call, new Exception(response.message()));
                return;
            }
            AppDefinitionRepresentation myTasks = new AppDefinitionRepresentation();
            myTasks.setName("My Tasks");
            myTasks.setId(-1L);
            response.body().getList().add(0, myTasks);
            displayData(response.body());
        }

        @Override
        public void onFailure(Call<ResultList<AppDefinitionRepresentation>> call, Throwable error)
        {
            displayError(error);
        }
    };

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AppsFoundationFragment()
    {
        emptyListMessageId = R.string.empty_app;
        retrieveDataOnCreation = true;
    }

    @Override
    protected void onRetrieveParameters(Bundle bundle)
    {
        super.onRetrieveParameters(bundle);
        drawerId = BundleUtils.getInt(bundle, ARGUMENT_DRAWER_ID);
    }

    @Override
    protected void performRequest()
    {
        getAPI().getApplicationService().getRuntimeAppDefinitions(callBack);
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new AppDefinitionRepresentationAdapter(getActivity(), R.layout.row_two_lines,
                new ArrayList<AppDefinitionRepresentation>(0));
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (drawerId != null)
        {
            hide(R.id.fab);
        }
    }
}
