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

package com.activiti.android.ui.fragments.process.filter;

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
import com.activiti.android.sdk.model.ProcessSorting;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.client.api.constant.RequestConstant;
import com.afollestad.materialdialogs.MaterialDialog;

public class ProcessSortingDialogFragment extends DialogFragment
{
    public static final String ARGUMENT_SORT = "sort";

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessSortingDialogFragment()
    {
        super();
    }

    public static ProcessSortingDialogFragment newInstanceByTemplate(Bundle b)
    {
        ProcessSortingDialogFragment cbf = new ProcessSortingDialogFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // HELPER
    // ///////////////////////////////////////////////////////////////////////////
    public static String getSortTitle(Context context, String sortValue)
    {
        String value = context.getString(R.string.process_filter_created_desc);
        try
        {
            switch (ProcessSorting.fromValue(sortValue))
            {
                case CREATED_DESC:
                    value = context.getString(R.string.process_filter_created_desc);
                    break;
                case CREATED_ASC:
                    value = context.getString(R.string.process_filter_created_asc);
                    break;
            }
        }
        catch (IllegalArgumentException e)
        {
            // Do nothing
        }
        return value;
    }

    public static String getSortValue(int which)
    {
        String value = RequestConstant.SORT_CREATED_DESC;
        switch (which)
        {
            case 0:
                value = RequestConstant.SORT_CREATED_DESC;
                break;
            case 1:
                value = RequestConstant.SORT_CREATED_ASC;
                break;
        }
        return value;
    }

    public static int getSortIndex(String sortValue)
    {
        int value = 0;
        try
        {
            switch (ProcessSorting.fromValue(sortValue))
            {
                case CREATED_DESC:
                    value = 0;
                    break;
                case CREATED_ASC:
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
        String sort = RequestConstant.SORT_CREATED_DESC;
        if (getArguments() != null)
        {
            sort = getArguments().getString(ARGUMENT_SORT);
        }

        final ProcessFiltersFragment frag = (ProcessFiltersFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(DisplayUtils.hasCentralPane(getActivity()) ? R.id.central_left_drawer : R.id.right_drawer);

        return new MaterialDialog.Builder(getActivity()).title(R.string.task_filter_sort)
                .items(R.array.process_filter_sorting).cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).itemsCallbackSingleChoice(getSortIndex(sort), new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                    {
                        if (frag != null)
                        {
                            frag.onSortSelection(getSortValue(which));
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

        public Builder sort(String sort)
        {
            extraConfiguration.putString(ARGUMENT_SORT, sort);
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
