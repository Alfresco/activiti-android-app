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
package com.activiti.android.ui.fragments.content;

import android.content.Context;
import android.view.View;

import com.activiti.android.platform.provider.mimetype.MimeType;
import com.activiti.android.platform.provider.mimetype.MimeTypeManager;
import com.activiti.android.platform.provider.transfer.ContentTransferManager;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;

/**
 * Created by jpascal on 07/05/2015.
 */
public class ContentHelper
{
    public static void openin(Context context, RelatedContentRepresentation item)
    {
        ContentTransferManager.downloadTransfer(context, item.getName(), item.getMimeType(),
                Long.toString(item.getId()));
    }

    public static void updateIcon(final Context context, TwoLinesViewHolder vh, RelatedContentRepresentation item,
            boolean setupOverflowMenu)
    {
        // Thumbnail
        MimeType mime = MimeTypeManager.getInstance(context).findByMimeType(item.getMimeType());
        if (mime == null)
        {
            mime = MimeTypeManager.getInstance(context).getMimetype(item.getName());
        }
        vh.icon.setVisibility(View.VISIBLE);
        vh.icon.setImageResource(mime.getSmallIconId(context));

        // Overflow menu
        if (!setupOverflowMenu) { return; }
    }

}
