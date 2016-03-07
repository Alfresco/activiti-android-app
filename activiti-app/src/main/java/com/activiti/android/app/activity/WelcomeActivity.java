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
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.app.fragments.account.ServerFragment;
import com.activiti.android.app.fragments.account.SignInFragment;
import com.activiti.android.app.fragments.account.WelcomeFragment;
import com.activiti.android.ui.fragments.FragmentDisplayer;
import com.activiti.client.api.constant.ActivitiAPI;
import com.squareup.picasso.Picasso;

/**
 * A login screen that offers login via email/password.
 */
public class WelcomeActivity extends FragmentActivity
{
    public static final String EXTRA_ADD_ACCOUNT = "addAccount";

    protected Picasso picasso;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        if (getSupportFragmentManager().findFragmentByTag(WelcomeFragment.TAG) == null)
        {
            FragmentDisplayer.with(this)
                    .load(WelcomeFragment.with(this).addExtra(getIntent().getExtras()).createFragment()).animate(null)
                    .back(false).into(FragmentDisplayer.PANEL_LEFT);
        }

        picasso = new Picasso.Builder(this).build();
    }

    public void signIn(View v)
    {
        ServerFragment.with(this).display();
    }

    public void signInOnline(View v)
    {
        SignInFragment.with(this).hostname(ActivitiAPI.SERVER_URL_ENDPOINT).display();
    }

    public Picasso getPicasso()
    {
        return picasso;
    }

}
