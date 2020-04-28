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

package com.activiti.android.platform.provider.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.event.ProfilePictureEvent;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.storage.AlfrescoStorageManager;
import com.activiti.android.platform.storage.IOUtils;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.sdk.services.ServiceRegistry;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;

public class ContentTransferSyncAdapter extends AbstractThreadedSyncAdapter
{

    // ////////////////////////////////////////////////////
    // SYNC MODE
    // ////////////////////////////////////////////////////
    public static final String ARGUMENT_MODE = "syncMode";

    public static final int MODE_OPEN_IN = 1;

    public static final int MODE_SAVE_AS = 2;

    public static final int MODE_SHARE = 4;

    public static final int MODE_SAF_UPLOAD = 8;

    public static final String ARGUMENT_TASK_ID = "task";

    public static final String ARGUMENT_PROCESS_ID = "processId";

    public static final String ARGUMENT_PROFILE_ID = "profileId";

    public static final String ARGUMENT_CONTENT_ID = "contentId";

    public static final String ARGUMENT_CONTENT_PATH = "contentPath";

    public static final String ARGUMENT_CONTENT_URI = "contentUri";

    public static final String ARGUMENT_MIMETYPE = "mimetype";

    public static final String ARGUMENT_FILE_PATH = "filePath";

    private final AccountManager mAccountManager;

    private ServiceRegistry api;

    private int mode;

    private String taskId, processId, profileId, mimetype, filePath, contentUri, contentId, contentPath;

