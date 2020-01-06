/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
 *
 *  This file is part of Alfresco Activiti Mobile for Android.
 *
 *  Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package com.activiti.android.ui.fragments.task.create;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.event.CreateTaskEvent;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.fragments.task.TasksFoundationFragment;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

public class CreateStandaloneTaskDialogFragment extends AlfrescoFragment
{
    private static final String ARGUMENT_APP_ID = "processDefinitionId";

    private static final String ARGUMENT_TASK_ID = "taskId";

    protected Integer fieldId;

    protected String appId;

    protected String taskId;

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
        taskId = BundleUtils.getString(getArguments(), ARGUMENT_TASK_ID);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(taskId != null ? R.string.task_action_add_checklist : R.string.task_create_new)
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
                        TaskRepresentation tr = new TaskRepresentation();
                        tr.setName(nameET.getText().toString());
                        tr.setDescription(descriptionET.getText().toString());
                        tr.setCategory(appId);
                        tr.setAssignee(ActivitiAccountManager.getInstance(getActivity()).getUser());
                        createTask(tr);
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
            descriptionET = ((EditText) ((MaterialDialog) getDialog()).getCustomView()
                    .findViewById(R.id.create_task_description));
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
    private void createTask(TaskRepresentation rep)
    {
        if (!TextUtils.isEmpty(taskId))
        {
            getAPI().getTaskService().addSubtask(taskId, rep, new Callback<TaskRepresentation>()
            {
                @Override
                public void onResponse(Call<TaskRepresentation> call, Response<TaskRepresentation> response)
                {
                    // Analytics
                    AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_TASK,
                            AnalyticsManager.ACTION_CREATE, AnalyticsManager.LABEL_SUBTASK, 1,
                            !response.isSuccessful());

                    if (!response.isSuccessful())
                    {
                        onFailure(call, new Exception(response.message()));
                        return;
                    }

                    EventBusManager.getInstance()
                            .post(new CreateTaskEvent(null, response.body(), getLastAppId(), taskId));
                    Snackbar.make(getActivity().findViewById(R.id.left_panel),
                            String.format(getString(R.string.task_alert_checklist_added), response.body().getName()),
                            Snackbar.LENGTH_SHORT).show();

                    dismiss();
                }

                @Override
                public void onFailure(Call<TaskRepresentation> call, Throwable error)
                {
                    Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            getAPI().getTaskService().create(rep, new Callback<TaskRepresentation>()
            {
                @Override
                public void onResponse(Call<TaskRepresentation> call, Response<TaskRepresentation> response)
                {
                    // Analytics
                    AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_TASK,
                            AnalyticsManager.ACTION_CREATE, AnalyticsManager.LABEL_TASK, 1, !response.isSuccessful());

                    if (!response.isSuccessful())
                    {
                        onFailure(call, new Exception(response.message()));
                        return;
                    }

                    EventBusManager.getInstance()
                            .post(new CreateTaskEvent(null, response.body(), getLastAppId(), null));
                    Snackbar.make(getActivity().findViewById(R.id.left_panel),
                            String.format(getString(R.string.task_alert_created), response.body().getName()),
                            Snackbar.LENGTH_SHORT).show();

                    dismiss();
                }

                @Override
                public void onFailure(Call<TaskRepresentation> call, Throwable error)
                {
                    Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        }
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
