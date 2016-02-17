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

package com.activiti.android.ui.fragments.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activiti.android.app.ActivitiVersionNumber;
import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.fragments.comment.CommentsFragment;
import com.activiti.android.app.fragments.process.ProcessDiagram;
import com.activiti.android.app.fragments.process.ProcessesFragment;
import com.activiti.android.app.fragments.task.TaskDetailsFragment;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.event.CompleteProcessEvent;
import com.activiti.android.platform.event.CompleteTaskEvent;
import com.activiti.android.platform.exception.ExceptionMessageUtils;
import com.activiti.android.platform.integration.alfresco.AlfrescoIntegrator;
import com.activiti.android.platform.integration.alfresco.NodeRefUtils;
import com.activiti.android.platform.intent.IntentUtils;
import com.activiti.android.platform.provider.integration.Integration;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.mimetype.MimeType;
import com.activiti.android.platform.provider.mimetype.MimeTypeManager;
import com.activiti.android.platform.provider.transfer.ContentTransferManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.sdk.model.TaskState;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.common.AbstractDetailsFragment;
import com.activiti.android.ui.fragments.content.ContentsFoundationFragment;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.android.ui.utils.Formatter;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.runtime.ProcessContentRepresentation;
import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.activiti.client.api.model.runtime.request.QueryTasksRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by jpascal on 07/03/2015.
 */
public class ProcessDetailsFoundationFragment extends AbstractDetailsFragment
{
    public static final String TAG = ProcessDetailsFoundationFragment.class.getName();

    public static final String ARGUMENT_PROCESS_ID = "processId";

    public static final String ARGUMENT_APP_ID = "appId";

    protected boolean hasActiveTasks = false, hasCompletedTasks = false, hasFieldContentLoaded = false;

    protected List<TaskRepresentation> activeTaskRepresentations, completedTaskRepresentations;

    protected List<ProcessContentRepresentation> fieldContents = new ArrayList<>(0);

    protected Long appId;

    protected MaterialDialog waitingDialog;

    protected RelatedContentRepresentation selectedContent;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ProcessDetailsFoundationFragment()
    {
        super();
    }

