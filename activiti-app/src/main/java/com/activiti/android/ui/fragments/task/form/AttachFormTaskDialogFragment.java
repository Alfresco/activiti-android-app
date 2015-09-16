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
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.fragments.task.TaskDetailsFoundationFragment;
import com.activiti.client.api.model.editor.ModelRepresentation;
import com.activiti.client.api.model.editor.ModelsRepresentation;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

public class AttachFormTaskDialogFragment extends AlfrescoFragment
{
    private static final String ARGUMENT_TASK_ID = "taskId";

    private static final String ARGUMENT_FORM_ID = "formId";

    private FormDefinitionModelAdapter adapter;

    private String taskId;

    private Long formId, selectedModelId = -1L;

    private ModelRepresentation model;

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
        taskId = BundleUtils.getString(getArguments(), ARGUMENT_TASK_ID);
        formId = BundleUtils.getLong(getArguments(), ARGUMENT_FORM_ID);

        retrieveFormModels();

        adapter = new FormDefinitionModelAdapter(getActivity(), R.layout.row_two_lines,
                new ArrayList<ModelRepresentation>(0), formId, model);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(R.string.task_form_select_title).titleColor(getResources().getColor(R.color.accent))
                .positiveText(R.string.general_action_cancel).positiveColor(getResources().getColor(R.color.accent))
                .adapter(adapter, new MaterialDialog.ListCallback()
                {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence)
                    {
                        model = ((ModelRepresentation) materialDialog.getListView().getAdapter().getItem(i));

                        if (model.getId().equals(formId))
                        {
                            ((MDButton) materialDialog.getActionButton(DialogAction.POSITIVE))
                                    .setText(R.string.general_action_reset);
                        }
                        else
                        {
                            ((MDButton) materialDialog.getActionButton(DialogAction.POSITIVE))
                                    .setText(R.string.task_form_select_attach_form);
                        }

                        adapter.select(model);
                        adapter.notifyDataSetChanged();
                    }
                }).cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        getDialog().dismiss();
                    }
                });

        if (formId != null)
        {
            builder.neutralText(R.string.task_form_select_remove_form);
            builder.neutralColor(getResources().getColor(R.color.app_theme_4));
            builder.callback(new MaterialDialog.ButtonCallback()
            {
                @Override
                public void onPositive(MaterialDialog dialog)
                {
                    attachForm(model);
                }

                @Override
                public void onNeutral(MaterialDialog dialog)
                {
                    removeForm();
                }
            });
        }
        else
        {
            builder.callback(new MaterialDialog.ButtonCallback()
            {
                @Override
                public void onPositive(MaterialDialog dialog)
                {
                    attachForm(model);
                }
            });
        }

        return builder.show();
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

    private void attachForm(ModelRepresentation formModel)
    {
        if (model == null) { return; }
        ((TaskDetailsFoundationFragment) getAttachedFragment()).attachForm(formModel);
    }

    private void removeForm()
    {
        ((TaskDetailsFoundationFragment) getAttachedFragment()).removeForm();
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

        public Builder formKey(Long formKey)
        {
            if (formKey != null)
            {
                extraConfiguration.putLong(ARGUMENT_FORM_ID, formKey);
            }
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
