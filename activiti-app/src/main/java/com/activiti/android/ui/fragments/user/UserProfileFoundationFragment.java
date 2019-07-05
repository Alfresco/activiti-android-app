/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
 *
 *  This file is part of Alfresco Activiti Mobile for Android.
 *
 *  Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package com.activiti.android.ui.fragments.user;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.exception.ExceptionMessageUtils;
import com.activiti.android.platform.provider.transfer.ContentTransferManager;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.form.EditTextDialogFragment;
import com.activiti.android.ui.fragments.processDefinition.ProcessDefinitionFoundationFragment;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.client.api.model.idm.GroupRepresentation;
import com.activiti.client.api.model.idm.UserRepresentation;
import com.activiti.client.api.model.idm.request.UpdateProfileRepresentation;

/**
 * Created by jpascal on 07/03/2015.
 */
public class UserProfileFoundationFragment extends AlfrescoFragment implements EditTextDialogFragment.onEditTextFragment
{
    public static final String TAG = ProcessDefinitionFoundationFragment.class.getName();

    protected static final String ARGUMENT_USER_ID = "userId";

    private static final int EDIT_NAME_ID = 0;

    private static final int EDIT_FIRSTNAME_ID = 1;

    private static final int EDIT_EMAIL_ID = 2;

    private static final int EDIT_COMPANY_ID = 4;

    protected String userId;

    protected UserRepresentation userRepresentation;

    protected List<GroupRepresentation> memberOfGroups;

    private int fieldId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public UserProfileFoundationFragment()
    {
        super();
    }

