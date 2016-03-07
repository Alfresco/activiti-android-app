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

package com.activiti.android.app.fragments;

import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.platform.utils.ConnectivityUtils;
import com.activiti.android.ui.fragments.builder.LeafFragmentBuilder;
import com.activiti.android.ui.utils.DisplayUtils;

public class HelpDialogFragment extends DialogFragment
{
    public static final String TAG = HelpDialogFragment.class.getName();

    private boolean isDefault = false;

    private WebView webView;

    private View emptyView;

    private TextView emptyTextView;

    private String defaultUrl = null;

    private String rootUrl = null;

    private MenuItem refreshIcon;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public HelpDialogFragment()
    {
        setHasOptionsMenu(true);
    }

    public static HelpDialogFragment newInstanceByTemplate(Bundle b)
    {
        HelpDialogFragment cbf = new HelpDialogFragment();
        cbf.setArguments(b);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.app_webview, container, false);

        getToolbar().setVisibility(View.GONE);

        webView = (WebView) v.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        emptyView = v.findViewById(R.id.empty);
        emptyTextView = (TextView) v.findViewById(R.id.empty_text);
        emptyTextView.setText(Html.fromHtml(getString(R.string.error_offline)));

        final Activity activity = getActivity();

        defaultUrl = activity.getString(R.string.help_user_guide_default_url);

        webView.setWebViewClient(new WebViewClient()
        {
            boolean hasError = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                hasError = false;
                getActivity().setProgressBarIndeterminateVisibility(true);
                refreshIcon.setVisible(false);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                super.onReceivedError(view, errorCode, description, failingUrl);

                // We redirect to default EN documentation if locale docs are
                // not available.
                if ((errorCode == ERROR_FILE_NOT_FOUND || errorCode == ERROR_HOST_LOOKUP) && !isDefault
                        && failingUrl.equals(rootUrl))
                {
                    hasError = true;
                    view.loadUrl(defaultUrl);
                    view.setVisibility(View.GONE);
                }
                else if (!ConnectivityUtils.hasInternetAvailable(getActivity()))
                {
                    view.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    hasError = true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                if (hasError)
                {
                    view.setVisibility(View.GONE);
                }
                else
                {
                    view.setVisibility(View.VISIBLE);
                }

                if (getActivity() != null)
                {
                    getActivity().setProgressBarIndeterminateVisibility(false);
                    refreshIcon.setVisible(true);
                }
            }

            public void onFormResubmission(WebView view, Message dontResend, Message resend)
            {
                resend.sendToTarget();
            }

        });

        webView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack())
                            {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });

        rootUrl = getUrl(activity);
        webView.loadUrl(rootUrl);

        return v;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getToolbar().setVisibility(View.VISIBLE);
    }

    protected Toolbar getToolbar()
    {
        if (DisplayUtils.hasCentralPane(getActivity()))
        {
            return (Toolbar) getActivity().findViewById(R.id.toolbar_central);
        }
        else
        {
            return (Toolbar) getActivity().findViewById(R.id.toolbar);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    private String getUrl(Activity activity)
    {
        String prefix = activity.getString(R.string.docs_prefix);
        String urlValue = null;
        if (TextUtils.isEmpty(prefix))
        {
            isDefault = true;
            urlValue = activity.getString(R.string.help_user_guide_default_url);
        }
        else
        {
            isDefault = false;
            urlValue = String.format(activity.getString(R.string.help_user_guide_url), prefix);
        }
        return urlValue;
    }

    public void refresh()
    {
        webView.reload();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        refreshIcon = menu.add(Menu.NONE, R.id.menu_refresh, Menu.FIRST, R.string.refresh);
        refreshIcon.setIcon(R.drawable.ic_refresh_white);
        refreshIcon.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_refresh:
                refresh();
                return true;
        }
        return false;
    }

    public static class Builder extends LeafFragmentBuilder
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
        // CREATE
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        }
    }
}