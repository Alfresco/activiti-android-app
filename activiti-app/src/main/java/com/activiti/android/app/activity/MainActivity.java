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

package com.activiti.android.app.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.app.fragments.HelpDialogFragment;
import com.activiti.android.app.fragments.account.AccountsFragment;
import com.activiti.android.app.fragments.app.AppInstancesFragment;
import com.activiti.android.app.fragments.settings.GeneralSettingsFragment;
import com.activiti.android.app.fragments.task.TasksFragment;
import com.activiti.android.app.fragments.user.UserProfileFragment;
import com.activiti.android.platform.account.AccountsPreferences;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.event.ProfilePictureEvent;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.preferences.InternalAppPreferences;
import com.activiti.android.platform.rendition.RenditionManager;
import com.activiti.android.ui.activity.AlfrescoActivity;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.form.picker.UserPickerFragment;
import com.activiti.android.ui.fragments.task.filter.TaskFilterPropertiesFragment;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.client.api.model.idm.UserRepresentation;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

public class MainActivity extends AlfrescoActivity
{
    protected ActionBarDrawerToggle mDrawerToggle;

    protected DrawerLayout mDrawerLayout;

    protected DrawerLayout mCentralDrawerLayout;

    protected ViewGroup mLeftDrawer, mRightDrawer, mCentralLeftDrawer;

    protected Toolbar toolbar;

    protected Picasso picasso;

    protected UserRepresentation userRepresentation;

    // ///////////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if there's an account
        if (!ActivitiAccountManager.getInstance(this).hasAccount())
        {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }
        else if (session == null && account == null)
        {
            connect(null);
        }

        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // INIT
        initSlideMenu();

