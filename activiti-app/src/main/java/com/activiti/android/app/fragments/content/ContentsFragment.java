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

package com.activiti.android.app.fragments.content;

import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.provider.transfer.ContentTransferEvent;
import com.activiti.android.platform.provider.transfer.ContentTransferManager;
import com.activiti.android.platform.provider.transfer.ContentTransferSyncAdapter;
import com.activiti.android.platform.provider.transfer.DownloadTransferEvent;
import com.activiti.android.platform.provider.transfer.DownloadTransferUriEvent;
import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;
import com.activiti.android.ui.fragments.content.ContentAdapter;
import com.activiti.android.ui.fragments.content.ContentsFoundationFragment;
import com.activiti.android.ui.utils.IntentUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.otto.Subscribe;

public class ContentsFragment extends ContentsFoundationFragment
{
    public static final String TAG = ContentsFragment.class.getName();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ContentsFragment()
    {
        super();
        eventBusRequired = true;
    }

    public static ContentsFragment newInstanceByTemplate(Bundle b)
    {
        ContentsFragment cbf = new ContentsFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart()
    {
        super.onStart();
        getRootView().setBackgroundColor(getResources().getColor(R.color.primary_background));
        UIUtils.displayActionBarBack((MainActivity) getActivity(), getToolbar());
    }

    @Override
    public void onStop()
    {
        super.onStop();
        UIUtils.setActionBarDefault((MainActivity) getActivity(), getToolbar());
    }

    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        RelatedContentRepresentation rep = (RelatedContentRepresentation) l.getAdapter().getItem(position);

        ContentTransferManager.downloadTransfer(getActivity(), rep.getName(), rep.getMimeType(),
                Long.toString(rep.getId()));

        waitingDialog = new MaterialDialog.Builder(getActivity()).title(R.string.please_wait)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).content(R.string.content_message_content_pending).progress(true, 0).show();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onContentTransfer(ContentTransferEvent event)
    {
        if (event.hasException)
        {
            Snackbar.make(getActivity().findViewById(R.id.left_panel), event.exception.getMessage(),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (adapter != null)
        {
            Snackbar.make(getActivity().findViewById(R.id.left_panel),
                    String.format(getString(R.string.task_alert_related_content_added), event.response.getName()),
                    Snackbar.LENGTH_SHORT).show();

            ((ContentAdapter) adapter).add(event.response);
            refresh();
        }
    }

    @Subscribe
    public void onDownloadTransferUriEvent(DownloadTransferUriEvent event)
    {
        Snackbar.make(getActivity().findViewById(R.id.left_panel), R.string.document_saved, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Subscribe
    public void onDownloadTransferEvent(DownloadTransferEvent event)
    {
        if (event.hasException)
        {
            Snackbar.make(getActivity().findViewById(R.id.left_panel), event.exception.getMessage(),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (waitingDialog != null)
        {
            waitingDialog.dismiss();
        }

        try
        {
            switch (event.mode)
            {
                case ContentTransferSyncAdapter.MODE_SHARE:
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, event.data.getName());
                    sendIntent.putExtra(Intent.EXTRA_STREAM, IntentUtils.exposeFile(event.data, sendIntent, getContext()));
                    sendIntent.setType(event.mimetype);
                    getActivity().startActivity(
                            Intent.createChooser(sendIntent, getResources().getText(R.string.action_send_file)));
                    break;
                case ContentTransferSyncAdapter.MODE_OPEN_IN:
                    Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                    viewIntent.putExtra(Intent.EXTRA_SUBJECT, event.data.getName());
                    viewIntent.setDataAndType(IntentUtils.exposeFile(event.data, viewIntent, getContext()), event.mimetype);
                    startActivity(viewIntent);
                    break;
            }
        }
        catch (ActivityNotFoundException e)
        {
            Snackbar.make(getActivity().findViewById(R.id.left_panel), R.string.file_editor_error_open,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends ListingFragmentBuilder
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

        public Builder taskId(String taskId)
        {
            extraConfiguration.putString(ARGUMENT_TASK_ID, taskId);
            return this;
        }

        public Builder processId(String processId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_ID, processId);
            return this;
        }

        public Builder readOnly(boolean readOnly)
        {
            extraConfiguration.putBoolean(ARGUMENT_READONLY, readOnly);
            return this;
        }

        public Builder title(String title)
        {
            extraConfiguration.putString(ARGUMENT_TITLE, title);
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
