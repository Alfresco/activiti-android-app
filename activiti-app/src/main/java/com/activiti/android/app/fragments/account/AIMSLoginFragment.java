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

package com.activiti.android.app.fragments.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;

import java.util.Map;

/**
 * Created by Alexandru Chiriac on 2020-01-07.
 */
public class AIMSLoginFragment extends AlfrescoFragment {

    public static final String TAG = AIMSLoginFragment.class.getName();

    public static AIMSLoginFragment newInstanceByTemplate(Bundle args) {
        AIMSLoginFragment fragment = new AIMSLoginFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static AIMSLoginFragment.Builder with(FragmentActivity activity) {
        return new AIMSLoginFragment.Builder(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRootView(inflater.inflate(R.layout.fr_aims_account_signin, container, false));

        return getRootView();
    }

    public static class Builder extends AlfrescoFragmentBuilder {

        public Builder(FragmentActivity activity) {
            super(activity);
            this.extraConfiguration = new Bundle();
        }

        public Builder(FragmentActivity appActivity, Map<String, Object> configuration) {
            super(appActivity, configuration);
        }

        protected Fragment createFragment(Bundle b) {
            return newInstanceByTemplate(b);
        }
    }
}
