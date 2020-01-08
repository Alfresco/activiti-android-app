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

package com.activiti.android.app.activity;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.activiti.android.app.R;
import com.activiti.android.app.fragments.account.AIMSLoginFragment;
import com.activiti.android.ui.fragments.FragmentDisplayer;

/**
 * Created by Alexandru Chiriac on 2020-01-07.
 */
public class AIMSWelcomeActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aims_activity_signin);

        if (getSupportFragmentManager().findFragmentByTag(AIMSLoginFragment.TAG) == null) {
            FragmentDisplayer.with(this)
                    .load(AIMSLoginFragment.with(this).addExtra(getIntent().getExtras()).createFragment()).animate(null)
                    .back(false).into(FragmentDisplayer.PANEL_LEFT);
        }
    }
}
