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

package com.activiti.android.ui.fragments.filter;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.platform.provider.appIcon.AppIcon;
import com.activiti.android.platform.provider.appIcon.AppIconManager;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.base.BaseListAdapter;
import com.activiti.android.ui.fragments.task.filter.TaskFilterPropertiesFragment;
import com.activiti.android.ui.holder.SingleIconLineViewHolder;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.runtime.UserTaskFilterRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Jean Marie Pascal
 */
public class UserFilterAdapter extends BaseListAdapter<UserTaskFilterRepresentation, SingleIconLineViewHolder>
{
    protected Typeface tf;

    private final int d16;

    private WeakReference<UserFilterFoundationFragment> frRef;

    private List<UserTaskFilterRepresentation> selectedItems;

    private Long lastFilterUsedId;

    private Long appId;

    public UserFilterAdapter(UserFilterFoundationFragment fr, int textViewResourceId,
            List<UserTaskFilterRepresentation> listItems, List<UserTaskFilterRepresentation> selectedItems,
            Long lastFilterUsedId, Long appId)
    {
        super(fr.getActivity(), textViewResourceId, listItems);
        frRef = new WeakReference<>(fr);
        this.selectedItems = selectedItems;
        this.lastFilterUsedId = lastFilterUsedId;
        this.appId = appId;
        vhClassName = SingleIconLineViewHolder.class.getName();
        try
        {
            String fontPath = "fonts/glyphicons-halflings-regular.ttf";
            tf = Typeface.createFromAsset(fr.getActivity().getAssets(), fontPath);
        }
        catch (Exception e)
        {
            // No icons available
        }
        d16 = getContext().getResources().getDimensionPixelSize(R.dimen.d_16);
    }

    @Override
    protected void updateTopText(SingleIconLineViewHolder vh, UserTaskFilterRepresentation item)
    {
        vh.topText.setText(item.getName());

    }

    @Override
    protected void updateBottomText(SingleIconLineViewHolder vh, UserTaskFilterRepresentation item)
    {
        boolean isSelected = selectedItems.contains(item);

        UIUtils.setBackground((View) vh.icon.getParent(),
                isSelected ? getContext().getResources().getDrawable(R.drawable.list_longpressed_holo) : null);
        vh.icon.setTextColor(
                getContext().getResources().getColor(isSelected ? R.color.accent : R.color.secondary_text));
        vh.topText.setTextColor(
                getContext().getResources().getColor(isSelected ? R.color.accent : R.color.secondary_text));
    }

    @Override
    protected void updateIcon(SingleIconLineViewHolder vh, UserTaskFilterRepresentation item)
    {
        // Font path
        final AppIcon appIcon = AppIconManager.getInstance(getContext()).findByIconId(item.getIcon());

        if (tf != null)
        {
            vh.icon.setText(appIcon.getCharacter());
            vh.icon.setTypeface(tf);
        }
        else
        {
            vh.icon.setVisibility(View.GONE);
        }

        vh.choose.setVisibility(View.VISIBLE);
        vh.choose.setImageResource(R.drawable.ic_more_grey);
        vh.choose.setBackgroundResource(R.drawable.activititheme_list_selector_holo_light);
        vh.choose.setTag(item);
        vh.choose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                PopupMenu popup = new PopupMenu(getContext(), v);
                UserTaskFilterRepresentation content = ((UserTaskFilterRepresentation) v.getTag());
                if (getCount() == 1)
                {
                    popup.getMenuInflater().inflate(R.menu.filters_ro, popup.getMenu());
                }
                else
                {
                    popup.getMenuInflater().inflate(R.menu.filters, popup.getMenu());
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.filter_delete:
                                MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext())
                                        .title(R.string.common_filter_delete)
                                        .cancelListener(new DialogInterface.OnCancelListener()
                                {
                                    @Override
                                    public void onCancel(DialogInterface dialog)
                                    {
                                        dialog.dismiss();
                                    }
                                }).content(String.format(
                                        getContext().getString(R.string.content_message_delete_confirmation),
                                        ((UserTaskFilterRepresentation) v.getTag()).getName()))
                                        .positiveText(R.string.general_action_confirm)
                                        .negativeText(R.string.general_action_cancel)
                                        .callback(new MaterialDialog.ButtonCallback()
                                {
                                    @Override
                                    public void onPositive(MaterialDialog dialog)
                                    {
                                        frRef.get().getAPI().getUserFiltersService().deleteUserTaskFilter(
                                                ((UserTaskFilterRepresentation) v.getTag()).getId(),
                                                new Callback<Void>()
                                        {
                                            @Override
                                            public void success(Void aVoid, Response response)
                                            {
                                                frRef.get().refresh();
                                            }

                                            @Override
                                            public void failure(RetrofitError error)
                                            {

                                            }
                                        });
                                    }
                                });
                                builder.show();
                                break;
                            case R.id.filter_edit:
                                UserTaskFilterRepresentation filterEdit = ((UserTaskFilterRepresentation) v.getTag());
                                FragmentDisplayer.with(frRef.get().getActivity())
                                        .replace(TaskFilterPropertiesFragment.with(frRef.get().getActivity())
                                                .appId(appId).userFilter(filterEdit).createFragment())
                                        .back(true).animate(null).into(R.id.right_drawer);

                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public void addAll(Collection<? extends UserTaskFilterRepresentation> collection)
    {
        if (selectedItems.isEmpty())
        {
            for (UserTaskFilterRepresentation item : collection)
            {
                if (lastFilterUsedId == -1 && item.getRecent())
                {
                    lastFilterUsedId = item.getId();
                }
                if (item.getId().equals(lastFilterUsedId))
                {
                    selectedItems.clear();
                    selectedItems.add(item);
                    frRef.get().refreshTasks();
                }
            }
        }

        super.addAll(collection);
    }
}
