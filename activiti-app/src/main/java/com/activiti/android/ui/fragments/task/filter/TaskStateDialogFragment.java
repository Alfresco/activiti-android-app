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

package com.activiti.android.ui.fragments.task.filter;

import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.sdk.model.TaskState;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.client.api.constant.RequestConstant;
import com.afollestad.materialdialogs.MaterialDialog;

public class TaskStateDialogFragment extends DialogFragment
{
    public static final String ARGUMENT_STATE = "sort";

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TaskStateDialogFragment()
    {
        super();
    }

    public static TaskStateDialogFragment newInstanceByTemplate(Bundle b)
    {
        TaskStateDialogFragment cbf = new TaskStateDialogFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // HELPER
    // ///////////////////////////////////////////////////////////////////////////
    public static String getStateTitle(Context context, String state)
    {
        String value = context.getString(R.string.task_filter_state_open);
        try
        {
            switch (TaskState.fromValue(state))
            {
                case OPEN:
                    value = context.getString(R.string.task_filter_state_open);
                    break;
                case COMPLETED:
                    value = context.getString(R.string.task_filter_state_completed);
                    break;
            }
        }
        catch (IllegalArgumentException e)
        {
            // Do nothing
        }
        return value;
    }

    public static String getStateValue(int which)
    {
        String value = RequestConstant.STATE_OPEN;
        switch (which)
        {
            case 0:
                value = RequestConstant.STATE_OPEN;
                break;
            case 1:
                value = RequestConstant.STATE_COMPLETED;
                break;
        }
        return value;
    }

    public static int getStateIndex(String state)
    {
        int value = 0;
        try
        {
            switch (TaskState.fromValue(state))
            {
                case OPEN:
                    value = 0;
                    break;
                case COMPLETED:
                    value = 1;
                    break;
            }
        }
        catch (IllegalArgumentException e)
        {
            // Do nothing
        }
        return value;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String state = RequestConstant.STATE_OPEN;
        if (getArguments() != null)
        {
            state = getArguments().getString(ARGUMENT_STATE);
        }

        final CommonTaskFilterFragment frag = (CommonTaskFilterFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(
                        DisplayUtils.hasCentralPane(getActivity()) ? R.id.central_left_drawer : R.id.right_drawer);

        return new MaterialDialog.Builder(getActivity()).title(R.string.task_filter_text)
                .items(R.array.task_filter_state).cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).itemsCallbackSingleChoice(getStateIndex(state), new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                    {
                        if (frag != null)
                        {
                            frag.onStateSelection(getStateValue(which));
                            dismiss();
                            return true;
                        }
                        return false;
                    }
                }).show();
    }

    public static class Builder extends AlfrescoFragmentBuilder
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

        public Builder state(String state)
        {
            extraConfiguration.putString(ARGUMENT_STATE, state);
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
