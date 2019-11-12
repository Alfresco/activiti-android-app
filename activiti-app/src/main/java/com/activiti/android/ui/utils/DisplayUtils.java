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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import androidx.fragment.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;

import com.activiti.android.app.R;

public abstract class DisplayUtils
{

    public static int getFragmentPlace(FragmentActivity a)
    {
        int id = R.id.left_pane_body;
        if (DisplayUtils.hasCentralPane(a))
        {
            id = R.id.central_pane_body;
        }
        return id;
    }

    // ///////////////////////////////////////////
    // FLAGS
    // ///////////////////////////////////////////
    public static boolean hasLeftPane(FragmentActivity a)
    {
        return true;
    }

    public static boolean hasCentralPane(FragmentActivity a)
    {
        return a.getResources().getBoolean(R.bool.hasTabletLayout);
    }

    // ///////////////////////////////////////////
    // RETRIEVE FRAGMENT IDS
    // ///////////////////////////////////////////
    public static int getLeftFragmentId(FragmentActivity a)
    {
        return R.id.left_pane_body;
    }

    public static int getCentralFragmentId(FragmentActivity a)
    {
        return R.id.central_pane_body;
    }

    public static int getMainPaneId(FragmentActivity a)
    {
        if (hasCentralPane(a)) { return getCentralFragmentId(a); }
        return getLeftFragmentId(a);
    }

    // ///////////////////////////////////////////
    // RETRIEVE PANEL
    // ///////////////////////////////////////////
    public static View getLeftPane(FragmentActivity a)
    {
        return a.findViewById(R.id.left_panel);
    }

    public static View getCentralPane(FragmentActivity a)
    {
        return a.findViewById(R.id.central_panel);
    }

    public static View getMainPane(FragmentActivity a)
    {
        if (hasCentralPane(a)) { return getCentralPane(a); }
        return getLeftPane(a);
    }

    // ///////////////////////////////////////////
    // SHOW / HIDE
    // ///////////////////////////////////////////
    public static void hide(View v)
    {
        v.setVisibility(View.GONE);
    }

    public static void hide(View v, int id)
    {
        v.findViewById(id).setVisibility(View.GONE);
    }

    public static void show(View v)
    {
        v.setVisibility(View.VISIBLE);
    }

    public static void show(View v, int id)
    {
        v.findViewById(id).setVisibility(View.VISIBLE);
    }

    // ///////////////////////////////////////////
    // SIZE OF THE SCREEN
    // ///////////////////////////////////////////
    public static int getWidth(Activity context)
    {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = context.getResources().getDisplayMetrics().density;
        int width = Math.round(outMetrics.widthPixels / density);

        Resources res = context.getResources();

        int coeff = 150;

        return coeff;
    }

    public static int getDPI(DisplayMetrics dm, int sizeInDp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp, dm);
    }

    public static int getPixels(Context context, int sizeInDp)
    {
        return context.getResources().getDimensionPixelSize(sizeInDp);
    }

    public static LinearLayout.LayoutParams resizeLayout(Context context, int widthInDp, int heightInDp)
    {
        return new LinearLayout.LayoutParams(getDPI(context.getResources().getDisplayMetrics(), widthInDp),
                getDPI(context.getResources().getDisplayMetrics(), heightInDp));
    }
}
