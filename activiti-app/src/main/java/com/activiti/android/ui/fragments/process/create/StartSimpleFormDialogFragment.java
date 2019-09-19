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

package com.activiti.android.ui.fragments.process.create;

import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.widget.EditText;

import com.activiti.android.app.R;
import com.activiti.android.app.fragments.process.ProcessDetailsFragment;
import com.activiti.android.app.fragments.process.ProcessesFragment;
import com.activiti.android.app.fragments.task.startform.StartFormFragment;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.event.StartProcessEvent;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.editor.form.FormDefinitionRepresentation;
import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;
import com.activiti.client.api.model.runtime.request.CreateProcessInstanceRepresentation;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

public class StartSimpleFormDialogFragment extends AlfrescoFragment
{
    private static final String ARGUMENT_PROCESS_ID = "processId";

    private static final String ARGUMENT_PROCESSDEF_ID = "processDefinitionId";

    private static final String ARGUMENT_PROCESSDEF_NAME = "processDefinitionName";

    protected String processDefinitionId, processDefinitionName;

    protected EditText nameView;

    private MDButton positiveButton;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public StartSimpleFormDialogFragment()
    {
        super();
    }

    public static StartSimpleFormDialogFragment newInstanceByTemplate(Bundle b)
    {
        StartSimpleFormDialogFragment cbf = new StartSimpleFormDialogFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        processDefinitionId = getArguments().getString(ARGUMENT_PROCESSDEF_ID);
        processDefinitionName = getArguments().getString(ARGUMENT_PROCESSDEF_NAME);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.form_default_outcome_start_process))
                .titleColor(getResources().getColor(R.color.accent)).customView(R.layout.fr_create_process, false)
                .positiveText(R.string.process_action_create).autoDismiss(false)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        positiveButton = dialog.getActionButton(DialogAction.POSITIVE);
                        positiveButton.setEnabled(false);

                        String processName;
                        if (nameView.getText().length() == 0)
                        {
                            processName = createDefaultName();
                        }
                        else
                        {
                            processName = nameView.getText().toString();
                        }
                        createStartForm(processDefinitionId, new CreateProcessInstanceRepresentation(processDefinitionId, processName));
                    }
                });
        return builder.show();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (getDialog() != null)
        {
            nameView = ((EditText) ((MaterialDialog) getDialog()).getCustomView().findViewById(R.id.create_task_name));
            nameView.setHint(createDefaultName());
            nameView.setSelection(nameView.getText().length());
            UIUtils.showKeyboard(getActivity(), nameView);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UIUtils.showKeyboard(getActivity(), nameView);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    private String createDefaultName()
    {
        StringBuilder defaultName = new StringBuilder(processDefinitionName);
        defaultName.append(" - ");
        defaultName.append(DateFormat.getLongDateFormat(getActivity()).format(new Date()));
        return defaultName.toString();
    }

    private void createStartForm(String processDefinitionId, final CreateProcessInstanceRepresentation rep) {
        getAPI().getProcessDefinitionService().getProcessDefinitionStartForm(
                processDefinitionId, new Callback<FormDefinitionRepresentation>() {
            @Override
            public void onResponse(Call<FormDefinitionRepresentation> call, Response<FormDefinitionRepresentation> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception(response.message()));
                    createProcess(rep);
                    return;
                }

                StartFormFragment.with(getActivity())
                        .startFormDefinitionId(response.body().getProcessDefinitionId())
                        .processName(rep.getName())
                        .display();

                dismiss();
            }

            @Override
            public void onFailure(Call<FormDefinitionRepresentation> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.left_panel), t.getMessage(), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void createProcess(final CreateProcessInstanceRepresentation rep)
    {
        getAPI().getProcessService().startNewProcessInstance(rep, new Callback<ProcessInstanceRepresentation>()
        {
            @Override
            public void onResponse(Call<ProcessInstanceRepresentation> call,
                    Response<ProcessInstanceRepresentation> response)
            {
                // Analytics
                AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_PROCESS,
                        AnalyticsManager.ACTION_CREATE, AnalyticsManager.LABEL_PROCESS, 1, !response.isSuccessful());

                if (!response.isSuccessful())
                {
                    positiveButton.setEnabled(true);
                    onFailure(call, new Exception(response.message()));
                    return;
                }

                ProcessDetailsFragment.with(getActivity()).processId(response.body().getId()).display();

                try
                {
                    // EventBusManager.getInstance().post(new
                    // StartProcessEvent(null, representation, getLastAppId()));
                    ProcessesFragment fr = (ProcessesFragment) getActivity().getSupportFragmentManager()
                            .findFragmentByTag(ProcessesFragment.TAG);
                    if (fr != null)
                    {
                        fr.onStartedProcessEvent(new StartProcessEvent(null, response.body(), getLastAppId()));
                    }
                }
                catch (Exception e)
                {
                    EventBusManager.getInstance().post(new StartProcessEvent(null, response.body(), null));
                }

                dismiss();
            }

            @Override
            public void onFailure(Call<ProcessInstanceRepresentation> call, Throwable error)
            {
                positiveButton.setEnabled(true);
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_LONG)
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

        public Builder processDefinitionId(String processDefinitionId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESSDEF_ID, processDefinitionId);
            return this;
        }

        public Builder processId(String processId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_ID, processId);
            return this;
        }

        public Builder processDefinitionName(String processDefinitionName)
        {
            extraConfiguration.putString(ARGUMENT_PROCESSDEF_NAME, processDefinitionName);
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
