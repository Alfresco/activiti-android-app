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

import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.common.ListingModeFragment;

/**
 * Goal is to createNewTask a Fragment based on configuration provided.
 * 
 * @author jpascal
 */
public abstract class ListingFragmentBuilder extends AlfrescoFragmentBuilder
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    protected ListingFragmentBuilder()
    {

    }

    public ListingFragmentBuilder(FragmentActivity activity)
    {
        this(activity, null, null);
    }

    public ListingFragmentBuilder(FragmentActivity activity, Map<String, Object> configuration)
    {
        this(activity, configuration, null);
    }

    public ListingFragmentBuilder(FragmentActivity activity, Map<String, Object> configuration, Bundle b)
    {
        super(activity, configuration, b);
    }

    public AlfrescoFragmentBuilder mode(int mode)
    {
        if (extraConfiguration == null)
        {
            extraConfiguration = new Bundle();
        }
        extraConfiguration.putSerializable(ListingModeFragment.ARGUMENT_MODE, mode);
        return this;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // DISPLAY
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void display()
    {
        // Display Fragment
        FragmentDisplayer.load(this).into(FragmentDisplayer.PANEL_LEFT);
    }
}
