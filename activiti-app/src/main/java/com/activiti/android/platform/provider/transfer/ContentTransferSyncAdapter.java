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

package com.activiti.android.platform.provider.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import retrofit.client.Response;
import retrofit.mime.TypedFile;

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
        Log.d("Activiti", "onPerformSync GroupInstance for account[" + account.name + "]");

        if (ActivitiSession.getInstance() != null)
        {
            api = ActivitiSession.getInstance().getServiceRegistry();
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

            // Retrieve ActivitiAccount
            long accountId = Long.parseLong(AccountManager.get(getContext()).getUserData(account,
                    ActivitiAccount.ACCOUNT_ID));

            switch (mode)
            {
                case MODE_SAVE_AS:
                    Response dlResponse = api.getContentService().download(contentId);

                    ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(
                            Uri.parse(contentUri), "w");
                    FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                    IOUtils.saveBytesToStream(IOUtils.getBytesFromStream(dlResponse.getBody().in()), fileOutputStream);

                    // SHARE IT
                    EventBusManager.getInstance().post(
                            new DownloadTransferUriEvent("-1", mode, Uri.parse(contentUri), mimetype));

                    break;
                case MODE_SHARE:
                    File lFile = new File(filePath);

                    Response response = api.getContentService().download(contentId);
                    IOUtils.saveBytesToFile(IOUtils.getBytesFromStream(response.getBody().in()), lFile.getPath());

                    // SHARE IT
                    EventBusManager.getInstance().post(new DownloadTransferEvent("-1", mode, lFile, mimetype));
                    break;
                case MODE_OPEN_IN:
                    File dlFile = new File(storageManager.getTempFolder(accountId), filePath);

                    Response resp = api.getContentService().download(contentId);
                    IOUtils.saveBytesToFile(IOUtils.getBytesFromStream(resp.getBody().in()), dlFile.getPath());

                    // OPEN IT
                    EventBusManager.getInstance().post(new DownloadTransferEvent("-1", mode, dlFile, mimetype));
                    break;
                case MODE_SAF_UPLOAD:
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
                    IOUtils.saveBytesToFile(IOUtils.getBytesFromStream(inputStream), tempFile.getPath());

                    // Upload it
                    RelatedContentRepresentation content = null;
                    if (!TextUtils.isEmpty(taskId))
                    {
                        content = api.getContentService().createRelatedContentOnTask(taskId,
                                new TypedFile(mimetype, tempFile));
                        EventBusManager.getInstance().post(new ContentTransferEvent("-1", mode, content));
                    }
                    else if (!TextUtils.isEmpty(processId))
                    {
                        content = api.getContentService().createRelatedContentOnProcessInstance(processId,
                                new TypedFile(mimetype, tempFile));
                        EventBusManager.getInstance().post(new ContentTransferEvent("-1", mode, content));
                    }
                    else if (!TextUtils.isEmpty(profileId))
                    {
                        api.getProfileService().updateProfilePicture(new TypedFile(mimetype, tempFile));
                        EventBusManager.getInstance().post(new ProfilePictureEvent());
                    }
                    else
                    {
                        content = api.getContentService().createTemporaryRawRelatedContent(
                                new TypedFile(mimetype, tempFile));
                        EventBusManager.getInstance().post(new ContentTransferEvent("-1", mode, content));
                    }
                    break;
            }
        }
        catch (Exception e)
        {
            Log.e("Activiti", Log.getStackTraceString(e));
            EventBusManager.getInstance().post(new ContentTransferEvent("-1", e));
        }
    }
}
