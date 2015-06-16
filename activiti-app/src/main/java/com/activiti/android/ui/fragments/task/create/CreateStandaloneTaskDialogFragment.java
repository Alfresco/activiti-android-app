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

package com.activiti.android.ui.fragments.task.create;

import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.activiti.android.app.R;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.event.CreateTaskEvent;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.fragments.task.TasksFoundationFragment;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.activiti.client.api.model.runtime.request.CreateTaskRepresentation;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class CreateStandaloneTaskDialogFragment extends AlfrescoFragment
{
    private static final String ARGUMENT_APP_ID = "processDefinitionId";

    protected Integer fieldId;

    protected String appId;

    protected EditText nameET, descriptionET;

    protected TasksFoundationFragment fragmentPick;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public CreateStandaloneTaskDialogFragment()
    {
        super();
    }

    public static CreateStandaloneTaskDialogFragment newInstanceByTemplate(Bundle b)
    {
        CreateStandaloneTaskDialogFragment cbf = new CreateStandaloneTaskDialogFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        appId = getArguments().getString(ARGUMENT_APP_ID);
        if (appId != null && appId.equals("-1"))
        {
            appId = null;
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity()).title(R.string.task_create_new)
                .titleColor(getResources().getColor(R.color.accent)).customView(R.layout.fr_create_task, false)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).positiveText(R.string.task_action_create_confirm).callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        createTask(new CreateTaskRepresentation(nameET.getText().toString(), descriptionET.getText()
                                .toString(), appId));
                    }
                });
        return builder.cancelable(false).autoDismiss(false).build();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (getDialog() != null)
        {
            descriptionET = ((EditText) ((MaterialDialog) getDialog()).getCustomView().findViewById(
                    R.id.create_task_description));
            nameET = ((EditText) ((MaterialDialog) getDialog()).getCustomView().findViewById(R.id.create_task_name));
            nameET.setSelection(nameET.getText().length());
            ((MaterialDialog) getDialog()).getActionButton(DialogAction.POSITIVE).setEnabled(false);
            nameET.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    if (s.length() == 0)
                    {
                        ((MaterialDialog) getDialog()).getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }
                    else
                    {
                        ((MaterialDialog) getDialog()).getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                }
            });
            UIUtils.showKeyboard(getActivity(), nameET);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UIUtils.showKeyboard(getActivity(), nameET);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    private void createTask(CreateTaskRepresentation rep)
    {
        getAPI().getTaskService().create(rep, new Callback<TaskRepresentation>()
        {
            @Override
            public void success(TaskRepresentation taskRepresentation, Response response)
            {
                EventBusManager.getInstance().post(new CreateTaskEvent(null, taskRepresentation, getLastAppId()));
                Snackbar.make(getActivity().findViewById(R.id.left_panel),
                        String.format(getString(R.string.task_alert_created), taskRepresentation.getName()),
                        Snackbar.LENGTH_SHORT).show();

                dismiss();
            }

            @Override
            public void failure(RetrofitError error)
            {
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
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

        public Builder appId(String appId)
        {
            extraConfiguration.putString(ARGUMENT_APP_ID, appId);
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