    protected AlfrescoStorageManager storageManager;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public ContentTransferSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        storageManager = AlfrescoStorageManager.getInstance(context);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult)
    {
        Log.d("Activiti", "onPerformSync ContentTransfert for account[" + account.name + "]");

        // Retrieve ActivitiAccount
        long accountId = Long.parseLong(mAccountManager.getUserData(account, ActivitiAccount.ACCOUNT_ID));

        // Retrieve Applications from Server
        ActivitiSession session = ActivitiSession.with(String.valueOf(accountId));
        if (session != null)
        {
            api = session.getServiceRegistry();
        }
        else
        {
            return;
        }

        try
        {
            // Retrieve extra informations
            if (extras != null)
            {
                mode = BundleUtils.getInt(extras, ARGUMENT_MODE);
                taskId = BundleUtils.getString(extras, ARGUMENT_TASK_ID);
                processId = BundleUtils.getString(extras, ARGUMENT_PROCESS_ID);
                profileId = BundleUtils.getString(extras, ARGUMENT_PROFILE_ID);
                contentId = BundleUtils.getString(extras, ARGUMENT_CONTENT_ID);
                contentUri = BundleUtils.getString(extras, ARGUMENT_CONTENT_URI);
                contentPath = BundleUtils.getString(extras, ARGUMENT_CONTENT_PATH);
                mimetype = BundleUtils.getString(extras, ARGUMENT_MIMETYPE);
                filePath = BundleUtils.getString(extras, ARGUMENT_FILE_PATH);
            }

            switch (mode)
            {
                case MODE_SAVE_AS:
                {
                    Response<ResponseBody> dlResponse = api.getContentService().download(contentId);

                    ParcelFileDescriptor pfd = getContext().getContentResolver()
                            .openFileDescriptor(Uri.parse(contentUri), "w");
                    FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                    IOUtils.saveBytesToStream(IOUtils.getBytesFromStream(dlResponse.body().byteStream()),
                            fileOutputStream);

                    // Analytics
                    AnalyticsHelper.reportOperationEvent(getContext(), AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                            AnalyticsManager.ACTION_DOWNLOAD, mimetype, 1, false);

                    // SHARE IT
                    EventBusManager.getInstance()
                            .post(new DownloadTransferUriEvent("-1", mode, Uri.parse(contentUri), mimetype));

                    break;
                }
                case MODE_SHARE:
                {
                    File lFile = new File(filePath);

                    Response<ResponseBody> response = api.getContentService().download(contentId);
                    if (response.isSuccessful())
                    {
                        IOUtils.saveBytesToFile(IOUtils.getBytesFromStream(response.body().byteStream()),
                                lFile.getPath());
                    }

                    // Analytics
                    AnalyticsHelper.reportOperationEvent(getContext(), AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                            AnalyticsManager.ACTION_SHARE, mimetype, 1, false);

                    // SHARE IT
                    EventBusManager.getInstance().post(new DownloadTransferEvent("-1", mode, lFile, mimetype));
                    break;
                }
                case MODE_OPEN_IN:
                {
                    File dlFile = new File(storageManager.getTempFolder(accountId), filePath);

                    Response<ResponseBody> resp = api.getContentService().download(contentId);
                    IOUtils.saveBytesToFile(IOUtils.getBytesFromStream(resp.body().byteStream()), dlFile.getPath());

                    // Analytics
                    AnalyticsHelper.reportOperationEvent(getContext(), AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                            AnalyticsManager.ACTION_OPEN, mimetype, 1, false);

                    // OPEN IT
                    EventBusManager.getInstance().post(new DownloadTransferEvent("-1", mode, dlFile, mimetype));
                    break;
                }
                case MODE_SAF_UPLOAD:
                {
                    File tempFile = new File(storageManager.getTempFolder(accountId), filePath);

                    InputStream inputStream = null;
                    if (!TextUtils.isEmpty(contentUri))
                    {
                        inputStream = getContext().getContentResolver().openInputStream(Uri.parse(contentUri));
                    }
                    else if (!TextUtils.isEmpty(contentPath))
                    {
                        inputStream = new FileInputStream(new File(contentPath));
                    }

                    // Retrieve content data
                    IOUtils.copyFile(inputStream, tempFile);

                    // Upload it
                    RelatedContentRepresentation content = null;
                    if (!TextUtils.isEmpty(taskId))
                    {
                        RequestBody requestBody = RequestBody.create(MediaType.parse(mimetype), tempFile);
                        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                        multipartBuilder.addFormDataPart("file", tempFile.getName(), requestBody);
                        Response<RelatedContentRepresentation> response =
                                api.getContentService().createRelatedContentOnTask(taskId, multipartBuilder.build());

                        if (response.isSuccessful())
                        {
                            content = response.body();
                            EventBusManager.getInstance().post(new ContentTransferEvent("-1", mode, content));
                        }
                        else
                        {
                            EventBusManager.getInstance().post(new ContentTransferEvent("-1", contentUri, new Exception(response.message())));
                        }

                        // Analytics
                        AnalyticsHelper.reportOperationEvent(getContext(),
                                AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT, AnalyticsManager.ACTION_ADD_CONTENT,
                                content != null ? content.getMimeType() : "", 1, content == null);
                    }
                    else if (!TextUtils.isEmpty(processId))
                    {
                        RequestBody requestBody = RequestBody.create(MediaType.parse(mimetype), tempFile);
                        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                        multipartBuilder.addFormDataPart("file", tempFile.getName(), requestBody);
                        Response<RelatedContentRepresentation> response =
                                api.getContentService().createRelatedContentOnProcessInstance(processId, multipartBuilder.build());

                        if (response.isSuccessful())
                        {
                            content = response.body();
                            EventBusManager.getInstance().post(new ContentTransferEvent("-1", mode, content));
                        }
                        else
                        {
                            EventBusManager.getInstance().post(new ContentTransferEvent("-1", contentUri, new Exception(response.message())));
                        }

                        // Analytics
                        AnalyticsHelper.reportOperationEvent(getContext(),
                                AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT, AnalyticsManager.ACTION_ADD_CONTENT,
                                content != null ? content.getMimeType() : "", 1, content == null);
                    }
                    else if (!TextUtils.isEmpty(profileId))
                    {
                        RequestBody requestBody = RequestBody.create(MediaType.parse(mimetype), tempFile);
                        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                        multipartBuilder.addFormDataPart("file", tempFile.getName(), requestBody);
                        api.getProfileService().updateProfilePicture(multipartBuilder.build());
                        EventBusManager.getInstance().post(new ProfilePictureEvent());
                    }
                    else
                    {
                        RequestBody requestBody = RequestBody.create(MediaType.parse(mimetype), tempFile);
                        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                        multipartBuilder.addFormDataPart("file", tempFile.getName(), requestBody);
                        Response<RelatedContentRepresentation> response =
                                api.getContentService().createTemporaryRawRelatedContent(multipartBuilder.build());
                        if (response.isSuccessful())
                        {
                            content = response.body();
                            EventBusManager.getInstance().post(new ContentTransferEvent("-1", mode, content));
                        }
                        else
                        {
                            EventBusManager.getInstance().post(new ContentTransferEvent("-1", contentUri, new Exception(response.message())));
                        }
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            Log.e("Activiti", Log.getStackTraceString(e));
            EventBusManager.getInstance().post(new ContentTransferEvent("-1", e));
        }
    }
}
