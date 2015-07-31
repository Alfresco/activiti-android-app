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

package com.activiti.android.ui.fragments.task.form;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.fragments.task.TaskDetailsFoundationFragment;
import com.activiti.client.api.model.editor.ModelRepresentation;
import com.activiti.client.api.model.editor.ModelsRepresentation;
import com.activiti.client.api.model.runtime.request.AttachFormTaskRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;

public class AttachFormTaskDialogFragment extends AlfrescoFragment
{
    private static final String ARGUMENT_TASK_ID = "taskId";

    private FormDefinitionModelAdapter adapter;

    private String taskId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public AttachFormTaskDialogFragment()
    {
        super();
    }

    public static AttachFormTaskDialogFragment newInstanceByTemplate(Bundle b)
    {
        AttachFormTaskDialogFragment cbf = new AttachFormTaskDialogFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        taskId = getArguments().getString(ARGUMENT_TASK_ID);

        retrieveFormModels();

        adapter = new FormDefinitionModelAdapter(getActivity(), R.layout.row_single_line,
                new ArrayList<ModelRepresentation>(0));

        return new MaterialDialog.Builder(getActivity()).title(R.string.form_default_outcome_start_process)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        getDialog().dismiss();
                    }
                }).titleColor(getResources().getColor(R.color.accent))
                .adapter(adapter, new MaterialDialog.ListCallback()
                {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence)
                    {
                        ModelRepresentation model = ((ModelRepresentation) materialDialog.getListView().getAdapter()
                                .getItem(i));
                        attachForm(model.getName(), new AttachFormTaskRepresentation(model.getId()));
                        getDialog().dismiss();
                    }
                }).show();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    private void retrieveFormModels()
    {
        getAPI().getModelService().getFormModels(new Callback<ModelsRepresentation>()
        {
            @Override
            public void success(ModelsRepresentation modelsRepresentation, Response response)
            {
                adapter.addAll(modelsRepresentation.getData());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error)
            {

            }
        });
    }

    private void attachForm(String modelName, final AttachFormTaskRepresentation attach)
    {
        ((TaskDetailsFoundationFragment) getAttachedFragment()).attachForm(modelName, attach);
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

        public Builder bindFragmentTag(String fragmentListTag)
        {
            extraConfiguration.putString(ARGUMENT_BIND_FRAGMENT_TAG, fragmentListTag);
            return this;
        }

        public Builder taskId(String taskId)
        {
            extraConfiguration.putString(ARGUMENT_TASK_ID, taskId);
            return this;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        };
    }
}
