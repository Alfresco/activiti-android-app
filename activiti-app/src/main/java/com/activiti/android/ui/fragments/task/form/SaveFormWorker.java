package com.activiti.android.ui.fragments.task.form;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.services.TaskService;
import com.activiti.android.ui.utils.WorkerManagerUtils;
import com.activiti.client.api.model.runtime.SaveFormRepresentation;
import com.alfresco.auth.AuthInterceptor;
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

    private ActivitiSession session;

    private AuthInterceptor authInterceptor;

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

        long accountId = getInputData().getLong(WorkerManagerUtils.FORM_SAVE_ACCOUNT_ID, -1);
        String username = getInputData().getString(WorkerManagerUtils.FORM_SAVE_USERNAME);
        ActivitiAccount account = ActivitiAccountManager.getInstance(getApplicationContext()).getByAccountId(accountId);

        // If the account is missing or has changed than drop the changes.
        if (account == null || !account.getUsername().equals(username)) {
            return CallbackToFutureAdapter.getFuture(completer -> {
                completer.set(Result.failure());
                return completer;
            });
        }

        authInterceptor = new AuthInterceptor(
                getApplicationContext(),
                String.valueOf(account.getId()),
                account.getAuthType(),
                account.getAuthState(),
                account.getAuthConfig());

        session = new ActivitiSession.Builder()
                .connect(account.getServerUrl())
                .authInterceptor(authInterceptor)
                .build();

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
                    cleanupSession();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable error) {
                    completer.set(Result.retry());
                    cleanupSession();
                }
            });
            return completer;
        });
    }

    @Override
    public void onStopped() {
        super.onStopped();
        cleanupSession();
    }

    private void cleanupSession() {
        if (authInterceptor != null) {
            authInterceptor.finish();
            authInterceptor = null;
        }
        session = null;
    }
}
