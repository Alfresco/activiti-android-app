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

package com.activiti.android.ui.fragments.builder;

import java.lang.ref.WeakReference;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.FragmentDisplayer;

/**
 * Goal is to createNewTask a Fragment based on configuration provided.
 *
 * @author jpascal
 */
public abstract class AlfrescoFragmentBuilder
{
    private static final String TAG = AlfrescoFragmentBuilder.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // MEMBERS
    // ///////////////////////////////////////////////////////////////////////////
    protected int menuIconId;

    protected int menuTitleId;

    protected OnClickListener onClick;

    protected WeakReference<FragmentActivity> activity;

    protected String[] templateArguments = new String[0];

    protected boolean sessionRequired = true;

    protected boolean hasBackStack = true;

    protected Map<String, Object> configuration;

    protected Bundle extraConfiguration;

    // ///////////////////////////////////////////////////////////////////////////
    // DEFAULT CLICK LISTENER
    // Used for creating the fragment associated to the configuration like menu
    // ///////////////////////////////////////////////////////////////////////////
    protected OnClickListener onDefaultClick = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            // Retrieve configuration
            AlfrescoFragmentBuilder builder = (AlfrescoFragmentBuilder) v.getTag();

            // Create the properties bundle
            FragmentDisplayer.load(builder).into(FragmentDisplayer.PANEL_LEFT);

            // Remove
            /*
             * if (getActivity() instanceof MainActivity) { ((MainActivity)
             * getActivity()).hideSlideMenu(); }
             */
        }
    };

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    protected AlfrescoFragmentBuilder()
    {

    }

    /**
     * Used by the Factory
     *
     * @param activity
     */
    public AlfrescoFragmentBuilder(FragmentActivity activity)
    {
        this(activity, null, null);
    }

    /**
     * Used by the configurationManager
     *
     * @param activity
     * @param configuration
     */
    public AlfrescoFragmentBuilder(FragmentActivity activity, Map<String, Object> configuration)
    {
        this(activity, configuration, null);
    }

    /**
     * Used by ?
     *
     * @param activity
     * @param configuration
     * @param b
     */
    public AlfrescoFragmentBuilder(FragmentActivity activity, Map<String, Object> configuration, Bundle b)
    {
        this.onClick = onDefaultClick;
        this.activity = new WeakReference<>(activity);
        this.configuration = configuration;
        this.extraConfiguration = b;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS
    // ///////////////////////////////////////////////////////////////////////////
    public FragmentActivity getActivity()
    {
        return activity.get();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SETTERS
    // ///////////////////////////////////////////////////////////////////////////
    public AlfrescoFragmentBuilder addExtra(Bundle b)
    {
        if (b == null || b.isEmpty()) { return this; }
        if (extraConfiguration == null)
        {
            this.extraConfiguration = b;
        }
        else
        {
            extraConfiguration.putAll(b);
        }
        return this;
    }

    public AlfrescoFragmentBuilder back(boolean hasBackStack)
    {
        this.hasBackStack = hasBackStack;
        return this;
    }

    public boolean hasBackStack()
    {
        return hasBackStack;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU CONFIGURATION
    // Responsible to display the fragment after selection
    // ///////////////////////////////////////////////////////////////////////////
    public Bundle createArguments()
    {
        return prepareArguments(configuration, extraConfiguration);
    }

    public Fragment createFragment()
    {
        Fragment frag = createFragment(createArguments());
        if (frag == null) { return null; }

        // Analytics
        // Report only fragment & report at creation enable (cf. pager case)
        if (AnalyticsManager.getInstance(getActivity()) != null
                || AnalyticsManager.getInstance(getActivity()).isEnable())
        {
            if (frag instanceof AnalyticsManager.FragmentAnalyzed
                    && ((AnalyticsManager.FragmentAnalyzed) frag).reportAtCreationEnable())
            {
                // Track all fragment created
                AnalyticsHelper.reportScreen(getActivity(), ((AnalyticsManager.FragmentAnalyzed) frag).getScreenName());
            }
        }

        return frag;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // DISPLAY
    // ///////////////////////////////////////////////////////////////////////////
    public void display()
    {
        // Display Fragment
        FragmentDisplayer.load(this).into(FragmentDisplayer.PANEL_CENTRAL);
    }

    public void display(int viewId)
    {
        // Display Fragment
        FragmentDisplayer.load(this).into(viewId);
    }

    public void displayAsDialog()
    {
        // Display Fragment
        FragmentDisplayer.load(this).asDialog();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // FRAGMENT CREATION
    // ///////////////////////////////////////////////////////////////////////////
    protected Fragment createFragment(Bundle b)
    {
        return null;
    }

    protected Bundle prepareArguments(Map<String, Object> properties, Bundle extra)
    {
        // Create the properties bundle
        Bundle b = new Bundle();

        // Configuration from the CODE
        BundleUtils.addIfNotEmpty(b, extraConfiguration);

        // Add latest Extra parameters if necessary
        BundleUtils.addIfNotEmpty(b, extra);

        return b;
    }
}