    public static ProcessDetailsFoundationFragment newInstanceByTemplate(Bundle b)
    {
        ProcessDetailsFoundationFragment cbf = new ProcessDetailsFoundationFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_process_details, container, false));
        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null)
        {
            processId = BundleUtils.getString(getArguments(), ARGUMENT_PROCESS_ID);
            appId = BundleUtils.getLong(getArguments(), ARGUMENT_APP_ID);
        }

        load();
    }

    protected void load()
    {
        displayLoading();

        // Retrieve Information
        getAPI().getProcessService().getById(processId, new Callback<ProcessInstanceRepresentation>()
        {
            @Override
            public void onResponse(Call<ProcessInstanceRepresentation> call,
                    Response<ProcessInstanceRepresentation> response)
            {
                processInstanceRepresentation = response.body();

                UIUtils.setTitle(getActivity(), response.body().getName(), getString(R.string.task_title_details));

                displayInfo();
                requestExtraInfo();
                displayCards();

                commentFragment = (CommentsFragment) CommentsFragment.with(getActivity()).readonly(isEnded)
                        .processId(processId).createFragment();
                FragmentDisplayer.with(getActivity()).back(false).animate(null).replace(commentFragment)
                        .into(R.id.right_drawer);
            }

            @Override
            public void onFailure(Call<ProcessInstanceRepresentation> call, Throwable error)
            {
                displayError(error);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        UIUtils.setTitle(getActivity(), getString(R.string.task_title_details), null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        if (requestCode == ContentTransferManager.PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            ContentTransferManager.prepareTransfer(resultData, this, processId, ContentTransferManager.TYPE_PROCESS_ID);
        }
        else if (requestCode == ContentTransferManager.CREATE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            ContentTransferManager.startSaveAsTransfer(getActivity(), Long.toString(selectedContent.getId()),
                    resultData.getData().toString());
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // API REQUEST
    // ///////////////////////////////////////////////////////////////////////////
    private void requestExtraInfo()
    {
        if (processInstanceRepresentation == null) { return; }

        // Retrieve active tasks
        QueryTasksRepresentation request = new QueryTasksRepresentation(null, null, null, processId, null,
                TaskState.OPEN.value(), null, null, null, 5L);
        getAPI().getTaskService().list(request, new Callback<ResultList<TaskRepresentation>>()
        {
            @Override
            public void onResponse(Call<ResultList<TaskRepresentation>> call,
                    Response<ResultList<TaskRepresentation>> response)
            {
                activeTaskRepresentations = response.body().getList();
                hasActiveTasks = true;
                displayCards();
            }

            @Override
            public void onFailure(Call<ResultList<TaskRepresentation>> call, Throwable error)
            {
                hasActiveTasks = true;
            }
        });

        // Retrieve completed tasks
        request = new QueryTasksRepresentation(null, null, null, processId, null, TaskState.COMPLETED.value(), null,
                null, null, 5L);
        getAPI().getTaskService().list(request, new Callback<ResultList<TaskRepresentation>>()
        {
            @Override
            public void onResponse(Call<ResultList<TaskRepresentation>> call,
                    Response<ResultList<TaskRepresentation>> response)
            {
                completedTaskRepresentations = response.body().getList();
                hasCompletedTasks = true;
                displayCards();
            }

            @Override
            public void onFailure(Call<ResultList<TaskRepresentation>> call, Throwable error)
            {
                hasCompletedTasks = true;
            }
        });

        // Retrieve Contents
        getAPI().getProcessService().getAttachments(processId, new Callback<ResultList<RelatedContentRepresentation>>()
        {
            @Override
            public void onResponse(Call<ResultList<RelatedContentRepresentation>> call,
                    Response<ResultList<RelatedContentRepresentation>> response)
            {
                relatedContentRepresentations = response.body().getList();
                hasContentLoaded = true;
                displayCards();
            }

            @Override
            public void onFailure(Call<ResultList<RelatedContentRepresentation>> call, Throwable error)
            {
                hasContentLoaded = true;
            }
        });

        if (getVersionNumber() >= ActivitiVersionNumber.VERSION_1_2_2)
        {
            // Retrieve Field Contents
            getAPI().getProcessService().getFieldContents(processId,
                    new Callback<ResultList<ProcessContentRepresentation>>()
            {
                @Override
                        public void onResponse(Call<ResultList<ProcessContentRepresentation>> call,
                                Response<ResultList<ProcessContentRepresentation>> response)
                {
                            fieldContents = response.body().getList();
                    hasFieldContentLoaded = true;
                    displayCards();
                }

                @Override
                        public void onFailure(Call<ResultList<ProcessContentRepresentation>> call, Throwable error)
                {
                    hasContentLoaded = true;
                }
            });
        }
        else
        {
            hasFieldContentLoaded = true;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CARDS
    // ///////////////////////////////////////////////////////////////////////////
    protected void displayLoading()
    {
        hide(R.id.details_container);
        show(R.id.details_loading);
        show(R.id.progressbar);
        hide(R.id.empty);
    }

    protected void displayData()
    {
        show(R.id.details_container);
        hide(R.id.details_loading);
        hide(R.id.progressbar);
        hide(R.id.empty);
    }

    protected void displayHelp()
    {
        show(R.id.details_container);
        hide(R.id.details_loading);
        hide(R.id.progressbar);
        hide(R.id.empty);
        hide(R.id.task_details_people_card);
        hide(R.id.details_contents_card);
        if (isEnded)
        {
            hide(R.id.task_details_help_card);
        }
        else
        {
            createHelpSection();
        }
    }

    protected void displayError(Throwable error)
    {
        hide(R.id.details_container);
        show(R.id.details_loading);
        hide(R.id.progressbar);
        show(R.id.empty);

        // Update controls in regards
        TextView emptyText = (TextView) viewById(R.id.empty_text);
        emptyText.setText(ExceptionMessageUtils.getMessage(getActivity(), error));
        Button bRetry = (Button) viewById(R.id.empty_action);
        bRetry.setText(R.string.retry);
        bRetry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                load();
            }
        });
        bRetry.setVisibility(View.VISIBLE);
    }

    protected void displayCards()
    {
        if (hasContentLoaded && hasActiveTasks && hasCompletedTasks && hasFieldContentLoaded)
        {
            if (commentRepresentations.isEmpty() && relatedContentRepresentations.isEmpty()
                    && activeTaskRepresentations.isEmpty() && completedTaskRepresentations.isEmpty()
                    && fieldContents.isEmpty())
            {
                displayHelp();
            }
            else
            {
                // displayComments(commentRepresentations);
                displayContents(relatedContentRepresentations);
                displayTasks(activeTaskRepresentations, true);
                displayTasks(completedTaskRepresentations, false);
                displayFieldContents(fieldContents);
                displayData();
            }
        }
        else
        {
            displayLoading();
        }
    }

    private void createHelpSection()
    {
        // Do we need an help section for empty process ?
    }

    protected void displayInfo()
    {
        // What's the status ?
        isEnded = (processInstanceRepresentation.getEnded() != null);

        // Header
        ((TextView) viewById(R.id.process_details_name)).setText(processInstanceRepresentation.getName());

        // START DATE
        Date startedAt = processInstanceRepresentation.getStarted();
        HolderUtils.configure(viewById(R.id.process_details_started_at), getString(R.string.process_field_started),
                DateFormat.getLongDateFormat(getActivity()).format(startedAt.getTime()), R.drawable.ic_schedule_grey);

        // STARTED BY
        HolderUtils.configure(viewById(R.id.process_details_started_by), getString(R.string.process_field_started_by),
                processInstanceRepresentation.getStartedBy().getFullname(), R.drawable.ic_assignment_ind_grey);

        // Complete
        if (isEnded)
        {
            displayCompletedProperties(processInstanceRepresentation);
            Button delete = (Button) viewById(R.id.process_action_cancel);
            delete.setText(R.string.process_action_delete);
            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                            .title(R.string.process_popup_delete_title)
                            .content(
                                    String.format(getString(R.string.process_popup_delete_description),
                                            processInstanceRepresentation.getName()))
                            .positiveText(R.string.general_action_confirm).negativeText(R.string.general_action_cancel)
                            .callback(new MaterialDialog.ButtonCallback()
                            {
                                @Override
                                public void onPositive(MaterialDialog dialog)
                                {
                                    cancelProcess();
                                }
                            });
                    builder.show();
                }
            });
        }
        else
        {
            Button cancel = (Button) viewById(R.id.process_action_cancel);
            cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                            .title(R.string.process_popup_cancel_title)
                            .content(
                                    String.format(getString(R.string.process_popup_cancel_description),
                                            processInstanceRepresentation.getName()))
                            .positiveText(R.string.general_action_confirm).negativeText(R.string.general_action_cancel)
                            .callback(new MaterialDialog.ButtonCallback()
                            {
                                @Override
                                public void onPositive(MaterialDialog dialog)
                                {
                                    cancelProcess();
                                }
                            });
                    builder.show();
                }
            });
        }

        displayActionsSection();
    }

    private void displayCompletedProperties(ProcessInstanceRepresentation process)
    {
        TwoLinesViewHolder vh = HolderUtils.configure((LinearLayout) viewById(R.id.process_details_property_container),
                R.layout.row_two_lines_inverse, getString(R.string.task_field_ended),
                Formatter.formatToRelativeDate(getActivity(), process.getEnded()), R.drawable.ic_history_grey);

        if (viewById(R.id.process_container_large) != null)
        {
            ((RelativeLayout) vh.icon.getParent()).setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        }

        long diff = process.getEnded().getTime() - process.getStarted().getTime();
        vh = HolderUtils.configure((LinearLayout) viewById(R.id.process_details_property_container),
                R.layout.row_two_lines_inverse, getString(R.string.task_field_duration),
                Formatter.formatDuration(getActivity(), diff), R.drawable.ic_schedule_grey);

        if (viewById(R.id.process_container_large) != null)
        {
            ((RelativeLayout) vh.icon.getParent()).setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONTENTS
    // ///////////////////////////////////////////////////////////////////////////
    protected void displayFieldContents(List<ProcessContentRepresentation> processContents)
    {
        show(R.id.details_fieldcontents_card);
        if (processContents == null || (processContents != null && processContents.isEmpty())
                || getVersionNumber() < ActivitiVersionNumber.VERSION_1_2_2)
        {
            hide(R.id.details_fieldcontents_card);
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // CONTENTS
        LinearLayout contentContainer = (LinearLayout) viewById(R.id.details_fieldcontents_container);
        contentContainer.removeAllViews();
        View v;
        if (processContents == null || processContents.isEmpty())
        {
            // Nothing
            hide(R.id.details_fieldcontents_card);
        }
        else
        {
            TwoLinesViewHolder vh;
            MimeType mime;
            RelatedContentRepresentation content;
            ProcessContentRepresentation processContent;
            for (int j = 0; j < processContents.size(); j++)
            {
                processContent = processContents.get(j);

                LinearLayout vr = (LinearLayout) inflater.inflate(R.layout.form_header, null);
                ((TextView) vr.findViewById(R.id.header_title)).setText(processContent.getField().getName());
                contentContainer.addView(vr);

                for (int i = 0; i < processContent.getContent().size(); i++)
                {
                    content = processContent.getContent().get(i);

                    v = inflater.inflate(R.layout.row_two_lines, contentContainer, false);
                    mime = MimeTypeManager.getInstance(getActivity()).findByMimeType(content.getMimeType());
                    if (mime == null)
                    {
                        mime = MimeTypeManager.getInstance(getActivity()).getMimetype(content.getName());
                    }
                    TwoLinesViewHolder tvh = HolderUtils.configure(v, content.getName(), content.getCreatedBy()
                            .getFullname(), mime.getSmallIconId(getActivity()));

                    // FIXME Duplicate Code with ContentAdapter
                    tvh.choose.setVisibility(View.VISIBLE);
                    tvh.choose.setImageResource(R.drawable.ic_more_grey);
                    tvh.choose.setVisibility(View.VISIBLE);
                    tvh.choose.setBackgroundResource(R.drawable.activititheme_list_selector_holo_light);
                    tvh.choose.setTag(content);
                    tvh.choose.setClickable(true);
                    tvh.choose.setPadding(16, 16, 16, 16);
                    tvh.choose.setImageResource(R.drawable.ic_more_grey);
                    tvh.choose.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(final View v)
                        {
                            PopupMenu popup = new PopupMenu(getActivity(), v);
                            RelatedContentRepresentation content = ((RelatedContentRepresentation) v.getTag());
                            if (content.isLink() && !TextUtils.isEmpty(content.getSource()))
                            {
                                if (content.getSource().contains("alfresco"))
                                {
                                    popup.getMenuInflater().inflate(R.menu.contents_alfresco, popup.getMenu());
                                }
                                else
                                {
                                    popup.getMenuInflater().inflate(R.menu.contents, popup.getMenu());
                                }
                            }
                            else
                            {
                                popup.getMenuInflater().inflate(R.menu.contents, popup.getMenu());
                            }

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                            {
                                public boolean onMenuItemClick(MenuItem item)
                                {
                                    switch (item.getItemId())
                                    {
                                        case R.id.content_send_file:
                                            selectedContent = ((RelatedContentRepresentation) v.getTag());
                                            waitingDialog = ContentsFoundationFragment.sendFile(
                                                    ProcessDetailsFoundationFragment.this,
                                                    ((RelatedContentRepresentation) v.getTag()));
                                            break;

                                        case R.id.content_share_link:
                                            ContentsFoundationFragment.shareLink(ProcessDetailsFoundationFragment.this,
                                                    ((RelatedContentRepresentation) v.getTag()));
                                            break;

                                        case R.id.content_download:
                                            selectedContent = ((RelatedContentRepresentation) v.getTag());
                                            ContentsFoundationFragment.download(ProcessDetailsFoundationFragment.this,
                                                    (RelatedContentRepresentation) v.getTag());
                                            break;

                                        case R.id.content_open_alfresco:
                                            final RelatedContentRepresentation content = ((RelatedContentRepresentation) v
                                                    .getTag());
                                            try
                                            {
                                                if (TextUtils.isEmpty(content.getSource())
                                                        || !content.getSource().contains("alfresco")) { throw new ActivityNotFoundException(); }

                                                // Retrieve Integration
                                                Integration integration = IntegrationManager.getInstance(getActivity())
                                                        .getById(
                                                                Long.parseLong(content.getSource().replace("alfresco-",
                                                                        "")), getAccount().getId());

                                                if (integration == null
                                                        || integration.getOpenType() == Integration.OPEN_UNDEFINED)
                                                {
                                                    throw new ActivityNotFoundException();
                                                }
                                                else if (integration.getOpenType() == Integration.OPEN_BROWSER)
                                                {
                                                    IntentUtils.startWebBrowser(getActivity(), content.getLinkUrl());
                                                }
                                                else if (integration.getOpenType() == Integration.OPEN_NATIVE_APP)
                                                {
                                                    String nodeRef = Uri.parse(content.getLinkUrl()).getQueryParameter(
                                                            "nodeRef");
                                                    Intent i = AlfrescoIntegrator.viewDocument(
                                                            integration.getAlfrescoAccountId(),
                                                            NodeRefUtils.getCleanIdentifier(nodeRef));
                                                    startActivity(i);
                                                }
                                                break;
                                            }
                                            catch (ActivityNotFoundException e)
                                            {
                                                // Revert to Alfresco WebApp
                                                MaterialDialog.Builder builder2 = new MaterialDialog.Builder(
                                                        getActivity())
                                                        .title(R.string.integration_alfresco_open)
                                                        .cancelListener(new DialogInterface.OnCancelListener()
                                                        {
                                                            @Override
                                                            public void onCancel(DialogInterface dialog)
                                                            {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .content(
                                                                Html.fromHtml(getString(R.string.integration_alfresco_open_summary)))
                                                        .positiveText(R.string.integration_alfresco_open_play)
                                                        .negativeText(R.string.integration_alfresco_open_web)
                                                        .callback(new MaterialDialog.ButtonCallback()
                                                        {
                                                            @Override
                                                            public void onPositive(MaterialDialog dialog)
                                                            {
                                                                IntentUtils.startPlayStore(getActivity(),
                                                                        AlfrescoIntegrator.ALFRESCO_APP_PACKAGE);
                                                            }

                                                            @Override
                                                            public void onNegative(MaterialDialog dialog)
                                                            {
                                                                IntentUtils.startWebBrowser(getActivity(),
                                                                        content.getLinkUrl());
                                                            }
                                                        });
                                                builder2.show();
                                            }
                                            break;
                                    }
                                    return true;
                                }
                            });
                            popup.show();
                        }
                    });

                    contentContainer.addView(v);
                }
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // TASKS
    // ///////////////////////////////////////////////////////////////////////////
    protected void displayTasks(List<TaskRepresentation> taskRepresentations, boolean isActive)
    {
        if (isActive && isEnded)
        {
            hide(R.id.process_details_active_tasks_card);
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // TASKS
        LinearLayout activeTaskContainer = (LinearLayout) viewById(isActive ? R.id.process_details_active_tasks_container
                : R.id.process_details_completed_tasks_container);
        activeTaskContainer.removeAllViews();
        View v;
        if (taskRepresentations == null || taskRepresentations.isEmpty())
        {
            v = inflater.inflate(R.layout.row_single_line, activeTaskContainer, false);
            ((TextView) v.findViewById(R.id.toptext)).setText(isActive ? R.string.process_message_no_tasks
                    : R.string.process_message_no_completed_tasks);
            v.findViewById(R.id.icon).setVisibility(View.GONE);
            activeTaskContainer.addView(v);
        }
        else
        {
            TaskRepresentation taskRepresentation;
            int max = (taskRepresentations.size() > TASKS_MAX_ITEMS) ? TASKS_MAX_ITEMS : taskRepresentations.size();
            for (int i = 0; i < max; i++)
            {
                taskRepresentation = taskRepresentations.get(i);
                v = inflater.inflate(R.layout.row_two_lines_caption_borderless, activeTaskContainer, false);
                v.setTag(taskRepresentation);
                HolderUtils.configure(v, taskRepresentation.getName(), Formatter.formatToRelativeDate(getActivity(),
                        taskRepresentation.getCreated()),
                        (taskRepresentation.getAssignee() != null) ? taskRepresentation.getAssignee().getFullname()
                                : getString(R.string.task_message_no_assignee), R.drawable.ic_account_circle_grey);
                activeTaskContainer.addView(v);
                v.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        TaskDetailsFragment.with(getActivity()).task((TaskRepresentation) v.getTag()).display();
                    }
                });

            }
        }
    }

    private void displayActionsSection()
    {
        viewById(R.id.process_action_share_link).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                IntentUtils.actionShareLink(ProcessDetailsFoundationFragment.this,
                        processInstanceRepresentation.getName(), getAPI().getProcessService().getShareUrl(processId));
            }
        });

        // Admin feature
        // Display Process diagram
        if (getAccount().isAdmin() && !isEnded)
        {
            show(R.id.process_action_show_diagram);
            viewById(R.id.process_action_show_diagram).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ProcessDiagram.with(getActivity()).processId(processInstanceRepresentation.getId())
                            .tenantId(processInstanceRepresentation.getTenantId())
                            .processName(processInstanceRepresentation.getName()).display();
                }
            });
        }
        else
        {
            hide(R.id.process_action_show_diagram);
        }

        if (isEnded)
        {
            hide(R.id.process_action_add_content);
            hide(R.id.process_action_add_comment);
        }
        else
        {
            viewById(R.id.process_action_add_content).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ContentTransferManager.requestGetContent(ProcessDetailsFoundationFragment.this);
                }
            });

            viewById(R.id.process_action_add_comment).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((MainActivity) getActivity()).setRightMenuVisibility(!((MainActivity) getActivity())
                            .isRightMenuVisible());
                }
            });
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    private void cancelProcess()
    {
        getAPI().getProcessService().delete(processId, new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                // Event
                try
                {
                    ProcessesFragment fr = (ProcessesFragment) getActivity().getSupportFragmentManager()
                            .findFragmentByTag(ProcessesFragment.TAG);
                    if (fr != null)
                    {
                        fr.onCompletedProcessEvent(new CompleteProcessEvent(null, null, Long.toString(getLastAppId())));
                    }

                }
                catch (Exception e)
                {
                    EventBusManager.getInstance().post(new CompleteTaskEvent(null, null, null));
                }

                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable error)
            {

            }
        });
    }
}
