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
import android.view.MenuItem;

import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.fragments.process.ProcessDiagramFoundationFragment;
import com.activiti.android.ui.utils.UIUtils;

public class ProcessDiagram extends ProcessDiagramFoundationFragment
{
    public static final String TAG = ProcessDiagram.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessDiagram()
    {
        super();
        setHasOptionsMenu(true);
    }

    public static ProcessDiagram newInstanceByTemplate(Bundle b)
    {
        ProcessDiagram cbf = new ProcessDiagram();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart()
    {
        super.onStart();
        if (getActivity() instanceof MainActivity)
        {
            UIUtils.displayActionBarBack((MainActivity) getActivity());
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (getActivity() instanceof MainActivity)
        {
            UIUtils.setActionBarDefault((MainActivity) getActivity());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends AlfrescoFragmentBuilder
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

        public Builder processId(String processModelId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_ID, processModelId);
            return this;
        }

        public Builder tenantId(String tenantId)
        {
            extraConfiguration.putString(ARGUMENT_TENANT_ID, tenantId);
            return this;
        }

        public Builder processName(String processName)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_NAME, processName);
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
