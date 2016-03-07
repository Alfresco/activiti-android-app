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

package com.activiti.android.app.fragments.process;

import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.activiti.android.app.ActivitiVersionNumber;
import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.fragments.filters.FiltersFragment;
import com.activiti.android.app.fragments.task.TasksFragment;
import com.activiti.android.platform.event.CompleteProcessEvent;
import com.activiti.android.platform.event.StartProcessEvent;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;
import com.activiti.android.ui.fragments.process.ProcessesFoundationFragment;
import com.activiti.android.ui.fragments.process.create.StartProcessDialogFragment;
import com.activiti.android.ui.fragments.process.filter.ProcessFiltersFragment;
import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;
import com.squareup.otto.Subscribe;

public class ProcessesFragment extends ProcessesFoundationFragment
{
    public static final String TAG = ProcessesFragment.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessesFragment()
    {
        super();
        setHasOptionsMenu(true);
        eventBusRequired = true;
    }

    public static ProcessesFragment newInstanceByTemplate(Bundle b)
    {
        ProcessesFragment cbf = new ProcessesFragment();
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
                StartProcessDialogFragment.with(getActivity()).appId(appId).displayAsDialog();
            }
        };
    }

    @Override
    protected String onCreateTitle(String title)
    {
        if (appName == null)
        {
            return getString(R.string.general_navigation_processes);
        }
        else
        {
            mSubTitle = getString(R.string.general_navigation_processes);
            return appName;
        }
    }

    @Override
    public void onStart()
    {
        if (getVersionNumber() >= ActivitiVersionNumber.VERSION_1_3_0)
        {
            FragmentDisplayer.with(getActivity()).back(false).animate(null).replace(
                    FiltersFragment.with(getActivity()).appId(appId).typeId(FiltersFragment.TYPE_TASK).createFragment())
                    .into(R.id.right_drawer);
        }
        else
        {
            Fragment fr = getFragmentManager().findFragmentById(R.id.right_drawer);
            if (fr == null || (fr != null && !(fr instanceof ProcessFiltersFragment)))
            {
                if (fr != null)
                {
                    FragmentDisplayer.with(getActivity()).back(false).animate(null).remove(fr);
                }
                FragmentDisplayer.with(getActivity()).back(false).animate(null)
                        .replace(ProcessFiltersFragment
                                .newInstanceByTemplate(getArguments() != null ? getArguments() : new Bundle()))
                        .into(R.id.right_drawer);
            }
            setLockRightMenu(false);
        }
        setLockRightMenu(false);

        super.onStart();
    }

    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        resetRightMenu();
        ProcessInstanceRepresentation item = (ProcessInstanceRepresentation) l.getItemAtPosition(position);
        ProcessDetailsFragment.with(getActivity()).processId(item.getId()).back(true).display();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // FILTERING
    // ///////////////////////////////////////////////////////////////////////////
    public Bundle getBundle()
    {
        return new Bundle(bundle);
    }

    public void setFilterBundle(Bundle filterBundle)
    {
        bundle.putAll(filterBundle);
        onRetrieveParameters(bundle);
        refresh();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.processes_menu_filter:
                if (getActivity() instanceof MainActivity)
                {
                    ((MainActivity) getActivity())
                            .setRightMenuVisibility(!((MainActivity) getActivity()).isRightMenuVisible());
                }
                return true;
            case R.id.processes_menu_tasks:

                FragmentDisplayer.with(getActivity()).animate(FragmentDisplayer.SLIDE_DOWN).back(false)
                        .replace(TasksFragment.with(getActivity()).appName(appName).appId(appId).createFragment())
                        .into(FragmentDisplayer.PANEL_LEFT);

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
    public void onCompletedProcessEvent(CompleteProcessEvent event)
    {
        if (event.hasException) { return; }
        try
        {
            Long eventAppId = Long.parseLong(event.appId);
            if (eventAppId != null && eventAppId.longValue() == appId.longValue())
            {
                // refresh();
            }
        }
        catch (Exception e)
        {

        }
    }

    @Subscribe
    public void onStartedProcessEvent(StartProcessEvent event)
    {
        if (event.hasException) { return; }
        try
        {
            Long eventAppId = event.appId;
            if (eventAppId != null && eventAppId.longValue() == appId.longValue())
            {
                refresh();
            }
        }
        catch (Exception e)
        {

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

        public Builder appName(String appName)
        {
            extraConfiguration.putSerializable(RequestConstant.ARGUMENT_APPDEFINITION, appName);
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
