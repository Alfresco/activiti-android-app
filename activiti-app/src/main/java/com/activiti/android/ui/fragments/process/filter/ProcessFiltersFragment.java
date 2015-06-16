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

package com.activiti.android.ui.fragments.process.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.app.fragments.process.ProcessesFragment;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.processDefinition.ProcessDefinitionFoundationFragment;
import com.activiti.android.ui.fragments.task.filter.TaskSortingDialogFragment;
import com.activiti.android.ui.fragments.task.filter.TaskStateDialogFragment;
import com.activiti.client.api.constant.RequestConstant;

/**
 * Created by jpascal on 07/03/2015.
 */
public class ProcessFiltersFragment extends AlfrescoFragment implements RequestConstant
{
    public static final String TAG = ProcessDefinitionFoundationFragment.class.getName();

    protected ProcessesFragment frag;

    protected Bundle bundle;

    protected String sort, state;

    protected TextView sortText, stateText;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessFiltersFragment()
    {
        super();
    }

    public static ProcessFiltersFragment newInstanceByTemplate(Bundle b)
    {
        ProcessFiltersFragment cbf = new ProcessFiltersFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_process_filters, container, false));

        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        frag = (ProcessesFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ProcessesFragment.TAG);

        sortText = (TextView) viewById(R.id.process_filter_sort_value);
        stateText = (TextView) viewById(R.id.process_filter_state_value);

        refreshInfo();

        LinearLayout sortClick = (LinearLayout) viewById(R.id.process_filter_sort);
        sortClick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ProcessSortingDialogFragment.with(getActivity()).sort(sort).displayAsDialog();
            }
        });

        LinearLayout stateButton = (LinearLayout) viewById(R.id.process_filter_state);
        stateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ProcessStateDialogFragment.with(getActivity()).state(state).displayAsDialog();
            }
        });

        viewById(R.id.action_reset).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Reset all parameters
                bundle.putString(ARGUMENT_SORT, null);
                bundle.putString(ARGUMENT_STATE, null);
                getArguments().putString(ARGUMENT_SORT, null);
                getArguments().putString(ARGUMENT_STATE, null);
                sendInfoToTasks();
                refreshInfo();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (frag != null)
        {
            bundle = frag.getBundle();
            refreshInfo();
        }
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
            sort = (getArguments().containsKey(RequestConstant.ARGUMENT_SORT)) ? getArguments().getString(
                    RequestConstant.ARGUMENT_SORT) : "";
            state = (getArguments().containsKey(RequestConstant.ARGUMENT_STATE)) ? getArguments().getString(
                    RequestConstant.ARGUMENT_STATE) : "";
        }

        sortText.setText(TaskSortingDialogFragment.getSortTitle(getActivity(), sort));
        stateText.setText(TaskStateDialogFragment.getStateTitle(getActivity(), state));
    }
}
