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

package com.activiti.android.platform.provider.appIcon;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import androidx.annotation.StringDef;

import com.activiti.android.app.R;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class AppIcon implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long id;

    private String iconId;

    private String textValue;

    public AppIcon(Long id, String iconId, String textValue)
    {
        super();
        this.id = id;
        this.iconId = iconId;
        this.textValue = textValue;
    }

    public long getId()
    {
        return id;
    }

    public String getIconId()
    {
        return iconId;
    }

    public String getTextValue()
    {
        return textValue;
    }

    public Spanned getCharacter()
    {
        return Html.fromHtml("&#x".concat(textValue));
    }

    @StringDef({ THEME_1, THEME_2, THEME_3, THEME_4, THEME_5, THEME_6, THEME_7, THEME_8, THEME_9, THEME_10 })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeColor
    {
    }

    public static final String THEME_1 = "theme-1";

    public static final String THEME_2 = "theme-2";

    public static final String THEME_3 = "theme-3";

    public static final String THEME_4 = "theme-4";

    public static final String THEME_5 = "theme-5";

    public static final String THEME_6 = "theme-6";

    public static final String THEME_7 = "theme-7";

    public static final String THEME_8 = "theme-8";

    public static final String THEME_9 = "theme-9";

    public static final String THEME_10 = "theme-10";

    // Attach the annotation
    public int getColorId(@ThemeColor String mode)
    {
        if (TextUtils.isEmpty(mode)) { return R.color.secondary_text; }
        switch (mode)
        {
            case THEME_1:
                return R.color.app_theme_1;
            case THEME_2:
                return R.color.app_theme_2;
            case THEME_3:
                return R.color.app_theme_3;
            case THEME_4:
                return R.color.app_theme_4;
            case THEME_5:
                return R.color.app_theme_5;
            case THEME_6:
                return R.color.app_theme_6;
            case THEME_7:
                return R.color.app_theme_7;
            case THEME_8:
                return R.color.app_theme_8;
            case THEME_9:
                return R.color.app_theme_9;
            case THEME_10:
                return R.color.app_theme_10;
            default:
                return R.color.secondary_text;
        }
    }
}
