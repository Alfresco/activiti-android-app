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

package com.activiti.android.ui.fragments.task.filter;

import java.util.ArrayList;
import java.util.Map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModel;
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.fragments.processDefinition.ProcessDefinitionModelAdapter;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.client.api.constant.RequestConstant;
import com.afollestad.materialdialogs.MaterialDialog;

public class TaskProcessDefinitionFragment extends AlfrescoFragment
{
    public static final String ARGUMENT_PROCESS_DEFINITION_ID = "processDefId";

    public static final String ARGUMENT_APP_ID = "processDefinitionId";

    private ProcessDefinitionModelAdapter adapter;

    private CommonTaskFilterFragment frag;

    private String processDefId;

    private Long appId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TaskProcessDefinitionFragment()
    {
        super();
    }

    public static TaskProcessDefinitionFragment newInstanceByTemplate(Bundle b)
    {
        TaskProcessDefinitionFragment cbf = new TaskProcessDefinitionFragment();
        cbf.setArguments(b);
        return cbf;
    }

    public static String getProcessTitle(FragmentActivity activity, String processId)
    {
        return processId;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String state = RequestConstant.STATE_OPEN;
        if (getArguments() != null)
        {
            processDefId = getArguments().getString(ARGUMENT_PROCESS_DEFINITION_ID);
            appId = getArguments().getLong(ARGUMENT_APP_ID);
        }

        frag = (CommonTaskFilterFragment) getActivity().getSupportFragmentManager().findFragmentById(
                DisplayUtils.hasCentralPane(getActivity()) ? R.id.central_left_drawer : R.id.right_drawer);

        Map<Long, ProcessDefinitionModel> models = ProcessDefinitionModelManager.getInstance(getActivity())
                .getAllByAppId(getAccount().getId(), appId);

        adapter = new ProcessDefinitionModelAdapter(getActivity(), R.layout.row_single_line,
                new ArrayList<>(models.values()));

        return new MaterialDialog.Builder(getActivity()).title(R.string.task_filter_text)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).adapter(adapter, new MaterialDialog.ListCallback()
                {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence)
                    {
                        frag.onProcessDefinitionSelection(
                                ((ProcessDefinitionModel) materialDialog.getListView().getAdapter().getItem(i)));
                        getDialog().dismiss();
                    }
                }).show();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
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

        public Builder appId(Long appId)
        {
            extraConfiguration.putLong(ARGUMENT_APP_ID, appId);
            return this;
        }

        public Builder processDefId(String processDefId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_DEFINITION_ID, processDefId);
            return this;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        }

        ;
    }
}
