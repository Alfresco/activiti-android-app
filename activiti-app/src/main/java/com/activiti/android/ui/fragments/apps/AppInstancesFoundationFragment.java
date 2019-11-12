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

package com.activiti.android.ui.fragments.apps;

import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.widget.BaseAdapter;

import com.activiti.android.app.R;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceSchema;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.base.BaseCursorGridFragment;

public class AppInstancesFoundationFragment extends BaseCursorGridFragment
{
    public static final String TAG = AppInstancesFoundationFragment.class.getName();

    protected static final String ARGUMENT_DRAWER_ID = "drawerId";

    protected Integer drawerId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AppInstancesFoundationFragment()
    {
        emptyListMessageId = R.string.empty_app;
        retrieveDataOnCreation = true;
        eventBusRequired = true;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onRetrieveParameters(Bundle bundle)
    {
        super.onRetrieveParameters(bundle);
        drawerId = BundleUtils.getInt(bundle, ARGUMENT_DRAWER_ID);
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new AppInstanceCursorAdapter(getActivity(), null, R.layout.row_single_line_caption);
    }

    @Override
    protected void performRequest()
    {
        if (getAccount() == null) { return; }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(getActivity(), RuntimeAppInstanceManager.CONTENT_URI,
                RuntimeAppInstanceSchema.COLUMN_ALL, RuntimeAppInstanceSchema.COLUMN_ACCOUNT_ID + " = "
                        + getAccount().getId() + "", null, null);
    }

    @Override
    protected void displayDataView()
    {
        super.displayDataView();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (drawerId != null)
        {
            hide(R.id.fab);
        }
    }
}
