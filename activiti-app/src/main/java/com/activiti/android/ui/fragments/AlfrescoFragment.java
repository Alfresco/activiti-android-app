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

package com.activiti.android.ui.fragments;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.preferences.InternalAppPreferences;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.model.runtime.AppVersion;
import com.activiti.android.sdk.services.ServiceRegistry;
import com.activiti.android.ui.utils.DisplayUtils;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Base Fragment for All fragments available inside the UI Library.
 * 
 * @author Jean Marie Pascal
 */
public abstract class AlfrescoFragment extends DialogFragment implements AnalyticsManager.FragmentAnalyzed
{
    protected static final String ARGUMENT_BIND_FRAGMENT_TAG = "fragmentTag";

    /** Flag to display an error if the session is not present. */
    protected boolean eventBusRequired = false;

    /** Root View */
    private WeakReference<View> vRoot;

    private Long lastAppId;

    private WeakReference<MaterialDialog> dialogRef;

    protected String screenName;

    /** Flag to send screen event with analytics. */
    protected boolean reportAtCreation = true;

    protected ActivitiSession session;

    // /////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        if (getAccount() != null)
        {
            lastAppId = InternalAppPreferences.getLongPref(getActivity(), getAccount().getId(),
                    InternalAppPreferences.PREF_LAST_APP_USED);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        if (eventBusRequired)
        {
            try
            {
                EventBusManager.getInstance().register(this);
            }
            catch (Exception e)
            {
                // Do nothing
            }
        }
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (eventBusRequired)
        {
            try
            {
                EventBusManager.getInstance().unregister(this);
            }
            catch (Exception e)
            {
                // Do nothing
            }
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    // /////////////////////////////////////////////////////////////
    // VIEW MANAGEMENT
    // ////////////////////////////////////////////////////////////
    protected View getRootView()
    {
        if (vRoot == null) { return null; }
        return vRoot.get();
    }

    protected void setRootView(View rootView)
    {
        this.vRoot = new WeakReference<View>(rootView);
    }

    protected View viewById(int id)
    {
        if (getRootView() == null) { return null; }
        return getRootView().findViewById(id);
    }

    protected void hide(int id)
    {
        if (getRootView() == null) { return; }
        if (getRootView().findViewById(id) == null) { return; }
        getRootView().findViewById(id).setVisibility(View.GONE);
    }

    protected void show(int id)
    {
        if (getRootView() == null) { return; }
        if (getRootView().findViewById(id) == null) { return; }
        getRootView().findViewById(id).setVisibility(View.VISIBLE);
    }

    public ActivitiSession getSession()
    {
        if (session == null)
        {
            Long accountId = getAccount().getId();
            session = ActivitiSession.with(String.valueOf(accountId));
        }
        return session;
    }

    public ServiceRegistry getAPI()
    {
        return getSession().getServiceRegistry();
    }

    public ActivitiAccount getAccount()
    {
        return ActivitiAccountManager.getInstance(getActivity()).getCurrentAccount();
    }

    public int getVersionNumber()
    {
        AppVersion info = new AppVersion(getAccount().getServerVersion());
        return info.getFullVersionNumber();
    }

    protected Long getLastAppId()
    {
        return lastAppId;
    }

    protected Fragment getAttachedFragment()
    {
        if (getArguments() == null) { return null; }
        String fragmentId = BundleUtils.getString(getArguments(), ARGUMENT_BIND_FRAGMENT_TAG);
        return (TextUtils.isEmpty(fragmentId)) ? null : getFragmentManager().findFragmentByTag(fragmentId);
    }

    public void displayWaiting(int contentId)
    {
        dialogRef = new WeakReference<>(new MaterialDialog.Builder(getActivity()).title(R.string.please_wait)
                .content(contentId).progress(true, 0).show());
    }

    public void hideWaiting()
    {
        if (dialogRef != null && dialogRef.get() != null)
        {
            dialogRef.get().dismiss();
        }
    }

    protected void setLockRightMenu(boolean lock)
    {
        if (getActivity() instanceof MainActivity)
        {
            ((MainActivity) getActivity()).setLockRightDrawer(lock);
        }
    }

    protected void resetRightMenu()
    {
        if (getActivity() instanceof MainActivity)
        {
            Fragment fr = getFragmentManager().findFragmentById(R.id.right_drawer);
            if (fr != null)
            {
                FragmentDisplayer.with(getActivity()).back(false).animate(null).remove(fr);
            }
            ((ViewGroup) getActivity().findViewById(R.id.right_drawer)).removeAllViews();
            setLockRightMenu(true);
        }
    }

    protected Toolbar getToolbar()
    {
        if (DisplayUtils.hasCentralPane(getActivity()))
        {
            return (Toolbar) getActivity().findViewById(R.id.toolbar_central);
        }
        else
        {
            return (Toolbar) getActivity().findViewById(R.id.toolbar);
        }
    }

    // /////////////////////////////////////////////////////////////
    // ANLYTICS
    // ////////////////////////////////////////////////////////////
    public String getScreenName()
    {
        return TextUtils.isEmpty(screenName) ? getClass().getSimpleName() : screenName;
    }

    @Override
    public boolean reportAtCreationEnable()
    {
        return reportAtCreation;
    }
}
