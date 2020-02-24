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
import com.alfresco.auth.fragments.AdvancedSettingsFragment
import com.alfresco.auth.fragments.BasicAuthFragment
import com.alfresco.auth.fragments.HelpFragment
import com.alfresco.auth.fragments.SsoAuthFragment
import com.alfresco.auth.fragments.WelcomeFragment
import com.alfresco.common.getViewModel
import com.alfresco.auth.ui.AlfrescoAuthActivity
import com.alfresco.auth.ui.PkceAuthUiModel
import com.alfresco.auth.ui.observe


abstract class WelcomeActivity : AlfrescoAuthActivity<AIMSWelcomeViewModel>() {

    override val viewModel: AIMSWelcomeViewModel by lazy {
        getViewModel { AIMSWelcomeViewModel(applicationContext) }
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

        observe(viewModel.authType, ::onAuthType)
        observe(viewModel.authResult, ::onPkceAuthUiModel)

        observe(viewModel.hasNavigation, ::onNavigation)

        observe(viewModel.startSSO, ::aimsLogin)

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

    abstract fun onCredentials(user: String?, token: String?)

    fun onAuthType(authType: AuthenticationType) {
        when (authType) {
            is AuthenticationType.SSO -> {
                SsoAuthFragment.with(this).display()
            }

            is AuthenticationType.Basic -> {
                BasicAuthFragment.with(this).withHostname(authType.hostname).withCloud(authType.withCloud).display()
            }

            is AuthenticationType.Unknown -> {
                Toast.makeText(this, "Auth type: unknown", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onPkceAuthUiModel(authResult: PkceAuthUiModel) {
        when (authResult.success) {
            true -> {
                onCredentials(authResult.userEmail, authResult.accessToken)
            }

            false -> {
                Toast.makeText(this, "Login Failed: " + authResult.error, Toast.LENGTH_LONG).show()
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