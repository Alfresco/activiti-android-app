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

package com.activiti.android.ui.fragments.accounts;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.holder.TwoLinesViewHolder;

/**
 * @author Jean Marie Pascal
 */
public class AccountAdapter extends BaseListAdapter<ActivitiAccount, TwoLinesViewHolder>
{
    public static final int ADD_ACCOUNT_ID = -1;

    public static final int MANAGE_ACCOUNT_ID = -2;

    protected Context context;

    public AccountAdapter(Activity context, int textViewResourceId, List<ActivitiAccount> listItems)
    {
        super(context, textViewResourceId, listItems);
        this.context = context;
    }

    @Override
    protected void updateTopText(TwoLinesViewHolder vh, ActivitiAccount item)
    {

        if (item.getId() == ADD_ACCOUNT_ID)
        {
            vh.bottomText.setVisibility(View.GONE);
            vh.topText.setText(R.string.account_add);
            vh.icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_add_circle_outline_grey));
        }
        else if (item.getId() == MANAGE_ACCOUNT_ID)
        {
            vh.bottomText.setVisibility(View.GONE);
            vh.topText.setText(R.string.account_manage);
            vh.icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_settings_grey));
        }
        else
        {
            vh.bottomText.setText(item.getLabel());
            vh.topText.setText(item.getUsername());
            vh.icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_account_box_grey));
        }

    }

    @Override
    protected void updateBottomText(TwoLinesViewHolder vh, ActivitiAccount item)
    {

    }

    @Override
    protected void updateIcon(TwoLinesViewHolder vh, ActivitiAccount item)
    {
    }
}
