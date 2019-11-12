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

package com.activiti.android.app.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.activiti.android.app.R
import com.activiti.android.app.fragments.auth.AuthViewModel
import com.activiti.android.app.fragments.auth.StartAuthFragment
import com.activiti.android.ui.fragments.FragmentDisplayer

/**
 * A login screen that offers login via email/password.
 */
class WelcomeActivity : FragmentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        if (supportFragmentManager.findFragmentByTag(StartAuthFragment.tag) == null) {
            FragmentDisplayer.with(this)
                    .load(StartAuthFragment.with(this).createFragment())
                    .animate(null)
                    .back(false)
                    .into(FragmentDisplayer.PANEL_LEFT)
        }
    }

    companion object {
        val EXTRA_ADD_ACCOUNT = "addAccount"
    }
}
