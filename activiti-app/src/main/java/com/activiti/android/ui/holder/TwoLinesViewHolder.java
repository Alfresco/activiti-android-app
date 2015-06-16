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

package com.activiti.android.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activiti.android.app.R;

/**
 * Default class for listing item viewholder.
 * 
 * @author Jean Marie Pascal
 */
public class TwoLinesViewHolder extends SingleLineViewHolder
{
    public TextView bottomText;

    public ImageView choose;

    public TwoLinesViewHolder(View v)
    {
        super(v);
        bottomText = (TextView) v.findViewById(R.id.bottomtext);
        choose = (ImageView) v.findViewById(R.id.choose);
    }
}
