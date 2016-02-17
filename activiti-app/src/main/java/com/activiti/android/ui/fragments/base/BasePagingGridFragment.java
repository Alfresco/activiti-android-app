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

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.platform.exception.ExceptionMessageUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.common.ResultList;

/**
 * Created by jpascal on 12/12/2014.
 */
public abstract class BasePagingGridFragment extends BaseGridFragment
{
    protected View.OnClickListener actionListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            refresh();
            v.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        if (displayAsList)
        {
            gv.setColumnWidth(UIUtils.getDPI(getActivity(), 2000));
        }
    }

    // /////////////////////////////////////////////////////////////
    // LOAD MORE
    // ////////////////////////////////////////////////////////////
    @SuppressWarnings("rawtypes")
    protected void displayData(ResultList<?> response)
    {
        if (getActivity() == null) { return; }

        if (displayAsList)
        {
            gv.setColumnWidth(UIUtils.getDPI(getActivity(), 2000));
        }

        if (adapter == null)
        {
            adapter = onAdapterCreation();
            ((BaseListAdapter) adapter).setFragmentSettings(getArguments());
        }
        displayPagingData((ResultList<?>) response);
        setListShown(true);
        onDataDisplayed();
    }

    protected void displayPagingData(ResultList<?> response)
    {
        // No Items
        if (response.getTotal() == 0)
        {
            ev.setVisibility(View.VISIBLE);
            gv.setEmptyView(ev);
            if (adapter != null)
            {
                gv.invalidateViews();
                gv.setAdapter(null);
            }
        }
        else
        {
            gv.setEmptyView(null);
            ev.setVisibility(View.GONE);
        }

        // All Items are presents
        if (response.getTotal() == response.getStart() + response.getSize())
        {
            ((ArrayAdapter<Object>) adapter).addAll(response.getList());
            gv.invalidateViews();
            gv.setAdapter(adapter);
            setListShown(true);
            isFullLoad = Boolean.TRUE;
        }
        else
        {
            ((ArrayAdapter<Object>) adapter).addAll(response.getList());
            gv.invalidateViews();
            gv.setAdapter(adapter);
            setListShown(true);
            isFullLoad = Boolean.FALSE;
            isLockVisibleLoader = Boolean.FALSE;
            // loadMore(response.getStart() + response.getSize());
        }
        refreshHelper.setRefreshComplete();

        if (selectedPosition != 0)
        {
            gv.setSelection(selectedPosition);
        }
    }

    protected void displayError(Throwable error)
    {
        refreshHelper.setRefreshComplete();
        refreshHelper.setEnabled(false);
        setListShown(true);

        if (adapter == null)
        {
            // Display Empty View
            gv.setEmptyView(ev);

            // Update controls in regards
            TextView emptyText = (TextView) viewById(R.id.empty_text);
            emptyText.setText(ExceptionMessageUtils.getMessage(getActivity(), error));
            Button bRetry = (Button) viewById(R.id.empty_action);
            if (actionListener != null)
            {
                bRetry.setVisibility(View.VISIBLE);
                bRetry.setText(R.string.retry);

                if (ExceptionMessageUtils.isGeneralError(error))
                {
                    // TODO implement other connectivity issues
                }

                bRetry.setOnClickListener(actionListener);
            }
            else
            {
                bRetry.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    /** Event after data has been displayed. */
    protected void onDataDisplayed()
    {
        // Can be used by derived classes.
    }

    @Override
    public void refresh()
    {
        adapter = null;

        // Event refreshEditionView
        onPrepareRefresh();

        // Execute the request
        performRequest();
    }

    public void refreshOutside()
    {
        requestRefresh = true;
        refresh();
    }

    public void requestRefresh()
    {
        requestRefresh = true;
    }

    @Override
    protected void performRequest(long skipCount)
    {
        performRequest();
    }
}
