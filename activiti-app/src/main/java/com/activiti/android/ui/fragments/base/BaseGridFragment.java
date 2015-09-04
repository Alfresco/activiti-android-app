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

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.common.RefreshFragment;
import com.activiti.android.ui.fragments.common.RefreshHelper;
import com.github.clans.fab.FloatingActionButton;

/**
 * Created by jpascal on 12/12/2014.
 */
public abstract class BaseGridFragment extends AlfrescoFragment implements RefreshFragment
{
    protected GridView gv;

    protected View pb;

    protected View ev;

    protected View footer;

    protected int emptyListMessageId;

    protected int selectedPosition;

    protected BaseAdapter adapter;

    protected boolean displayAsList = true;

    protected boolean enableTitle = true;

    protected boolean requestRefresh = false;

    protected boolean retrieveDataOnCreation = true;

    protected int layoutId = R.layout.grid;

    protected int titleId = -1;

    protected String mTitle = "", mSubTitle = "";

    protected Bundle bundle;

    protected RefreshHelper refreshHelper;

    protected FloatingActionButton fab;

    protected DrawerLayout mDrawerLayout;

    protected ViewGroup mRightDrawer;

    /** Indicator to retain if everything has been loaded */
    protected boolean isFullLoad = Boolean.FALSE;

    protected boolean isLockVisibleLoader = Boolean.FALSE;

    // /////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Retrieve arguments
        if (getArguments() != null)
        {
            bundle = getArguments();
            onRetrieveParameters(bundle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container == null && getDialog() == null) { return null; }

        setRootView(inflater.inflate(layoutId, container, false));

        init(getRootView(), emptyListMessageId);

        return getRootView();
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (getRootView() != null)
        {
            refreshHelper = new RefreshHelper(getActivity(), this, getRootView());
        }
        else
        {
            refreshHelper = new RefreshHelper(getActivity(), this, null);
        }

        // Perform the request on creation to populate gridView
        if (retrieveDataOnCreation && adapter == null && !requestRefresh)
        {
            performRequest();
        }
        else if (requestRefresh)
        {
            requestRefresh = false;
            performRequest();
        }
        else if (retrieveDataOnCreation && adapter != null)
        {
            setListShown(true);
            gv.setAdapter(adapter);
        }
        else
        {
            // Display Empty view
            setListShown(true);
            adapter = onAdapterCreation();
            gv.setAdapter(adapter);
        }
    }

    /** Called this method to retrieve argument parameters. */
    protected void onRetrieveParameters(Bundle bundle)
    {
        // Can be implemented by the derived class.
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        savePosition();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setListShown(!(adapter == null));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (enableTitle)
        {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(onCreateTitle(mTitle));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(onCreateSubTitle());
        }
        getActivity().invalidateOptionsMenu();
    }

    // /////////////////////////////////////////////////////////////
    // TITLE
    // ////////////////////////////////////////////////////////////
    /** Title is displayed during onResume. */
    protected String onCreateTitle(String title)
    {
        return TextUtils.isEmpty(title) ? (titleId == -1) ? mTitle : getString(titleId) : title;
    }

    protected String onCreateSubTitle()
    {
        return TextUtils.isEmpty(mSubTitle) ? null : mSubTitle;
    }

    // /////////////////////////////////////////////////////////////
    // ITEMS MANAGEMENT
    // ////////////////////////////////////////////////////////////
    protected abstract void performRequest();

    protected abstract void performRequest(long skipCount);

    /**
     * Called when the restManager is created for the first time.
     */
    protected BaseAdapter onAdapterCreation()
    {
        return adapter;
    }

