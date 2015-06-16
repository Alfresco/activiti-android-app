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

package com.activiti.android.ui.fragments.common;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.app.fragments.comment.CommentsFragment;
import com.activiti.android.app.fragments.content.ContentsFragment;
import com.activiti.android.platform.provider.mimetype.MimeType;
import com.activiti.android.platform.provider.mimetype.MimeTypeManager;
import com.activiti.android.platform.provider.transfer.ContentTransferEvent;
import com.activiti.android.platform.provider.transfer.ContentTransferManager;
import com.activiti.android.platform.rendition.RenditionManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.runtime.CommentRepresentation;
import com.activiti.client.api.model.runtime.ProcessInstanceRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.TaskRepresentation;
import com.activiti.client.api.model.runtime.request.AddContentRelatedRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by jpascal on 18/03/2015.
 */
public class AbstractDetailsFragment extends AlfrescoFragment
{
    protected static final int SECTION_MAX_ITEMS = 3;

    protected ProcessInstanceRepresentation processInstanceRepresentation;

    protected TaskRepresentation taskRepresentation;

    protected boolean hasContentLoaded = false, isEnded = false;

    protected List<CommentRepresentation> commentRepresentations = new ArrayList<>(0);

    protected List<RelatedContentRepresentation> relatedContentRepresentations = new ArrayList<>(0);

    protected String taskId, processId;

    protected Picasso picasso;

    protected Transformation roundedTransformation;

