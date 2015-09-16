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

package com.activiti.android.app.fragments.filters;

import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.GridView;

import com.activiti.android.app.fragments.task.TasksFragment;
import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;
import com.activiti.android.ui.fragments.filter.UserFilterFoundationFragment;
import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.runtime.TaskFilterRepresentation;
import com.activiti.client.api.model.runtime.UserTaskFilterRepresentation;

public class UserFiltersFragment extends UserFilterFoundationFragment implements RequestConstant
{
    public static final String TAG = UserFiltersFragment.class.getName();

    protected TasksFragment frag;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public UserFiltersFragment()
    {
        super();
        setRetainInstance(true);
    }

    public static UserFiltersFragment newInstanceByTemplate(Bundle b)
    {
        UserFiltersFragment cbf = new UserFiltersFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        frag = (TasksFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TasksFragment.TAG);
    }

    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        UserTaskFilterRepresentation taskFilter = (UserTaskFilterRepresentation) l.getItemAtPosition(position);

        saveFilterPref(appId, taskFilter.getId(), typeId);
        lastFilterUsedId = taskFilter.getId();
        selectedFilter.clear();
        selectedFilter.add(taskFilter);
        refreshTasks(true);
        adapter.notifyDataSetChanged();
    }

    public static Bundle createBundle(TaskFilterRepresentation filter)
    {
        Bundle b = new Bundle();
        b.putString(ARGUMENT_PROCESSDEFINITION_ID, filter.getProcessDefinitionId());
        b.putString(ARGUMENT_TEXT, filter.getName());
        b.putString(ARGUMENT_ASSIGNMENT, filter.getAssignment());
        b.putString(ARGUMENT_STATE, filter.getState());
        b.putString(ARGUMENT_SORT, filter.getSort());
        return b;
    }

    @Override
    public void refreshTasks(boolean refresh)
    {
        if (!selectedFilter.isEmpty())
        {
            frag.setFilterBundle(createBundle(selectedFilter.get(0).getFilter()), refresh);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends ListingFragmentBuilder
    {
        // ///////////////////////////////////////////////////////////////////////////
        // CONSTRUCTORS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder(FragmentActivity activity)
        {
            super(activity);
            this.extraConfiguration = new Bundle();
        }

        public Builder(FragmentActivity appActivity, Map<String, Object> configuration)
        {
            super(appActivity, configuration);
        }

        public Builder filterId(Long filterId)
        {
            extraConfiguration.putLong(ARGUMENT_USERFILTER_ID, filterId);
            return this;
        }

        public Builder appId(Long appId)
        {
            extraConfiguration.putLong(ARGUMENT_APP_ID, appId);
            return this;
        }

        public Builder typeId(int typeId)
        {
            extraConfiguration.putInt(ARGUMENT_TYPE, typeId);
            return this;
        }


        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        };
    }
}
