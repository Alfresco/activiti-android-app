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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.activiti.android.app.R;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.Manager;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.integration.alfresco.AlfrescoIntegrator;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.provider.CursorUtils;
import com.activiti.android.platform.provider.integration.Integration;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.mimetype.MimeType;
import com.activiti.android.platform.provider.mimetype.MimeTypeManager;
import com.activiti.android.platform.utils.AndroidVersion;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.request.AddContentRelatedRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Jean Marie Pascal
 */
public class ContentTransferManager extends Manager
{
    protected static final Object LOCK = new Object();

    public static final int PICKER_REQUEST_CODE = 99;

    public static final int CREATE_REQUEST_CODE = 97;

    public static final int TYPE_TASK_ID = 0;

    public static final int TYPE_PROCESS_ID = 1;

    public static final int TYPE_PROFILE_ID = 2;

    public static final int TYPE_LINK_ID = 4;

    protected static Manager mInstance;

    protected static boolean allowLink = false;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    protected ContentTransferManager(Context applicationContext)
    {
        super(applicationContext);
    }

    public static ContentTransferManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new ContentTransferManager(context);
            }
            return (ContentTransferManager) mInstance;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SYNC
    // ///////////////////////////////////////////////////////////////////////////
    public static void downloadTransfer(Context activity, String name, String mimetype, String contentId)
    {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putInt(ContentTransferSyncAdapter.ARGUMENT_MODE, ContentTransferSyncAdapter.MODE_OPEN_IN);
        settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_FILE_PATH, name);
        settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_CONTENT_ID, contentId);
        settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_MIMETYPE, mimetype);
        ContentResolver.requestSync(ActivitiAccountManager.getInstance(activity).getCurrentAndroidAccount(),
                ContentTransferProvider.AUTHORITY, settingsBundle);
    }

    public static void startSaveAsTransfer(Activity activity, String contentId, String contentUri)
    {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putInt(ContentTransferSyncAdapter.ARGUMENT_MODE, ContentTransferSyncAdapter.MODE_SAVE_AS);
        settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_CONTENT_URI, contentUri);
        settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_CONTENT_ID, contentId);
        ContentResolver.requestSync(ActivitiAccountManager.getInstance(activity).getCurrentAndroidAccount(),
                ContentTransferProvider.AUTHORITY, settingsBundle);
    }

    public static void startShare(Activity activity, String contentId, String filepath, String mimetype)
    {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putInt(ContentTransferSyncAdapter.ARGUMENT_MODE, ContentTransferSyncAdapter.MODE_SHARE);
        settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_FILE_PATH, filepath);
        settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_CONTENT_ID, contentId);
        settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_MIMETYPE, mimetype);
        ContentResolver.requestSync(ActivitiAccountManager.getInstance(activity).getCurrentAndroidAccount(),
                ContentTransferProvider.AUTHORITY, settingsBundle);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATIC
    // ///////////////////////////////////////////////////////////////////////////
    public static final void requestGetContentFromProvider(Fragment fr)
    {
        String tmpMimetype = "*/*";
        if (AndroidVersion.isKitKatOrAbove())
        {
            isMediaProviderSupported(fr.getActivity());

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(tmpMimetype);
            fr.startActivityForResult(intent, PICKER_REQUEST_CODE);
        }
    }

    public static final void requestGetContent(Fragment fr)
    {
        requestGetContent(fr, null);
    }

    public static final void requestGetContent(Fragment fr, String mimetype)
    {
        String tmpMimetype = mimetype;
        if (TextUtils.isEmpty(mimetype))
        {
            tmpMimetype = "*/*";
        }

        if (AndroidVersion.isKitKatOrAbove())
        {
            isMediaProviderSupported(fr.getActivity());

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(tmpMimetype);
            fr.startActivityForResult(Intent.createChooser(intent, "chooser"), PICKER_REQUEST_CODE);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(tmpMimetype);
            fr.startActivityForResult(intent, PICKER_REQUEST_CODE);
        }
    }

    private static void isMediaProviderSupported(Context context)
    {
        final PackageManager pm = context.getPackageManager();
        // Pick up provider with action string
        final Intent i = new Intent(DocumentsContract.PROVIDER_INTERFACE);
        final List<ResolveInfo> providers = pm.queryIntentContentProviders(i, 0);
        for (ResolveInfo info : providers)
        {
            if (info != null && info.providerInfo != null)
            {
                final String authority = info.providerInfo.authority;
                isMediaDocumentProvider(Uri.parse("content://" + authority));
            }
        }
    }

    private static boolean isMediaDocumentProvider(final Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static final void requestCreateLocalContent(Fragment fr, String filename, String mimetype, File file)
    {

        if (AndroidVersion.isKitKatOrAbove())
        {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mimetype);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.putExtra(Intent.EXTRA_TITLE, filename);
            fr.startActivityForResult(intent, CREATE_REQUEST_CODE);
        }
        else
        {
            // Good question ? Send Intent ? File Picker ?
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATIC
    // ///////////////////////////////////////////////////////////////////////////
    public static void upload()
    {

    }

    public static final AddContentRelatedRepresentation prepareTransfer(Intent resultData, AlfrescoFragment fr,
            String id, int type)
    {
        Uri uri = null;
        if (resultData != null)
        {
            uri = resultData.getData();
            Log.i("TAG", "uri: " + uri);

            // Detect if it comes from Alfresco ?
            if (AlfrescoIntegrator.STORAGE_ACCESS_PROVIDER.equals(uri.getAuthority()))
            {
                // It's alfresco !
                // Let's see if it's possible to link...
                AddContentRelatedRepresentation content = prepareLink(fr, uri);
                if (content == null)
                {
                    // Link is impossible we upload the document
                    requestUpload(fr, uri, id, type, resultData.getType());
                }
                else
                {
                    // Link is possible.
                    // Let's request the user on how to handle this
                    uploadAs(fr, uri, content, id, type, resultData.getType());
                }
            }
            else
            {
                // It's something else
                requestUpload(fr, uri, id, type, resultData.getType());
            }
        }
        return null;
    }

    private static void uploadAs(final AlfrescoFragment fr, final Uri uri,
            final AddContentRelatedRepresentation content, final String id, final int type, final String mimetype)
    {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(fr.getActivity()).content(R.string.upload_as_text)
                .positiveText(R.string.upload_as_link).negativeText(R.string.upload_as_file)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dialog.dismiss();
                    }
                }).callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        ContentTransferManager.requestAlfrescoLink(content, fr, id, type);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog)
                    {
                        ContentTransferManager.requestUpload(fr, uri, id, type, mimetype);
                    }
                });
        builder.show();
    }

    public static void requestAlfrescoLink(AddContentRelatedRepresentation content, final AlfrescoFragment fr,
            String id, int type)
    {
        switch (type)
        {
            case TYPE_TASK_ID:
                fr.getAPI().getTaskService().linkAttachment(id, content, new Callback<RelatedContentRepresentation>()
                {
                    @Override
                    public void onResponse(Call<RelatedContentRepresentation> call,
                            Response<RelatedContentRepresentation> response)
                    {
                        // Analytics
                        AnalyticsHelper.reportOperationEvent(fr.getActivity(),
                                AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT, AnalyticsManager.ACTION_LINK_CONTENT,
                                response.isSuccessful() ? response.body().getMimeType() : "", 1,
                                !response.isSuccessful());

                        EventBusManager.getInstance().post(new ContentTransferEvent("-1",
                                ContentTransferSyncAdapter.MODE_SAF_UPLOAD, response.body()));
                    }

                    @Override
                    public void onFailure(Call<RelatedContentRepresentation> call, Throwable error)
                    {
                        if (fr != null)
                        {
                            Snackbar.make(fr.getActivity().findViewById(R.id.left_panel), error.getMessage(),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case TYPE_PROCESS_ID:
                fr.getAPI().getProcessService().linkAttachment(id, content, new Callback<RelatedContentRepresentation>()
                {
                    @Override
                    public void onResponse(Call<RelatedContentRepresentation> call,
                            Response<RelatedContentRepresentation> response)
                    {
                        // Analytics
                        AnalyticsHelper.reportOperationEvent(fr.getActivity(),
                                AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT, AnalyticsManager.ACTION_LINK_CONTENT,
                                response.isSuccessful() ? response.body().getMimeType() : "", 1,
                                !response.isSuccessful());

                        EventBusManager.getInstance().post(new ContentTransferEvent("-1",
                                ContentTransferSyncAdapter.MODE_SAF_UPLOAD, response.body()));
                    }

                    @Override
                    public void onFailure(Call<RelatedContentRepresentation> call, Throwable error)
                    {
                        if (fr != null)
                        {
                            Snackbar.make(fr.getActivity().findViewById(R.id.left_panel), error.getMessage(),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case TYPE_LINK_ID:
                fr.getAPI().getContentService().createTemporaryRelatedContent(content,
                        new Callback<RelatedContentRepresentation>()
                        {
                            @Override
                            public void onResponse(Call<RelatedContentRepresentation> call,
                                    Response<RelatedContentRepresentation> response)
                            {
                                EventBusManager.getInstance().post(new ContentTransferEvent("-1",
                                        ContentTransferSyncAdapter.MODE_SAF_UPLOAD, response.body()));
                            }

                            @Override
                            public void onFailure(Call<RelatedContentRepresentation> call, Throwable error)
                            {
                                if (fr != null)
                                {
                                    Snackbar.make(fr.getActivity().findViewById(R.id.left_panel), error.getMessage(),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            default:
                fr.getAPI().getContentService().createTemporaryRawRelatedContent(content,
                        new Callback<RelatedContentRepresentation>()
                        {
                            @Override
                            public void onResponse(Call<RelatedContentRepresentation> call,
                                    Response<RelatedContentRepresentation> response)
                            {
                                EventBusManager.getInstance().post(new ContentTransferEvent("-1",
                                        ContentTransferSyncAdapter.MODE_SAF_UPLOAD, response.body()));
                            }

                            @Override
                            public void onFailure(Call<RelatedContentRepresentation> call, Throwable error)
                            {
                                EventBusManager.getInstance()
                                        .post(new ContentTransferEvent("-1", new Exception(error.getMessage())));
                                Log.d("ContentTransferManager", Log.getStackTraceString(error.getCause()));
                            }
                        });
                break;
        }
    }

    public static void requestUpload(Fragment fr, Uri uri, String objectId, int type, String mimetyp)
    {
        Cursor cursor = null;
        String name = null, source, sourceId, mimetype = mimetyp;
        try
        {
            Bundle settingsBundle = new Bundle();

            // Retrieve other info
            cursor = fr.getActivity().getContentResolver().query(uri, null, null, null, null);

            if (cursor == null && uri != null)
            {
                // Is it a file ?
                File file = new File(uri.getPath());
                if (file.exists())
                {
                    // It's a file
                    name = file.getName();
                    settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_CONTENT_PATH, file.getPath());
                    if (mimetype == null)
                    {
                        MimeType mime = MimeTypeManager.getInstance(fr.getActivity()).getMimetype(file.getName());
                        mimetype = (mime != null) ? mime.getMimeType() : mimetyp;
                    }
                }
            }
            else if (cursor != null && cursor.moveToFirst())
            {
                name = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
                mimetype = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE));
                settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_CONTENT_URI, uri.toString());
            }
            else
            {
                throw new Exception("Cursor is empty");
            }

            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

            settingsBundle.putInt(ContentTransferSyncAdapter.ARGUMENT_MODE, ContentTransferSyncAdapter.MODE_SAF_UPLOAD);
            settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_FILE_PATH, name);
            if (type == TYPE_TASK_ID)
            {
                settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_TASK_ID, objectId);
            }
            else if (type == TYPE_PROCESS_ID)
            {
                settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_PROCESS_ID, objectId);
            }
            else if (type == TYPE_PROFILE_ID)
            {
                settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_PROFILE_ID, objectId);
            }
            settingsBundle.putString(ContentTransferSyncAdapter.ARGUMENT_MIMETYPE, mimetype);
            ContentResolver.requestSync(ActivitiAccountManager.getInstance(fr.getActivity()).getCurrentAndroidAccount(),
                    ContentTransferProvider.AUTHORITY, settingsBundle);
        }
        catch (Exception e)
        {
            Log.w("COntent Transfer", Log.getStackTraceString(e));
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
    }

    public static AddContentRelatedRepresentation prepareLink(Fragment fr, Uri uri)
    {
        Cursor cursor = null;
        String name = null, source, sourceId, mimetype;
        String nodeId, type, alfAccountId;
        try
        {
            // Retrieve Document information
            cursor = fr.getActivity().getApplicationContext().getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst())
            {
                type = cursor.getString(cursor.getColumnIndex("alf_type"));
                alfAccountId = cursor.getString(cursor.getColumnIndex("alf_account_id"));
                name = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
                mimetype = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE));
            }
            else
            {
                throw new Exception("Cursor is empty");
            }

            ActivitiAccount acc = ActivitiAccountManager.getInstance(fr.getActivity()).getCurrentAccount();
            Integration integration = IntegrationManager.getInstance(fr.getActivity())
                    .getByAlfrescoId(Long.parseLong(alfAccountId), acc.getId());

            // Retrieve NodeId
            String pathSegment = uri.getLastPathSegment();
            nodeId = pathSegment.split("&")[1].substring(3);
            // nodeId =
            // NodeRefUtils.getCleanIdentifier(uri.getLastPathSegment().split("&")[1].substring(3));

            // Be aware this is an hack !
            // Ideally we should populate with site Id.
            // In this case all link are absolute from root.
            sourceId = nodeId.concat("@A");
            source = "alfresco-" + integration.getId();

            return new AddContentRelatedRepresentation(name, true, source, sourceId, mimetype);
        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
    }

    public static RelatedContentRepresentation getRelatedContent(Context context, Uri uri)
    {
        RelatedContentRepresentation content = null;
        Cursor cursor = null;
        String name = null, source, sourceId, mimetype;
        try
        {
            // Retrieve Document information
            cursor = context.getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            name = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
            mimetype = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE));

            content = RelatedContentRepresentation.parse(uri.toString(), name, mimetype);

        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            CursorUtils.closeCursor(cursor);
        }
        return content;
    }

}
