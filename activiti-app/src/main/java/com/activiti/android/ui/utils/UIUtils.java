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

package com.activiti.android.ui.utils;

import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.utils.AndroidVersion;

/**
 * Utility around UI Management.
 * 
 * @author Jean Marie Pascal
 */
@TargetApi(16)
public class UIUtils
{

    private static final Pattern NAME_PATTERN = Pattern
            .compile("(.*[\"\\*\\\\>\\<\\?\\/\\:\\|]+.*)|(.*[\\.]?.*[\\.]+$)|(.*[ ]+$)");

    public static int getDPI(Context context, int sizeInDp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Set the background view with the drawable associated.
     *
     * @param v
     * @param background
     */
    @SuppressWarnings("deprecation")
    public static void setBackground(View v, Drawable background)
    {
        if (AndroidVersion.isJBOrAbove())
        {
            v.setBackground(background);
        }
        else
        {
            v.setBackgroundDrawable(background);
        }
    }

    /**
     * Retrieve screen dimension.
     *
     * @param activity
     * @return
     */
    public static int[] getScreenDimension(Activity activity)
    {
        int width = 0;
        int height = 0;

        Display display = activity.getWindowManager().getDefaultDisplay();
        if (AndroidVersion.isHCMR2OrAbove())
        {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
        }
        else
        {
            width = display.getWidth(); // deprecated
            height = display.getHeight(); // deprecated
        }

        return new int[] { width, height };
    }

    public static void showKeyboard(Activity activity, View v)
    {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(v, 0);
    }

    public static void hideKeyboard(Activity activity, View v)
    {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private static Button initFooterView(View vRoot, int viewId, int actionId)
    {
        Button button = (Button) vRoot.findViewById(viewId);
        button.setText(actionId);
        return button;
    }

    public static Button initValidation(View vRoot, int actionId)
    {
        return initFooterView(vRoot, R.id.validate_action, actionId);
    }

    public static Button initCancel(View vRoot, int actionId)
    {
        return initFooterView(vRoot, R.id.cancel, actionId);
    }

    public static Button initClear(View vRoot, int actionId, boolean hide)
    {
        Button bcreate = initFooterView(vRoot, R.id.clear, actionId);
        if (hide)
        {
            bcreate.setVisibility(View.GONE);
        }
        return bcreate;
    }

    public static Button initCancel(View vRoot, int actionId, boolean hide)
    {
        Button bcreate = initCancel(vRoot, actionId);
        if (hide)
        {
            bcreate.setVisibility(View.GONE);
        }
        return bcreate;
    }

    public static Button initValidation(View vRoot, int actionId, boolean hide)
    {
        Button bcreate = initValidation(vRoot, actionId);
        if (hide)
        {
            vRoot.findViewById(R.id.cancel).setVisibility(View.GONE);
        }
        return bcreate;
    }

    public static void setTitle(FragmentActivity activity, String title, String subTitle)
    {
        setTitle(activity, title, subTitle, false);
    }

    public static void setTitle(FragmentActivity activity, String title, String subTitle, boolean isLeaf)
    {
        if (isLeaf && DisplayUtils.hasCentralPane(activity))
        {
            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar_central);
            toolbar.setTitle(title);
            toolbar.setSubtitle(subTitle);
        }
        else
        {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setTitle(title);
            actionBar.setSubtitle(subTitle);
        }
    }

    public static void displayActionBarBack(MainActivity activity)
    {
        if (activity == null || activity.getmDrawerToggle() == null) { return; }
        activity.lockSlidingMenu();
        activity.getmDrawerToggle().setDrawerIndicatorEnabled(false);
        activity.getmDrawerToggle().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
    }

    public static void displayActionBarBack(final MainActivity activity, Toolbar toolbar)
    {
        if (toolbar == null) { return; }
        if (DisplayUtils.hasCentralPane(activity))
        {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    activity.onBackPressed();
                }
            });
        }
        else
        {
            displayActionBarBack(activity);
        }

    }

    public static void setActionBarDefault(MainActivity activity, Toolbar toolbar)
    {
        if (DisplayUtils.hasCentralPane(activity))
        {
            toolbar.setNavigationIcon(null);
        }
        else
        {
            setActionBarDefault(activity);
        }
    }

    public static void setActionBarDefault(MainActivity activity)
    {
        if (activity == null || activity.getmDrawerToggle() == null) { return; }
        activity.unlockSlidingMenu();
        activity.getmDrawerToggle().setDrawerIndicatorEnabled(true);
        activity.getmDrawerToggle().setHomeAsUpIndicator(null);

        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        actionBar.setCustomView(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);
    }

    public static View setActionBarCustomView(FragmentActivity activity, int layoutId, boolean fillParent)
    {
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(layoutId);

        if (fillParent)
        {
            View v = actionBar.getCustomView();
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            v.setLayoutParams(lp);
        }
        return actionBar.getCustomView();
    }
}
