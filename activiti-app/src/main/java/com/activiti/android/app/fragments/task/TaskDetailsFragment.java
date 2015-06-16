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

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.fragments.process.ProcessDetailsFragment;
import com.activiti.android.platform.provider.transfer.ContentTransferEvent;
import com.activiti.android.sdk.model.runtime.ParcelTask;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.fragments.comment.FragmentWithComments;
import com.activiti.android.ui.fragments.task.TaskDetailsFoundationFragment;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.squareup.otto.Subscribe;

public class TaskDetailsFragment extends TaskDetailsFoundationFragment implements FragmentWithComments
{
    public static final String TAG = TaskDetailsFragment.class.getName();

    protected Menu menu;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TaskDetailsFragment()
    {
        super();
        eventBusRequired = true;
        setHasOptionsMenu(true);
    }

    public static TaskDetailsFragment newInstanceByTemplate(Bundle b)
    {
        TaskDetailsFragment cbf = new TaskDetailsFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart()
    {
        Fragment fr = getFragmentManager().findFragmentById(R.id.right_drawer);
        if (fr == null || (fr != null && !(fr.equals(commentFragment))))
        {
            if (fr != null)
            {
                FragmentDisplayer.with(getActivity()).back(false).animate(null).remove(fr);
            }
        }
        setLockRightMenu(false);

        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        onProcessListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ProcessDetailsFragment.with(getActivity()).processId(processInstanceRepresentation.getId()).display();
            }
        };
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        if (taskRepresentation != null)
        {
            menu.clear();
            inflater.inflate(R.menu.process_details, menu);
            this.menu = menu;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.display_comments:
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
    // INTERFACE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void hasComment(boolean hascomment)
    {
        if (menu != null && hascomment)
        {
            menu.findItem(R.id.display_comments).setIcon(R.drawable.ic_comment);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onContentTransfer(ContentTransferEvent event)
    {
        super.onContentTransfer(event);
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

        public Builder taskId(String taskId)
        {
            extraConfiguration.putString(ARGUMENT_TASK_ID, taskId);
            return this;
        }

        public Builder task(TaskRepresentation task)
        {
            extraConfiguration.putParcelable(ARGUMENT_TASK, new ParcelTask(task));
            return this;
        }

        public Builder bindFragmentTag(String fragmentListTag)
        {
            extraConfiguration.putString(ARGUMENT_BIND_FRAGMENT_TAG, fragmentListTag);
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
