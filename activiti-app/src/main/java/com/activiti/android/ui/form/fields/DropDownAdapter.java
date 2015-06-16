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

package com.activiti.android.ui.form.fields;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.SingleLineViewHolder;
import com.activiti.client.api.model.editor.form.OptionRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class DropDownAdapter extends BaseListAdapter<OptionRepresentation, SingleLineViewHolder>
{
    public DropDownAdapter(Context context, int textViewResourceId, List<OptionRepresentation> listItems)
    {
        super(context, textViewResourceId, listItems);
        this.vhClassName = SingleLineViewHolder.class.getName();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View v = null;

        // If this is the initial dummy entry, make it hidden
        if (position == 0)
        {
            TextView tv = new TextView(getContext());
            tv.setHeight(0);
            tv.setVisibility(View.GONE);
            v = tv;
        }
        else
        {
            // Pass convertView as null to prevent reuse of special case views
            v = super.getView(position, null, parent);
        }

        // Hide scroll bar because it appears sometimes unnecessarily, this does
        // not prevent scrolling
        parent.setVerticalScrollBarEnabled(false);
        return v;
    }

    @Override
    protected void updateTopText(SingleLineViewHolder vh, OptionRepresentation item)
    {
        vh.topText.setText(item.getName());
    }

    @Override
    protected void updateBottomText(SingleLineViewHolder vh, OptionRepresentation item)
    {
    }

    @Override
    protected void updateIcon(SingleLineViewHolder vh, OptionRepresentation item)
    {
        vh.icon.setVisibility(View.GONE);
    }
}
