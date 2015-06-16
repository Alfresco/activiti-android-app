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

package com.activiti.android.ui.fragments.comment;

import java.lang.ref.WeakReference;
import java.util.List;

import android.text.TextUtils;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.rendition.RenditionManager;
import com.activiti.android.sdk.model.runtime.AppVersion;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.TwoLinesCaptionViewHolder;
import com.activiti.android.ui.utils.Formatter;
import com.activiti.client.api.model.runtime.CommentRepresentation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * @author Jean Marie Pascal
 */
public class CommentAdapter extends BaseListAdapter<CommentRepresentation, TwoLinesCaptionViewHolder>
{
    protected WeakReference<CommentsFoundationFragment> frRef;

    protected boolean hasThumbnail = false;

    protected Picasso picasso;

    protected Transformation roundedTransformation;

    public CommentAdapter(CommentsFoundationFragment fr, int textViewResourceId, List<CommentRepresentation> listItems)
    {
        super(fr.getActivity(), textViewResourceId, listItems);
        this.frRef = new WeakReference<>(fr);
        vhClassName = TwoLinesCaptionViewHolder.class.getName();

        String serverVersion = ActivitiAccountManager.getInstance(getContext()).getCurrentAccount().getServerVersion();
        if (!TextUtils.isEmpty(serverVersion))
        {
            AppVersion version = new AppVersion(serverVersion);
            if (version.is120OrAbove())
            {
                hasThumbnail = true;
                picasso = ((MainActivity) fr.getActivity()).getPicasso();
                roundedTransformation = RenditionManager.getRoundedTransformation(fr.getActivity());
            }
        }
    }

    @Override
    protected void updateTopText(TwoLinesCaptionViewHolder vh, CommentRepresentation item)
    {
        vh.topText.setText(item.getCreatedBy().getFullname());
        vh.topTextRight.setText(Formatter.formatToRelativeDate(frRef.get().getActivity(), item.getCreated()));
    }

    @Override
    protected void updateBottomText(TwoLinesCaptionViewHolder vh, CommentRepresentation item)
    {
        vh.bottomText.setText(item.getMessage());
        vh.bottomText.setSingleLine(false);
        vh.bottomText.setMaxLines(25);
    }

    @Override
    protected void updateIcon(TwoLinesCaptionViewHolder vh, CommentRepresentation item)
    {
        vh.icon.setImageResource(R.drawable.ic_account_circle_grey);
        // Activate thumbnail
        if (hasThumbnail)
        {
            picasso.cancelRequest(vh.icon);
            picasso.load(frRef.get().getAPI().getUserGroupService().getPicture(item.getCreatedBy().getId()))
                    .placeholder(R.drawable.ic_account_circle_grey).fit().transform(roundedTransformation)
                    .into(vh.icon);
        }
    }
}
