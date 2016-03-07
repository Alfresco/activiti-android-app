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

package com.activiti.android.app.fragments.app;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.GridView;

import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.fragments.task.TasksFragment;
import com.activiti.android.platform.event.CompleteTaskEvent;
import com.activiti.android.platform.event.CreateTaskEvent;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.preferences.InternalAppPreferences;
import com.activiti.android.platform.provider.app.RuntimeAppInstance;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.provider.group.GroupInstanceManager;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelManager;
import com.activiti.android.ui.activity.AlfrescoActivity;
import com.activiti.android.ui.fragments.apps.AppInstanceCursorAdapter;
import com.activiti.android.ui.fragments.apps.AppInstancesFoundationFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.squareup.otto.Subscribe;

public class AppInstancesFragment extends AppInstancesFoundationFragment
{
    public static final String TAG = AppInstancesFragment.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AppInstancesFragment()
    {
        super();
        enableTitle = false;
    }

    public static AppInstancesFragment newInstanceByTemplate(Bundle b)
    {
        AppInstancesFragment cbf = new AppInstancesFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart()
    {
        getActivity().invalidateOptionsMenu();
        super.onStart();
    }

    @Override
    public void refresh()
    {
        if (getActivity() instanceof AlfrescoActivity)
        {
            ((AlfrescoActivity) getActivity()).checkIsAdmin();
        }
        syncAdapters(getActivity());
    }

    public static void syncAdapters(Context context)
    {
        RuntimeAppInstanceManager.sync(context);
        ProcessDefinitionModelManager.sync(context);
        GroupInstanceManager.sync(context);
        IntegrationManager.sync(context);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIST ACTION
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        Cursor cursor = (Cursor) l.getItemAtPosition(position);
        adapter.notifyDataSetChanged();

        RuntimeAppInstance item = RuntimeAppInstanceManager.getInstance(getActivity()).getByProviderId(id);
        boolean back = true;
        if (drawerId != null)
        {
            // Analytics
            AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_SESSION,
                    AnalyticsManager.ACTION_SWITCH, AnalyticsManager.LABEL_APPS, 1, false);

            ((AppInstanceCursorAdapter) l.getAdapter()).setSelected(item.getId());
            ((AppInstanceCursorAdapter) l.getAdapter()).notifyDataSetInvalidated();

            // Flag last used Application
            InternalAppPreferences.savePref(getActivity(), getAccount().getId(),
                    InternalAppPreferences.PREF_LAST_APP_USED, item.getId());
            InternalAppPreferences.savePref(getActivity(), getAccount().getId(),
                    InternalAppPreferences.PREF_LAST_APP_NAME, item.getName());

            // Clean Right Menu.
            resetRightMenu();

            ((MainActivity) getActivity()).hideSlideMenu();
            back = false;
            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        TasksFragment.with((MainActivity) v.getContext()).appName(item.getName()).appId(item.getId()).back(back)
                .display();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onCompletedTaskEvent(CompleteTaskEvent event)
    {
        if (event.hasException) return;
        try
        {
            Long appId = -1L;
            if (event.appId != null)
            {
                appId = Long.parseLong(event.appId);
            }
            RuntimeAppInstanceManager.sync(getActivity(), appId);
        }
        catch (Exception e)
        {

        }
    }

    @Subscribe
    public void onTaskCreated(CreateTaskEvent event)
    {
        if (event.hasException) return;
        try
        {
            Long appId = -1L;
            if (event.appId != null)
            {
                appId = event.appId;
            }
            RuntimeAppInstanceManager.sync(getActivity(), appId);
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

        public Builder drawer(int drawerId)
        {
            extraConfiguration.putInt(ARGUMENT_DRAWER_ID, drawerId);
            return this;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // SETTERS
        // ///////////////////////////////////////////////////////////////////////////

        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        }

    }
}
