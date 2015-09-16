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

package com.activiti.android.ui.fragments.process;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.utils.UIUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class ProcessDiagramFoundationFragment extends AlfrescoFragment
{
    public static final String TAG = ProcessDiagramFoundationFragment.class.getName();

    public static final String ARGUMENT_PROCESS_ID = "processId";

    public static final String ARGUMENT_TENANT_ID = "tenantId";

    public static final String ARGUMENT_PROCESS_NAME = "processName";

    protected String processId, tenantId, processName;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessDiagramFoundationFragment()
    {
        super();
    }

    public static ProcessDiagramFoundationFragment newInstanceByTemplate(Bundle b)
    {
        ProcessDiagramFoundationFragment cbf = new ProcessDiagramFoundationFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_process_diagram, container, false));

        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null)
        {
            processName = BundleUtils.getString(getArguments(), ARGUMENT_PROCESS_NAME);
            processId = BundleUtils.getString(getArguments(), ARGUMENT_PROCESS_ID);
            tenantId = BundleUtils.getString(getArguments(), ARGUMENT_TENANT_ID);
        }

        displayInfo();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UIUtils.setTitle(getActivity(), processName, getString(R.string.process_title_diagram));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    private void displayInfo()
    {
        ((ImageViewTouch) viewById(R.id.process_diagram_view))
                .setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        // Icon
        ((MainActivity) getActivity()).getPicasso()
                .load(Uri.parse(getAPI().getProcessService().getProcessDiagramUrl(processId, tenantId)))
                .into((ImageView) viewById(R.id.process_diagram_view));
    }
}
