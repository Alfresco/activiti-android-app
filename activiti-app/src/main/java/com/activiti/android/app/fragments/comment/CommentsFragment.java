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

package com.activiti.android.app.fragments.comment;

import java.util.Map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.sdk.model.runtime.ParcelTask;
import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;
import com.activiti.android.ui.fragments.comment.CommentsFoundationFragment;
import com.activiti.client.api.model.runtime.TaskRepresentation;

public class CommentsFragment extends CommentsFoundationFragment
{
    public static final String TAG = CommentsFragment.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public CommentsFragment()
    {
        super();
        setRetainInstance(true);
    }

    public static CommentsFragment newInstanceByTemplate(Bundle b)
    {
        CommentsFragment cbf = new CommentsFragment();
        cbf.setArguments(b);
        return cbf;
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
            extraConfiguration.putString(ARGUMENT_TASK_ID, taskId);
            return this;
        }

        public Builder task(TaskRepresentation task)
        {
            extraConfiguration.putParcelable(ARGUMENT_TASK, new ParcelTask(task));
            return this;
        }

        public Builder processId(String processId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_ID, processId);
            return this;
        }

        public Builder create(boolean isCreate)
        {
            extraConfiguration.putBoolean(ARGUMENT_CREATE, isCreate);
            return this;
        }

        public Builder readonly(boolean isReadonly)
        {
            extraConfiguration.putBoolean(ARGUMENT_RO, isReadonly);
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
