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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.platform.provider.group.GroupInstance;
import com.activiti.android.platform.provider.group.GroupInstanceManager;
import com.activiti.android.sdk.model.TaskAssignment;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.client.api.constant.RequestConstant;
import com.afollestad.materialdialogs.MaterialDialog;

public class TaskAssignmentDialogFragment extends AlfrescoFragment
{
    public static final String ARGUMENT_ASSIGNMENT = "assignment";

    public static final String PREFIX_GROUP = "group_";

    private TaskAssignmentAdapter adapter;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public TaskAssignmentDialogFragment()
    {
        super();
    }

    public static TaskAssignmentDialogFragment newInstanceByTemplate(Bundle b)
    {
        TaskAssignmentDialogFragment cbf = new TaskAssignmentDialogFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // HELPER
    // ///////////////////////////////////////////////////////////////////////////
    public static String getAssignmentTitle(Context context, String assignment, long accountId)
    {
        String value = context.getString(R.string.task_filter_assignment_involved);
        try
        {
            // Do nothing
            if (assignment != null && assignment.startsWith(PREFIX_GROUP))
            {
                String groupId = assignment.replace(PREFIX_GROUP, "");
                if (TextUtils.isDigitsOnly(groupId))
                {
                    GroupInstance group = GroupInstanceManager.getInstance(context).getById(Long.parseLong(groupId),
                            accountId);
                    return String.format(context.getString(R.string.task_filter_assignment_group), group.getName());
                }
            }

            switch (TaskAssignment.fromValue(assignment))
            {
                case INVOLVED:
                    value = context.getString(R.string.task_filter_assignment_involved);
                    break;
                case ASSIGNEE:
                    value = context.getString(R.string.task_filter_assignment_assignee);
                    break;
                case CANDIDATE:
                    value = context.getString(R.string.task_filter_assignment_candidate);
                    break;
            }
        }
        catch (Exception e)
        {
            // Do Nothing
        }
        return value;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String state = RequestConstant.STATE_OPEN;
        if (getArguments() != null)
        {
            state = getArguments().getString(ARGUMENT_ASSIGNMENT);
        }

        final CommonTaskFilterFragment frag = (CommonTaskFilterFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(
                        DisplayUtils.hasCentralPane(getActivity()) ? R.id.central_left_drawer : R.id.right_drawer);

        adapter = new TaskAssignmentAdapter(getActivity(), R.layout.row_single_line, getItems());

        return new MaterialDialog.Builder(getActivity()).title(R.string.task_filter_text)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).adapter(adapter, new MaterialDialog.ListCallback()
                {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence)
                    {
                        if (frag != null)
                        {
                            frag.onAssignmentSelection((((AssignmentItem) materialDialog.getListView().getAdapter()
                                    .getItem(i)).value));
                        }
                        getDialog().dismiss();
                    }
                }).show();
    }

    protected List<AssignmentItem> getItems()
    {
        // Init the list
        Map<Long, GroupInstance> functionnalGroups = GroupInstanceManager.getInstance(getActivity())
                .getFunctionnalByAccountId(getAccount().getId());

        ArrayList<AssignmentItem> items = new ArrayList<>(functionnalGroups.size() + 3);
        items.add(new AssignmentItem(getString(R.string.task_filter_assignment_involved),
                RequestConstant.ASSIGNMENT_INVOLVED));
        items.add(new AssignmentItem(getString(R.string.task_filter_assignment_assignee),
                RequestConstant.ASSIGNMENT_ASSIGNEE));
        items.add(new AssignmentItem(getString(R.string.task_filter_assignment_candidate),
                RequestConstant.ASSIGNMENT_CANDIDATE));
        for (GroupInstance group : functionnalGroups.values())
        {
            items.add(new AssignmentItem(String.format(getString(R.string.task_filter_assignment_group),
                    group.getName()), "group_".concat(Long.toString(group.getId()))));
        }

        return items;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // FILTER ITEM
    // ///////////////////////////////////////////////////////////////////////////
    public class AssignmentItem
    {
        public final String label;

        public final String value;

        public AssignmentItem(String label, String value)
        {
            this.label = label;
            this.value = value;
        }

    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
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

        public Builder assignment(String assignment)
        {
            extraConfiguration.putString(ARGUMENT_ASSIGNMENT, assignment);
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
