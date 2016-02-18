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

package com.activiti.android.ui.fragments.form.picker;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.android.ui.fragments.builder.ListingFragmentBuilder;
import com.activiti.android.ui.fragments.user.LightGroupAdapter;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.common.ResultList;
import com.activiti.client.api.model.idm.LightGroupRepresentation;

/**
 * JM
 * 
 * @author Jean Marie Pascal
 */
public class UserGroupPickerFragment extends IdmPickerFragment
{
    public static final String TAG = UserGroupPickerFragment.class.getName();

    private Map<Long, LightGroupRepresentation> selectedItems = new HashMap<>(1);

    // //////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // //////////////////////////////////////////////////////////////////////
    public UserGroupPickerFragment()
    {
        emptyListMessageId = R.string.group_not_found;
        retrieveDataOnCreation = false;
        setHasOptionsMenu(true);
    }

    // //////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // //////////////////////////////////////////////////////////////////////

    protected static UserGroupPickerFragment newInstanceByTemplate(Bundle b)
    {
        UserGroupPickerFragment cbf = new UserGroupPickerFragment();
        cbf.setArguments(b);
        return cbf;
    }

    @Override
    protected void onRetrieveParameters(Bundle bundle)
    {
        super.onRetrieveParameters(bundle);
        if (fragmentPick instanceof onPickGroupFragment && fieldId != null)
        {
            selectedItems = ((onPickGroupFragment) fragmentPick).getGroupsSelected(fieldId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(super.onCreateView(inflater, container, savedInstanceState));

        searchForm.setHint(R.string.group_search_placeholder);

        if (getMode() == MODE_PICK)
        {
            show(R.id.validation_panel);
            validation = UIUtils.initValidation(getRootView(), R.string.general_action_confirm);
            updatePickButton();
            validation.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onGroupSelect(selectedItems);
                    if (getDialog() != null)
                    {
                        getDialog().dismiss();
                    }
                    else
                    {
                        getFragmentManager().popBackStack();
                    }
                }
            });
            Button clear = UIUtils.initClear(getRootView(), R.string.general_action_clear, false);
            clear.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onGroupClear();
                    if (getDialog() != null)
                    {
                        getDialog().dismiss();
                    }
                    else
                    {
                        getFragmentManager().popBackStack();
                    }
                }
            });
        }

        return getRootView();
    }

    // //////////////////////////////////////////////////////////////////////
    // LOADERS
    // //////////////////////////////////////////////////////////////////////
    protected Callback<ResultList<LightGroupRepresentation>> callBack = new Callback<ResultList<LightGroupRepresentation>>()
    {
        @Override
        public void onResponse(Call<ResultList<LightGroupRepresentation>> call,
                Response<ResultList<LightGroupRepresentation>> response)
        {
            if (!response.isSuccess())
            {
                onFailure(call, new Exception(response.message()));
                return;
            }
            displayData(response.body());
        }

        @Override
        public void onFailure(Call<ResultList<LightGroupRepresentation>> call, Throwable error)
        {
            displayError(error);
        }
    };

    @Override
    protected ArrayAdapter<?> onAdapterCreation()
    {
        return new LightGroupAdapter(getActivity(), R.layout.row_two_lines, new ArrayList<LightGroupRepresentation>(0),
                selectedItems);
    }

    @Override
    protected void performRequest()
    {
        getAPI().getUserGroupService().getGroups(keywords, groupId, callBack);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIST ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        LightGroupRepresentation item = (LightGroupRepresentation) l.getItemAtPosition(position);

        if (mode == MODE_PICK && !singleChoice)
        {
            l.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        }
        else if (mode == MODE_PICK && !singleChoice)
        {
            l.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        }
        else if (mode == MODE_LISTING && DisplayUtils.hasCentralPane(getActivity()))
        {
            l.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        }

        Boolean hideDetails = false;
        if (!selectedItems.isEmpty())
        {
            hideDetails = selectedItems.containsKey(item.getId());
            if (mode == MODE_PICK && !singleChoice)
            {
                selectedItems.remove(item.getId());
            }
            else
            {
                selectedItems.clear();
            }
        }
        l.setItemChecked(position, true);
        v.setSelected(true);

        selectedItems.put(item.getId(), item);

        if (hideDetails)
        {
            if (mode == MODE_PICK)
            {
                selectedItems.remove(item.getId());
                updatePickButton();
            }
            else if (mode == MODE_LISTING && DisplayUtils.hasCentralPane(getActivity()))
            {
                FragmentDisplayer.with(getActivity()).remove(DisplayUtils.getCentralFragmentId(getActivity()));
                selectedItems.clear();
            }
        }
        else
        {
            if (mode == MODE_LISTING)
            {
                // Show properties
                // UserProfileFragment.with(getActivity()).personId(item.getIdentifier()).display();
            }
            else if (mode == MODE_PICK)
            {
                validation.setEnabled(true);
                updatePickButton();
            }
        }
    }

    protected void updatePickButton()
    {
        validation.setEnabled(!selectedItems.isEmpty());
        validation.setText(String.format(
                MessageFormat.format(getString(R.string.picker_assign_group), selectedItems.size()),
                selectedItems.size()));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LISTENERS
    // ///////////////////////////////////////////////////////////////////////////
    public void onGroupSelect(Map<Long, LightGroupRepresentation> selectedItems)
    {
        if (fragmentPick == null) { return; }
        if (fieldId != null && fragmentPick instanceof onPickGroupFragment)
        {
            ((onPickGroupFragment) fragmentPick).onGroupSelected(fieldId, selectedItems);
        }
    }

    public void onGroupClear()
    {
        if (fragmentPick == null) { return; }
        if (fieldId != null && fragmentPick instanceof onPickGroupFragment)
        {
            ((onPickGroupFragment) fragmentPick).onGroupClear(fieldId);
        }
    }

    public interface onPickGroupFragment
    {
        void onGroupSelected(String fieldId, Map<Long, LightGroupRepresentation> p);

        void onGroupClear(String fieldId);

        Map<Long, LightGroupRepresentation> getGroupsSelected(String fieldId);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends ListingFragmentBuilder
    {
        // ///////////////////////////////////////////////////////////////////////////
        // CONSTRUCTORS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder(FragmentActivity activity)
        {
            super(activity);
            this.extraConfiguration = new Bundle();
        }

        public Builder(FragmentActivity appActivity, Map<String, Object> configuration)
        {
            super(appActivity, configuration);
        }

        // ///////////////////////////////////////////////////////////////////////////
        // SETTERS
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        }

        // ///////////////////////////////////////////////////////////////////////////
        // SETTERS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder fieldId(String fieldId)
        {
            extraConfiguration.putString(ARGUMENT_FIELD_ID, fieldId);
            return this;
        }

        public Builder singleChoice(Boolean singleChoice)
        {
            extraConfiguration.putBoolean(ARGUMENT_SINGLE_CHOICE, singleChoice);
            return this;
        }

        public Builder fragmentTag(String fragmentTag)
        {
            extraConfiguration.putString(ARGUMENT_FRAGMENT_TAG, fragmentTag);
            return this;
        }

        public Builder title(String title)
        {
            extraConfiguration.putString(ARGUMENT_TITLE, title);
            return this;
        }

        public Builder restrictGroup(String groupId)
        {
            extraConfiguration.putString(ARGUMENT_GROUP_ID, groupId);
            return this;
        }
    }
}
