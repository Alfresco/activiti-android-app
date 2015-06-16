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

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.widget.BaseAdapter;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.client.api.model.idm.LightUsersRepresentation;
import com.activiti.client.api.model.idm.UserRepresentation;

public class UsersFoundationFragment extends BasePagingGridFragment
{
    public static final String TAG = UsersFoundationFragment.class.getName();

    protected List<UserRepresentation> selectedItems = new ArrayList<UserRepresentation>(1);

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
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

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public UsersFoundationFragment()
    {
        emptyListMessageId = R.string.empty_users;
        retrieveDataOnCreation = true;
    }

    @Override
    protected void performRequest()
    {
        getAPI().getUserGroupService().getUsers(null, callBack);
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new UserAdapter(getActivity(), R.layout.row_two_lines, new ArrayList<UserRepresentation>(0));
    }
}
