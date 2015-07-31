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

package com.activiti.android.ui.fragments.task.form;

import java.util.List;

import android.app.Activity;
import android.view.View;

import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.SingleLineViewHolder;
import com.activiti.client.api.model.editor.ModelRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class FormDefinitionModelAdapter extends BaseListAdapter<ModelRepresentation, SingleLineViewHolder>
{
    public FormDefinitionModelAdapter(Activity context, int textViewResourceId, List<ModelRepresentation> listItems)
    {
        super(context, textViewResourceId, listItems);
    }

    @Override
    protected void updateTopText(SingleLineViewHolder vh, ModelRepresentation item)
    {
        vh.topText.setText(item.getName());
        vh.topText.setMaxLines(2);
        vh.topText.setSingleLine(false);
    }

    @Override
    protected void updateBottomText(SingleLineViewHolder vh, ModelRepresentation item)
    {
    }

    @Override
    protected void updateIcon(SingleLineViewHolder vh, ModelRepresentation item)
    {
        vh.icon.setVisibility(View.GONE);
    }
}
