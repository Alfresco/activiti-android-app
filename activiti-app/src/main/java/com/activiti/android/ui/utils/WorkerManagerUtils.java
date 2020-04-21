package com.activiti.android.ui.utils;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.activiti.android.ui.fragments.task.form.SaveFormWorker;
import com.activiti.client.api.model.runtime.SaveFormRepresentation;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

/**
 * Created by Bogdan Roatis on 5/3/2019.
 */
public class WorkerManagerUtils {

    public static final String FORM_SAVE_TASK_ID = "taskId";
    public static final String FORM_SAVE_ACCOUNT_ID = "accountId";
    public static final String FORM_SAVE_USERNAME = "username";
    public static final String FORM_SAVE_REP = "rep";
    private static final long FORM_INITIAL_RETRY_DELAY = 1000L;

    /**
     * Creates and enqueues a worker for saving the values of a form.
     * This worker should be used when the user doesn't have a network connection. When the user
     * has a network connection the worker will then attempt to make an API call to save the values.
     *
     * @param taskId   The id of the task that contains the form
     * @param accountId The internal account id used for getting the associated account.
     * @param username The username of the current account making the API call.
     * @param saveFormRepresentation The form values representation object
     */
    public static void startFormSaverWorker(String taskId,
                                            Long accountId,
                                            String username,
                                            SaveFormRepresentation saveFormRepresentation) {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data dataInput = new Data.Builder()
                .putString(FORM_SAVE_TASK_ID, taskId)
                .putLong(FORM_SAVE_ACCOUNT_ID, accountId)
                .putString(FORM_SAVE_USERNAME, username)
                .putString(FORM_SAVE_REP, new Gson().toJson(saveFormRepresentation))
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SaveFormWorker.class)
                .setConstraints(constraints)
                .setInputData(dataInput)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        FORM_INITIAL_RETRY_DELAY,
                        TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance().enqueueUniqueWork(
                "saveForm: " + taskId,
                ExistingWorkPolicy.REPLACE,
                workRequest);
    }
}
