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

package com.activiti.android.ui.fragments.task.form;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.activiti.android.app.ActivitiVersionNumber;
import com.activiti.android.app.R;
import com.activiti.android.app.fragments.task.TasksFragment;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.event.CompleteTaskEvent;
import com.activiti.android.platform.event.SaveTaskEvent;
import com.activiti.android.platform.exception.ExceptionMessageUtils;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.provider.transfer.ContentTransferManager;
import com.activiti.android.platform.utils.ConnectivityUtils;
import com.activiti.android.sdk.model.runtime.AppVersion;
import com.activiti.android.sdk.model.runtime.ParcelTask;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.form.fields.MultiValueField;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.form.picker.DatePickerFragment;
import com.activiti.android.ui.fragments.form.picker.UserGroupPickerFragment;
import com.activiti.android.ui.fragments.form.picker.UserPickerFragment;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.android.ui.utils.WorkerManagerUtils;
import com.activiti.client.api.model.editor.form.FormDefinitionRepresentation;
import com.activiti.client.api.model.editor.form.request.CompleteFormRepresentation;
import com.activiti.client.api.model.idm.LightGroupRepresentation;
import com.activiti.client.api.model.idm.LightUserRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.RestVariable;
import com.activiti.client.api.model.runtime.SaveFormRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jpascal on 28/03/2015.
 */
