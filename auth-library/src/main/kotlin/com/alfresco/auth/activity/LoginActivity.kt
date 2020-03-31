package com.alfresco.auth.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.alfresco.android.aims.R
import com.alfresco.auth.AuthConfig
import com.alfresco.auth.AuthType
import com.alfresco.auth.Credentials
import com.alfresco.auth.fragments.AdvancedSettingsFragment
import com.alfresco.auth.fragments.BasicAuthFragment
import com.alfresco.auth.fragments.HelpFragment
import com.alfresco.auth.fragments.SsoAuthFragment
import com.alfresco.auth.fragments.WelcomeFragment
import com.alfresco.common.getViewModel
import com.alfresco.auth.ui.AuthenticationActivity
import com.alfresco.auth.ui.observe


abstract class LoginActivity : AuthenticationActivity<LoginViewModel>() {

    override val viewModel: LoginViewModel by lazy {
        getViewModel {
            LoginViewModel.with(applicationContext)
        }
    }

    private lateinit var progressView: RelativeLayout

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContentView(R.layout.aims_activity_welcome)

        setupToolbar()

        progressView = findViewById(R.id.rlProgress)
        ViewCompat.setElevation(progressView, 10f)

        if (supportFragmentManager.findFragmentByTag(WelcomeFragment.TAG) == null) {
            WelcomeFragment.with(this).replace()
        }

        observe(viewModel.hasNavigation, ::onNavigation)

        observe(viewModel.onAuthType, ::onAuthType)
        observe(viewModel.startSSO, ::login)

        observe(viewModel.onShowHelp, ::showHelp)
        observe(viewModel.onShowSettings, ::showSettings)
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.aims_save_settings -> return false

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onLoading(isLoading: Boolean) {
        progressView.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun onNavigation(hasNavigation: Boolean) {
        if (hasNavigation) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener { onBackPressed() }

        } else {
            toolbar.setNavigationIcon(null)
            toolbar.setNavigationOnClickListener(null)
        }
    }

    abstract fun onCredentials(credentials: Credentials, endpoint: String, authConfig: AuthConfig)

    override fun onCredentials(credentials: Credentials) {
        onCredentials(credentials, viewModel.getApplicationServiceUrl(), viewModel.authConfig)
    }

    override fun onError(error: String) {
        // TODO: not implemented
    }

    override fun onAuthType(type: AuthType) {
        when (type) {
            AuthType.SSO -> {
                SsoAuthFragment.with(this).display()
            }

            AuthType.BASIC -> {
                BasicAuthFragment.with(this).display()
            }

            AuthType.UNKNOWN -> {
                Toast.makeText(this, "Auth type: unknown", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSettings(ignored: Int) {
        AdvancedSettingsFragment.with(this).display()
    }

    private fun showHelp(@StringRes msgResId: Int) {
        HelpFragment.with(this).message(msgResId).show()
    }
}
