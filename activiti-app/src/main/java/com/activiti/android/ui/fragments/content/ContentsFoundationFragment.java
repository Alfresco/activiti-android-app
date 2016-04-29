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

package com.activiti.android.ui.fragments.content;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;

import com.activiti.android.app.R;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.provider.transfer.ContentTransferManager;
import com.activiti.android.platform.storage.AlfrescoStorageManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.request.AddContentRelatedRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;

public class ContentsFoundationFragment extends BasePagingGridFragment implements RequestConstant
{
    public static final String TAG = ContentsFoundationFragment.class.getName();

    protected static final String ARGUMENT_READONLY = "isReadOnly";

    protected static final String ARGUMENT_TITLE = "titleId";

    protected static final String ARGUMENT_TASK_ID = RequestConstant.ARGUMENT_TASK_ID;

    protected static final String ARGUMENT_PROCESS_ID = RequestConstant.ARGUMENT_PROCESS_ID;

    protected String taskId, titleValue;

    protected String processId;

    protected Boolean isReadOnly = false;

    protected RelatedContentRepresentation selectedContent;

    protected MenuItem switchViewItem;

    protected MaterialDialog waitingDialog;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ContentsFoundationFragment()
    {
        emptyListMessageId = R.string.empty_content;
        retrieveDataOnCreation = true;
        enableTitle = true;
        setHasOptionsMenu(true);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    protected void onRetrieveParameters(Bundle bundle)
    {
        taskId = BundleUtils.getString(bundle, ARGUMENT_TASK_ID);
        processId = BundleUtils.getString(bundle, ARGUMENT_PROCESS_ID);
        isReadOnly = BundleUtils.getBoolean(bundle, ARGUMENT_READONLY);
        titleValue = BundleUtils.getString(bundle, ARGUMENT_TITLE);
    }

    @Override
    protected String onCreateTitle(String titlef)
    {
        if (titleValue == null)
        {
            return getString(R.string.task_section_content);
        }
        else
        {
            mSubTitle = getString(R.string.task_section_content);
            return titleValue;
        }
    }

    protected Callback<ResultList<RelatedContentRepresentation>> callBack = new Callback<ResultList<RelatedContentRepresentation>>()
    {
        @Override
        public void onResponse(Call<ResultList<RelatedContentRepresentation>> call,
                Response<ResultList<RelatedContentRepresentation>> response)
        {
            if (!response.isSuccessful())
            {
                onFailure(call, new Exception(response.message()));
                return;
            }
            displayData(response.body());
        }

        @Override
        public void onFailure(Call<ResultList<RelatedContentRepresentation>> call, Throwable error)
        {
            displayError(error);
        }
    };

    @Override
    protected void performRequest()
    {
        if (!TextUtils.isEmpty(taskId))
        {
            getAPI().getTaskService().getAttachments(taskId, callBack);
        }
        else if (!TextUtils.isEmpty(processId))
        {
            getAPI().getProcessService().getAttachments(processId, callBack);
        }
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new ContentAdapter(this, (isListView()) ? R.layout.row_two_lines : R.layout.row_tile_single_line,
                new ArrayList<RelatedContentRepresentation>(0), isReadOnly);
    }

    @Override
    protected View.OnClickListener onPrepareFabClickListener()
    {
        if (isReadOnly) { return null; }
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ContentTransferManager.requestGetContent(ContentsFoundationFragment.this);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        if (requestCode == ContentTransferManager.PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            if (!TextUtils.isEmpty(processId))
            {
                ContentTransferManager.prepareTransfer(resultData, this, taskId,
                        ContentTransferManager.TYPE_PROCESS_ID);
            }
            else if (!TextUtils.isEmpty(taskId))
            {
                ContentTransferManager.prepareTransfer(resultData, this, taskId, ContentTransferManager.TYPE_TASK_ID);
            }
        }
        else if (requestCode == ContentTransferManager.CREATE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            ContentTransferManager.startSaveAsTransfer(getActivity(), Long.toString(selectedContent.getId()),
                    resultData.getData().toString());
        }
    }

    @Override
    protected void displayData(ResultList<?> response)
    {
        super.displayData(response);

        if (isListView())
        {
            gv.setColumnWidth(UIUtils.getDPI(getActivity(), 2000));
        }
        else
        {
            int padding4 = UIUtils.getDPI(getActivity(), 4);
            gv.setHorizontalSpacing(padding4);
            gv.setVerticalSpacing(padding4);
            gv.setColumnWidth(UIUtils.getDPI(getActivity(), 150));
            gv.setPadding(padding4, 0, padding4, 0);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    public void delete(Long contentId)
    {
        getAPI().getContentService().delete(contentId, new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if (!response.isSuccessful())
                {
                    onFailure(call, new Exception(response.message()));
                    return;
                }
                refresh();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable error)
            {
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void download(RelatedContentRepresentation content)
    {
        selectedContent = content;
        download(this, content);
    }

    public static void download(AlfrescoFragment fr, RelatedContentRepresentation content)
    {
        // Analytics
        AnalyticsHelper.reportOperationEvent(fr.getActivity(), AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                AnalyticsManager.ACTION_DOWNLOAD, content.getMimeType(), 1, false);

        ContentTransferManager.requestCreateLocalContent(fr, content.getName(), content.getMimeType(), null);
    }

    public void sendFile(RelatedContentRepresentation content)
    {
        selectedContent = content;
        waitingDialog = sendFile(this, content);
    }

    public static MaterialDialog sendFile(AlfrescoFragment fr, RelatedContentRepresentation content)
    {
        // Analytics
        AnalyticsHelper.reportOperationEvent(fr.getActivity(), AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                AnalyticsManager.ACTION_SEND, content.getMimeType(), 1, false);

        File tmpFolder = AlfrescoStorageManager.getInstance(fr.getActivity()).getTempFolder(fr.getAccount().getId());
        File dlFile = new File(tmpFolder, content.getName());
        // Download and sendFile
        ContentTransferManager.startShare(fr.getActivity(), Long.toString(content.getId()), dlFile.getPath(),
                content.getMimeType());

        return new MaterialDialog.Builder(fr.getActivity()).title(R.string.please_wait)
                .content(R.string.content_message_content_pending).progress(true, 0).show();
    }

    public static void shareLink(AlfrescoFragment fr, RelatedContentRepresentation content)
    {
        if (!TextUtils.isEmpty(content.getLinkUrl()))
        {
            // Analytics
            AnalyticsHelper.reportOperationEvent(fr.getActivity(), AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                    AnalyticsManager.ACTION_SHARE, content.getMimeType(), 1, false);

            // Send link
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, content.getLinkUrl());
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, content.getName());
            sendIntent.setType("text/plain");
            fr.startActivity(Intent.createChooser(sendIntent, fr.getResources().getString(R.string.action_share_link)));
        }
    }

    public void uploadAs(final Uri uri, final AddContentRelatedRepresentation content, final String id, final int type,
            final String mimetype)
    {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity()).content(R.string.upload_as_text)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).positiveText(R.string.upload_as_link).negativeText(R.string.upload_as_file)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        ContentTransferManager.requestAlfrescoLink(content, ContentsFoundationFragment.this, id, type);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog)
                    {
                        ContentTransferManager.requestUpload(ContentsFoundationFragment.this, uri, id, type, mimetype);
                    }
                });
        builder.show();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        Menu tmpMenu = menu;
        if (!DisplayUtils.hasCentralPane(getActivity()))
        {
            tmpMenu.clear();
        }
        else
        {
            tmpMenu = getToolbar().getMenu();
            tmpMenu.clear();
            // Set an OnMenuItemClickListener to handle menu item clicks
            getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    return onOptionsItemSelected(item);
                }
            });

        }

        switchViewItem = tmpMenu.add(0, R.id.alfresco_action, 0, R.string.list);
        switchViewItem.setIcon(R.drawable.ic_view_module_white);
        switchViewItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.alfresco_action:
                if (switchViewItem.getTitle().equals(getString(R.string.list)))
                {
                    switchViewItem.setTitle(getString(R.string.grid));
                    switchViewItem.setIcon(R.drawable.ic_view_list_white);
                    displayAsList = false;
                }
                else
                {
                    displayAsList = true;
                    switchViewItem.setTitle(getString(R.string.list));
                    switchViewItem.setIcon(R.drawable.ic_view_module_white);
                }

                // Analytics
                AnalyticsHelper.reportOperationEvent(getContext(), AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                        AnalyticsManager.ACTION_SWITCH,
                        !displayAsList ? AnalyticsManager.LABEL_AS_LIST : AnalyticsManager.LABEL_AS_GRID, 1, false);

                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isListView()
    {
        if (switchViewItem != null)
        {
            displayAsList = switchViewItem.getTitle().equals(getString(R.string.list));
        }
        return displayAsList;
    }
}