    protected void init(View v, Integer estring)
    {
        pb = v.findViewById(R.id.progressbar);
        gv = (GridView) v.findViewById(R.id.gridview);
        ev = v.findViewById(R.id.empty);
        footer = v.findViewById(R.id.load_more);
        TextView evt = (TextView) v.findViewById(R.id.empty_text);
        if (estring != null && estring > 0)
        {
            evt.setText(estring);
        }

        if (adapter != null)
        {
            if (adapter.getCount() == 0)
            {
                gv.setEmptyView(ev);
            }
            else
            {
                gv.setAdapter(adapter);
                gv.setSelection(selectedPosition);
            }

        }

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id)
            {
                savePosition();
                onListItemClick((GridView) l, v, position, id);
            }
        });

        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id)
            {
                return onListItemLongClick((GridView) l, v, position, id);
            }
        });

        AbsListView.OnScrollListener listener = new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                savePosition();

                // endless scroll
                if (totalItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount && !isLockVisibleLoader)
                {
                    loadMore(totalItemCount);
                    isLockVisibleLoader = Boolean.TRUE;
                }

                if (refreshHelper != null && view != null && view.getChildCount() > 0)
                {
                    boolean firstItemVisible = view.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = view.getChildAt(0).getTop() == 0;
                    refreshHelper.setEnabled(firstItemVisible && topOfFirstItemVisible);
                }
            }
        };

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        View.OnClickListener onFabClickListener = onPrepareFabClickListener();
        if (onFabClickListener != null)
        {
            fab.setVisibility(View.VISIBLE);
            gv.setOnScrollListener(listener);
            fab.setOnClickListener(onFabClickListener);
        }
        else
        {
            fab.setVisibility(View.GONE);
            gv.setOnScrollListener(listener);
        }
    }

    // /////////////////////////////////////////////////////////////
    // ITEMS SELECTION
    // ////////////////////////////////////////////////////////////
    protected View.OnClickListener onPrepareFabClickListener()
    {
        return null;
    }

    /**
     * Affect a clickListener to the principal GridView.
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener)
    {
        gv.setOnItemClickListener(clickListener);
    }

    public void onListItemClick(GridView l, View v, int position, long id)
    {
        // Can be implemented on children
    }

    public boolean onListItemLongClick(GridView l, View v, int position, long id)
    {
        // Can be implemented on children
        return false;
    }

    protected void loadMore(int skipCount)
    {
        if (isFullLoad) { return; }
        onPrepareRefresh();
        performRequest(skipCount);
    }

    // /////////////////////////////////////////////////////////////
    // ITEMS MANAGEMENT
    // ////////////////////////////////////////////////////////////
    /**
     * Control whether the getProcessInstances is being displayed.
     *
     * @param shown : If true, the getProcessInstances view is shown; if false,
     *            the progress indicator. The initial value is true.
     */
    protected void setListShown(Boolean shown)
    {
        if (shown)
        {
            gv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            if (adapter != null && adapter.isEmpty())
            {
                ev.setVisibility(View.VISIBLE);
            }
            else
            {
                ev.setVisibility(View.GONE);
            }
        }
        else
        {
            ev.setVisibility(View.GONE);
            gv.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
        }
    }

    protected void prepareEmptyView(View ev, ImageView emptyImageView, TextView firstEmptyMessage,
            TextView secondEmptyMessage)
    {

    }

    protected void prepareEmptyInitialView(View ev, ImageView emptyImageView, TextView firstEmptyMessage,
            TextView secondEmptyMessage)
    {
        prepareEmptyView(ev, emptyImageView, firstEmptyMessage, secondEmptyMessage);
    }

    protected void displayEmptyView()
    {
        if (!isVisible()) { return; }
        gv.setEmptyView(ev);
        isFullLoad = Boolean.TRUE;
        if (adapter != null)
        {
            gv.setAdapter(null);
        }
        prepareEmptyView(ev, (ImageView) ev.findViewById(R.id.empty_picture),
                (TextView) ev.findViewById(R.id.empty_text), (TextView) ev.findViewById(R.id.empty_text_description));
    }

    protected void displayDataView()
    {
        if (adapter != null && !adapter.isEmpty())
        {
            setListShown(true);
            gv.setAdapter(adapter);
        }
    }

    protected final void savePosition()
    {
        if (gv != null)
        {
            selectedPosition = gv.getFirstVisiblePosition();
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // REFRESH
    // //////////////////////////////////////////////////////////////////////
    protected void onPrepareRefresh()
    {
        if (refreshHelper == null) { return; }
        refreshHelper.setRefreshing();
    }
}
