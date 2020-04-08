package com.activiti.android.app.activity;

import android.content.Intent;

import org.jetbrains.annotations.NotNull;

public class SplashActivity extends com.alfresco.ui.SplashActivity {

    @NotNull
    @Override
    public Intent getMainIntent() {
        return new Intent(this, MainActivity.class);
    }
}