    public static UserProfileFoundationFragment newInstanceByTemplate(Bundle b)
    {
        UserProfileFoundationFragment cbf = new UserProfileFoundationFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_user_profile, container, false));
        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null)
        {
            userId = getAccount().getUserId();
        }

        load();

    }

    protected void load()
    {
        displayLoading();
        getAPI().getProfileService().getProfile(new Callback<UserRepresentation>()
        {
            @Override
            public void onResponse(Call<UserRepresentation> call, Response<UserRepresentation> response)
            {
                if (!response.isSuccessful())
                {
                    onFailure(call, new Exception(response.message()));
                    return;
                }

                userRepresentation = response.body();
                if (memberOfGroups == null && userRepresentation.getGroups() != null
                        && !userRepresentation.getGroups().isEmpty())
                {
                    memberOfGroups = userRepresentation.getGroups();
                }
                displayInfo();
                displayData();
            }

            @Override
            public void onFailure(Call<UserRepresentation> call, Throwable error)
            {
                displayError(error);
                if (getActivity() != null)
                {
                    Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        if (requestCode == ContentTransferManager.PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            ContentTransferManager.prepareTransfer(resultData, this, userId, ContentTransferManager.TYPE_PROFILE_ID);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getToolbar().setVisibility(View.GONE);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getToolbar().setVisibility(View.VISIBLE);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    private void displayInfo()
    {
        if (userRepresentation == null) { return; }

        // Icon
        ((MainActivity) getActivity()).getPicasso().load(Uri.parse(getAPI().getProfileService().getProfilePictureURL()))
                .fit().into((ImageView) viewById(R.id.circleView));

        viewById(R.id.circleView).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ContentTransferManager.requestGetContent(UserProfileFoundationFragment.this, "image/*");
            }
        });

        // Header
        ((TextView) viewById(R.id.profile_header_fullname)).setText(userRepresentation.getFullname());
        ((TextView) viewById(R.id.profile_header_email)).setText(userRepresentation.getEmail());

        // Details Card
        HolderUtils.configure(viewById(R.id.profile_firstname), getString(R.string.idm_profile_mgmt_first_name),
                userRepresentation.getFirstName(), -1);
        viewById(R.id.profile_firstname_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditTextDialogFragment.with(getActivity()).fieldId(EDIT_FIRSTNAME_ID).tag(getTag())
                        .value(userRepresentation.getFirstName()).hintId(R.string.idm_profile_mgmt_first_name)
                        .displayAsDialog();
            }
        });

        HolderUtils.configure(viewById(R.id.profile_lastname), getString(R.string.idm_profile_mgmt_last_name),
                userRepresentation.getLastName(), -1);
        viewById(R.id.profile_lastname_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditTextDialogFragment.with(getActivity()).fieldId(EDIT_NAME_ID).tag(getTag())
                        .value(userRepresentation.getLastName()).hintId(R.string.idm_profile_mgmt_last_name)
                        .displayAsDialog();
            }
        });

        HolderUtils.configure(viewById(R.id.profile_email), getString(R.string.idm_profile_mgmt_email),
                userRepresentation.getEmail(), -1);
        viewById(R.id.profile_email_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditTextDialogFragment.with(getActivity()).fieldId(EDIT_EMAIL_ID).tag(getTag())
                        .value(userRepresentation.getEmail()).hintId(R.string.idm_profile_mgmt_email).notNull(true)
                        .displayAsDialog();
            }
        });

        HolderUtils.configure(viewById(R.id.profile_company), getString(R.string.idm_profile_mgmt_company),
                userRepresentation.getCompany(), -1);
        viewById(R.id.profile_company_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditTextDialogFragment.with(getActivity()).fieldId(EDIT_COMPANY_ID).tag(getTag())
                        .value(userRepresentation.getCompany()).hintId(R.string.idm_profile_mgmt_company)
                        .displayAsDialog();
            }
        });

        // GROUPS
        if (memberOfGroups == null)
        {
            hide(R.id.user_groups_title);
            hide(R.id.user_groups_container);
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout userContainer = (LinearLayout) viewById(R.id.user_groups);
        userContainer.removeAllViews();
        View v;
        for (GroupRepresentation group : memberOfGroups)
        {
            v = inflater.inflate(R.layout.row_single_line, userContainer, false);
            HolderUtils.configure(v, group.getName(), R.drawable.ic_group_grey);
            userContainer.addView(v);
        }
    }

    public void refreshPicture()
    {
        ((MainActivity) getActivity()).getPicasso()
                .invalidate(Uri.parse(getAPI().getProfileService().getProfilePictureURL()));
        ((MainActivity) getActivity()).getPicasso().load(Uri.parse(getAPI().getProfileService().getProfilePictureURL()))
                .fit().into((ImageView) viewById(R.id.circleView));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onTextEdited(int id, String newValue)
    {
        fieldId = id;
        switch (id)
        {
            case EDIT_FIRSTNAME_ID:
                edit(new UpdateProfileRepresentation(newValue, userRepresentation.getLastName(),
                        userRepresentation.getEmail(), userRepresentation.getCompany()));
                break;
            case EDIT_NAME_ID:
                edit(new UpdateProfileRepresentation(userRepresentation.getFirstName(), newValue,
                        userRepresentation.getEmail(), userRepresentation.getCompany()));
                break;
            case EDIT_EMAIL_ID:
                edit(new UpdateProfileRepresentation(userRepresentation.getFirstName(),
                        userRepresentation.getLastName(), newValue, userRepresentation.getCompany()));
                break;
            case EDIT_COMPANY_ID:
                edit(new UpdateProfileRepresentation(userRepresentation.getFirstName(),
                        userRepresentation.getLastName(), userRepresentation.getEmail(), newValue));
                break;
        }
    }

    @Override
    public void onTextClear(int id)
    {
        fieldId = id;
        switch (id)
        {
            case EDIT_FIRSTNAME_ID:
                edit(new UpdateProfileRepresentation(null, userRepresentation.getLastName(),
                        userRepresentation.getEmail(), userRepresentation.getCompany()));
                break;
            case EDIT_NAME_ID:
                edit(new UpdateProfileRepresentation(userRepresentation.getFirstName(), null,
                        userRepresentation.getEmail(), userRepresentation.getCompany()));
                break;
            case EDIT_EMAIL_ID:
                edit(new UpdateProfileRepresentation(userRepresentation.getFirstName(),
                        userRepresentation.getLastName(), null, userRepresentation.getCompany()));
                break;
            case EDIT_COMPANY_ID:
                edit(new UpdateProfileRepresentation(userRepresentation.getFirstName(),
                        userRepresentation.getLastName(), userRepresentation.getEmail(), null));
                break;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION
    // ///////////////////////////////////////////////////////////////////////////
    private void edit(final UpdateProfileRepresentation update)
    {
        getAPI().getProfileService().updateProfile(update, new Callback<UserRepresentation>()
        {
            @Override
            public void onResponse(Call<UserRepresentation> call, Response<UserRepresentation> response)
            {
                if (!response.isSuccessful())
                {
                    onFailure(call, new Exception(response.message()));
                    return;
                }

                userRepresentation = response.body();
                displayInfo();

                switch (fieldId)
                {
                    case EDIT_EMAIL_ID:
                        if (TextUtils.isEmpty(userRepresentation.getExternalId()))
                        {
                            ActivitiSession.getInstance().updateCredentials(userRepresentation.getEmail(),
                                    getAccount().getPassword());
                            ActivitiAccountManager.getInstance(getActivity()).update(getAccount().getId(),
                                    ActivitiAccount.ACCOUNT_USERNAME, userRepresentation.getEmail());
                        }
                        break;
                    case EDIT_FIRSTNAME_ID:
                    case EDIT_NAME_ID:
                        ActivitiAccountManager.getInstance(getActivity()).update(getAccount().getId(),
                                ActivitiAccount.ACCOUNT_USER_FULLNAME, userRepresentation.getFullname());
                        break;
                    case EDIT_COMPANY_ID:
                        break;
                }
            }

            @Override
            public void onFailure(Call<UserRepresentation> call, Throwable error)
            {
                Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CARDS
    // ///////////////////////////////////////////////////////////////////////////
    protected void displayLoading()
    {
        hide(R.id.details_container);
        show(R.id.details_loading);
        show(R.id.progressbar);
        hide(R.id.empty);
    }

    protected void displayData()
    {
        show(R.id.details_container);
        hide(R.id.details_loading);
        hide(R.id.progressbar);
        hide(R.id.empty);
    }

    protected void displayError(Throwable error)
    {
        hide(R.id.details_container);
        show(R.id.details_loading);
        hide(R.id.progressbar);
        show(R.id.empty);

        // Update controls in regards
        TextView emptyText = (TextView) viewById(R.id.empty_text);
        if (getActivity() != null) {
            emptyText.setText(ExceptionMessageUtils.getMessage(getActivity(), error));
        }
        Button bRetry = (Button) viewById(R.id.empty_action);
        bRetry.setText(R.string.retry);
        bRetry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                load();
            }
        });
        bRetry.setVisibility(View.VISIBLE);
    }
}
