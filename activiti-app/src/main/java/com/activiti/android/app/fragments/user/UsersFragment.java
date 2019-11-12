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

package com.activiti.android.app.fragments.user;

import java.util.Map;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;
import com.activiti.android.ui.fragments.user.UsersFoundationFragment;
import com.activiti.client.api.constant.RequestConstant;

public class UsersFragment extends UsersFoundationFragment
{
    public static final String TAG = UsersFragment.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public UsersFragment()
    {
        super();
    }

    public static UsersFragment newInstanceByTemplate(Bundle b)
    {
        UsersFragment cbf = new UsersFragment();
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
        protected int menuId;

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

        public Builder processDefinitionId(String processDefinitionId)
        {
            extraConfiguration.putString(RequestConstant.ARGUMENT_PROCESSDEFINITION_ID, processDefinitionId);
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
