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

package com.activiti.android.app.fragments.task.startform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.fragments.process.ProcessDetailsFragment;
import com.activiti.android.app.fragments.process.ProcessesFragment;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.event.StartProcessEvent;
import com.activiti.android.platform.exception.ExceptionMessageUtils;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.sdk.model.runtime.AppVersion;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.LeafFragmentBuilder;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.editor.form.FormDefinitionRepresentation;
import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;
import com.activiti.client.api.model.runtime.RestVariable;
import com.activiti.client.api.model.runtime.request.CreateProcessInstanceRepresentation;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartFormFragment extends AlfrescoFragment {
    public static final String TAG = StartFormFragment.class.getName();

    protected static final String ARGUMENT_START_FORM_PROCESS_DEFINITION_ID = "processDefinitionId";
    protected static final String ARGUMENT_START_FORM_ID = "startFormId";
    protected static final String ARGUMENT_PROCESS_NAME = "processName";

    private FormDefinitionRepresentation startForm;
    private String startFormId, startFormProcessDefinitionId;
    private String processName;
    protected FormManager formManager;
    protected Map<String, View> outcomesView;
    protected Boolean refresh = false;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public StartFormFragment() {
        super();
        eventBusRequired = true;
        setHasOptionsMenu(true);
    }

    public static StartFormFragment newInstanceByTemplate(Bundle b) {
        StartFormFragment cbf = new StartFormFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resetRightMenu();

        // This fragment must retain instance because there's temporary walue.
        // We want to avoid their lost after rotation or picker
        setRetainInstance(true);

        if (getArguments() != null) {
            startFormProcessDefinitionId = getArguments().getString(ARGUMENT_START_FORM_PROCESS_DEFINITION_ID);
            startFormId = getArguments().getString(ARGUMENT_START_FORM_ID);
            processName = getArguments().getString(ARGUMENT_PROCESS_NAME);
        }

        if (getRootView() == null) {
            setRootView(inflater.inflate(R.layout.form_container, container, false));
            show(R.id.progressbar_group);
            show(R.id.progressbar);
            hide(R.id.form_master);
        }

        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (formManager == null) {
            if (startFormProcessDefinitionId != null) {
                requestDefinitionForm();
            } else {
                requestInstanceForm();
            }
        } else {
            // Hack : we detect the fragment has been recreated
            // We enforce their value display
            // A picker might have updated its value and its not currently
            // displayed
            refresh = true;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // REQUEST
    // ///////////////////////////////////////////////////////////////////////////
    protected void requestDefinitionForm() {
        displayLoading();
        getAPI().getProcessDefinitionService().getProcessDefinitionStartForm(
                startFormProcessDefinitionId, new Callback<FormDefinitionRepresentation>() {
                    @Override
                    public void onResponse(Call<FormDefinitionRepresentation> call, Response<FormDefinitionRepresentation> response) {
                        if (!response.isSuccessful()) {
                            onFailure(call, new Exception(response.message()));
                            return;
                        }

                        generateForm(response.body());
                    }

                    @Override
                    public void onFailure(Call<FormDefinitionRepresentation> call, Throwable t) {
                        displayError(t);
                        Snackbar.make(getActivity().findViewById(R.id.left_panel), t.getMessage(), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    protected void requestInstanceForm() {
        displayLoading();
        getAPI().getProcessService().getStartFormProcessInstance(startFormId, new Callback<FormDefinitionRepresentation>() {
            @Override
            public void onResponse(Call<FormDefinitionRepresentation> call,
                                   Response<FormDefinitionRepresentation> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception(response.message()));
                    return;
                }

                generateForm(response.body());
            }

            @Override
            public void onFailure(Call<FormDefinitionRepresentation> call, Throwable error) {
                displayError(error);
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    protected void generateForm(FormDefinitionRepresentation formDefinitionRepresentation) {
        AppVersion version = new AppVersion(getAccount().getServerVersion());

        formManager = new FormManager(StartFormFragment.this,
                (ViewGroup) viewById(R.id.form_container), formDefinitionRepresentation, version);

        startForm = formDefinitionRepresentation;

        if (startFormId != null) {
            getAPI().getProcessService().getHistoricFormVariables(startFormId, variablesCallback);
        } else {
            generateViews();
        }
    }

    public void generateViews() {
        if (startFormId == null) {
            formManager.displayEditForm();
        } else {
            formManager.displayReadForm();
        }

        outcomesView = formManager.getOutComesView();
        Button button;
        for (Map.Entry<String, View> outcomeEntry : outcomesView.entrySet()) {
            button = outcomeEntry.getValue().findViewById(R.id.outcome_button);
            button.setTag(outcomeEntry.getKey());
            button.setEnabled(startFormId == null);
            button.setText(startFormId != null ? R.string.form_default_outcome_complete : R.string.form_default_outcome_start_process);
            button.setOnClickListener(v -> {
                UIUtils.hideKeyboard(getActivity(), v);
                completeTask((String) v.getTag());
            });
        }

        displayData();
        getActivity().invalidateOptionsMenu();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        if (refresh) {
            // We do the refreshEditionView to display the latest value inside
            // the BaseField
            formManager.refreshViews();
            refresh = false;
        }
        super.onStart();
        UIUtils.displayActionBarBack((MainActivity) getActivity(), getToolbar());
    }

    @Override
    public void onStop() {
        super.onStop();
        UIUtils.setActionBarDefault((MainActivity) getActivity(), getToolbar());
    }

    private Callback<List<RestVariable>> variablesCallback = new Callback<List<RestVariable>>() {
        @Override
        public void onResponse(Call<List<RestVariable>> call, Response<List<RestVariable>> response) {
            if (response.isSuccessful()) {
                formManager.insertVariables(response.body());
            }

            generateViews();
        }

        @Override
        public void onFailure(Call<List<RestVariable>> call, Throwable t) {
        }
    };

    protected void displayLoading() {
        hide(R.id.form_master);
        show(R.id.details_loading);
        show(R.id.progressbar);
        hide(R.id.empty);
    }

    protected void displayData() {
        show(R.id.form_master);
        hide(R.id.details_loading);
        hide(R.id.progressbar);
        hide(R.id.empty);
    }

    protected void displayError(Throwable error) {
        hide(R.id.form_master);
        show(R.id.details_loading);
        hide(R.id.progressbar);
        show(R.id.empty);

        // Update controls in regards
        TextView emptyText = (TextView) viewById(R.id.empty_text);
        if (getActivity() != null) {
            emptyText.setText(ExceptionMessageUtils.getMessage(getActivity(), error));
        }
        Button bRetry = (Button) viewById(R.id.empty_action);
        bRetry.setText(R.string.retry);
        bRetry.setOnClickListener(v -> requestInstanceForm());
        bRetry.setVisibility(View.VISIBLE);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // COMPLETE
    // ///////////////////////////////////////////////////////////////////////////
    protected void completeTask(String outcome) {
        // Form valid ?
        if (!formManager.checkValidation()) {
            Snackbar.make(getActivity().findViewById(R.id.left_panel), R.string.form_message_validation,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!formManager.hasOutcome()) {
            outcome = null;
        }

        createProcess(new CreateProcessInstanceRepresentation(startFormProcessDefinitionId, processName, formManager.getValues(), outcome));
    }

    private void createProcess(final CreateProcessInstanceRepresentation rep) {
        getAPI().getProcessService().startNewProcessInstance(rep, new Callback<ProcessInstanceRepresentation>() {
            @Override
            public void onResponse(Call<ProcessInstanceRepresentation> call,
                                   Response<ProcessInstanceRepresentation> response) {
                // Analytics
                AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_PROCESS,
                        AnalyticsManager.ACTION_CREATE, AnalyticsManager.LABEL_PROCESS, 1, !response.isSuccessful());

                if (!response.isSuccessful()) {
                    onFailure(call, new Exception(response.message()));
                    return;
                }

                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }

                ProcessDetailsFragment.with(getActivity()).processId(response.body().getId()).display();

                try {
                    ProcessesFragment fr = (ProcessesFragment) getActivity().getSupportFragmentManager()
                            .findFragmentByTag(ProcessesFragment.TAG);
                    if (fr != null) {
                        fr.onStartedProcessEvent(new StartProcessEvent(null, response.body(), getLastAppId()));
                    }
                } catch (Exception e) {
                    EventBusManager.getInstance().post(new StartProcessEvent(null, response.body(), null));
                }

                dismiss();
            }

            @Override
            public void onFailure(Call<ProcessInstanceRepresentation> call, Throwable error) {
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity) {
        return new Builder(activity);
    }

    public static class Builder extends LeafFragmentBuilder {
        // ///////////////////////////////////////////////////////////////////////////
        // CONSTRUCTORS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder(FragmentActivity activity) {
            super(activity);
            this.extraConfiguration = new Bundle();
        }

        public Builder(FragmentActivity appActivity, Map<String, Object> configuration) {
            super(appActivity, configuration);
        }

        public Builder startFormDefinitionId(String processDefinitionId) {
            extraConfiguration.putString(ARGUMENT_START_FORM_PROCESS_DEFINITION_ID, processDefinitionId);
            return this;
        }

        public Builder processName(String processName) {
            extraConfiguration.putString(ARGUMENT_PROCESS_NAME, processName);
            return this;
        }

        public Builder startFormId(String processId) {
            extraConfiguration.putString(ARGUMENT_START_FORM_ID, processId);
            return this;
        }

        public Builder bindFragmentTag(String fragmentListTag) {
            extraConfiguration.putString(ARGUMENT_BIND_FRAGMENT_TAG, fragmentListTag);
            return this;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b) {
            return newInstanceByTemplate(b);
        }
    }
}
