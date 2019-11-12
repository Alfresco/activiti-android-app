package com.activiti.android.ui.fragments.task.form;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.services.TaskService;
import com.activiti.android.ui.utils.WorkerManagerUtils;
import com.activiti.client.api.model.runtime.SaveFormRepresentation;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Worker for saving the values of a form
 * <p>
 * Created by Bogdan Roatis on 4/25/2019.
 */
public class SaveFormWorker extends ListenableWorker {

    public SaveFormWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        final String taskId = getInputData().getString(WorkerManagerUtils.FORM_SAVE_TASK_ID);
        SaveFormRepresentation saveFormRepresentation =
                new Gson().fromJson(
                        getInputData().getString(WorkerManagerUtils.FORM_SAVE_REP),
                        SaveFormRepresentation.class);

        ActivitiSession session = new ActivitiSession.Builder()
                .connect(getInputData().getString(WorkerManagerUtils.FORM_SAVE_ENDPOINT),
                        getInputData().getString(WorkerManagerUtils.FORM_SAVE_USERNAME),
                        getInputData().getString(WorkerManagerUtils.FORM_SAVE_PASSWORD)).build();

        TaskService taskService = session.getServiceRegistry().getTaskService();

        return CallbackToFutureAdapter.getFuture(completer -> {
            taskService.saveTaskForm(taskId, saveFormRepresentation, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    // Analytics
                    AnalyticsHelper.reportOperationEvent(getApplicationContext(), AnalyticsManager.CATEGORY_TASK,
                            AnalyticsManager.ACTION_FORM, AnalyticsManager.LABEL_SAVE, 1, !response.isSuccessful());

                    if (!response.isSuccessful()) {
                        onFailure(call, new Exception(response.message()));
                    } else {
                        completer.set(Result.success());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable error) {
                    completer.set(Result.retry());
                }
            });
            return completer;
        });
    }
}
