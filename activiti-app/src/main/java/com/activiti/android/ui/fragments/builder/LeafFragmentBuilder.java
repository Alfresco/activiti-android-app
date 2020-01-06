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

package com.activiti.android.ui.fragments.builder;

import androidx.fragment.app.FragmentActivity;

import java.util.Map;

import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.utils.DisplayUtils;

public abstract class LeafFragmentBuilder extends AlfrescoFragmentBuilder
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public LeafFragmentBuilder(FragmentActivity activity)
    {
        super(activity);
        this.hasBackStack = !DisplayUtils.hasCentralPane(activity);
    }

    public LeafFragmentBuilder(FragmentActivity activity, Map<String, Object> configuration)
    {
        super(activity, configuration);
        this.hasBackStack = !DisplayUtils.hasCentralPane(activity);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // DISPLAY
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void display()
    {
        // Clear Central Panel ?
        // FragmentDisplayer.clearCentralPane(getActivity());

        // Display Fragment
        FragmentDisplayer.load(this).back(hasBackStack).into(FragmentDisplayer.PANEL_CENTRAL);
    }
}
