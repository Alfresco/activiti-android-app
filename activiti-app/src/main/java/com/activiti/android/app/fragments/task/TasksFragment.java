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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.activiti.android.app.ActivitiVersionNumber;
import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.fragments.filters.FiltersFragment;
import com.activiti.android.app.fragments.process.ProcessesFragment;
import com.activiti.android.platform.event.CompleteTaskEvent;
import com.activiti.android.platform.event.CreateTaskEvent;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;
import com.activiti.android.ui.fragments.task.TasksFoundationFragment;
import com.activiti.android.ui.fragments.task.create.CreateStandaloneTaskDialogFragment;
import com.activiti.android.ui.fragments.task.filter.TaskFiltersFragment;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.squareup.otto.Subscribe;

public class TasksFragment extends TasksFoundationFragment
{
    public static final String TAG = TasksFragment.class.getName();

    protected View selectedView;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TasksFragment()
    {
        super();
        setHasOptionsMenu(true);
        eventBusRequired = true;
        retrieveDataOnCreation = !(getVersionNumber() >= ActivitiVersionNumber.VERSION_1_3_0);
        ;
    }

    public static TasksFragment newInstanceByTemplate(Bundle b)
    {
        TasksFragment cbf = new TasksFragment();
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
                CreateStandaloneTaskDialogFragment.with(getActivity()).appId(Long.toString(appId)).displayAsDialog();
            }
        };
    }

    @Override
    protected String onCreateTitle(String title)
    {
        if (appName == null)
        {
            return getString(R.string.app_tasks_title);
        }
        else
        {
            mSubTitle = getString(R.string.general_navigation_tasks);
            return appName;
        }
    }

    @Override
    public void onStart()
    {
        setLockRightMenu(DisplayUtils.hasCentralPane(getActivity()));

        if (getVersionNumber() >= ActivitiVersionNumber.VERSION_1_3_0)
        {
            FragmentDisplayer.with(getActivity()).back(false).animate(null)
                    .replace(FiltersFragment.with(getActivity()).appId(appId).typeId(FiltersFragment.TYPE_TASK)
                            .createFragment())
                    .into(DisplayUtils.hasCentralPane(getActivity()) ? R.id.central_left_drawer : R.id.right_drawer);
            UIUtils.setTitle(getActivity(), mTitle, mSubTitle);
        }
        else
        {
            Fragment fr = getFragmentManager().findFragmentById(R.id.right_drawer);
            if (fr == null || (fr != null && !(fr instanceof TaskFiltersFragment)))
            {
                if (fr != null)
                {
                    FragmentDisplayer.with(getActivity()).back(false).animate(null).remove(fr);
                }
                FragmentDisplayer.with(getActivity()).back(false).animate(null)
                        .replace(TaskFiltersFragment
                                .newInstanceByTemplate(getArguments() != null ? getArguments() : new Bundle()))
                        .into(R.id.right_drawer);
            }
        }

        super.onStart();
    }

    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        if (selectedView != null)
        {
            selectedView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
        selectedView = v;
        if (!DisplayUtils.hasCentralPane(getActivity()))
        {
            resetRightMenu();
        }
        else
        {
            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        TaskRepresentation taskRepresentation = (TaskRepresentation) l.getItemAtPosition(position);
        if (!selectedTask.contains(taskRepresentation))
        {
            TaskDetailsFragment.with(getActivity()).task(taskRepresentation).bindFragmentTag(getTag()).back(true)
                    .display();
            if (DisplayUtils.hasCentralPane(getActivity()))
            {
                selectedTask.clear();
                selectedTask.add(taskRepresentation);
                v.setBackgroundColor(getResources().getColor(R.color.secondary_background));
            }
        }
        else
        {
            if (DisplayUtils.hasCentralPane(getActivity()))
            {
                selectedTask.clear();
                selectedView = null;
                setLockRightMenu(true);
            }
        }
    }

    public void setSelectedTask(TaskRepresentation taskRepresentation)
    {
        if (!selectedTask.contains(taskRepresentation))
        {
            TaskDetailsFragment.with(getActivity()).task(taskRepresentation).bindFragmentTag(getTag()).back(true)
                    .display();
            selectedTask.clear();
            selectedTask.add(taskRepresentation);
        }
        else
        {
            selectedTask.clear();
            selectedView = null;
            setLockRightMenu(true);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // FILTERING
    // ///////////////////////////////////////////////////////////////////////////
    public Bundle getBundle()
    {
        if (bundle != null) { return new Bundle(bundle); }
        return null;
    }

    public void setFilterBundle(Bundle filterBundle, boolean refresh)
    {
        bundle.putAll(filterBundle);
        updateParameters(bundle);

        if ((adapter == null || adapter.isEmpty()) || refresh)
        {
            refresh();
        }
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.tasks_menu_filter:
                if (getActivity() instanceof MainActivity)
                {
                    if (DisplayUtils.hasCentralPane(getActivity()))
                    {
                        ((MainActivity) getActivity())
                                .setCentralLefttMenuVisibility(!((MainActivity) getActivity()).isCentralMenuVisible());
                    }
                    else
                    {
                        ((MainActivity) getActivity())
                                .setRightMenuVisibility(!((MainActivity) getActivity()).isCentralMenuVisible());
                    }

                }
                return true;
            case R.id.tasks_menu_process:
                ProcessesFragment.Builder builder = ProcessesFragment.with(getActivity()).appName(appName);
                FragmentDisplayer.with(getActivity()).animate(FragmentDisplayer.SLIDE_DOWN).back(false)
                        .replace(builder.createFragment()).into(FragmentDisplayer.PANEL_LEFT);
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
            Long eventAppId = Long.parseLong(event.appId);
            if (eventAppId != null && eventAppId.longValue() == appId.longValue())
            {
                refresh();
            }
        }
        catch (Exception e)
        {

        }
    }

    @Subscribe
    public void onTaskCreated(CreateTaskEvent event)
    {
        if (event.hasException) { return; }
        if (event.appId != null && event.appId.longValue() == appId.longValue())
        {
            refresh();
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

        public Builder appId(Long appId)
        {
            extraConfiguration.putLong(RequestConstant.ARGUMENT_APPDEFINITION_ID, appId);
            return this;
        }

        public Builder appName(String appName)
        {
            extraConfiguration.putSerializable(RequestConstant.ARGUMENT_APPDEFINITION, appName);
            return this;
        }

        public Builder processId(String processId)
        {
            extraConfiguration.putString(RequestConstant.ARGUMENT_PROCESS_ID, processId);
            return this;
        }

        public Builder processDefinitionId(String processDefinitionId)
        {
            extraConfiguration.putString(RequestConstant.ARGUMENT_PROCESSDEFINITION_ID, processDefinitionId);
            return this;
        }

        public Builder keywords(String keywords)
        {
            extraConfiguration.putString(RequestConstant.ARGUMENT_TEXT, keywords);
            return this;
        }

        public Builder assignee(Long assignee)
        {
            extraConfiguration.putLong(RequestConstant.ARGUMENT_ASSIGNEE, assignee);
            return this;
        }

        public Builder assignment(String assignment)
        {
            extraConfiguration.putString(RequestConstant.ARGUMENT_ASSIGNMENT, assignment);
            return this;
        }

        public Builder state(String state)
        {
            extraConfiguration.putString(RequestConstant.ARGUMENT_STATE, state);
            return this;
        }

        public Builder sort(String sort)
        {
            extraConfiguration.putString(RequestConstant.ARGUMENT_SORT, sort);
            return this;
        }

        public Builder page(int page)
        {
            extraConfiguration.putInt(RequestConstant.ARGUMENT_PAGE, page);
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
