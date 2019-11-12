/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
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

package com.activiti.android.platform.intent;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.fragment.app.Fragment;
import androidx.core.app.ShareCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.activiti.android.app.R;

/**
 * Created by jpascal on 27/04/2015.
 */
public class IntentUtils
{

    public static void startPlayStore(Context context, String appPackage)
    {
        final String appPackageName = appPackage;
        try
        {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void startWebBrowser(Context context, String url)
    {
        final String webUrl = url;
        try
        {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            // Display error ?
        }
    }

    /**
     * Allow to send a link to other application installed in the device.
     *
     * @param fr
     * @param url
     */
    public static void actionShareLink(Fragment fr, String title, String url)
    {
        try
        {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, title);
            i.putExtra(Intent.EXTRA_TEXT, url);
            fr.startActivity(Intent.createChooser(i, fr.getActivity().getText(R.string.task_action_share_link)));
        }
        catch (ActivityNotFoundException e)
        {

        }
    }

    public static boolean actionSendFeedbackEmail(Fragment fr)
    {
        try
        {
            ShareCompat.IntentBuilder iBuilder = ShareCompat.IntentBuilder.from(fr.getActivity());
            Context context = fr.getContext();
            // Email
            iBuilder.addEmailTo(context.getResources().getStringArray(R.array.bugreport_email));

            // Prepare Subject
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;

            String subject = "Alfresco Activiti Android Feedback";
            iBuilder.setSubject(subject);

            // Content
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            String densityBucket = getDensityString(dm);

            Map<String, String> info = new LinkedHashMap<>();
            info.put("Version", versionName);
            info.put("Version code", Integer.toString(versionCode));
            info.put("Make", Build.MANUFACTURER);
            info.put("Model", Build.MODEL);
            info.put("Resolution", dm.heightPixels + "x" + dm.widthPixels);
            info.put("Density", dm.densityDpi + "dpi (" + densityBucket + ")");
            info.put("Release", Build.VERSION.RELEASE);
            info.put("API", String.valueOf(Build.VERSION.SDK_INT));
            info.put("Language", context.getResources().getConfiguration().locale.getDisplayLanguage());

            StringBuilder builder = new StringBuilder();
            builder.append("\n\n\n\n");
            builder.append("Alfresco Activiti Mobile and device details\n");
            builder.append("-------------------\n").toString();
            for (Map.Entry entry : info.entrySet())
            {
                builder.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
            }

            builder.append("-------------------\n\n").toString();
            iBuilder.setType("message/rfc822");
            iBuilder.setText(builder.toString());
            iBuilder.setChooserTitle(fr.getString(R.string.settings_feedback_email)).startChooser();

            return true;
        }
        catch (Exception e)
        {
            Log.d("Action Send Feedback", Log.getStackTraceString(e));
        }

        return false;
    }

    private static String getDensityString(DisplayMetrics displayMetrics)
    {
        switch (displayMetrics.densityDpi)
        {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return "unknown";
        }
    }

}
