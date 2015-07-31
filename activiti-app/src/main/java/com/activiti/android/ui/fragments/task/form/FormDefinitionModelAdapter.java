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

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.client.api.model.editor.ModelRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class FormDefinitionModelAdapter extends BaseListAdapter<ModelRepresentation, TwoLinesViewHolder>
{
    ModelRepresentation selectedModel;

    Long originalModel;

    public FormDefinitionModelAdapter(Activity context, int textViewResourceId, List<ModelRepresentation> listItems,
            Long originalModel, ModelRepresentation selectedModel)
    {
        super(context, textViewResourceId, listItems);
        this.originalModel = originalModel;
        this.selectedModel = selectedModel;
    }

    public void select(ModelRepresentation model)
    {
        this.selectedModel = model;
    }

    @Override
    protected void updateTopText(TwoLinesViewHolder vh, ModelRepresentation item)
    {
        if (originalModel != null && item.getId().equals(originalModel))
        {
            vh.topText.setText(item.getName() + " (current)");
        }
        else
        {
            vh.topText.setText(item.getName());
        }
        vh.topText.setMaxLines(2);
        vh.topText.setSingleLine(false);
    }

    @Override
    protected void updateBottomText(TwoLinesViewHolder vh, ModelRepresentation item)
    {
        vh.bottomText.setText(item.getDescription());
        vh.bottomText.setMaxLines(3);
        vh.bottomText.setSingleLine(false);
    }

    @Override
    protected void updateIcon(TwoLinesViewHolder vh, ModelRepresentation item)
    {
        vh.icon.setImageResource(R.drawable.ic_assignment_grey);
        if (selectedModel != null && item.getId().equals(selectedModel.getId()))
        {

            vh.choose.setVisibility(View.VISIBLE);
            vh.choose.setImageResource(R.drawable.ic_done_grey);
        }
        else
        {
            vh.choose.setVisibility(View.GONE);
        }
    }
}
