package com.activiti.android.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class IntentUtils {

    public static Uri exposeFile(File file, Intent intent, Context context) {
        Uri sendUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            sendUri = Uri.fromFile(file);
        }

        return sendUri;
    }
}
