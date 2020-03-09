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

package com.activiti.android.app.fragments.account;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.account.ActivitiAccount;
import com.activiti.android.platform.account.ActivitiAccountManager;
import com.activiti.android.platform.rendition.RenditionManager;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.sdk.ActivitiSession;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.utils.UIUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

public class OptionalFragment extends AlfrescoFragment
{
    public static final String TAG = OptionalFragment.class.getName();

    private static final String ARGUMENT_ACCOUNT_ID = "accountId";

    private ActivitiAccount account;

    private MaterialEditText accountView;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public OptionalFragment()
    {
        super();
    }

    public static OptionalFragment newInstanceByTemplate(Bundle b)
    {
        OptionalFragment cbf = new OptionalFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_account_optional, container, false));
        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Long accountId = BundleUtils.getLong(getArguments(), ARGUMENT_ACCOUNT_ID);
        account = ActivitiAccountManager.getInstance(getActivity()).getByAccountId(accountId);
        ActivitiSession session = new ActivitiSession.Builder().connect(account.getServerUrl(),
                account.getUsername(), account.getPassword(), account.getAuthType()).build();

        accountView = ((MaterialEditText) viewById(R.id.account_name));
        accountView.setHint(account.getLabel());
        accountView.requestFocus();
        UIUtils.showKeyboard(getActivity(), accountView);

        Picasso picasso = new RenditionManager(getActivity(), session).getPicasso();
        picasso.load(session.getServiceRegistry().getProfileService().getProfilePictureURL())
                .placeholder(R.drawable.ic_account_box_grey).fit().into((ImageView) viewById(R.id.profile_picture));

        viewById(R.id.account_action_server).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (accountView.getText().length() > 0)
                {
                    ActivitiAccountManager.getInstance(getActivity()).update(account.getId(),
                            ActivitiAccount.ACCOUNT_TITLE, accountView.getText().toString().trim());
                }

                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends AlfrescoFragmentBuilder
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

        public Builder acocuntId(Long accountId)
        {
            extraConfiguration.putLong(ARGUMENT_ACCOUNT_ID, accountId);
            return this;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        };
    }
}