        // Load Left Drawer
        FragmentDisplayer.with(this).back(false).animate(null)
                .load(AppInstancesFragment.with(this).drawer(R.id.left_drawer_content).createFragment())
                .into(R.id.left_drawer_content);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Configure
        if (mDrawerLayout == null || mLeftDrawer == null || mDrawerToggle == null)
        {
            // Configure navigation drawer
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mLeftDrawer = (ViewGroup) findViewById(R.id.left_drawer);
            mRightDrawer = (ViewGroup) findViewById(R.id.right_drawer);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close)
            {
                public void onDrawerClosed(View drawerView)
                {
                    if (drawerView.equals(mLeftDrawer))
                    {
                        supportInvalidateOptionsMenu();
                    }
                    mDrawerToggle.syncState();
                }

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
                public void onDrawerOpened(View drawerView)
                {
                    if (drawerView.equals(mLeftDrawer))
                    {
                        supportInvalidateOptionsMenu();
                    }
                    mDrawerToggle.syncState();
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset)
                {
                    super.onDrawerSlide(drawerView, slideOffset);
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }

        if (DisplayUtils.hasCentralPane(this) && mCentralDrawerLayout == null)
        {
            mCentralDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_central);
            if (mCentralDrawerLayout != null)
            {
                mCentralDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
            mCentralLeftDrawer = (ViewGroup) findViewById(R.id.central_left_drawer);
        }

        if (!isLeftMenuVisible() && getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            if (getSupportFragmentManager().findFragmentById(DisplayUtils.getLeftFragmentId(this)) == null)
            {
                loadDefaultFragment();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed()
    {
        if (isLeftMenuVisible())
        {
            hideSlideMenu();
        }
        else if (isRightMenuVisible())
        {
            if (getFragment(TaskFilterPropertiesFragment.TAG) != null
                    && getFragment(TaskFilterPropertiesFragment.TAG).isVisible())
            {
                getSupportFragmentManager().popBackStack();
            }
            else
            {
                setRightMenuVisibility(false);
            }
        }
        else
        {
            super.onBackPressed();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        for (int i = 0; i < menu.size(); i++)
        {
            menu.getItem(i).setVisible(!mDrawerLayout.isDrawerOpen(mLeftDrawer));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
                if (isVisible(UserPickerFragment.TAG))
                {
                    return false;
                }
                else if (mDrawerLayout.getDrawerLockMode(mLeftDrawer) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                {
                    return false;
                }
                else
                {
                    mDrawerToggle.onOptionsItemSelected(item);
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LEFT DRAWER
    // ///////////////////////////////////////////////////////////////////////////
    public void showLeftMenu()
    {
        mDrawerLayout.openDrawer(mLeftDrawer);
    }

    public void setCentralLefttMenuVisibility(boolean visible)
    {
        if (DisplayUtils.hasCentralPane(this))
        {
            if (visible)
            {
                mCentralDrawerLayout.openDrawer(mCentralLeftDrawer);
            }
            else
            {
                mCentralDrawerLayout.closeDrawer(mCentralLeftDrawer);
            }
        }
    }

    public void setRightMenuVisibility(boolean visible)
    {
        if (visible)
        {
            mDrawerLayout.openDrawer(mRightDrawer);
        }
        else
        {
            mDrawerLayout.closeDrawer(mRightDrawer);
        }
    }

    public void hideSlideMenu()
    {
        mDrawerLayout.closeDrawer(mLeftDrawer);
    }

    public boolean isLeftMenuVisible()
    {
        return mDrawerLayout.isDrawerOpen(mLeftDrawer);
    }

    public boolean isRightMenuVisible()
    {
        return mDrawerLayout.isDrawerOpen(mRightDrawer);
    }

    public boolean isCentralMenuVisible()
    {
        if (DisplayUtils.hasCentralPane(this))
        {
            return mCentralDrawerLayout.isDrawerOpen(mCentralLeftDrawer);
        }
        else
        {
            return mDrawerLayout.isDrawerOpen(mRightDrawer);
        }
    }

    public void lockSlidingMenu()
    {
        getSupportActionBar().setHomeButtonEnabled(false);
        if (mDrawerLayout != null)
        {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void setLockRightDrawer(boolean lock)
    {
        if (mDrawerLayout == null) { return; }
        if (lock)
        {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        }
        else
        {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
        }
    }

    public void unlockSlidingMenu()
    {
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void initSlideMenu()
    {
        if (account == null && session == null) { return; }

        if (userRepresentation == null)
        {
            ((TextView) findViewById(R.id.drawer_account_email)).setText(account.getUsername());
            ((TextView) findViewById(R.id.drawer_account_name)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.drawer_account_name)).setText(account.getUserFullname());

            // TODO Offline icon access
            picasso = new RenditionManager(MainActivity.this, session).getPicasso();
            picasso.load(getAPI().getProfileService().getProfilePictureURL()).fit()
                    .into(((ImageView) findViewById(R.id.drawer_account_icon)));
        }

        findViewById(R.id.drawer_account).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.left_drawer_content);
                displayApplicationsMenu(frag instanceof AccountsFragment);
            }
        });
    }

    public void displayApplicationsMenu(boolean isAccount)
    {
        if (isAccount)
        {
            ((ImageView) findViewById(R.id.account_switcher)).setImageResource(R.drawable.ic_expand_more_white);
            FragmentDisplayer
                    .with(MainActivity.this).animate(null).back(false).replace(AppInstancesFragment
                            .with(MainActivity.this).drawer(R.id.left_drawer_content).createFragment())
                    .into(R.id.left_drawer_content);
        }
        else
        {
            ((ImageView) findViewById(R.id.account_switcher)).setImageResource(R.drawable.ic_expand_less_white);
            FragmentDisplayer.with(MainActivity.this).animate(null).back(false)
                    .replace(AccountsFragment.with(MainActivity.this).createFragment()).into(R.id.left_drawer_content);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC ICON CLICK METHOD
    // ///////////////////////////////////////////////////////////////////////////
    public void displayHelp(View v)
    {
        if (getFragment(GeneralSettingsFragment.TAG) == null)
        {
            HelpDialogFragment.with(MainActivity.this).back(true).display();
        }
        hideSlideMenu();
    }

    public void displaySettings(View v)
    {
        if (getFragment(GeneralSettingsFragment.TAG) == null)
        {
            GeneralSettingsFragment.with(MainActivity.this).back(true).display();
        }
        hideSlideMenu();
    }

    public void displayUserProfile(View v)
    {
        if (getFragment(UserProfileFragment.TAG) == null)
        {
            UserProfileFragment.with(MainActivity.this).back(true).display();
        }
        hideSlideMenu();
    }

    public Long getAppId()
    {
        return InternalAppPreferences.getLongPref(this, account.getId(), InternalAppPreferences.PREF_LAST_APP_USED);
    }

    public void loadDefaultFragment()
    {
        Long latestAppId = InternalAppPreferences.getLongPref(this, account.getId(),
                InternalAppPreferences.PREF_LAST_APP_USED);
        String latestAppName = InternalAppPreferences.getStringPref(this, account.getId(),
                InternalAppPreferences.PREF_LAST_APP_NAME);
        Fragment fr = TasksFragment.with(MainActivity.this).appName(latestAppName).appId(latestAppId).createFragment();
        FragmentDisplayer.with(MainActivity.this).animate(null).back(false).replace(fr)
                .into(FragmentDisplayer.PANEL_LEFT);
    }

    public void switchAccount(ActivitiAccount acc)
    {
        // Analytics
        AnalyticsHelper.reportOperationEvent(this, AnalyticsManager.CATEGORY_SESSION, AnalyticsManager.ACTION_SWITCH,
                acc.getServerType(), 1, false);

        AccountsPreferences.setDefaultAccount(this, acc.getId());
        connect(acc.getId());
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        initSlideMenu();
        displayApplicationsMenu(true);
        loadDefaultFragment();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    private void refreshProfilePicture()
    {
        picasso = new RenditionManager(MainActivity.this, session).getPicasso();
        picasso.load(getAPI().getProfileService().getProfilePictureURL()).fit()
                .into(((ImageView) findViewById(R.id.drawer_account_icon)));
    }

    @Subscribe
    public void onProfileUpdated(ProfilePictureEvent event)
    {
        refreshProfilePicture();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    public Picasso getPicasso()
    {
        return picasso;
    }

    public ActionBarDrawerToggle getmDrawerToggle()
    {
        return mDrawerToggle;
    }
}
