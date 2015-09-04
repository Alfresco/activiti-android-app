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

package com.activiti.android.ui.fragments.filter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.activiti.android.app.R;
import com.activiti.android.platform.preferences.InternalAppPreferences;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.android.ui.fragments.task.filter.TaskFilterPropertiesFragment;
import com.activiti.client.api.model.common.ResultListDataRepresentation;
import com.activiti.client.api.model.runtime.UserTaskFilterRepresentation;
import com.activiti.client.api.model.runtime.UserTaskFiltersRepresentation;

public class UserFilterFoundationFragment extends BasePagingGridFragment
{
    public static final String TAG = UserFilterFoundationFragment.class.getName();

    public static final int TYPE_TASK = 1;

    public static final int TYPE_PROCESS = 2;

    protected static final String ARGUMENT_APP_ID = "appId";

    protected static final String ARGUMENT_TYPE = "typeId";

    protected static final String ARGUMENT_USERFILTER_ID = "userId";

    protected Long appId, lastFilterUsedId;

    protected int typeId;

    protected String filterId;

    protected ArrayList<UserTaskFilterRepresentation> selectedFilter = new ArrayList<>(0);

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public UserFilterFoundationFragment()
    {
        retrieveDataOnCreation = true;
        setRetainInstance(true);
        enableTitle = false;
    }

    protected Callback<UserTaskFiltersRepresentation> callBack = new Callback<UserTaskFiltersRepresentation>()
    {
        @Override
        public void success(UserTaskFiltersRepresentation response, Response response2)
        {
            displayData(response);
            gv.smoothScrollToPosition(response.getSize());
        }

        @Override
        public void failure(RetrofitError error)
        {
            displayError(error);
        }
    };

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    protected void onRetrieveParameters(Bundle bundle)
    {
        appId = BundleUtils.getLong(bundle, ARGUMENT_APP_ID);
        typeId = BundleUtils.getInt(bundle, ARGUMENT_TYPE);
        filterId = BundleUtils.getString(bundle, ARGUMENT_USERFILTER_ID);
        lastFilterUsedId = getFilterUsed(getActivity(), getAccount().getId(), appId, typeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_filters, container, false));

        init(getRootView(), R.string.task_help_add_comment);

        ImageButton addNew = (ImageButton) viewById(R.id.filter_add_new);

        addNew.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentDisplayer.with(getActivity())
                        .replace(TaskFilterPropertiesFragment
                                .newInstanceByTemplate(getArguments() != null ? getArguments() : new Bundle()))
                        .back(true).animate(null).into(R.id.right_drawer);
            }
        });

        return getRootView();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void performRequest()
    {
        if (appId != null)
        {
            getAPI().getUserFiltersService().getUserTaskFilters(appId != -1L ? appId : null, callBack);
        }
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new UserFilterAdapter(this, R.layout.row_single_line_icons,
                new ArrayList<UserTaskFilterRepresentation>(0), selectedFilter, lastFilterUsedId, appId);
    }

    @Override
    protected void displayData(ResultListDataRepresentation<?> response)
    {
        super.displayData(response);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    protected void saveFilterPref(Long appId, Long filterId, int typeId)
    {
        Log.d(TAG, "[SAVE] FILTER ID: " + filterId + " - " + createUniqueId(appId, typeId));
        InternalAppPreferences.savePref(getActivity(), getAccount().getId(), createUniqueId(appId, typeId), filterId);
    }

    protected static Long getFilterUsed(Context context, Long accountId, Long appId, int typeId)
    {
        Log.d(TAG,
                "[GET] FILTER ID: "
                        + InternalAppPreferences.getLongPref(context, accountId, createUniqueId(appId, typeId)) + " - "
                        + createUniqueId(appId, typeId));
        return InternalAppPreferences.getLongPref(context, accountId, createUniqueId(appId, typeId));
    }

    private static String createUniqueId(Long appId, int typeId)
    {
        StringBuilder builder = new StringBuilder(InternalAppPreferences.PREF_LAST_FILTER_USED);
        switch (typeId)
        {
            case TYPE_PROCESS:
                builder.append("P");
                break;
            default:
                builder.append("T");
                break;
        }
        builder.append(appId);
        return builder.toString();
    }

    public void refreshTasks()
    {
    }

    public void requestRefresh()
    {
        requestRefresh = true;
        setListShown(false);
        ((UserFilterAdapter) adapter).clear();
    }
}
