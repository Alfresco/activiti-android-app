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

package com.activiti.android.app.fragments.process;

import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.provider.transfer.ContentTransferEvent;
import com.activiti.android.platform.provider.transfer.ContentTransferSyncAdapter;
import com.activiti.android.platform.provider.transfer.DownloadTransferEvent;
import com.activiti.android.platform.provider.transfer.DownloadTransferUriEvent;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.builder.LeafFragmentBuilder;
import com.activiti.android.ui.fragments.comment.FragmentWithComments;
import com.activiti.android.ui.fragments.process.ProcessDetailsFoundationFragment;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.IntentUtils;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.otto.Subscribe;

/**
 * Created by jpascal on 07/03/2015.
 */
public class ProcessDetailsFragment extends ProcessDetailsFoundationFragment implements FragmentWithComments
{
    public static final String TAG = ProcessDetailsFragment.class.getName();

    protected Menu menu;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessDetailsFragment()
    {
        super();
        eventBusRequired = true;
        setHasOptionsMenu(true);
    }

    public static ProcessDetailsFragment newInstanceByTemplate(Bundle b)
    {
        ProcessDetailsFragment cbf = new ProcessDetailsFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart()
    {
        Fragment fr = getFragmentManager().findFragmentById(R.id.right_drawer);
        if (fr == null || (fr != null && !(fr.equals(commentFragment))))
        {
            if (fr != null)
            {
                FragmentDisplayer.with(getActivity()).back(false).animate(null).remove(fr);
            }
        }
        setLockRightMenu(false);

        super.onStart();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        if (processInstanceRepresentation != null)
        {
            if (!DisplayUtils.hasCentralPane(getActivity()))
            {
                menu.clear();
                inflater.inflate(R.menu.process_details, menu);
            }
            else
            {
                getToolbar().getMenu().clear();
                getToolbar().inflateMenu(R.menu.process_details);
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
            this.menu = menu;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.display_comments:
                if (getActivity() instanceof MainActivity)
                {
                    ((MainActivity) getActivity()).setRightMenuVisibility(!((MainActivity) getActivity())
                            .isRightMenuVisible());
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    // ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onDownloadTransferUriEvent(DownloadTransferUriEvent event)
    {
        Snackbar.make(getActivity().findViewById(R.id.left_panel), R.string.document_saved, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Subscribe
    public void onContentTransfer(ContentTransferEvent event)
    {
        super.onContentTransfer(event);
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
    // INTERFACE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void hasComment(boolean hascomment)
    {
        if (menu != null && hascomment)
        {
            menu.findItem(R.id.display_comments).setIcon(R.drawable.ic_comment);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends LeafFragmentBuilder
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

        public Builder processId(String processId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_ID, processId);
            return this;
        }

        public Builder appId(Long appId)
        {
            extraConfiguration.putLong(ARGUMENT_APP_ID, appId);
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
