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

package com.activiti.android.ui.fragments.apps;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.preferences.InternalAppPreferences;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.runtime.AppDefinitionRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class AppDefinitionRepresentationAdapter extends
        BaseListAdapter<AppDefinitionRepresentation, TwoLinesViewHolder>
{
    protected Context context;

    protected long latestAppId;

    public AppDefinitionRepresentationAdapter(Activity context, int textViewResourceId,
            List<AppDefinitionRepresentation> listItems)
    {
        super(context, textViewResourceId, listItems);
        this.context = context;
        this.latestAppId = InternalAppPreferences.getLongPref(context, ActivitiAccountManager.getInstance(context)
                .getCurrentAccount().getId(), InternalAppPreferences.PREF_LAST_APP_USED);
    }

    @Override
    protected void updateTopText(TwoLinesViewHolder vh, AppDefinitionRepresentation item)
    {
        vh.topText.setText(item.getName());
        vh.topText.setTextColor(context.getResources().getColor(R.color.secondary_text));
    }

    @Override
    protected void updateBottomText(TwoLinesViewHolder vh, AppDefinitionRepresentation item)
    {
        if (item.getId() == latestAppId)
        {
            UIUtils.setBackground((View) vh.icon.getParent(),
                    context.getResources().getDrawable(R.drawable.list_longpressed_holo));
            vh.topText.setTextColor(context.getResources().getColor(R.color.accent));
        }
        else
        {
            UIUtils.setBackground((View) vh.icon.getParent(), null);
        }
        vh.bottomText.setVisibility(View.GONE);
    }

    @Override
    protected void updateIcon(TwoLinesViewHolder vh, AppDefinitionRepresentation item)
    {
        vh.icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_action_inbox));
    }

    public void setSelected(long latestAppId)
    {
        this.latestAppId = latestAppId;
    }
}
