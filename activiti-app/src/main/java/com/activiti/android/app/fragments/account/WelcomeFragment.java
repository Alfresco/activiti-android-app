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

package com.activiti.android.app.fragments.account;

import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.WelcomeActivity;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;

public class WelcomeFragment extends AlfrescoFragment
{
    public static final String TAG = WelcomeFragment.class.getName();

    private static final int NUM_PAGES = 3;

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public WelcomeFragment()
    {
        super();
    }

    public static WelcomeFragment newInstanceByTemplate(Bundle b)
    {
        WelcomeFragment cbf = new WelcomeFragment();
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

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRetainInstance(false);

        if (getRootView() != null) { return getRootView(); }

        setRootView(inflater.inflate(R.layout.fr_welcome, container, false));

        // Instantiate a ViewPager and a PagerAdapter.
        Bundle extras = getArguments();
        if (extras != null && extras.containsKey(WelcomeActivity.EXTRA_ADD_ACCOUNT))
        {
            hide(R.id.welcome_title);
            hide(R.id.welcome_pager);
            hide(R.id.welcome_pager_indicator);
            LinearLayout layout = (LinearLayout) viewById(R.id.welcome_page_actions_container);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        }
        else if (getActivity().findViewById(R.id.double_panel) != null)
        {
            hide(R.id.welcome_title);
            hide(R.id.welcome_pager);
            hide(R.id.welcome_pager_indicator);
            LinearLayout layout = (LinearLayout) viewById(R.id.welcome_page_actions_container);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        }
        else
        {
            CircleIndicator defaultIndicator = (CircleIndicator) viewById(R.id.welcome_pager_indicator);
            mPager = (ViewPager) viewById(R.id.welcome_pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
            mPager.setAdapter(mPagerAdapter);
            defaultIndicator.setViewPager(mPager);
        }

        return getRootView();
    }

    @Override
    public void onStart()
    {
        super.onStart();
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

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        };
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment fr = new WelcomePageFragment();
            Bundle b = new Bundle();
            b.putInt(WelcomePageFragment.ARGUMENT_POSITION, position);
            fr.setArguments(b);
            return fr;
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }
    }
}
