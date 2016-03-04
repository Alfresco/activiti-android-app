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

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.integration.alfresco.AlfrescoIntegrator;
import com.activiti.android.platform.integration.alfresco.NodeRefUtils;
import com.activiti.android.platform.integration.analytics.AnalyticsHelper;
import com.activiti.android.platform.integration.analytics.AnalyticsManager;
import com.activiti.android.platform.intent.IntentUtils;
import com.activiti.android.platform.provider.integration.Integration;
import com.activiti.android.platform.provider.integration.IntegrationManager;
import com.activiti.android.platform.provider.mimetype.MimeType;
import com.activiti.android.platform.provider.mimetype.MimeTypeManager;
import com.activiti.android.sdk.model.runtime.AppVersion;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

/**
 * @author Jean Marie Pascal
 */
public class ContentAdapter extends BaseListAdapter<RelatedContentRepresentation, TwoLinesViewHolder>
{
    protected WeakReference<ContentsFoundationFragment> frRef;

    protected boolean hasThumbnail = false;

    protected Picasso picasso;

    protected int textViewResourceId;

    protected int imageWidth = 72;

    protected boolean isReadOnly;

    private final int d16;

    public ContentAdapter(ContentsFoundationFragment fr, int textViewResourceId,
            List<RelatedContentRepresentation> listItems, boolean isReadOnly)
    {
        super(fr.getActivity(), textViewResourceId, listItems);
        this.textViewResourceId = textViewResourceId;
        this.frRef = new WeakReference<>(fr);
        this.isReadOnly = isReadOnly;
        String serverVersion = ActivitiAccountManager.getInstance(getContext()).getCurrentAccount().getServerVersion();
        if (!TextUtils.isEmpty(serverVersion))
        {
            AppVersion version = new AppVersion(serverVersion);
            if (version.is120OrAbove())
            {
                hasThumbnail = true;
                picasso = ((MainActivity) fr.getActivity()).getPicasso();
                imageWidth = UIUtils.getDPI(getContext(), 150);
            }
        }
        d16 = getContext().getResources().getDimensionPixelSize(R.dimen.d_16);
    }

