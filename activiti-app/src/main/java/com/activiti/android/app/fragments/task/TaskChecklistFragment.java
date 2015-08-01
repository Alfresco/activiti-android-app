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

package com.activiti.android.app.fragments.task;

import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.event.CompleteTaskEvent;
import com.activiti.android.platform.event.CreateTaskEvent;
import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;
import com.activiti.android.ui.fragments.task.checklist.TaskCheklistFoundationFragment;
import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.squareup.otto.Subscribe;

public class TaskChecklistFragment extends TaskCheklistFoundationFragment
{
    public static final String TAG = TaskChecklistFragment.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TaskChecklistFragment()
    {
        super();
        setHasOptionsMenu(true);
        eventBusRequired = true;
    }

    public static TaskChecklistFragment newInstanceByTemplate(Bundle b)
    {
        TaskChecklistFragment cbf = new TaskChecklistFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    protected View.OnClickListener onPrepareFabClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        };
    }

    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        resetRightMenu();
        TaskRepresentation taskRepresentation = (TaskRepresentation) l.getItemAtPosition(position);
        TaskDetailsFragment.with(getActivity()).task(taskRepresentation).bindFragmentTag(getTag()).display();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.tasks_menu_filter:
                if (getActivity() instanceof MainActivity)
                {
                    ((MainActivity) getActivity()).setRightMenuVisibility(!((MainActivity) getActivity())
                            .isRightMenuVisible());
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onCompletedTaskEvent(CompleteTaskEvent event)
    {
        if (event.hasException) { return; }
        try
        {
        }
        catch (Exception e)
        {

        }
    }

    @Subscribe
    public void onTaskCreated(CreateTaskEvent event)
    {
        if (event.hasException) { return; }
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

        public Builder taskId(String taskId)
        {
            extraConfiguration.putString(RequestConstant.ARGUMENT_TASK_ID, taskId);
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