    protected CommentsFragment commentFragment;

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MainActivity)
        {
            picasso = ((MainActivity) getActivity()).getPicasso();
            roundedTransformation = RenditionManager.getRoundedTransformation(getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        if (requestCode == ContentTransferManager.PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            if (!TextUtils.isEmpty(taskId))
            {
                ContentTransferManager.prepareTransfer(resultData, this, taskId, ContentTransferManager.TYPE_TASK_ID);
            }
            else if (!TextUtils.isEmpty(processId))
            {
                ContentTransferManager.prepareTransfer(resultData, this, processId,
                        ContentTransferManager.TYPE_PROCESS_ID);
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONTENTS
    // ///////////////////////////////////////////////////////////////////////////
    protected void displayContents(List<RelatedContentRepresentation> attachments)
    {
        show(R.id.details_contents_card);
        if (attachments == null || (attachments != null && attachments.isEmpty()))
        {
            hide(R.id.details_contents_card);
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // CONTENTS
        LinearLayout contentContainer = (LinearLayout) viewById(R.id.details_contents_container);
        contentContainer.removeAllViews();
        View v;
        if (attachments == null || attachments.isEmpty())
        {
            v = inflater.inflate(R.layout.row_single_line, contentContainer, false);
            ((TextView) v.findViewById(R.id.toptext)).setText(R.string.task_message_no_related_content);
            v.findViewById(R.id.icon).setVisibility(View.GONE);
            contentContainer.addView(v);
        }
        else
        {
            TwoLinesViewHolder vh;
            MimeType mime;
            RelatedContentRepresentation content;
            int max = (attachments.size() > getNumberItems()) ? getNumberItems() : attachments.size();
            for (int i = 0; i < max; i++)
            {
                content = attachments.get(i);

                v = inflater.inflate(isEnded ? R.layout.row_two_lines : R.layout.row_two_lines_swipe, contentContainer,
                        false);
                mime = MimeTypeManager.getInstance(getActivity()).findByMimeType(content.getMimeType());
                if (mime == null)
                {
                    mime = MimeTypeManager.getInstance(getActivity()).getMimetype(content.getName());
                }
                HolderUtils.configure(v, content.getName(), content.getCreatedBy().getFullname(),
                        mime.getSmallIconId(getActivity()));

                if (!isEnded)
                {
                    SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe_layout);
                    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                    swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);

                    LinearLayout actions = (LinearLayout) swipeLayout.findViewById(R.id.bottom_wrapper);
                    ImageButton action = (ImageButton) inflater.inflate(R.layout.form_swipe_action_,
                            (LinearLayout) swipeLayout.findViewById(R.id.bottom_wrapper), false);
                    action.setImageResource(R.drawable.ic_remove_circle_outline_white);
                    action.setTag(content);
                    action.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(final View v)
                        {

                            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                                    .title(R.string.content_title_delete)
                                    .cancelListener(new DialogInterface.OnCancelListener()
                                    {
                                        @Override
                                        public void onCancel(DialogInterface dialog)
                                        {
                                            dismiss();
                                        }
                                    })
                                    .content(
                                            String.format(getString(R.string.content_message_delete_confirmation),
                                                    ((RelatedContentRepresentation) v.getTag()).getName()))
                                    .positiveText(R.string.general_action_confirm)
                                    .negativeText(R.string.general_action_cancel)
                                    .callback(new MaterialDialog.ButtonCallback()
                                    {
                                        @Override
                                        public void onPositive(MaterialDialog dialog)
                                        {
                                            removeContent(((RelatedContentRepresentation) v.getTag()));
                                        }
                                    });
                            builder.show();

                        }
                    });
                    actions.addView(action);
                }
                contentContainer.addView(v);
            }
        }

        v = inflater.inflate(R.layout.footer_two_buttons_borderless, contentContainer, false);
        Button b = (Button) v.findViewById(R.id.button_action_left);
        if (attachments == null || attachments.isEmpty())
        {
            b.setVisibility(View.GONE);
        }
        else
        {
            b.setText(R.string.see_all);
            b.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!TextUtils.isEmpty(taskId))
                    {
                        resetRightMenu();
                        ContentsFragment.with(getActivity()).readOnly(isEnded).taskId(taskId)
                                .title(taskRepresentation.getName()).display();
                    }
                    else if (!TextUtils.isEmpty(processId))
                    {
                        resetRightMenu();
                        ContentsFragment.with(getActivity()).processId(processId).readOnly(isEnded)
                                .title(processInstanceRepresentation.getName()).display();
                    }
                }
            });
        }

        b = (Button) v.findViewById(R.id.button_action_right);
        if (isEnded)
        {
            b.setVisibility(View.GONE);
        }
        else
        {
            b.setText(R.string.task_action_add_content);
            b.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ContentTransferManager.requestGetContent(AbstractDetailsFragment.this);
                }
            });
        }
        contentContainer.addView(v);
    }

    protected void displayCards()
    {
        // Must be implemented by subclass
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    protected void clearBackground(View tv)
    {
        if (isEnded)
        {
            UIUtils.setBackground(tv, null);
        }
    }

    protected int getNumberItems()
    {
        return SECTION_MAX_ITEMS;
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // METHODS CONTENT
    // //////////////////////////////////////////////////////////////////////////////////////
    private void addContent(final AddContentRelatedRepresentation update)
    {
        if (update == null)
        {
            Snackbar.make(getActivity().findViewById(R.id.left_panel), R.string.error_link_creation,
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        getAPI().getTaskService().linkAttachment(taskId, update, new Callback<RelatedContentRepresentation>()
        {
            @Override
            public void success(RelatedContentRepresentation content, Response response)
            {
                relatedContentRepresentations.add(content);
                displayContents(relatedContentRepresentations);
            }

            @Override
            public void failure(RetrofitError error)
            {
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    protected void removeContent(final RelatedContentRepresentation content)
    {
        getAPI().getTaskService().deleteAttachment(content.getId(), new Callback<Void>()
        {
            @Override
            public void success(Void resp, Response response)
            {
                relatedContentRepresentations.remove(content);
                if (relatedContentRepresentations.isEmpty())
                {
                    displayCards();
                }
                else
                {
                    displayContents(relatedContentRepresentations);
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                if (error.getResponse().getStatus() == 500)
                {
                    relatedContentRepresentations.remove(content);
                    if (relatedContentRepresentations.isEmpty())
                    {
                        displayCards();
                    }
                    else
                    {
                        displayContents(relatedContentRepresentations);
                    }
                }
                else
                {
                    Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
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

        if (getActivity() != null)
        {
            relatedContentRepresentations.add(event.response);
            displayCards();
        }
    }
}
