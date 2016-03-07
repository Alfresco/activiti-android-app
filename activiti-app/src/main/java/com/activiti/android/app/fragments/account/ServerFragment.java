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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.android.ui.utils.UIUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ServerFragment extends AlfrescoFragment
{
    public static final String TAG = ServerFragment.class.getName();

    private MaterialEditText hostname;

    private CheckBox https;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public ServerFragment()
    {
        super();
    }

    public static ServerFragment newInstanceByTemplate(Bundle b)
    {
        ServerFragment cbf = new ServerFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_account_server, container, false));
        return getRootView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        final LinearLayout backField = (LinearLayout) viewById(R.id.account_action_server_container);
        final Button actionContinue = (Button) viewById(R.id.account_action_server);
        actionContinue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String value = createHostnameURL(hostname.getText().toString(), https.isChecked());
                if (value == null)
                {
                    hostname.setError("Your hostname seems invalid");
                }
                else
                {
                    // TODO Heartbeat
                    SignInFragment.with(getActivity()).hostname(hostname.getText().toString()).https(https.isChecked())
                            .display();
                }
            }
        });

        https = (CheckBox) viewById(R.id.signing_https);

        hostname = (MaterialEditText) viewById(R.id.signing_hostname);
        hostname.requestFocus();
        UIUtils.showKeyboard(getActivity(), hostname);

        if (hostname.getText().length() == 0)
        {
            actionContinue.setEnabled(false);
            backField.setBackgroundColor(getResources().getColor(R.color.accent_disable));
        }

        hostname.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() == 0)
                {
                    actionContinue.setEnabled(false);
                    backField.setBackgroundColor(getResources().getColor(R.color.accent_disable));
                }
                else
                {
                    actionContinue.setEnabled(true);
                    backField.setBackgroundColor(getResources().getColor(R.color.accent));
                }
            }
        });
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    public static String createHostnameURL(String hostname, boolean https)
    {
        String value = hostname.trim().toLowerCase();

        StringBuilder builder = new StringBuilder();
        boolean isComplete = false;

        // Check if starts with http ?
        if (value.toLowerCase().startsWith("http") || value.startsWith("https"))
        {
            // Do Nothing. We consider it's a plain url
            isComplete = true;
            builder.append(value);
            builder.append(value.endsWith("/") ? "" : "/");
        }
        else
        {
            builder.append((https) ? "https://" : "http://");
            builder.append(value);
            builder.append(value.endsWith("/") ? "activit-app/" : "/activiti-app/");
        }

        // Check it's a valid URL
        try
        {
            URL url = new URL(builder.toString());
        }
        catch (MalformedURLException e)
        {
            // Display Error !
            return null;
        }

        return builder.toString();
    }

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

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        };
    }
}