public class TaskFormFoundationFragment extends AlfrescoFragment implements DatePickerFragment.OnPickDateFragment,
        UserPickerFragment.onPickAuthorityFragment, UserGroupPickerFragment.onPickGroupFragment {
    public static final String TAG = TaskFormFoundationFragment.class.getName();

    protected static final String ARGUMENT_TASK = "parcelTask";

    protected String processId;

    protected ParcelTask task;

    protected Long appId;

    protected FormManager formManager;

    protected Boolean isEnded = true, refresh = false;

    protected Map<String, View> outcomesView;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TaskFormFoundationFragment() {
        super();
        eventBusRequired = true;
        setHasOptionsMenu(true);
    }

    public static TaskFormFoundationFragment newInstanceByTemplate(Bundle b) {
        TaskFormFoundationFragment cbf = new TaskFormFoundationFragment();
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
            task = getArguments().getParcelable(ARGUMENT_TASK);
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
            generateForm();
        } else {
            // Hack : we detect the fragment has been recreated
            // We enforce their value display
            // A picker might have updated its value and its not currently
            // displayed
            refresh = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == ContentTransferManager.PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            MultiValueField field = (MultiValueField) formManager.getCurrentPickerField();
            RelatedContentRepresentation content = ContentTransferManager.getRelatedContent(getActivity(),
                    resultData.getData());
            field.addValue(content);
            formManager.setCurrentPickerField(null);
        }
    }

    @Override
    public void onStart() {
        if (refresh) {
            // We do the refreshEditionView to display the latest value inside
            // the BaseField
            formManager.refreshViews();
            refresh = false;
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        UIUtils.setTitle(getActivity(), task.name, getString(R.string.form_message_title), true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getVersionNumber() >= ActivitiVersionNumber.VERSION_1_2_2 && !isEnded) {
            if (!DisplayUtils.hasCentralPane(getActivity())) {
                menu.clear();
                inflater.inflate(R.menu.task_form, menu);
            } else {
                getToolbar().getMenu().clear();
                getToolbar().inflateMenu(R.menu.task_form);
                // Set an OnMenuItemClickListener to handle menu item clicks
                getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.task_form_save:
                SaveFormRepresentation saveFormRepresentation = new SaveFormRepresentation(formManager.getValues());
                if (ConnectivityUtils.hasInternetAvailable(getContext())) {
                    getAPI().getTaskService().saveTaskForm(task.id, saveFormRepresentation, new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            // Analytics
                            AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_TASK,
                                    AnalyticsManager.ACTION_FORM, AnalyticsManager.LABEL_SAVE, 1, !response.isSuccessful());

                            if (!response.isSuccessful()) {
                                onFailure(call, new Exception(response.message()));
                                return;
                            }

                            try {
                                EventBusManager.getInstance().post(new SaveTaskEvent(null, task.id, task.category));
                            } catch (Exception e) {
                                // Do nothing
                            }
                            Snackbar.make(getActivity().findViewById(R.id.left_panel), R.string.task_alert_saved,
                                    Snackbar.LENGTH_LONG).show();

                            // Refresh Task Fragment
                            if (!DisplayUtils.hasCentralPane(getActivity())) {
                                Fragment fr = getAttachedFragment();
                                if (fr != null && fr instanceof TasksFragment) {
                                    ((TasksFragment) fr).refresh();
                                }
                            }

                            getActivity().onBackPressed();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable error) {
                            Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    WorkerManagerUtils.startFormSaverWorker(
                            task.id,
                            getAccount().getServerUrl(),
                            getAccount().getUsername(),
                            getAccount().getPassword(),
                            saveFormRepresentation);

                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.offline_save_form_message_title)
                            .cancelListener(dialog -> dismiss())
                            .content(getString(R.string.offline_save_form_message))
                            .positiveText(R.string.ok)
                            .show();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // REQUEST
    // ///////////////////////////////////////////////////////////////////////////
    protected void generateForm() {
        displayLoading();
        getAPI().getTaskService().getTaskForm(task.id, new Callback<FormDefinitionRepresentation>() {
            @Override
            public void onResponse(Call<FormDefinitionRepresentation> call,
                                   Response<FormDefinitionRepresentation> response) {
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception(response.message()));
                    return;
                }
                AppVersion version = new AppVersion(getAccount().getServerVersion());
                formManager = new FormManager(TaskFormFoundationFragment.this,
                        (ViewGroup) viewById(R.id.form_container), response.body(), version);

                isEnded = task.endDate != null;
                getAPI().getTaskService().getFormVariables(task.id, variablesCallback);
            }

            @Override
            public void onFailure(Call<FormDefinitionRepresentation> call, Throwable error) {
                displayError(error);
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private Callback<List<RestVariable>> variablesCallback = new Callback<List<RestVariable>>() {
        @Override
        public void onResponse(Call<List< RestVariable >> call, Response<List<RestVariable>> response) {
            if (response.isSuccessful()) {
                formManager.insertVariables(response.body());
            }

            if (task.endDate == null) {
                formManager.displayEditForm();
            } else {
                formManager.displayReadForm();
            }

            outcomesView = formManager.getOutComesView();
            Button button;
            for (Map.Entry<String, View> outcomeEntry : outcomesView.entrySet()) {
                button = outcomeEntry.getValue().findViewById(R.id.outcome_button);
                button.setTag(outcomeEntry.getKey());
                button.setOnClickListener(v -> {
                    UIUtils.hideKeyboard(getActivity(), v);
                    completeTask((String) v.getTag());
                });
            }

            displayData();
            getActivity().invalidateOptionsMenu();
        }

        @Override
        public void onFailure(Call<List<RestVariable>> call, Throwable t) {
        }
    };

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

        CompleteFormRepresentation rep = new CompleteFormRepresentation(formManager.getValues(), outcome);
        getAPI().getTaskService().completeTaskForm(task.id, rep, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Analytics
                AnalyticsHelper.reportOperationEvent(getActivity(), AnalyticsManager.CATEGORY_TASK,
                        AnalyticsManager.ACTION_COMPLETE_TASK, AnalyticsManager.LABEL_WITH_FORM, 1,
                        !response.isSuccessful());

                if (!response.isSuccessful()) {
                    onFailure(call, new Exception(response.message()));
                    return;
                }
                try {
                    EventBusManager.getInstance().post(new CompleteTaskEvent(null, task.id, task.category));
                } catch (Exception e) {
                    // Do nothing
                }
                Snackbar.make(getActivity().findViewById(R.id.left_panel), R.string.task_alert_completed,
                        Snackbar.LENGTH_LONG).show();

                // Refresh Task Fragment
                if (!DisplayUtils.hasCentralPane(getActivity())) {
                    Fragment fr = getAttachedFragment();
                    if (fr != null && fr instanceof TasksFragment) {
                        ((TasksFragment) fr).refresh();
                    }
                }

                getActivity().onBackPressed();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable error) {
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // PICKER CALLBACK
    // //////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDatePicked(String fieldId, Calendar calendar) {
        formManager.setPropertyValue(fieldId, calendar.getTime());
    }

    @Override
    public void onDateClear(String dateId) {
        formManager.setPropertyValue(dateId, null);
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // PICKER CALLBACK
    // //////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPersonSelected(String fieldId, Map<String, LightUserRepresentation> p) {
        formManager.setPropertyValue(fieldId, p.get(p.keySet().toArray()[0]));
    }

    @Override
    public void onPersonClear(String fieldId) {
        formManager.setPropertyValue(fieldId, null);
    }

    @Override
    public Map<String, LightUserRepresentation> getPersonSelected(String fieldId) {
        return new HashMap<>(0);
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // PICKER CALLBACK
    // //////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onGroupSelected(String fieldId, Map<Long, LightGroupRepresentation> p) {
        formManager.setPropertyValue(fieldId, p.get(p.keySet().toArray()[0]));
    }

    @Override
    public void onGroupClear(String fieldId) {
        formManager.setPropertyValue(fieldId, null);
    }

    @Override
    public Map<Long, LightGroupRepresentation> getGroupsSelected(String fieldId) {
        return new HashMap<>(0);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CARDS
    // ///////////////////////////////////////////////////////////////////////////
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
        bRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateForm();
            }
        });
        bRetry.setVisibility(View.VISIBLE);
    }
}
