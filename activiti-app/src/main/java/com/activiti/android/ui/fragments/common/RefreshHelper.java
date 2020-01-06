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

package com.activiti.android.ui.fragments.common;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.activiti.android.app.R;
import com.activiti.android.ui.utils.AccessibilityUtils;

public class RefreshHelper implements SwipeRefreshLayout.OnRefreshListener
{
    private SwipeRefreshLayout swipeLayout;

    private WeakReference<RefreshFragment> fragmentRef;

    // //////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // //////////////////////////////////////////////////////////////////////
    public RefreshHelper(Activity activity, final RefreshFragment fragment, View rootView)
    {
        if (rootView == null) { return; }
        fragmentRef = new WeakReference<>(fragment);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.ptr_layout);

        if (AccessibilityUtils.isEnabled(activity))
        {
            swipeLayout.setEnabled(false);
            swipeLayout.setRefreshing(false);
        }
        else
        {
            swipeLayout.setOnRefreshListener(this);
            swipeLayout.setColorSchemeResources(R.color.alfresco_lime, R.color.alfresco_soil,
                    R.color.alfresco_tangerine, R.color.alfresco_sky);
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // METHODS
    // //////////////////////////////////////////////////////////////////////
    public void setRefreshComplete()
    {
        if (swipeLayout != null)
        {
            swipeLayout.setRefreshing(false);
        }
    }

    public void setRefreshing()
    {
        if (swipeLayout != null)
        {
            swipeLayout.setRefreshing(true);
        }
    }

    public void setEnabled(boolean isEnable)
    {
        if (swipeLayout != null)
        {
            swipeLayout.setEnabled(isEnable);
        }
    }

    @Override
    public void onRefresh()
    {
        if (fragmentRef != null && fragmentRef.get() != null)
        {
            fragmentRef.get().refresh();
        }
    }
}