    @Override
    protected void updateTopText(TwoLinesViewHolder vh, RelatedContentRepresentation item)
    {
        if (textViewResourceId == R.layout.row_tile_single_line)
        {
            vh.topText.setVisibility(View.GONE);
        }
        else
        {
            vh.topText.setText(item.getName());
            vh.topText.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void updateBottomText(TwoLinesViewHolder vh, RelatedContentRepresentation item)
    {
        if (textViewResourceId == R.layout.row_tile_single_line)
        {
            vh.bottomText.setText(item.getName());
        }
        else
        {
            vh.bottomText.setText(item.getCreatedBy().getFullname());
        }
    }

    @Override
    protected void updateIcon(TwoLinesViewHolder vh, RelatedContentRepresentation item)
    {
        MimeType mime = MimeTypeManager.getInstance(getContext()).findByMimeType(item.getMimeType());
        if (mime == null)
        {
            mime = MimeTypeManager.getInstance(getContext()).getMimetype(item.getName());
        }

        vh.icon.setImageResource(mime.getSmallIconId(getContext()));

        // Activate thumbnail
        if (hasThumbnail && textViewResourceId == R.layout.row_tile_single_line)
        {
            picasso.cancelRequest(vh.icon);
            String url = (MimeType.TYPE_IMAGE.equals(mime.getType()))
                    ? frRef.get().getAPI().getContentService().getDownloadUrl(item.getId())
                    : frRef.get().getAPI().getContentService().getThumbnailUrl(item.getId());
            picasso.load(url).placeholder(mime.getLargeIconId(getContext())).resize(imageWidth, imageWidth)
                    .centerInside().into(vh.icon);
        }

        vh.choose.setVisibility(View.VISIBLE);
        vh.choose.setImageResource(R.drawable.ic_more_grey);
        vh.choose.setBackgroundResource(R.drawable.activititheme_list_selector_holo_light);
        vh.choose.setTag(item);
        vh.choose.setClickable(true);
        vh.choose.setPadding(d16, d16, d16, d16);
        vh.choose.setImageResource(R.drawable.ic_more_grey);
        vh.choose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                PopupMenu popup = new PopupMenu(getContext(), v);
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

                if (!isReadOnly)
                {
                    popup.getMenu().add(0, R.id.content_remove, 1000, R.string.delete);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.content_remove:
                                MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext())
                                        .title(R.string.content_title_delete)
                                        .cancelListener(new DialogInterface.OnCancelListener()
                                {
                                    @Override
                                    public void onCancel(DialogInterface dialog)
                                    {
                                        dialog.dismiss();
                                    }
                                }).content(String.format(
                                        getContext().getString(R.string.content_message_delete_confirmation),
                                        ((RelatedContentRepresentation) v.getTag()).getName()))
                                        .positiveText(R.string.general_action_confirm)
                                        .negativeText(R.string.general_action_cancel)
                                        .callback(new MaterialDialog.ButtonCallback()
                                {
                                    @Override
                                    public void onPositive(MaterialDialog dialog)
                                    {
                                        // Analytics
                                        AnalyticsHelper.reportOperationEvent(frRef.get().getContext(),
                                                AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                                                AnalyticsManager.ACTION_DELETE,
                                                ((RelatedContentRepresentation) v.getTag()).getMimeType(), 1, false);

                                        frRef.get().delete(((RelatedContentRepresentation) v.getTag()).getId());
                                    }
                                });
                                builder.show();
                                break;
                            case R.id.content_send_file:
                                frRef.get().sendFile(((RelatedContentRepresentation) v.getTag()));
                                break;

                            case R.id.content_share_link:
                                frRef.get().shareLink(frRef.get(), ((RelatedContentRepresentation) v.getTag()));
                                break;

                            case R.id.content_download:
                                frRef.get().download((RelatedContentRepresentation) v.getTag());
                                break;

                            case R.id.content_open_alfresco:
                                final RelatedContentRepresentation content = ((RelatedContentRepresentation) v
                                        .getTag());
                                try
                                {
                                    if (TextUtils.isEmpty(content.getSource()) || !content.getSource()
                                            .contains("alfresco")) { throw new ActivityNotFoundException(); }

                                    // Retrieve Integration
                                    Integration integration = IntegrationManager.getInstance(getContext()).getById(
                                            Long.parseLong(content.getSource().replace("alfresco-", "")),
                                            frRef.get().getAccount().getId());

                                    if (integration == null || integration.getOpenType() == Integration.OPEN_UNDEFINED)
                                    {
                                        throw new ActivityNotFoundException();
                                    }
                                    else if (integration.getOpenType() == Integration.OPEN_BROWSER)
                                    {
                                        // Analytics
                                        AnalyticsHelper.reportOperationEvent(frRef.get().getContext(),
                                                AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                                                AnalyticsManager.ACTION_OPEN_BROWSER,
                                                ((RelatedContentRepresentation) v.getTag()).getMimeType(), 1, false);

                                        IntentUtils.startWebBrowser(getContext(), content.getLinkUrl());
                                    }
                                    else if (integration.getOpenType() == Integration.OPEN_NATIVE_APP)
                                    {
                                        // Analytics
                                        AnalyticsHelper.reportOperationEvent(frRef.get().getContext(),
                                                AnalyticsManager.CATEGORY_DOCUMENT_MANAGEMENT,
                                                AnalyticsManager.ACTION_OPEN_APP,
                                                ((RelatedContentRepresentation) v.getTag()).getMimeType(), 1, false);

                                        String nodeRef = Uri.parse(content.getLinkUrl()).getQueryParameter("nodeRef");
                                        Intent i = AlfrescoIntegrator.viewDocument(integration.getAlfrescoAccountId(),
                                                NodeRefUtils.getCleanIdentifier(nodeRef));
                                        frRef.get().startActivity(i);
                                    }
                                    break;
                                }
                                catch (ActivityNotFoundException e)
                                {
                                    // Revert to Alfresco WebApp
                                    MaterialDialog.Builder builder2 = new MaterialDialog.Builder(getContext())
                                            .title(R.string.integration_alfresco_open)
                                            .cancelListener(new DialogInterface.OnCancelListener()
                                    {
                                        @Override
                                        public void onCancel(DialogInterface dialog)
                                        {
                                            dialog.dismiss();
                                        }
                                    }).content(Html.fromHtml(
                                            getContext().getString(R.string.integration_alfresco_open_summary)))
                                            .positiveText(R.string.integration_alfresco_open_play)
                                            .negativeText(R.string.integration_alfresco_open_web)
                                            .callback(new MaterialDialog.ButtonCallback()
                                    {
                                        @Override
                                        public void onPositive(MaterialDialog dialog)
                                        {
                                            IntentUtils.startPlayStore(getContext(),
                                                    AlfrescoIntegrator.ALFRESCO_APP_PACKAGE);
                                        }

                                        @Override
                                        public void onNegative(MaterialDialog dialog)
                                        {
                                            IntentUtils.startWebBrowser(getContext(), content.getLinkUrl());
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
    }
}
