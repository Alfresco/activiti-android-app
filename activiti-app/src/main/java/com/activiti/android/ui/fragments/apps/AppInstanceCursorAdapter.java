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

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.preferences.InternalAppPreferences;
import com.activiti.android.platform.provider.app.RuntimeAppInstanceSchema;
import com.activiti.android.platform.provider.appIcon.AppIcon;
import com.activiti.android.platform.provider.appIcon.AppIconManager;
import com.activiti.android.ui.fragments.base.BaseCursorLoader;
import com.activiti.android.ui.holder.ViewHolder;
import com.activiti.android.ui.utils.UIUtils;

/**
 * @author Jean Marie Pascal
 */
public class AppInstanceCursorAdapter extends BaseCursorLoader<AppInstanceCursorAdapter.AppRowViewHolder>
{
    protected long latestAppId;

    protected Typeface tf;

    public AppInstanceCursorAdapter(Context context, Cursor c, int layoutId)
    {
        super(context, c, layoutId);
        vhClassName = AppRowViewHolder.class.getName();
        this.latestAppId = InternalAppPreferences.getLongPref(context, ActivitiAccountManager.getInstance(context)
                .getCurrentAccount().getId(), InternalAppPreferences.PREF_LAST_APP_USED);
        try
        {
            String fontPath = "fonts/glyphicons-halflings-regular.ttf";
            tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        }
        catch (Exception e)
        {
            // No icons available
        }
    }

    @Override
    protected void updateTopText(AppRowViewHolder vh, Cursor cursor)
    {
        vh.topText.setText(cursor.getString(RuntimeAppInstanceSchema.COLUMN_NAME_ID));
        vh.topTextRight.setText(Integer.toString(cursor.getInt(RuntimeAppInstanceSchema.COLUMN_NUMBER_1_ID)));
    }

    @Override
    protected void updateBottomText(AppRowViewHolder vh, Cursor cursor)
    {
        if (cursor.getLong(RuntimeAppInstanceSchema.COLUMN_APP_ID_ID) == latestAppId)
        {
            UIUtils.setBackground((View) vh.icon.getParent().getParent(),
                    context.getResources().getDrawable(R.drawable.list_longpressed_holo));
            vh.topText.setTextColor(context.getResources().getColor(R.color.accent));
            vh.icon.setTextColor(context.getResources().getColor(R.color.accent));
        }
        else
        {
            vh.icon.setTextColor(context.getResources().getColor(R.color.secondary_text));
            vh.topText.setTextColor(context.getResources().getColor(R.color.secondary_text));
            UIUtils.setBackground((View) vh.icon.getParent().getParent(), null);
        }
    }

    @SuppressWarnings("ResourceType")
    @Override
    protected void updateIcon(AppRowViewHolder vh, Cursor cursor)
    {
        // Font path
        AppIcon appIcon = AppIconManager.getInstance(context).findByIconId(
                cursor.getString(RuntimeAppInstanceSchema.COLUMN_ICON_ID));

        if (tf != null)
        {
            String themeId = cursor.getString(RuntimeAppInstanceSchema.COLUMN_THEME_ID);
            vh.icon.setText(appIcon.getCharacter());
            vh.icon.setTypeface(tf);
            vh.icon.setTextColor(context.getResources().getColor(appIcon.getColorId(themeId)));
        }
        else
        {
            vh.icon.setVisibility(View.GONE);
        }
    }

    public void setSelected(long latestAppId)
    {
        this.latestAppId = latestAppId;
    }

    public static class AppRowViewHolder extends ViewHolder
    {
        public TextView topText;

        public TextView icon;

        public TextView topTextRight;

        public AppRowViewHolder(View v)
        {
            super(v);
            icon = (TextView) v.findViewById(R.id.icon);
            topText = (TextView) v.findViewById(R.id.toptext);
            topTextRight = (TextView) v.findViewById(R.id.toptext_right);
        }
    }
}
