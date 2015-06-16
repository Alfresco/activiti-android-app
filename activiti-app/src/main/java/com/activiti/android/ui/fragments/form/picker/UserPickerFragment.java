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

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
import com.activiti.android.ui.fragments.task.form.TaskFormFoundationFragment;
import com.activiti.android.ui.fragments.user.LightUserAdapter;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.idm.LightUserRepresentation;
import com.activiti.client.api.model.idm.LightUsersRepresentation;

/**
 * @author Jean Marie Pascal
 */
public class UserPickerFragment extends IdmPickerFragment
{
    public static final String TAG = UserPickerFragment.class.getName();

    private Map<String, LightUserRepresentation> selectedItems = new HashMap<>(1);

    protected String taskId, processId;

    // //////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // //////////////////////////////////////////////////////////////////////
    public UserPickerFragment()
    {
        emptyListMessageId = R.string.person_not_found;
        retrieveDataOnCreation = false;
        setHasOptionsMenu(true);
    }

    // //////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // //////////////////////////////////////////////////////////////////////

    protected static UserPickerFragment newInstanceByTemplate(Bundle b)
    {
        UserPickerFragment cbf = new UserPickerFragment();
        cbf.setArguments(b);
        return cbf;
    }

    @Override
    protected void onRetrieveParameters(Bundle bundle)
    {
        super.onRetrieveParameters(bundle);
        taskId = getArguments().getString(ARGUMENT_TASK_ID);
        processId = getArguments().getString(ARGUMENT_PROCESS_ID);
        if (fragmentPick instanceof onPickAuthorityFragment && fieldId != null)
        {
            selectedItems = ((onPickAuthorityFragment) fragmentPick).getPersonSelected(fieldId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(super.onCreateView(inflater, container, savedInstanceState));

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
                    onSelect(selectedItems);
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

            if (fragmentPick instanceof TaskFormFoundationFragment)
            {
                Button clear = UIUtils.initClear(getRootView(), R.string.general_action_clear, false);
                clear.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onClear();
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
            else
            {
                UIUtils.initClear(getRootView(), R.string.general_action_clear, true);
            }
        }
        return getRootView();
    }

    // //////////////////////////////////////////////////////////////////////
    // LOADERS
    // //////////////////////////////////////////////////////////////////////
    protected Callback<LightUsersRepresentation> callBack = new Callback<LightUsersRepresentation>()
    {
        @Override
        public void success(LightUsersRepresentation response, Response response2)
        {
            displayData(response);
        }

        @Override
        public void failure(RetrofitError error)
        {
            displayError(error);
        }
    };

    @Override
    protected ArrayAdapter<?> onAdapterCreation()
    {
        return new LightUserAdapter(getActivity(), R.layout.row_two_lines, new ArrayList<LightUserRepresentation>(0),
                selectedItems);
    }

    @Override
    protected void performRequest()
    {
        getAPI().getUserGroupService().getUsers(keywords, taskId, processId, groupId, null, callBack);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIST ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onListItemClick(GridView l, View v, int position, long id)
    {
        LightUserRepresentation item = (LightUserRepresentation) l.getItemAtPosition(position);

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
            hideDetails = selectedItems.containsKey(item.getEmail());
            if (mode == MODE_PICK && !singleChoice)
            {
                selectedItems.remove(item.getEmail());
            }
            else
            {
                selectedItems.clear();
            }
        }
        l.setItemChecked(position, true);
        v.setSelected(true);

        selectedItems.put(item.getEmail(), item);

        if (hideDetails)
        {
            if (mode == MODE_PICK)
            {
                selectedItems.remove(item.getEmail());
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
                MessageFormat.format(getString(R.string.picker_assign_person), selectedItems.size()),
                selectedItems.size()));
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

        public Builder taskId(String taskId)
        {
            extraConfiguration.putString(ARGUMENT_TASK_ID, taskId);
            return this;
        }

        public Builder processId(String processId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_ID, processId);
            return this;
        }

        public Builder singleChoice(Boolean singleChoice)
        {
            extraConfiguration.putBoolean(ARGUMENT_SINGLE_CHOICE, singleChoice);
            return this;
        }

        public Builder restrictGroup(String groupId)
        {
            extraConfiguration.putString(ARGUMENT_GROUP_ID, groupId);
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
    }
}
