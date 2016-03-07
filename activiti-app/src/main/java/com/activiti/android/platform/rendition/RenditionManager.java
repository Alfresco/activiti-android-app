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

package com.activiti.android.platform.rendition;

import android.content.Context;

import com.activiti.android.app.R;
import com.activiti.android.sdk.ActivitiSession;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class RenditionManager
{
    protected Picasso picasso;

    public RenditionManager(Context context, ActivitiSession session)
    {
        if (context == null || session == null) { return; }
        picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(session.getOkHttpClient()))
                .loggingEnabled(false).indicatorsEnabled(false).build();
    }

    public Picasso getPicasso()
    {
        return picasso;
    }

    public static Transformation getRoundedTransformation(Context context)
    {
        return new RoundedTransformationBuilder()
                .borderColor(context.getResources().getColor(R.color.divider_translucent)).borderWidthDp(1)
                .cornerRadiusDp(48).oval(true).build();
    }
}
