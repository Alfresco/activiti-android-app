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

package com.activiti.android.ui.fragments.base;

import android.database.Cursor;
import android.view.View;
import android.widget.CursorAdapter;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.activiti.android.ui.utils.AccessibilityUtils;
import com.activiti.android.ui.utils.UIUtils;

/**
 * @author Jean Marie Pascal
 */
public abstract class BaseCursorGridFragment extends BaseGridFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String TAG = BaseCursorGridFragment.class.getName();

    protected int loaderId;

    @Override
    public void onStart()
    {
        super.onStart();
        if (displayAsList)
        {
            gv.setColumnWidth(UIUtils.getDPI(getActivity(), 2000));
        }
    }

    // /////////////////////////////////////////////////////////////
    // CURSOR ADAPTER
    // ////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor)
    {
        if (displayAsList)
        {
            gv.setColumnWidth(UIUtils.getDPI(getActivity(), 2000));
        }

        if (cursor.getCount() == 0)
        {
            displayEmptyView();
        }
        else
        {
            if (adapter == null)
            {
                adapter = onAdapterCreation();
                gv.setAdapter(adapter);
            }
            ((CursorAdapter) adapter).changeCursor(onChangeCursor(cursor));
        }
        setListShown(true);
        isFullLoad = Boolean.TRUE;
        isLockVisibleLoader = Boolean.FALSE;

        AccessibilityUtils.sendAccessibilityEvent(getActivity());
        refreshHelper.setRefreshComplete();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0)
    {
        if (adapter != null)
        {
            ((CursorAdapter) adapter).changeCursor(null);
        }
    }

    protected Cursor onChangeCursor(Cursor cursor)
    {
        return cursor;
    }

    // //////////////////////////////////////////////////////////////////////
    // REFRESH
    // //////////////////////////////////////////////////////////////////////
    @Override
    public void refresh()
    {
        onPrepareRefresh();
        adapter = null;
        if (getArguments() == null) { return; }
        getLoaderManager().restartLoader(loaderId, getArguments(), this);
        getLoaderManager().getLoader(loaderId).forceLoad();
    }

    protected void refreshSilently()
    {
        adapter = null;
        if (getArguments() == null) { return; }
        getLoaderManager().restartLoader(loaderId, getArguments(), this);
        getLoaderManager().getLoader(loaderId).forceLoad();
    }

    public void displayEmptyView()
    {
        if (!isVisible()) { return; }
        gv.setEmptyView(ev);
        if (adapter != null)
        {
            gv.setAdapter(null);
        }
        gv.setVisibility(View.GONE);
        ev.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }

    @Override
    protected void performRequest(long skipCount)
    {
        performRequest();
    }
}
