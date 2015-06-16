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

package com.activiti.android.ui.fragments.user;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.idm.LightUserRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class LightUserAdapter extends BaseListAdapter<LightUserRepresentation, TwoLinesViewHolder>
{
    protected Context context;

    protected Map<String, LightUserRepresentation> selectedItems;

    public LightUserAdapter(Activity context, int textViewResourceId, List<LightUserRepresentation> listItems,
            Map<String, LightUserRepresentation> selectedItems)
    {
        super(context, textViewResourceId, listItems);
        this.context = context;
        this.selectedItems = selectedItems;
    }

    @Override
    protected void updateTopText(TwoLinesViewHolder vh, LightUserRepresentation item)
    {
        vh.topText.setText(item.getFullname());
    }

    @Override
    protected void updateBottomText(TwoLinesViewHolder vh, LightUserRepresentation item)
    {
        vh.bottomText.setText(item.getEmail());
    }

    @Override
    protected void updateIcon(TwoLinesViewHolder vh, LightUserRepresentation item)
    {
        if (selectedItems.containsKey(item.getEmail()))
        {
            UIUtils.setBackground((View) vh.icon.getParent(),
                    context.getResources().getDrawable(R.drawable.list_longpressed_holo));
        }
        else
        {
            UIUtils.setBackground((View) vh.icon.getParent(), null);
        }
        vh.icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_account_box_grey));
    }
}
